package de.adrodoc55.minecraft.plugins.common.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.adrodoc55.common.CommonUtils;
import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;
import de.adrodoc55.minecraft.plugins.common.utils.CommandUtils;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;

public abstract class AbstractCommandHandler implements CommandHandler {

  private final String name;
  private final String[] path;

  protected AbstractCommandHandler(String[] path) {
    this.name = path[path.length - 1];
    this.path = path;
  }

  public final String getName() {
    return name;
  }

  public final String getPath() {
    return CommonUtils.join(" ", (Object[]) path);
  }

  private final String getBasicUsage() {
    return ChatColor.RED + "/" + getPath();
  }

  public final String getUsage() {
    List<Parameter> params = getParamDefinition().getParams();
    List<String> collect = CollectionUtils.collect(params, new Closure<Parameter, String>() {
      @Override
      public String call(Parameter p) {
        String result = p.getDisplay();
        if (p.isOptional()) {
          result = "[" + result + "]";
        } else {
          result = "<" + result + ">";
        }
        return result;
      }
    });
    return getBasicUsage() + " " + CommonUtils.join(" ", collect);
  }

  @Override
  public final boolean onCommand(CommandSender sender, Command command, String label,
      String[] args) {
    command.setUsage(getUsage());

    if (args.length < path.length - 1) {
      return false;
    }
    for (int x = 1; x < path.length; x++) {
      args[x - 1] = null; // args[x - 1] ist Teil der SubCommand Namen
    }
    args = CollectionUtils.removeNullValues(args);

    ParameterList paramDef = getParamDefinition();
    CommandContext context;
    try {
      context = new CommandContext(sender, command, label, args, paramDef);
    } catch (ParameterException ex) {
      MinecraftUtils.sendError(sender, ex.getMessage());
      return false;
    }
    return execute(context);
  }

  protected abstract ParameterList getParamDefinition();

  protected abstract boolean execute(CommandContext context);

  @Override
  public final List<String> onTabComplete(CommandSender sender, Command command, String alias,
      String[] args) {
    // ParameterList paramDefinition = getParamDefinition();

    String toComplete = args[args.length - 1];
    args[args.length - 1] = null;
    if (args.length < path.length - 1) {
      return new ArrayList<String>(0);
    }
    for (int x = 1; x < path.length; x++) {
      args[x - 1] = null; // args[x - 1] ist Teil der SubCommand Namen
    }
    args = CollectionUtils.removeNullValues(args);

    ParameterList paramDef = getParamDefinition();

    TabCompleteContext context = new TabCompleteContext(sender, command, alias, args, paramDef);

    List<String> elements = tabComplete(context);

    List<String> matchingElements = CommandUtils.elementsStartsWith(elements, toComplete);
    return matchingElements;
  }

  protected abstract List<String> tabComplete(TabCompleteContext context);

}
