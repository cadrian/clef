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

import java.util.Collection;

import javax.swing.JSplitPane;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

public class PropertiesComponentFactory<C extends Bean>
		extends AbstractFieldComponentFactory<Collection<? extends Property>, JSplitPane, C> {

	private final Entity entity;

	public PropertiesComponentFactory(final Entity entity, final boolean writable) {
		this(entity, writable, null);
	}

	public PropertiesComponentFactory(final Entity entity, final boolean writable, final String tab) {
		super(writable, tab);
		this.entity = entity;
	}

	@Override
	public FieldComponent<Collection<? extends Property>, JSplitPane> createComponent(final ApplicationContext context,
			final C contextBean) {
		return new PropertiesComponent(context, entity, writable);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<Collection> getDataType() {
		return Collection.class;
	}

}
