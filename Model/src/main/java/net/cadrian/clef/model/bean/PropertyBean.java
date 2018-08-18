package net.cadrian.clef.model.bean;

import java.util.Map;

public interface PropertyBean {

	Map<String, ? extends Property> getProperties();

	Property getProperty(String propertyName);

	void setProperty(Property property);

	Property delProperty(String propertyName);

}
