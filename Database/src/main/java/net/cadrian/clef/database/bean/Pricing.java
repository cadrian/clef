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
package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.Field;

public class Pricing extends AbstractBean<Pricing> {

	private static final String TABLE_NAME = "pricing";
	private static final List<Field<Pricing>> FIELDS = Arrays.asList();

	public Pricing() {
		this(null);
	}

	public Pricing(final Long id) {
		super(id);
	}

	@Override
	List<Field<Pricing>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Pricing createBean(final Long id) {
		return new Pricing(id);
	}

}
