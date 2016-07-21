package de.adrodoc55.minecraft.plugins.terrania.lock.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.adrodoc55.minecraft.plugins.common.command.CommandContext;
import de.adrodoc55.minecraft.plugins.common.command.CommandHandler;
import de.adrodoc55.minecraft.plugins.common.command.ParameterException;
import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.utils.MaterialUtils;
import de.adrodoc55.minecraft.plugins.common.utils.MinecraftUtils;

public abstract class AbstractLockCommand implements CommandHandler {

    private final String name;

    public AbstractLockCommand(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }

    private final String getUsage() {
        return ChatColor.RED + "/" + getName();
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        command.setUsage(getUsage());

        ParameterList paramDef = new ParameterList();
        CommandContext context;
        try {
            context = new CommandContext(sender, command, label, args, paramDef);
        } catch (ParameterException ex) {
            MinecraftUtils.sendError(sender, ex.getMessage());
            return false;
        }

        command.setUsage("");
        Player player;
        try {
            player = (Player) sender;
        } catch (ClassCastException ex) {
            String message = "Dieser Befehl kann nur von Spielern ausgeführt werden.";
            MinecraftUtils.sendError(sender, message);
            return false;
        }
        // Set<Material> transparent = new HashSet<Material>();
        // transparent.add(Material.AIR);
        Block targetBlock = player.getTargetBlock((Set<Material>) null, 10);
        if (targetBlock == null) {
            String message = "Du musst einen Block ansehen um diesen Befehl auszuführen.";
            MinecraftUtils.sendError(player, message);
            return false;
        }
        if (!MaterialUtils.isLockable(targetBlock.getType())) {
            String message = "Solch ein Block kann nicht abgeschlossen werden.";
            MinecraftUtils.sendError(player, message);
            return false;
        }
        return execute(context, player, targetBlock);
    }

    public abstract boolean execute(CommandContext context, Player player,
            Block block);

    @Override
    public final List<String> onTabComplete(CommandSender sender,
            Command command, String alias, String[] args) {
        return new ArrayList<String>(0);
    }

}
