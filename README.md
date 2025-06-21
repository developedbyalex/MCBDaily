# MCBDaily - Minecraft Daily Rewards Plugin

A simple yet powerful daily rewards plugin for Minecraft servers with GUI interface and PlaceholderAPI integration.

## Features

- 🎁 **Daily Reward System** - Players can claim rewards once every 24 hours
- 🖼️ **Interactive GUI** - Beautiful 27-slot GUI with lime/red concrete indicators
- ⏰ **PlaceholderAPI Integration** - Countdown placeholders for other plugins
- ⚙️ **Configurable Rewards** - Fully customizable reward commands in config.yml
- 🔒 **Permission System** - Fine-grained permission controls
- 👑 **Admin Commands** - Reload config and reset player cooldowns
- 💾 **Data Persistence** - Player data saved across server restarts

## Installation

1. Download the latest JAR file from [Releases](../../releases)
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure rewards in `plugins/MCBDaily/config.yml`
5. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for placeholder support

## Commands

| Command | Description | Permission | Aliases |
|---------|-------------|------------|---------|
| `/daily` | Open daily rewards GUI | `mcbdaily.claim` | `/rewards`, `/dailyreward` |
| `/daily reload` | Reload configuration | `mcbdaily.admin` | - |
| `/daily reset <player>` | Reset player's cooldown | `mcbdaily.admin` | - |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `mcbdaily.claim` | Claim daily rewards | `true` |
| `mcbdaily.bypass` | Bypass cooldown | `op` |
| `mcbdaily.admin` | Admin commands | `op` |

## Configuration

The plugin generates a `config.yml` file with the following structure:

```yaml
# MCBDaily Plugin Configuration
# ===========================

# Cooldown time in hours (default: 24 hours)
cooldown-hours: 24

# List of commands to execute when a player claims their daily reward
# Use %player% as a placeholder for the player's name
rewards:
  - "give %player% diamond 1"
  - "give %player% emerald 5"
  - "give %player% gold_ingot 10"
  - "eco give %player% 1000"
  - "msg %player% &aYou have received your daily reward!"

# Debug mode (logs additional information)
debug: false
```

## PlaceholderAPI Support

If PlaceholderAPI is installed, the following placeholders are available:

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%mcbdaily_countdown%` | Time until next claim | `Available now!` or `5h 23m 12s` |
| `%mcbdaily_can_claim%` | Whether player can claim | `true` or `false` |
| `%mcbdaily_time_left_millis%` | Time left in milliseconds | `19323000` |

## Building from Source

1. Clone this repository
2. Run `mvn clean package`
3. The JAR file will be generated in the `target/` directory

## Requirements

- Java 17 or higher
- Spigot/Paper 1.21+ (tested on 1.21.4)
- (Optional) PlaceholderAPI for placeholder support

## Support

If you encounter any issues or have suggestions, please open an issue on the [GitHub repository](../../issues).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details. 