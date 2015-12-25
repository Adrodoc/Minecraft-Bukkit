package de.adrodoc55.minecraft.plugins.common.utils;

import java.util.List;

import de.adrodoc55.common.collections.Closure;
import de.adrodoc55.common.collections.CollectionUtils;

public class CommandUtils {

	public static List<String> elementsStartsWith(Iterable<String> elements,
			final String prefix) {
		List<String> matchingElements = CollectionUtils.findAll(elements,
				new Closure<String, Boolean>() {
					@Override
					public Boolean call(String element) {
						return element.startsWith(prefix);
					}
				});
		return matchingElements;
	}
	
}
