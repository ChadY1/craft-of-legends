# Craft of Legends

Craft of Legends brings familiar multi-lane, champion-based gameplay to Minecraft servers. Configure champions, abilities, cooldown pacing, party rules, and database backends to fit your community for either single-server Paper setups or networked Bungeecord deployments.

## Requirements
- Java 17+ runtime (matching your target Paper/Bungeecord version)
- Paper 1.20+ or a Bungeecord/Waterfall proxy if running a network
- A database provider of your choice (SQLite bundled; MySQL/MariaDB/Postgres supported)

## Quick start (Paper)
1. Place the plugin JAR in your Paper server's `plugins/` folder.
2. Start the server once to generate `plugins/CraftOfLegends/config.yml`.
3. Copy the sample data files from `src/main/resources/samples/` into `plugins/CraftOfLegends/` to bootstrap champions, abilities, items, maps, monsters, quests, and messages.
4. Edit `config.yml` to pick your database provider, tune parties/cooldowns, enable minions/turrets/quests, and point `champions.data_file` to your customized `champions.yml`.
5. Restart the server after saving changes (or use your admin tooling to reload plugins) so the new settings take effect.

## Quick start (Bungeecord/Waterfall)
1. Place the plugin JAR in both the Bungeecord `plugins/` folder and each backend Paper server that should share data.
2. Configure `config.yml` on every server to use the same SQL provider and credentials.
3. Set `server.mode` to `bungeecord` and enable the `bungeecord` block for messaging and cross-server party sync.
4. Restart the proxy first, then restart backend servers so they all register channels and database migrations cleanly.

## Configuration guide
The primary configuration lives at `plugins/CraftOfLegends/config.yml`. Key sections include:

- **server**: toggle `mode` between `paper` and `bungeecord`, set locale, and enable debugging when troubleshooting.
- **database**: choose `provider` (`sqlite`, `mysql`, `mariadb`, `postgres`, or flat-file `json`/`yaml`) and fill in credentials. Use pooled SQL backends when multiple servers share progression.
- **parties**: caps party sizes, friendly fire, invite timeouts, privacy defaults, and cross-server sync options. Includes optional friends list sharing via `friends` sub-block and `samples/social.yml`.
- **champions**: points to a champion data file (`samples/champions.yml` by default), starter unlocks, respawn timers, scaling per level/star, and cosmetic toggles. Populate champions using the official wiki as references.
- **map/inhibitors/turrets/nexus**: configure health, damage, and respawn windows for objectives; reference lane structures in `samples/map.yml`.
- **minions**: spawn timing, scaling, and behaviors for lane minions that siege turrets/inhibitors; shares data with `samples/map.yml`.
- **monsters**: epic and camp monsters with respawn timers and buffs; data in `samples/monsters.yml`.
- **items/shop**: tune item stats, recipes, currencies, and shop menus (`samples/items.yml`, `samples/shop.yml`) and `/shop` command aliases.
- **abilities**: defines reusable ability presets, energy types, and global cooldown rules that champions reference or override in `abilities.yml`.
- **cooldowns**: controls recall/teleport timers, surrender and ranked retry delays, and scaling tables based on champion level or party size.
- **quests**: daily/weekly/seasonal quest pools in `samples/quests.yml` with reset cadence and completion messaging.
- **messages**: colorized templates for objectives, parties, quests, and announcements (`samples/messages.yml`).
- **controls**: optional keyboard hint overlays stored in `samples/controls.yml`.
- **bungeecord**: toggles proxy-wide syncing for cooldown progress and party state and sets the messaging channel name.

Each block in `config.yml` is annotated with comments describing expected values and defaults.

## Customizing champions and mechanics
- Use `src/main/resources/samples/champions.yml` as a template for defining stats, growth, abilities, and loadouts per champion. Copy it into `plugins/CraftOfLegends/champions.yml` and adjust values.
- Add or tune ability presets either directly in `config.yml` under `abilities.presets` or via `src/main/resources/samples/abilities.yml` if you prefer a dedicated file. Pair champions to ability IDs that match the League wiki to stay authentic.
- Keep ability IDs consistent between `champions.yml` and `abilities.presets` (or `abilities.yml`) so cooldowns, costs, and effects resolve correctly.
- Leverage `cooldowns.scaling` and `minions` pacing to emulate official lane timings, while `map` and `monsters` mirror inhibitors, nexus, Baron, and Dragon behavior.

## Usage examples
- Switch to MySQL with pooling:
  ```yaml
  database:
    provider: mysql
    mysql:
      host: db.internal
      port: 3306
      database: craft_of_legends
      user: craft_admin
      password: strong_password_here
      use_ssl: true
  ```
- Enable Bungeecord sync and align party settings:
  ```yaml
  server:
    mode: bungeecord
  bungeecord:
    enabled: true
    messaging_channel: craft-of-legends
    sync_cooldown_progress: true
    sync_party_state: true
  parties:
    allow_cross_server_sync: true
  ```
- Add a new champion entry using existing presets:
  ```yaml
  champions:
    katarina:
      display_name: "Katarina"
      title: "The Sinister Blade"
      role: assassin
      resource: energy
      difficulty: hard
      base_stats:
        health: 672
        health_regen: 7.5
        armor: 28
        magic_resist: 32
        move_speed: 340
      growth:
        health: 94
        health_regen: 0.7
        armor: 3.2
        magic_resist: 1.3
        move_speed: 1
      abilities:
        passive:
          name: Voracity
          tooltip: Reset cooldowns on takedowns.
        q: knifethrow_q
        w: spin_w
        e: shunpo_e
      r: deathlotus_r
  ```

## Commands and controls
- Admin utilities: `/colreload`, `/colgivechamp <player> <champion>`, `/colstart`, `/colend` (aliases configurable under `admin.command_aliases`).
- Player utilities: `/shop`, `/party`, `/friend`, `/quest` (behavior tuned via `shop`, `parties`, and `quests` blocks).
- Suggested keyboard layout mirrors League defaults (QWER abilities, 1-6 items, B recall, P shop, Tab scoreboard) and is configurable under `controls` with prompts sourced from `samples/controls.yml`.

## Troubleshooting tips
- If changes are not loading, confirm YAML syntax and ensure the referenced data files exist at the configured paths.
- For SQL providers, verify network connectivity and credentials; pool sizes may need to increase for larger networks.
- Enable `server.debug` to log verbose champion/ability loading details when diagnosing configuration issues.
