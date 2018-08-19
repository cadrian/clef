/*
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
 *
 */
package net.cadrian.clef.database.io;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

public class ClobField<T extends DatabaseBean> extends AbstractField<T, String> {

	public ClobField(final String name, final FieldGetter<T, String> getter, final FieldSetter<T, String> setter) {
		super(name, getter, setter);
	}

	@Override
	public String getCondition(final T bean) throws DatabaseException {
		throw new DatabaseException("Unexpected read condition on CLOB");
	}

	@Override
	public void setConditionValue(final int index, final T bean, final PreparedStatement ps) throws DatabaseException {
		try {
			final Clob clob = ps.getConnection().createClob();
			final String value = getter.get(bean);
			clob.setString(1, value.toString());
			ps.setClob(index, clob);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public void setValue(final ResultSet rs, final T newBean) throws DatabaseException {
		try (StringWriter w = new StringWriter()) {
			final Clob clob = rs.getClob(getName());
			if (clob != null) {
				try (Reader r = clob.getCharacterStream()) {
					final char[] cbuf = new char[4096];
					int n;
					while ((n = r.read(cbuf)) >= 0) {
						w.write(cbuf, 0, n);
					}
				}
				setter.set(newBean, w.toString());
			}
		} catch (SQLException | IOException e) {
			throw new DatabaseException(e);
		}
	}

}
