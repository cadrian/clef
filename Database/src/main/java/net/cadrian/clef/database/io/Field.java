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

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

public interface Field<T extends DatabaseBean> {

	String getName();

	boolean hasCondition(T bean) throws DatabaseException;

	String getCondition(T bean) throws DatabaseException;

	void setConditionValue(int index, T bean, PreparedStatement ps) throws DatabaseException;

	void setValue(ResultSet rs, T newBean) throws DatabaseException;

}
