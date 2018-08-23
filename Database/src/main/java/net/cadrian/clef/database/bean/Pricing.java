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

import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.StringField;

public class Pricing extends AbstractPropertyBean<Pricing> {

	private static final String TABLE_NAME = "pricing";
	private static final List<Field<Pricing>> FIELDS = Arrays.asList(
			new StringField<>("name", Pricing::getName, Pricing::setName, Condition.EQ),
			new StringField<>("notes", Pricing::getNotes, Pricing::setNotes, Condition.EQ));

	private String name;
	private String notes;

	public Pricing() {
		this(null);
	}

	public Pricing(final Long id) {
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
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
