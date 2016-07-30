package de.adrodoc55.minecraft.plugins.common.utils;

import de.adrodoc55.minecraft.plugins.common.command.CommandException;

public class InsufficientPermissionException extends CommandException {
  private static final long serialVersionUID = 1L;

  public InsufficientPermissionException() {
    super();
  }

  public InsufficientPermissionException(String permission) {
    super(permission);
  }
}
