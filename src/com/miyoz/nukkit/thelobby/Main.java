package com.miyoz.nukkit.thelobby;

import cn.nukkit.level.Level;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerToggleFlightEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.utils.Config;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Sound;
import cn.nukkit.event.inventory.InventoryTransactionEvent;


import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Main extends PluginBase implements Listener {

    private Config config;
    private Config itemConfig;
    private Map<Integer, String> itemCommands = new HashMap<>();
    private Map<Player, PermissionAttachment> permissionAttachments = new HashMap<>();
    private List<String> toggledDoubleJump = new ArrayList<>();
    private List<String> toggledFlight = new ArrayList<>();

    @Override
    public void onEnable() {
        // 加载配置文件
        this.saveDefaultConfig();
        config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);
        // 保存默认的 item.yml 文件
        saveResource("item.yml", false);
        itemConfig = new Config(this.getDataFolder() + "/item.yml", Config.YAML);

        // 加载物品命令
        loadItemCommands();

        // 注册事件监听器
        this.getServer().getPluginManager().registerEvents(this, this);

        // 注册命令
        this.getServer().getCommandMap().register("lobby", new LobbyCommand(this));
    }

    @EventHandler
    public void onInventoryTransaction(InventoryTransactionEvent event) {
        Player player = event.getTransaction().getSource();
        if (config.getStringList("lobby_worlds").contains(player.getLevel().getName())) {
            event.setCancelled(true);
        }
    }
    private void loadItemCommands() {
        List<Map<String, Object>> items = (List<Map<String, Object>>) itemConfig.getList("item");
        for (Map<String, Object> itemMap : items) {
            String name = (String) itemMap.get("name");
            String command = (String) itemMap.get("command");
            String id = (String) itemMap.get("id");
            int position = (int) itemMap.get("position");

            // 解析物品ID
            String[] idParts = id.split(":");
            int itemId = Integer.parseInt(idParts[0]);
            int meta = Integer.parseInt(idParts[1]);
            int count = Integer.parseInt(idParts[2]);

            // 将物品位置与命令关联
            itemCommands.put(position, command);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // 检查玩家是否在大厅世界
        if (config.getStringList("lobby_worlds").contains(player.getLevel().getName())) {
            // 设置玩家属性
            player.setAllowFlight(config.getBoolean("can_fly"));
            player.setHealth(config.getInt("life"));
            player.setMaxHealth(config.getInt("life"));
            player.setExperience(config.getInt("exp"));

            // 设置物品栏
            setLobbyInventory(player);

            // 设置建筑权限
            if (config.getStringList("build_player").contains(player.getName())) {
                PermissionAttachment attachment = player.addAttachment(this);
                attachment.setPermission("lobby.build", true);
                permissionAttachments.put(player, attachment);
            } else {
                PermissionAttachment attachment = player.addAttachment(this);
                attachment.setPermission("lobby.build", false);
                permissionAttachments.put(player, attachment);
            }
        }
    }

    private void setLobbyInventory(Player player) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) itemConfig.getList("item");
        for (Map<String, Object> itemMap : items) {
            String id = (String) itemMap.get("id");
            int position = (int) itemMap.get("position");
            String name = (String) itemMap.get("name");

            // 解析物品ID
            String[] idParts = id.split(":");
            int itemId = Integer.parseInt(idParts[0]);
            int meta = Integer.parseInt(idParts[1]);
            int count = Integer.parseInt(idParts[2]);

            // 创建物品并设置到物品栏
            Item item = Item.get(itemId, meta, count);
            item.setCustomName(name);
            player.getInventory().setItem(position, item);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();

        // 检查玩家是否在大厅世界
        if (config.getStringList("lobby_worlds").contains(player.getLevel().getName())) {
            // 阻止物品移动和丢弃
            event.setCancelled(true);
            // 获取物品位置
            int position = player.getInventory().getHeldItemIndex();

            // 检查是否有对应的命令
            if (itemCommands.containsKey(position)) {
                String command = itemCommands.get(position);
                player.getServer().dispatchCommand(player, command);
            }
        }
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.isCreative())
            return;

        if (toggledDoubleJump.contains(player.getName())) {
            player.sendMessage("§c你不能同时启用双击跳跃和飞行.");
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        player.setAllowFlight(false);
        if (toggledFlight.contains(player.getName())) {
            player.setMotion(player.getLocation().getDirectionVector().multiply(2.5D).add(0.0D, 1.5D, 0.0D));
            Level level = player.getLevel();
            level.addSound(player.getLocation(), Sound.MOB_ENDERDRAGON_FLAP);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isCreative())
            return;

        if (toggledDoubleJump.contains(player.getName())
                && player.isOnGround()
                && !player.getAllowFlight()) {
            player.setAllowFlight(true);
        }
    }

    public Config getConfig() {
        return config;
    }

    public void saveConfig() {
        config.save();
    }

    public class LobbyCommand extends Command {

        private Main plugin;

        public LobbyCommand(Main plugin) {
            super("lobby", "大厅系统指令", "/lobby help");
            this.plugin = plugin;
            this.setPermission("lobby.command");
            this.commandParameters.clear();
            this.commandParameters.put("default", new CommandParameter[]{
                    new CommandParameter("subcommand", CommandParameter.ARG_TYPE_STRING, false),
                    new CommandParameter("args", CommandParameter.ARG_TYPE_STRING, true)
            });

            // 定义新的权限
            this.setPermission("lobby.fly");
            this.setPermission("lobby.djump");
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (!this.testPermission(sender)) {
                return false;
            }

            if (args.length == 0) {
                sender.sendMessage("用法: /lobby help");
                return false;
            }

            String subcommand = args[0].toLowerCase();
            switch (subcommand) {
                case "admin":
                    if (args.length < 3) {
                        sender.sendMessage("用法: /lobby admin <add|remove> <name>");
                        return false;
                    }
                    String adminAction = args[1].toLowerCase();
                    String playerName = args[2];
                    List<String> buildPlayers = plugin.getConfig().getStringList("build_player");
                    switch (adminAction) {
                        case "add":
                            if (buildPlayers.contains(playerName)) {
                                sender.sendMessage(playerName + " 已经是建筑玩家了.");
                            } else {
                                buildPlayers.add(playerName);
                                plugin.getConfig().set("build_player", buildPlayers);
                                plugin.saveConfig();
                                sender.sendMessage(playerName + " 已添加为建筑玩家.");
                            }
                            break;
                        case "remove":
                            if (buildPlayers.contains(playerName)) {
                                buildPlayers.remove(playerName);
                                plugin.getConfig().set("build_player", buildPlayers);
                                plugin.saveConfig();
                                sender.sendMessage(playerName + " 已移除建筑玩家.");
                            } else {
                                sender.sendMessage(playerName + " 不是建筑玩家.");
                            }
                            break;
                        default:
                            sender.sendMessage("用法: /lobby admin <add|remove> <name>");
                            return false;
                    }
                    break;
                case "build":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("该指令只能在游戏内使用.");
                        return false;
                    }
                    Player player = (Player) sender;
                    PermissionAttachment attachment = permissionAttachments.get(player);
                    if (attachment == null) {
                        attachment = player.addAttachment((Plugin) plugin);
                        permissionAttachments.put(player, attachment);
                    }
                    boolean hasBuildPermission = player.hasPermission("lobby.build");
                    attachment.setPermission("lobby.build", !hasBuildPermission);
                    player.sendMessage("建筑模式 " + (!hasBuildPermission ? "已启用." : "已禁用."));
                    break;
                case "help":
                    sender.sendMessage("/lobby admin add <name> - 添加建筑玩家");
                    sender.sendMessage("/lobby admin remove <name> - 移除建筑玩家");
                    sender.sendMessage("/lobby build - 建筑模式");
                    sender.sendMessage("/lobby help - 展示帮助菜单");
                    sender.sendMessage("/lobby world add <name> - 添加大厅世界");
                    sender.sendMessage("/lobby world remove <name> - 移除大厅世界");
                    sender.sendMessage("/lobby reload - 重载配置");
                    sender.sendMessage("/lobby djump - 启用/禁用双击跳跃");
                    sender.sendMessage("/lobby fly - 启用/禁用飞行");
                    break;
                case "world":
                    if (args.length < 3) {
                        sender.sendMessage("用法: /lobby world <add|remove> <name>");
                        return false;
                    }
                    String worldAction = args[1].toLowerCase();
                    String worldName = args[2];
                    List<String> lobbyWorlds = plugin.getConfig().getStringList("lobby_worlds");
                    switch (worldAction) {
                        case "add":
                            if (lobbyWorlds.contains(worldName)) {
                                sender.sendMessage(worldName + " 已经是大厅世界了.");
                            } else {
                                lobbyWorlds.add(worldName);
                                plugin.getConfig().set("lobby_worlds", lobbyWorlds);
                                plugin.saveConfig();
                                sender.sendMessage(worldName + " 已添加为大厅世界.");
                            }
                            break;
                        case "remove":
                            if (lobbyWorlds.contains(worldName)) {
                                lobbyWorlds.remove(worldName);
                                plugin.getConfig().set("lobby_worlds", lobbyWorlds);
                                plugin.saveConfig();
                                sender.sendMessage(worldName + " 已移出大厅世界.");
                            } else {
                                sender.sendMessage(worldName + " 不是一个大厅世界.");
                            }
                            break;
                        default:
                            sender.sendMessage("用法: /lobby world <add|remove> <name>");
                            return false;
                    }
                    break;
                case "reload":
                    plugin.reloadConfig();
                    sender.sendMessage("已重新加载配置.");
                    break;
                case "djump":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("该指令只能在游戏内使用.");
                        return false;
                    }
                    Player djumpPlayer = (Player) sender;
                    if (!djumpPlayer.hasPermission("lobby.djump")) {
                        djumpPlayer.sendMessage("§c你没有权限使用此命令.");
                        return false;
                    }
                    if (toggledDoubleJump.contains(djumpPlayer.getName())) {
                        djumpPlayer.sendMessage("§c双击跳跃已禁用");
                        toggledDoubleJump.remove(djumpPlayer.getName());
                    } else {
                        if (toggledFlight.contains(djumpPlayer.getName())) {
                            djumpPlayer.sendMessage("§c你不能同时启用双击跳跃和飞行.");
                            return false;
                        }
                        djumpPlayer.sendMessage("§a双击跳跃已启用");
                        toggledDoubleJump.add(djumpPlayer.getName());
                    }
                    break;
                case "fly":
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("该指令只能在游戏内使用.");
                        return false;
                    }
                    Player flyPlayer = (Player) sender;
                    if (!flyPlayer.hasPermission("lobby.fly")) {
                        flyPlayer.sendMessage("§c你没有权限使用此命令.");
                        return false;
                    }
                    if (toggledFlight.contains(flyPlayer.getName())) {
                        flyPlayer.sendMessage("§c飞行已禁用");
                        toggledFlight.remove(flyPlayer.getName());
                        flyPlayer.setAllowFlight(false);
                    } else {
                        if (toggledDoubleJump.contains(flyPlayer.getName())) {
                            flyPlayer.sendMessage("§c你不能同时启用双击跳跃和飞行.");
                            return false;
                        }
                        flyPlayer.sendMessage("§a飞行已启用");
                        toggledFlight.add(flyPlayer.getName());
                        flyPlayer.setAllowFlight(true);
                    }
                    break;
                default:
                    sender.sendMessage("未知指令. 使用 /lobby help 查看指令列表.");
                    return false;
            }
            return true;
        }
    }
}