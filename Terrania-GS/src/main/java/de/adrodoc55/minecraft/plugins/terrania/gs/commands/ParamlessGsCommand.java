package de.adrodoc55.minecraft.plugins.terrania.gs.commands;

import java.util.ArrayList;
import java.util.List;

import de.adrodoc55.minecraft.plugins.common.command.ParameterList;
import de.adrodoc55.minecraft.plugins.common.command.TabCompleteContext;

public abstract class ParamlessGsCommand extends GsCommand {

	protected ParamlessGsCommand(String name) {
		super(name);
	}

	@Override
	protected final ParameterList getParamDefinition() {
		return new ParameterList();
	}

	@Override
	public final List<String> tabComplete(TabCompleteContext context) {
		return new ArrayList<String>(0);
	}

}
