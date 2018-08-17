package net.cadrian.clef.database.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

public class StringField<T extends DatabaseBean> extends AbstractField<T, String> {

	private final Condition condition;

	public StringField(final String name, final FieldGetter<T, String> getter, final FieldSetter<T, String> setter,
			final Condition condition) {
		super(name, getter, setter);
		this.condition = condition;
	}

	@Override
	public String getCondition(final T bean) throws DatabaseException {
		switch (condition) {
		case EQ:
			return getName() + "=?";
		case NE:
			return getName() + "!=?";
		case LIKE:
			return getName() + "LIKE ?";
		default:
			throw new DatabaseException("Unsupported condition: " + condition);
		}
	}

	@Override
	public void setConditionValue(final int index, final T bean, final PreparedStatement ps) throws DatabaseException {
		try {
			final String value = getter.get(bean);
			ps.setString(index, value);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public void setValue(final ResultSet rs, final T newBean) throws DatabaseException {
		try {
			final String value = rs.getString(getName());
			setter.set(newBean, value);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

}
