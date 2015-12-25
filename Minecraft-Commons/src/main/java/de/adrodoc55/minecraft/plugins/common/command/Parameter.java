package de.adrodoc55.minecraft.plugins.common.command;

public class Parameter {
	private String key;
	private String display;
	private boolean optional;
	private String value;

	public Parameter(String key) {
		this(key, false);
	}

	public Parameter(String key, String display) {
		this(key, display, false);
	}

	public Parameter(String key, boolean optional) {
		this(key, key, optional);
	}

	public Parameter(String key, String diplay, boolean optional) {
		this.key = key;
		this.display = diplay;
		this.optional = optional;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public String getValue() throws MissingParameterException {
		if (value == null) {
			if (!isOptional()) {
				throw new MissingParameterException(this);
			}
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

}
