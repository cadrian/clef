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
package net.cadrian.clef.ui.app.form;

import java.lang.reflect.Method;

import javax.swing.JComponent;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;

public class FieldModel<T extends Bean, D, J extends JComponent, C extends Bean> {
	final String name;
	final Class<D> type;
	final Method getter;
	final Method setter;
	final FieldComponentFactory<D, J, C> componentFactory;

	@SuppressWarnings("unchecked")
	FieldModel(final String name, final Method getter, final Method setter,
			final FieldComponentFactory<D, J, C> componentFactory) {
		this.name = name;
		this.type = (Class<D>) getter.getReturnType();
		this.getter = getter;
		this.setter = setter;
		this.componentFactory = componentFactory;
	}

}
