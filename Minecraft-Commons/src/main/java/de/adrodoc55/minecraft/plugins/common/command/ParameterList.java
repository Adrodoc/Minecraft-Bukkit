package de.adrodoc55.minecraft.plugins.common.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;

public class ParameterList {

	private final List<Parameter> params = new ArrayList<Parameter>();

	public List<Parameter> getParams() {
		return Collections.unmodifiableList(params);
	}

	public void add(Parameter param) throws DuplicateParameterException {
		List<String> paramKeys = CollectionUtils.collect(getParams(), new Closure<Parameter, String>() {
			@Override
			public String call(Parameter p) {
				return p.getKey();
			}
		});
		if (paramKeys.contains(param.getKey())) {
			throw new DuplicateParameterException(param);
		} else {
			params.add(param);
		}
	}

	public Map<String, String> toMap() throws MissingParameterException {
		Map<String, String> map = new HashMap<String, String>();
		for (Parameter p : getParams()) {
			String key = p.getKey();
			String value = p.getValue();
			map.put(key, value);
		}
		return map;
	}

	public static class DuplicateParameterException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private static String constructMessage(Parameter parameter) {
			String message = String.format("Der Parameter %s existiert bereits in dieser ParameterList.",
					parameter.getDisplay());
			return message;
		}

		public DuplicateParameterException(Parameter parameter) {
			super(constructMessage(parameter));
		}
	}

	public void fill(String[] args) {
		List<Parameter> params = getParams();
		List<Parameter> required = CollectionUtils.findAll(params, new Closure<Parameter, Boolean>() {
			@Override
			public Boolean call(Parameter p) {
				return !p.isOptional();
			}
		});
		int requiredCount = required.size();

		int valuesLeft = args.length;
		int y = 0;
		for (int x = 0; x < params.size(); x++) {
			if (y >= args.length) {
				return;
			}
			Parameter p = params.get(x);
			if (p.isOptional()) {
				if (requiredCount >= valuesLeft) {
					continue;
				}
			}
			p.setValue(args[y]);
			y++;
			valuesLeft--;
			requiredCount--;
		}
	}

}
