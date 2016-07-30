package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

// import static de.adrodoc55.minecraft.plugins.terrania.gs.TerraniaGSPlugin.logger;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.plugins.common.command.AbstractCommandHandler;

public abstract class GsCommand extends AbstractCommandHandler {

  public static final CreateGsCommand CREATE = new CreateGsCommand();
  public static final DeleteGsCommand DELETE = new DeleteGsCommand();
  public static final SetpriceGsCommand SETPRICE = new SetpriceGsCommand();
  public static final AddMemberGsCommand ADDMEMBER = new AddMemberGsCommand();
  public static final InfoGsCommand INFO = new InfoGsCommand();
  public static final ListGsCommand LIST = new ListGsCommand();
  public static final UpdateGsCommand UPDATE = new UpdateGsCommand();
  public static final SaveGsCommand SAVE = new SaveGsCommand();

  // private static final GsCommand[] INSTANCES = { CREATE, DELETE, SETPRICE,
  // INFO, LIST, UPDATE, SAVE };

  public static GsCommand getInstance(String subCommandName) {
    if (subCommandName == null) {
      return null;
    }
    // for (Class<? extends GsCommand> clazz : getCommandClasses()) {
    // GsCommand gsCommand;
    // try {
    // gsCommand = clazz.newInstance();
    // } catch (InstantiationException | IllegalAccessException e) {
    // continue;
    // }
    // if (subCommandName.equals(gsCommand.getName())) {
    // return gsCommand;
    // }
    // }
    for (GsCommand gsCommand : getCommands()) {
      if (subCommandName.equals(gsCommand.getName())) {
        return gsCommand;
      }
    }

    return null;
  }

  public static List<GsCommand> getCommands() {
    List<GsCommand> commands = new ArrayList<GsCommand>();
    commands.add(CREATE);
    commands.add(DELETE);
    commands.add(SETPRICE);
    commands.add(ADDMEMBER);
    commands.add(INFO);
    commands.add(LIST);
    commands.add(UPDATE);
    commands.add(SAVE);
    return commands;
  }

  // private static List<Class<? extends GsCommand>> getCommandClasses() {
  // List<Class<? extends GsCommand>> commands = new ArrayList<Class<? extends
  // GsCommand>>();
  // commands.add(CreateGsCommand.class);
  // commands.add(DeleteGsCommand.class);
  // commands.add(SetpriceGsCommand.class);
  // commands.add(InfoGsCommand.class);
  // commands.add(ListGsCommand.class);
  // commands.add(UpdateGsCommand.class);
  // commands.add(SaveGsCommand.class);
  // return commands;
  // }

  public static final String COMMAND = "gs";

  // private final String name;

  protected GsCommand(String name) {
    super(new String[] {COMMAND, name});
  }

  // public final String getName() {
  // return name;
  // }

  // public final String getUsage() {
  // List<Parameter> params = getParamDefinition().getParams();
  // List<String> collect = CollectionUtils.collect(params,
  // new Closure<Parameter, String>() {
  // @Override
  // public String call(Parameter p) {
  // String result = p.getDisplay();
  // if (p.isOptional()) {
  // result = "[" + result + "]";
  // } else {
  // result = "<" + result + ">";
  // }
  // return result;
  // }
  // });
  // return getBasicUsage() + " " + CommonUtils.join(" ", collect);
  // }

  // private final String getBasicUsage() {
  // return ChatColor.RED + CommonUtils.join(" ", "/" + COMMAND, name);
  // }

  // protected abstract ParameterList getParamDefinition();

  // @Override
  // public boolean onCommand(CommandSender sender, Command command,
  // String label, String[] args) {
  // command.setUsage(getUsage());
  //
  // args[0] = null; // args[0] ist der subCommand name
  // args = CollectionUtils.removeNullValues(args);
  //
  // ParameterList definition = getParamDefinition();
  // List<Parameter> unmodifiableParams = definition.getParams();
  // if (args.length > unmodifiableParams.size()) {
  // String message = String.format(
  // "Du hast zu viele Parameter angegeben.", args.length,
  // unmodifiableParams.size());
  // MinecraftUtils.sendError(sender, message);
  // return false;
  // }
  // fillParams(unmodifiableParams, args);
  // Map<String, String> params;
  // try {
  // params = definition.toMap();
  // } catch (MissingParameterException ex) {
  // String message = ex.getMessage();
  // MinecraftUtils.sendError(sender, message);
  // return false;
  // }
  // return execute(sender, command, params);
  // }

  // private void fillParams(List<Parameter> params, String[] args) {
  // int requiredCount = CollectionUtils.findAll(params,
  // new Closure<Parameter, Boolean>() {
  // @Override
  // public Boolean call(Parameter p) {
  // return !p.isOptional();
  // }
  // }).size();
  //
  // int argsLeft = args.length;
  // // logger().error("params.size() = " + params.size());
  // // logger().error("args.length = " + args.length);
  // // logger().error("argsLeft = " + argsLeft);
  // // logger().error("requiredCount = " + requiredCount);
  // int y = 0;
  // for (int x = 0; x < params.size(); x++) {
  // if (y >= args.length) {
  // return;
  // }
  // Parameter p = params.get(x);
  // // logger().error("p = " + p.getKey());
  // // logger().error("args[y] = " + args[y]);
  // if (p.isOptional()) {
  // if (requiredCount >= argsLeft) {
  // // logger().error("cancel");
  // continue;
  // }
  // }
  // // logger().error("setValue");
  // p.setValue(args[y]);
  // y++;
  // argsLeft--;
  // requiredCount--;
  // }
  // }

  // protected abstract boolean execute(CommandSender sender, Command command,
  // Map<String, String> params);

  // @Override
  // public final boolean onCommand(CommandSender sender, Command command,
  // String label, String[] args) {
  // command.setUsage(getUsage());
  //
  // args[0] = null; // args[0] ist der subCommand name
  // args = CollectionUtils.removeNullValues(args);
  //
  // ParameterList paramDef = getParamDefinition();
  // CommandContext context;
  // try {
  // context = new CommandContext(sender, command, label, args, paramDef);
  // } catch (ParameterException ex) {
  // MinecraftUtils.sendError(sender, ex.getMessage());
  // return false;
  // }
  // return execute(context);
  // }

  // protected abstract boolean execute(CommandContext context);

  // @Override
  // public final List<String> onTabComplete(CommandSender sender,
  // Command command, String alias, String[] args) {
  // // ParameterList paramDefinition = getParamDefinition();
  //
  // String toComplete = args[args.length - 1];
  // args[0] = null; // args[0] ist der subCommand name
  // args[args.length - 1] = null;
  // String[] completeArgs = CollectionUtils.removeNullValues(args);
  // List<String> elements = tabComplete(sender, command, completeArgs,
  // toComplete);
  //
  // List<String> matchingElements = CommandUtils.elementsStartsWith(
  // elements, toComplete);
  // return matchingElements;
  // }
  //
  // protected abstract List<String> tabComplete(CommandSender sender,
  // Command command, String[] completeArgs, String toComplete);

}
