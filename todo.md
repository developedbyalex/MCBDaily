# MCBDaily Plugin – ToDo

## 🔧 Project Setup
- [ ] Create a new maven-based Minecraft plugin project named `MCBDaily`
- [ ] Set up `plugin.yml` with:
  - [ ] Main class path
  - [ ] Name: `MCBDaily`
  - [ ] Version
  - [ ] Commands:
    - [ ] `/daily`
    - [ ] Aliases: `/rewards`, `/dailyreward`

## ⚙️ Command System
- [ ] Register `/daily` command with alias support
- [ ] On command execution:
  - [ ] Open a 27-slot GUI
  - [ ] Check if user is eligible to claim daily reward
  - [ ] Show **lime concrete** if eligible
  - [ ] Show **red concrete** if not eligible

## 🎁 Daily Reward System
- [ ] Create `config.yml` with reward section:
  - [ ] Example:
    ```yaml
    rewards:
      - 'command1'
      - 'command2'
    ```
- [ ] On eligible claim:
  - [ ] Execute commands from the config
  - [ ] Track last claim time per player
- [ ] Prevent reclaims within 24 hours

## 🧠 Player Data Management
- [ ] Store last claim timestamp per player (by UUID)
- [ ] Persist data on plugin disable
- [ ] Load data on plugin enable or player join

## 🖼️ GUI Design
- [ ] Create a 27-slot inventory GUI
- [ ] Slot 13 contains:
  - [ ] Lime concrete if player can claim
  - [ ] Red concrete if player is on cooldown
- [ ] Add item lore:
  - [ ] If claimable: "Click to claim your daily reward!"
  - [ ] If not: "You can claim again in: `%mcbdaily_countdown%`"

## ⏳ PlaceholderAPI Integration
- [ ] Hook into PlaceholderAPI
- [ ] Register placeholder: `%mcbdaily_countdown%`
  - [ ] Show time remaining in format: `dd hh mm ss`
  - [ ] Show `Available now!` if eligible

## ✅ Misc Features
- [ ] Reload config without restarting server
- [ ] Optional permissions:
  - [ ] `mcbdaily.claim`
  - [ ] `mcbdaily.bypass` (for bypassing cooldown)
- [ ] Optional debug mode for logging

---

## 💡 To add:
- [ ] Configurable GUI item material and slot
- [ ] Configurable cooldown duration
- [ ] Sound and/or particle effects on successful claim
- [ ] Admin command to reset a user's cooldown
- [ ] Log all claims to console or file
- [ ] Use MiniMessage/Adventure API for improved text formatting (optional)
