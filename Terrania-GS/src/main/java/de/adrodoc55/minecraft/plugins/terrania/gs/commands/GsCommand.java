package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

// import static de.adrodoc55.minecraft.plugins.terrania.gs.TerraniaGSPlugin.logger;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.permissions.Permissible;

import de.adrodoc55.minecraft.plugins.common.command.AbstractCommandHandler;

public abstract class GsCommand extends AbstractCommandHandler {

  public static final CreateGsCommand CREATE = new CreateGsCommand();
  public static final DeleteGsCommand DELETE = new DeleteGsCommand();
  public static final SetpriceGsCommand SETPRICE = new SetpriceGsCommand();
  public static final AddMemberGsCommand ADDMEMBER = new AddMemberGsCommand();
  public static final RemoveMemberGsCommand REMOVEMEMBER = new RemoveMemberGsCommand();
  public static final InfoGsCommand INFO = new InfoGsCommand();
  public static final ListGsCommand LIST = new ListGsCommand();
  public static final UpdateGsCommand UPDATE = new UpdateGsCommand();
  public static final SaveGsCommand SAVE = new SaveGsCommand();

  public static GsCommand getInstance(String subCommandName) {
    if (subCommandName == null) {
      return null;
    }
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
    commands.add(REMOVEMEMBER);
    commands.add(INFO);
    commands.add(LIST);
    commands.add(UPDATE);
    commands.add(SAVE);
    return commands;
  }

  public static final String COMMAND = "gs";

  protected GsCommand(String name) {
    super(new String[] {COMMAND, name});
  }

  public String getPermissionKey() {
    return "terrania.gs.commands.gs." + getName();
  }

  /**
   * Checks if the {@code permissible} might have permission to use this command. By default this is
   * {@code true} if {@code permissible} has the Permission returned by {@link #getPermissionKey()},
   * but for some subclasses this might be different. For example an owner of a gs can execute
   * certain commands even if he does not have generic access.
   *
   * @param permissible the {@link Permissible} to check
   * @return if the {@code permissible} might have permission to use this command
   */
  public boolean mightHavePermission(Permissible permissible) {
    return permissible.hasPermission(getPermissionKey());
  }
}
