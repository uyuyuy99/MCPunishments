# Punishments
Punishments is a plugin for spigot 1.21.6 that allows moderators to ban, mute, and kick players.

## Commands
- `/ban <player> [reason]` Permanently ban a player. Requires permission `punishments.admin.ban`
- `/banip <ip> [reason]` Permanently ban an IP. Requires permission `punishments.admin.banip`
- `/tempban <player> <time> [reason]` Temporarily ban a player. Requires permission `punishments.admin.tempban`
- `/unban <player>` Pardon a banned player. Requires permission `punishments.admin.unban`
- `/unbanip <ip>` Pardon a banned IP. Requires permission `punishments.admin.unbanip`
- `/mute <player> [reason]` Permanently mute a player. Requires permission `punishments.admin.mute`
- `/tempmute <player> <time> [reason]` Temporarily mute a player. Requires permission `punishments.admin.tempmute`
- `/unmute <player>` Un-mute a player. Requires permission `punishments.admin.unmute`
- `/kick <player> [reason]` Kick a player from the server. Requires permission `punishments.admin.kick`
- `/history <player>` View a player's punishment history. Requires permission `punishments.admin.history`

For the `<time>` arguments for tempban and tempmute, you should use a number followed by one of the following characters: `s m h d w`. For example, `10h` means 10 hours, `10d` means 10 days, etc.
