package net.cadrian.clef.model.bean;

import java.util.Map;

import net.cadrian.clef.model.Bean;

public interface PropertyBean extends Bean {

	Map<String, ? extends Property> getProperties();

	Property getProperty(String propertyName);

	void setProperty(Property property);

	Property delProperty(String propertyName);

}
