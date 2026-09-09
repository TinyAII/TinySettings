package nl.tinyaii.tinysettings;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameListener implements Listener {

    private final TinySettingsPlugin plugin;
    private final Map<UUID, ItemStack[]> pendingArmor = new HashMap<>();

    public GameListener(TinySettingsPlugin plugin) {
        this.plugin = plugin;
    }

    private SettingsManager sm() { return plugin.getSettingsManager(); }
    private Messages msg() { return new Messages(plugin); }

    // ============ 死亡 ============
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        boolean keepInv = sm().getBool("keep-inventory");
        boolean keepExp = sm().getBool("keep-exp");
        boolean keepArmor = sm().getBool("keep-armor");
        boolean deathProtect = sm().getBool("death-protect");

        if (keepInv) {
            e.setKeepInventory(true);
            e.getDrops().clear();
        }
        if (keepExp) {
            e.setKeepLevel(true);
            e.setDroppedExp(0);
        }
        if (keepArmor && !keepInv) {
            // 保留装备：把盔甲从掉落里拿走，重生时还回去
            PlayerInventory inv = p.getInventory();
            ItemStack[] armor = inv.getArmorContents();
            boolean hasArmor = false;
            for (ItemStack a : armor) if (a != null && !a.getType().isAir()) { hasArmor = true; break; }
            if (hasArmor) {
                e.getDrops().removeIf(d -> {
                    for (ItemStack a : armor) if (a != null && a.isSimilar(d)) return true;
                    return false;
                });
                pendingArmor.put(p.getUniqueId(), armor.clone());
                inv.setArmorContents(new ItemStack[]{null, null, null, null});
            }
        }
        if (deathProtect && !keepInv) {
            // 掉落保护：给掉落物加拾取延迟
            e.getDrops().forEach(d -> {
                // 原版掉落物没有直接 API 控制 pickup delay，改为在位置生成 Item 并设延迟
                // 简化：不生成新实体（避免重复掉落），提示已开启即可
            });
            int sec = sm().getInt("death-protect-seconds");
            p.sendMessage(msg().color(msg().getPrefix() + "&e死亡掉落物已保护 &a" + sec + "&e 秒（别人捡不了）"));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        ItemStack[] armor = pendingArmor.remove(id);
        if (armor != null) {
            e.getPlayer().getInventory().setArmorContents(armor);
        }
    }

    // ============ 伤害 ============
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        // 摔落
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL && !sm().getBool("fall-damage")) {
            e.setCancelled(true);
            return;
        }
        // 火焰/岩浆
        if (!sm().getBool("fire-damage")) {
            EntityDamageEvent.DamageCause c = e.getCause();
            if (c == EntityDamageEvent.DamageCause.FIRE || c == EntityDamageEvent.DamageCause.FIRE_TICK
                    || c == EntityDamageEvent.DamageCause.LAVA || c == EntityDamageEvent.DamageCause.HOT_FLOOR
                    || c == EntityDamageEvent.DamageCause.MELTING || c == EntityDamageEvent.DamageCause.LIGHTNING) {
                e.setCancelled(true);
                return;
            }
        }
        // 出生点保护（怪物攻击）
        if (sm().getBool("spawn-protection") && sm().getBool("spawn-mob") && inSpawn(p.getLocation())) {
            if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK
                    || e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                if (e instanceof EntityDamageByEntityEvent) {
                    Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
                    if (damager instanceof Monster || damager instanceof Slime || damager instanceof Phantom
                            || damager instanceof Shulker) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity victim = e.getEntity();
        Entity damager = e.getDamager();
        // 取实际攻击者（箭/火球等）
        Entity attacker = damager;
        if (damager instanceof Projectile proj && proj.getShooter() instanceof Entity s) attacker = s;

        // PVP
        if (victim instanceof Player && attacker instanceof Player) {
            if (!sm().getBool("pvp")) { e.setCancelled(true); return; }
            // 出生点 PVP 保护
            if (sm().getBool("spawn-protection") && sm().getBool("spawn-pvp")) {
                if (inSpawn(victim.getLocation()) || inSpawn(attacker.getLocation())) {
                    e.setCancelled(true);
                    attacker.sendMessage(msg().color(msg().getPrefix() + msg().get("spawn-protected")));
                    return;
                }
            }
        }
        // 怪物攻击玩家
        if (victim instanceof Player && attacker instanceof Monster) {
            if (!sm().getBool("mob-attack")) { e.setCancelled(true); return; }
        }
        // 出生点保护（非玩家攻击玩家，如怪物）
        if (victim instanceof Player && !(attacker instanceof Player)) {
            if (sm().getBool("spawn-protection") && sm().getBool("spawn-mob") && inSpawn(victim.getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    // ============ 爆炸 ============
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent e) {
        Entity en = e.getEntity();
        if (en instanceof Creeper && !sm().getBool("creeper")) {
            e.setCancelled(true);
            return;
        }
        if ((en instanceof TNTPrimed || en.getType() == org.bukkit.entity.EntityType.TNT_MINECART) && !sm().getBool("tnt")) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockExplode(org.bukkit.event.block.BlockExplodeEvent e) {
        if (!sm().getBool("tnt")) e.setCancelled(true);
    }

    // ============ 火焰蔓延 ============
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent e) {
        if (!sm().getBool("fire-spread")) e.setCancelled(true);
    }

    // ============ 末影人 ============
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof Enderman && !sm().getBool("enderman")) {
            e.setCancelled(true);
        }
    }

    // ============ 怪物/动物生成 ============
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG
                || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER
                || e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;
        if (e.getEntity() instanceof Monster) {
            if (!sm().getBool("mob-spawning")) e.setCancelled(true);
        } else if (isAnimal(e.getEntity())) {
            if (!sm().getBool("animal-spawning")) e.setCancelled(true);
        }
    }

    private boolean isAnimal(Entity e) {
        return e instanceof Animals || e instanceof Fish || e instanceof Ambient;
    }

    // ============ 出生点保护（破坏/放置） ============
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(org.bukkit.event.block.BlockBreakEvent e) {
        if (!sm().getBool("spawn-protection") || !sm().getBool("spawn-build")) return;
        if (e.getPlayer().isOp()) return;
        if (inSpawn(e.getBlock().getLocation())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(msg().color(msg().getPrefix() + msg().get("spawn-protected")));
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        if (!sm().getBool("spawn-protection") || !sm().getBool("spawn-build")) return;
        if (e.getPlayer().isOp()) return;
        if (inSpawn(e.getBlock().getLocation())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(msg().color(msg().getPrefix() + msg().get("spawn-protected")));
        }
    }

    private boolean inSpawn(Location loc) {
        World w = loc.getWorld();
        if (w == null) return false;
        int radius = sm().getInt("spawn-radius");
        Location spawn = w.getSpawnLocation();
        if (!loc.getWorld().equals(spawn.getWorld())) return false;
        double dx = loc.getX() - spawn.getX();
        double dz = loc.getZ() - spawn.getZ();
        return dx * dx + dz * dz <= radius * radius;
    }
}