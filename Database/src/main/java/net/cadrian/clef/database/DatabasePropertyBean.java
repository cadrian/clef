package net.cadrian.clef.database;

import java.util.Collection;

public interface DatabasePropertyBean extends DatabaseBean {
	Collection<Long> getProperties();

	void setProperties(final Collection<Long> properties);
}
