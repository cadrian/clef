package net.cadrian.clef.database.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

public class LongField<T extends DatabaseBean> extends AbstractField<T, Long> {

	private final Condition condition;

	public LongField(final String name, final FieldGetter<T, Long> getter, final FieldSetter<T, Long> setter,
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
		case GT:
			return getName() + ">?";
		case GE:
			return getName() + ">=?";
		case LT:
			return getName() + "<?";
		case LE:
			return getName() + "<=?";
		default:
			throw new DatabaseException("Unsupported condition: " + condition);
		}
	}

	@Override
	public void setConditionValue(final int index, final T bean, final PreparedStatement ps) throws DatabaseException {
		try {
			final Long value = getter.get(bean);
			ps.setLong(index, value);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public void setValue(final ResultSet rs, final T newBean) throws DatabaseException {
		try {
			final Long value = rs.getLong(getName());
			setter.set(newBean, value);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

}
