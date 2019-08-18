/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
 * This file is part of Clef.
 *
 * Clef is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Clef is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Clef.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.cadrian.clef.database.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

public class TimestampField<T extends DatabaseBean> extends AbstractField<T, Timestamp> {

	private final Condition condition;

	public TimestampField(final String name, final FieldGetter<T, Timestamp> getter,
			final FieldSetter<T, Timestamp> setter, final Condition condition) {
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
			final Timestamp value = getter.get(bean);
			ps.setTimestamp(index, value);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public void setValue(final ResultSet rs, final T newBean) throws DatabaseException {
		try {
			final Timestamp value = rs.getTimestamp(getName());
			setter.set(newBean, value);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

}
