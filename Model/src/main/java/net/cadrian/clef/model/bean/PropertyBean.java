package net.cadrian.clef.model.bean;

import java.util.Collection;

public interface PropertyBean {

	Collection<Property> getProperties();

	Property getProperty(String propertyName);

	void setProperty(Property property);

	Property delProperty(String propertyName);

}
