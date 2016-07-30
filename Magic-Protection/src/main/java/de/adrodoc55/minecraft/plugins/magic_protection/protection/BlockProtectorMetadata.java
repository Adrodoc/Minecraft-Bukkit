package de.adrodoc55.minecraft.plugins.magic_protection.protection;

import org.bukkit.OfflinePlayer;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.plugin.Plugin;

public class BlockProtectorMetadata extends MetadataValueAdapter {

  private OfflinePlayer protector;

  public BlockProtectorMetadata(Plugin owningPlugin, OfflinePlayer protector) {
    super(owningPlugin);
    this.protector = protector;
  }

  @Override
  public OfflinePlayer value() {
    return protector;
  }

  @Override
  public void invalidate() {
    protector = null;
  }

}
