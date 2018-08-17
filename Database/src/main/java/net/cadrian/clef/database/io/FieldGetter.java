package net.cadrian.clef.database.io;

import net.cadrian.clef.database.DatabaseBean;

@FunctionalInterface
public interface FieldGetter<T extends DatabaseBean, D> {

	D get(T bean);

}
