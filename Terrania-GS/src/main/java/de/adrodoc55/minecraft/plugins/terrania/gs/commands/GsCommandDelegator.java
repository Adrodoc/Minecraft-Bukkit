package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.command.CommandHandler;
import de.adrodoc55.minecraft.plugins.common.utils.CommandUtils;

public class GsCommandDelegator implements CommandHandler {

  // private static final String CREATE = "create";
  // private static final String DELETE = "delete";
  // private static final String SETPRICE = "setprice";
  // private static final String INFO = "info";
  // private static final String LIST = "list";
  // private static final String UPDATE = "update";
  // private static final String SAVE = "save";
  // private static final String[] SUB_COMMANDS = { CREATE, DELETE, SETPRICE,
  // INFO, LIST, UPDATE, SAVE };

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    List<GsCommand> commands = GsCommand.getCommands();
    List<String> commandNames = CollectionUtils.collect(commands, new Closure<GsCommand, String>() {
      @Override
      public String call(GsCommand gsCommand) {
        return gsCommand.getName();
      }
    });
    String usage = ChatColor.RED + "/" + GsCommand.COMMAND + " <"
        + CommonUtils.join(" | ", commandNames) + ">";
    command.setUsage(usage);
    if (args.length < 1) {
      return false;
    }
    GsCommand subCommand = GsCommand.getInstance(args[0]);
    if (subCommand == null) {
      return false;
    }
    return subCommand.onCommand(sender, command, label, args);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    List<GsCommand> subCommands = GsCommand.getCommands();
    List<String> subCommandNames =
        CollectionUtils.collect(subCommands, new Closure<GsCommand, String>() {
          @Override
          public String call(GsCommand c) {
            return c.getName();
          }
        });
    if (args.length == 0) {
      return new ArrayList<String>(0);
    } else if (args.length == 1) {
      String toComplete = args[0];
      return CommandUtils.elementsStartsWith(subCommandNames, toComplete);
    } else {
      GsCommand gsCommand = GsCommand.getInstance(args[0]);
      if (gsCommand == null) {
        return new ArrayList<String>(0);
      }
      return gsCommand.onTabComplete(sender, command, alias, args);
    }
  }

  // private static final String COMMAND = "gs";

  // @Override
  // public boolean onCommand(CommandSender sender, Command command,
  // String label, String[] args) {
  // if (!COMMAND.equals(command.getName())) {
  // return false;
  // }
  // String usage = ChatColor.RED
  // + "/"
  // + COMMAND
  // + " <"
  // + CommonUtils.join(" | ", CREATE, DELETE, SETPRICE, INFO, LIST,
  // SAVE) + ">";
  // command.setUsage(usage);
  // if (args.length == 0) {
  // return false;
  // }
  // switch (args[0]) {
  // case CREATE:
  // return create(sender, command, label, args);
  // case DELETE:
  // return delete(sender, command, label, args);
  // case SETPRICE:
  // return setprice(sender, command, label, args);
  // case INFO:
  // return info(sender, command, label, args);
  // case LIST:
  // return list(sender, command, label, args);
  // case UPDATE:
  // return update(sender, command, label, args);
  // case SAVE:
  // return save(sender, command, label, args);
  // default:
  // return false;
  // }
  //
  // }

  // private boolean create(CommandSender sender, Command command, String
  // label,
  // String[] args) {
  // String usage = ChatColor.RED
  // + CommonUtils.join(" ", "/" + COMMAND, CREATE, "<region>");
  // command.setUsage(usage);
  // if (args.length != 2) {
  // return false;
  // }
  // if (!(sender instanceof Player)) {
  // MinecraftUtils.sendMessage(sender,
  // "Dieser Befehl kann nur von Spielern ausgeführt werden.");
  // command.setUsage("");
  // return false;
  // }
  // Player player = (Player) sender;
  // String regionId = args[1];
  // WorldGuardPlugin worldGuardPlugin = TerraniaGSPlugin
  // .getPlugin(WorldGuardPlugin.class);
  // World regionWorld = player.getWorld();
  // ProtectedRegion region = worldGuardPlugin.getRegionManager(regionWorld)
  // .getRegion(regionId);
  // if (region == null) {
  // String message = String.format(
  // "No region could be found with the name of '%s'", regionId);
  // MinecraftUtils.sendMessage(sender, message);
  // return false;
  // }

  // Selection selection = TerraniaGSPlugin.getPlugin(WorldEditPlugin.class)
  // .getSelection(player);
  // if (selection == null) {
  // MinecraftUtils
  // .sendMessage(player,
  // "Du musst eine World Edit Selection haben um diesen Befehl aus zu führen.");
  // command.setUsage("");
  // return false;
  // }
  // String missingSignMessage =
  // "Du musst ein Schild ausgewählt habem um diesen Befehl auszuführen.";
  // if (selection.getArea() != 1) {
  // MinecraftUtils.sendMessage(player, missingSignMessage);
  // }
  // World signWorld = selection.getWorld();
  // Location signLocation = selection.getMaximumPoint();
  // Block signBlock = signWorld.getBlockAt(signLocation);
  // BlockState state = signBlock.getState();
  // if (!(state instanceof Sign)) {
  // MinecraftUtils.sendMessage(player, missingSignMessage);
  // }
  // // Block targetBlock = player.getTargetBlock(null, 10);
  // // BlockState state = targetBlock.getState();
  // // if (!(state instanceof Sign)) {
  // // MinecraftUtils
  // // .sendMessage(player,
  // // "Du musst ein Schild ansehen um diesen Befehl auszuführen.");
  // // return false;
  // // }
  // Sign sign = (Sign) state;
  // if (!regionWorld.equals(signWorld)) {
  // String message = ChatColor.RED
  // + "Die Region und das Schild müssen sich in der selben Welt befinden!";
  // MinecraftUtils.sendMessage(player, message);
  // command.setUsage("");
  // return false;
  // }
  // Grundstueck grundstueck = new Grundstueck(regionWorld, region.getId(),
  // sign);
  // try {
  // boolean didNotYetExist = GSManager.add(grundstueck);
  // if (!didNotYetExist) {
  // String message = ChatColor.RED
  // + String.format(
  // "Das Grundstück %s existiert bereits in der Welt %s",
  // grundstueck.getName(), grundstueck.getWorld()
  // .getName());
  // MinecraftUtils.sendMessage(player, message);
  // command.setUsage("");
  // return false;
  // }
  // } catch (ValidationException ex) {
  // String message = ChatColor.RED + ex.getMessage();
  // logger().warn(message);
  // ex.printStackTrace();
  // MinecraftUtils.sendMessage(sender, message);
  // command.setUsage("");
  // return false;
  // }
  // String message = ChatColor.YELLOW
  // + String.format(
  // "Das Grundstück %s wurde erfolgreich erstellt.",
  // grundstueck.getName());
  // MinecraftUtils.sendMessage(player, message);
  // return true;
  // }

  // private boolean delete(CommandSender sender, Command command, String
  // label,
  // String[] args) {
  // String usage = ChatColor.RED
  // + CommonUtils.join(" ", "/" + COMMAND, DELETE, "<grundstück>");
  // command.setUsage(usage);
  // if (args.length != 2) {
  // return false;
  // }
  // String gsName = args[1];
  // if (!(sender instanceof Player)) {
  // MinecraftUtils.sendMessage(sender,
  // "Dieser Befehl kann nur von Spielern ausgeführt werden.");
  // command.setUsage("");
  // return false;
  // }
  // Player player = (Player) sender;
  // World world = player.getWorld();

  // Grundstueck gs = GSManager.getGSManager(world).find(gsName);
  // if (gs == null) {
  // String message = ChatColor.RED + "Das Grundstück " + gsName
  // + " konnte nicht gefunden werden";
  // MinecraftUtils.sendMessage(sender, message);
  // command.setUsage("");
  // return false;
  // }
  // GSManager.remove(gs);
  // String message = ChatColor.YELLOW
  // + String.format(
  // "Das Grundstück %s wurde erfolgreich entfernt.",
  // gs.getName());
  // MinecraftUtils.sendMessage(player, message);
  // return true;
  // }

  // private boolean setprice(CommandSender sender, Command command,
  // String label, String[] args) {
  // String usage = ChatColor.RED
  // + CommonUtils.join(" ", "/" + COMMAND, SETPRICE,
  // "<grundstück>", "<preis>");
  // command.setUsage(usage);
  // if (args.length != 3) {
  // return false;
  // }
  // String gsName = args[1];
  // String price = args[2];
  // if (!(sender instanceof Player)) {
  // MinecraftUtils.sendMessage(sender,
  // "Dieser Befehl kann nur von Spielern ausgeführt werden.");
  // command.setUsage("");
  // return false;
  // }
  // Player player = (Player) sender;
  // World world = player.getWorld();

  // Grundstueck gs = GSManager.getGSManager(world).find(gsName);
  // if (gs == null) {
  // String message = ChatColor.RED + "Das Grundstück " + gsName
  // + " konnte nicht gefunden werden";
  // MinecraftUtils.sendMessage(sender, message);
  // return false;
  // }
  // try {
  // gs.setPrice(Double.parseDouble(price));
  // gs.updateSignConent();
  // } catch (NumberFormatException ex) {
  // String message = ChatColor.RED + price + " ist keine gültige Zahl";
  // MinecraftUtils.sendMessage(sender, message);
  // return false;
  // }
  // return true;
  // }

  // private boolean info(CommandSender sender, Command command, String label,
  // String[] args) {
  // String usage = ChatColor.RED
  // + CommonUtils.join(" ", "/" + COMMAND, INFO, "<grundstück>");
  // command.setUsage(usage);
  // if (args.length != 2) {
  // return false;
  // }
  // String gsName = args[1];
  // if (!(sender instanceof Player)) {
  // MinecraftUtils.sendMessage(sender,
  // "Dieser Befehl kann nur von Spielern ausgeführt werden.");
  // command.setUsage("");
  // return false;
  // }
  // Player player = (Player) sender;
  // World world = player.getWorld();
  // Grundstueck gs = GSManager.getGSManager(world).find(gsName);
  // if (gs == null) {
  // String message = ChatColor.RED + "Das Grundstück " + gsName
  // + " konnte nicht gefunden werden";
  // MinecraftUtils.sendMessage(sender, message);
  // command.setUsage("");
  // return false;
  // }
  // StringBuilder sb = new StringBuilder(ChatColor.YELLOW.toString());
  // sb.append("Informationen zum Grundstück " + gsName + "\n");
  // OfflinePlayer owner = gs.getOwner();
  // if (owner != null) {
  // sb.append(" - Besitzer: " + owner.getName() + "\n");
  // }
  // sb.append(" - Preis: " + gs.getPrice() + "\n");
  // sb.append(" - verbleibende Tage: " + gs.getDaysLeft() + "\n");
  // sb.append(" - Welt: " + gs.getWorld().getName() + "\n");
  // Sign sign = gs.getSign();
  // sb.append(" - Schild: x=" + sign.getX() + " y=" + sign.getY() + " z="
  // + sign.getZ() + "\n");
  // sb.append("\n");
  // String message = sb.toString();
  // MinecraftUtils.sendMessage(sender, message);
  // return true;
  // }

  // private boolean list(CommandSender sender, Command command, String label,
  // String[] args) {
  // String usage = ChatColor.RED
  // + CommonUtils.join(" ", "/" + COMMAND, LIST);
  // command.setUsage(usage);
  // if (args.length != 1) {
  // return false;
  // }
  // StringBuilder sb = new StringBuilder(ChatColor.YELLOW.toString());
  // sb.append("Grundstücke:\n");
  // Collection<GSManager> gsManagers = GSManager.getActiveInstances();
  // boolean empty = true;
  // for (GSManager gsm : gsManagers) {
  // Set<Grundstueck> grundstuecke = gsm.getGrundstuecke();
  // if (!grundstuecke.isEmpty()) {
  // empty = false;
  // sb.append(" Welt " + gsm.getWorld().getName() + ":\n");
  // List<String> names = CollectionUtils.collect(grundstuecke,
  // new Closure<Grundstueck, String>() {
  // @Override
  // public String call(Grundstueck parameter) {
  // return " - " + parameter.getName();
  // }
  // });
  // sb.append(CommonUtils.join("\n", names));
  // }
  // sb.append("\n");
  // }
  // if (empty) {
  // sb.append(ChatColor.GOLD + " keine Grundstücke vorhanden\n"
  // + ChatColor.YELLOW);
  // }
  // String message = sb.toString();
  // MinecraftUtils.sendMessage(sender, message);
  // return true;
  // }
  //
  // private boolean update(CommandSender sender, Command command, String
  // label,
  // String[] args) {
  // String usage = ChatColor.RED
  // + CommonUtils.join(" ", "/" + COMMAND, UPDATE);
  // command.setUsage(usage);
  // if (args.length != 1) {
  // return false;
  // }
  // for (GSManager gsm : GSManager.getActiveInstances()) {
  // for (Grundstueck gs : gsm.getGrundstuecke()) {
  // gs.updateSignConent();
  // }
  // }
  // String message = ChatColor.YELLOW + "Alle Grundstücke wurden geupdated";
  // MinecraftUtils.sendMessage(sender, message);
  // return true;
  // }

  // private boolean save(CommandSender sender, Command command, String label,
  // String[] args) {
  // String usage = ChatColor.RED
  // + CommonUtils.join(" ", "/" + COMMAND, SAVE);
  // command.setUsage(usage);
  // if (args.length != 1) {
  // return false;
  // }
  // GSManager.saveAll();
  // MinecraftUtils.sendMessage(sender,
  // "Alle Grundstücke wurden gespeichert");
  // return true;
  // }

  // else if (args.length == 2) {
  // String subCommand = args[0];
  // String toComplete = args[1];
  // switch (subCommand) {
  // case LIST:
  // case UPDATE:
  // case SAVE:
  // return CollectionUtils.newArrayList();
  // }
  // if (!(sender instanceof Player)) {
  // return CollectionUtils.newArrayList();
  // }
  // Player player = (Player) sender;
  // GSManager gsm = GSManager.getGSManager(player.getWorld());
  // Set<Grundstueck> grundstuecke = gsm.getGrundstuecke();
  // final List<String> gsNames = CollectionUtils.collect(grundstuecke,
  // new Closure<Grundstueck, String>() {
  // @Override
  // public String call(Grundstueck grundstueck) {
  // return grundstueck.getName();
  // }
  // });
  // switch (subCommand) {
  // case DELETE:
  // case SETPRICE:
  // case INFO:
  // List<String> matchingGsNames = CommandUtils.elementsStartsWith(
  // gsNames, toComplete);
  // return matchingGsNames;
  // case CREATE:
  // if (sender instanceof Player) {
  // RegionManager rm = JavaPlugin.getPlugin(
  // WorldGuardPlugin.class).getRegionManager(
  // player.getWorld());
  // Set<String> allRegionNames = rm.getRegions().keySet();
  // List<String> regionNames = CollectionUtils.findAll(
  // allRegionNames, new Closure<String, Boolean>() {
  // @Override
  // public Boolean call(String regionName) {
  // return !gsNames.contains(regionName);
  // }
  // });
  // List<String> matchingRegionNames = CommandUtils
  // .elementsStartsWith(regionNames, toComplete);
  // return matchingRegionNames;
  // }
  // break;
  // }
  // }
}
