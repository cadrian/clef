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

import net.cadrian.clef.database.io.ClobField;
import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.LongField;

public class Property extends AbstractBean<Property> {

	private static final String TABLE_NAME = "property";
	private static final List<Field<Property>> FIELDS = Arrays.asList(
			new LongField<>("property_descriptor_id", Property::getPropertyDescriptorId,
					Property::setPropertyDescriptorId, Condition.EQ),
			new ClobField<>("value", Property::getValue, Property::setValue));

	private Long propertyDescriptorId;
	private String value;

	public Property() {
		this(null);
	}

	public Property(final Long id) {
		super(id);
	}

	public Long getPropertyDescriptorId() {
		return propertyDescriptorId;
	}

	public void setPropertyDescriptorId(final Long dictionaryId) {
		propertyDescriptorId = dictionaryId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	List<Field<Property>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Property createBean(final Long id) {
		return new Property(id);
	}

}
