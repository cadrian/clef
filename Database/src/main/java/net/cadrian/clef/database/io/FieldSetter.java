package net.cadrian.clef.database.io;

import net.cadrian.clef.database.DatabaseBean;

@FunctionalInterface
public interface FieldSetter<T extends DatabaseBean, D> {

	void set(T bean, D value);

}
