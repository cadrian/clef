package net.cadrian.clef.model.bean;

import java.util.Collection;

import net.cadrian.clef.model.Bean;

public interface PropertyBean extends Bean {

	Collection<? extends Property> getProperties();

	void setProperties(Collection<? extends Property> properties);

}
