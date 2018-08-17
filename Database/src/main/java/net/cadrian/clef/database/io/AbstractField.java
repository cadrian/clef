package net.cadrian.clef.database.io;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

abstract class AbstractField<T extends DatabaseBean, D> implements Field<T> {

	private final String name;
	protected final FieldGetter<T, D> getter;
	protected final FieldSetter<T, D> setter;

	AbstractField(final String name, final FieldGetter<T, D> getter, final FieldSetter<T, D> setter) {
		this.name = name;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final boolean hasCondition(final T bean) throws DatabaseException {
		final Object data = getter.get(bean);
		return data != null;
	}

}
