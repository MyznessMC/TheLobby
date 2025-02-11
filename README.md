# TheLobby - Lobby System
A lightweight lobby system plugin for **Nukkit**
Compared to LobbyNK, it is more lightweight and includes some necessary and interesting features.
Note that this plugin is designed to work in conjunction with other management plugins.
It is recommended to use the following plugins alongside:
- DlevelWorldPlus
- HubCommand
- LuckPerms (this plugin supports multiple cores, pay special attention when downloading)

---

## Plugin Commands:

- `/lobby admin add <name>` - Add a build player
- `/lobby admin remove <name>` - Remove a build player
- `/lobby build` - Toggle build mode
- `/lobby help` - View command help
- `/lobby world add <name>` - Add a lobby world
- `/lobby world remove <name>` - Remove a lobby world
- `/lobby reload` - Reload configuration
- `/lobby djump` - Enable/Disable double jump
- `/lobby fly` - Enable/Disable flight

---

## Usage Guide:

1. **When a player joins the server**, if they are in a lobby world, they will automatically receive the lobby inventory.
2. **Each item in the inventory** has a corresponding command, clicking an item will execute the respective command.
3. **Use `/lobby admin add <name>`** to add a player to the build player list, allowing them to build in the lobby.
4. **Use `/lobby admin remove <name>`** to remove a player from the build player list.
5. **Use `/lobby build`** to toggle the player's build mode.
6. **Use `/lobby world add <name>`** to add a world to the lobby world list.
7. **Use `/lobby world remove <name>`** to remove a world from the lobby world list.
8. **Use `/lobby reload`** to reload the configuration file.
9. **Use `/lobby djump`** to enable or disable the double jump feature.
10. **Use `/lobby fly`** to enable or disable the flight feature.

---

## Additional Information:

- Players in lobby worlds will automatically receive the inventory defined in the configuration file.
- Each item in the inventory has a custom name displayed in the inventory.
- Use the **LuckPerms** plugin to manage permissions.

---

> **Permission Information:**
> - `lobby.command` - Permission to use the `/lobby` command (do not give to regular players)
> - `lobby.build` - Build mode permission
> - `lobby.fly` - Permission to use the `/lobby fly` command
> - `lobby.djump` - Permission to use the `/lobby djump` command
