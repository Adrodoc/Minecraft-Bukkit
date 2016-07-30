package de.adrodoc55.minecraft.plugins.common.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.adrodoc55.common.collections.CollectionUtils;

public class TabCompleteContext extends AbstractCommandHandlerContext {

  private final String keyToComplete;
  private final String valueToComplete;

  public TabCompleteContext(CommandSender sender, Command command, String alias, String[] args,
      ParameterList paramDef) {
    super(sender, command, alias);

    if (args.length < 1) {
      valueToComplete = "";
    } else {
      valueToComplete = args[args.length - 1];
      args[args.length - 1] = null;
    }
    args = CollectionUtils.removeNullValues(args);

    paramDef.fill(args);

    List<Parameter> params = paramDef.getParams();
    boolean passedRequiredParam = false;
    int x;
    for (x = params.size() - 1; x >= 0; x--) {
      Parameter param = params.get(x);
      String value;
      try {
        value = param.getValue();
      } catch (MissingParameterException e) {
        passedRequiredParam = true;
        continue;
      }
      if (value != null) {
        break;
      }
    }
    int firstEmptyParam = x + 1;
    for (int y = firstEmptyParam; y < params.size(); y++) {
      Parameter param = params.get(y);
      if (param.isOptional() != passedRequiredParam) {
        keyToComplete = param.getKey();
        return;
      }
    }
    keyToComplete = null;

    // toComplete = args[args.length - 1];
  }

  // @Override
  // public String get(int index) {
  // if (index < args.length - 1) {
  // return args[index];
  // } else {
  // throw new ArrayIndexOutOfBoundsException(String.valueOf(index));
  // }
  // }
  //
  // @Override
  // public int getArgsLength() {
  // return args.length - 1;
  // }

  public String getKeyToComplete() {
    return keyToComplete;
  }

  public String getValueToComplete() {
    return valueToComplete;
  }

}
