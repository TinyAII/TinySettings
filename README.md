# TinySettings 一键设置插件

死亡不掉落 / 难度 / PVP / 防爆 / 天气 / 时间 / 出生点保护，GUI + 命令全搞定，零依赖。

## 功能特性

- **GUI 一键切换**：游戏内 `/设置` 打开图形界面，染色玻璃显示开/关状态，点击即改
- **多选项进选择页**：难度（和平/简单/普通/困难）、时间（永昼/永夜/正常）、天气（晴/雨/雷雨/正常）点击进入单独选择页单选
- **布尔开关直接切**：2 个选项（开/关）点击立即切换
- **玩法设置**：死亡不掉落 / 死亡不掉经验 / 死亡保留装备 / 掉落保护
- **战斗设置**：PVP / 怪物攻击玩家 / 摔落伤害 / 火焰伤害
- **破坏防护**：TNT 破坏 / 苦力怕破坏 / 火焰蔓延 / 末影人搬方块
- **世界环境**：难度 / 怪物生成 / 动物生成 / 天气锁定 / 时间 / 天气
- **出生点保护**：出生点范围内禁止破坏/放置（OP 除外）、禁止 PVP、禁止怪物攻击
- **命令全双语**：中英命令都支持，改动即时生效
- **设置持久化**：改动写入 settings.yml，重启自动恢复
- **零依赖**：纯 Bukkit API，不支持也无需 Vault/Economy

## 命令

| 命令 | 说明 | 权限 |
|------|------|------|
| `/设置` | 打开设置 GUI | tinysettings.admin |
| `/设置 状态` | 查看全部设置状态 | tinysettings.query |
| `/设置 难度 <和平/简单/普通/困难>` | 设置难度 | tinysettings.admin |
| `/设置 不掉落 [开/关]` | 死亡不掉落 | tinysettings.admin |
| `/设置 防爆 [开/关]` | TNT/苦力怕防爆 | tinysettings.admin |
| `/设置 pvp [开/关]` | PVP 开关 | tinysettings.admin |
| `/设置 天气 <晴/雨/雷/正常>` | 设置天气 | tinysettings.admin |
| `/设置 时间 <永昼/永夜/正常>` | 设置时间 | tinysettings.admin |
| `/设置 重置` | 恢复默认设置 | tinysettings.admin |
| `/设置 重载` | 重载配置 | tinysettings.admin |

英文别名：`/settings`

## 配置

所有设置默认值见 `config.yml`，运行时改动自动存入 `settings.yml`。

```yaml
gameplay:
  keep-inventory: true   # 死亡不掉落（默认开）
  keep-exp: false        # 死亡不掉经验
  keep-armor: false      # 死亡保留装备
  death-protect: false   # 掉落保护
combat:
  pvp: true              # PVP
  mob-attack: true       # 怪物攻击玩家
  fall-damage: true      # 摔落伤害
  fire-damage: true      # 火焰伤害
protection:
  tnt: false             # TNT 破坏地形
  creeper: false         # 苦力怕破坏地形
  fire-spread: false     # 火焰蔓延
  enderman: false        # 末影人搬方块
world:
  difficulty: NORMAL     # 难度
  mob-spawning: true     # 怪物生成
  animal-spawning: true  # 动物生成
  time: NORMAL           # 时间
  weather: NORMAL        # 天气
  weather-lock: false    # 天气锁定
spawn-protection:
  enabled: true          # 出生点保护
  radius: 16             # 保护半径
  protect-pvp: true      # 保护范围内禁PVP
  protect-build: true    # 保护范围内禁破坏
  protect-mob: true      # 保护范围内怪物不攻击
```

## 安装

1. 将 `tinysettings-1.0.0.jar` 放入服务器 `plugins/` 目录
2. 重启服务器（或 `/reload`）
3. 进服使用 `/设置` 即可

## 兼容性

- Paper / Spigot / Purpur / Leaves 1.16 ~ 26.2
- Java 17+（MC 26.2 需 Java 25+）
- 零依赖，不需要其他插件

## 作者

TinyAII · MIT License

---
---

# TinySettings

One-click server settings plugin: Keep Inventory / Difficulty / PVP / Anti-Grief / Weather / Time / Spawn Protection — all via GUI + commands, zero dependency.

## Features

- **GUI one-click toggles**: `/设置` opens a graphical menu; colored glass shows on/off; one click to change
- **Multi-option selection pages**: Difficulty (Peaceful/Easy/Normal/Hard), Time (Day/Night/Normal), Weather (Clear/Rain/Thunder/Normal) each open a dedicated selection page
- **Boolean toggles**: 2-state settings toggle directly on click
- **Gameplay**: Keep Inventory / Keep Exp / Keep Armor / Death Drop Protection
- **Combat**: PVP / Mob Attack / Fall Damage / Fire Damage
- **Anti-Grief**: TNT / Creeper / Fire Spread / Enderman Grief
- **World**: Difficulty / Mob Spawning / Animal Spawning / Weather Lock / Time / Weather
- **Spawn Protection**: no building (non-OP), no PVP, no mob attack inside radius
- **Bilingual commands**: Chinese & English, instant apply
- **Persistent**: changes saved to settings.yml, auto-restored on restart
- **Zero dependency**: pure Bukkit API, no Vault/Economy required

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/设置` | Open settings GUI | tinysettings.admin |
| `/设置 状态` | Show all settings | tinysettings.query |
| `/设置 难度 <和平/简单/普通/困难>` | Set difficulty | tinysettings.admin |
| `/设置 不掉落 [开/关]` | Keep inventory | tinysettings.admin |
| `/设置 防爆 [开/关]` | Anti-grief (TNT/Creeper) | tinysettings.admin |
| `/设置 pvp [开/关]` | Toggle PVP | tinysettings.admin |
| `/设置 天气 <晴/雨/雷/正常>` | Set weather | tinysettings.admin |
| `/设置 时间 <永昼/永夜/正常>` | Set time | tinysettings.admin |
| `/设置 重置` | Reset to defaults | tinysettings.admin |
| `/设置 重载` | Reload config | tinysettings.admin |

English alias: `/settings`

## Compatibility

- Paper / Spigot / Purpur / Leaves 1.16 ~ 26.2
- Java 17+ (MC 26.2 requires Java 25+)
- Zero dependency

## Author

TinyAII · MIT License