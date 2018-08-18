package net.cadrian.clef.model.bean;

import net.cadrian.clef.model.Bean;

public interface Property extends Bean {

	String getName();

	String getValue();

	void setValue(String value);

}
