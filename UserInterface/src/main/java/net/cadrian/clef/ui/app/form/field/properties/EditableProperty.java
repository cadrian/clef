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
package net.cadrian.clef.ui.app.form.field.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;

class EditableProperty implements Comparable<EditableProperty> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EditableProperty.class);

	private final PropertyDescriptor propertyDescriptor;
	private Property property;
	private String value;
	private boolean dirty;

	EditableProperty(final PropertyDescriptor propertyDescriptor) {
		this.propertyDescriptor = propertyDescriptor;
		property = null;
		value = null;
		dirty = false;
	}

	EditableProperty(final Property property) {
		propertyDescriptor = property.getPropertyDescriptor();
		this.property = property;
		value = property.getValue();
		dirty = false;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public Property getProperty() {
		return property;
	}

	void setProperty(final Property property) {
		this.property = property;
		dirty = true;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
		dirty = true;
	}

	public void save() {
		if (dirty) {
			property.setValue(value);
			dirty = false;
		}
	}

	public boolean isDirty() {
		if (dirty) {
			LOGGER.debug("dirty: {}", property);
		}
		return dirty;
	}

	@Override
	public int compareTo(final EditableProperty o) {
		return propertyDescriptor.getName().compareTo(o.propertyDescriptor.getName());
	}

	@Override
	public String toString() {
		return propertyDescriptor.getName();
	}
}
