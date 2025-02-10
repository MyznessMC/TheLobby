[HEADING=1]TheLobby - Lobby System[/HEADING]
Lobby system for [B]Nukkit[/B]
This plugin provides a lobby system for Nukkit servers, allowing players to join a lobby world with predefined items and commands.
[HR][/HR]
[HEADING=2]PLUGIN COMMAND:[/HEADING]
/lobby admin add <name> - Add a player to the builder list
/lobby admin remove <name> - Remove a player from the builder list
/lobby build - Toggle build mode
/lobby help - View instruction help
/lobby world add <name> - Add a world to the lobby worlds list
/lobby world remove <name> - Remove a world from the lobby worlds list
/lobby reload - Reload the configuration file
[HR][/HR]
[HEADING=2]TUTORIAL:[/HEADING]
1. When a player joins the server and is in a lobby world, they will automatically receive the lobby inventory.
2. Each item in the inventory has a corresponding command that will be executed when the item is clicked.
3. Use /lobby admin add <name> to add a player to the builder list, allowing them to build in the lobby.
4. Use /lobby admin remove <name> to remove a player from the builder list.
5. Use /lobby build to toggle the player's build mode.
6. Use /lobby world add <name> to add a world to the lobby worlds list.
7. Use /lobby world remove <name> to remove a world from the lobby worlds list.
8. Use /lobby reload to reload the configuration file.
[HR][/HR]
[HEADING=2]ADDITIONAL INFORMATION:[/HEADING]
[LIST]
[*]Players in lobby worlds will automatically receive the inventory defined in the configuration file.
[*]Each item in the inventory has a custom name displayed in the inventory.
[*]Use the [B]LuckPerms[/B] plugin to manage permissions.
[/LIST]
[QUOTE]
lobby.command - Permission to use /lobby commands
lobby.build - Permission for build mode
[/QUOTE]
