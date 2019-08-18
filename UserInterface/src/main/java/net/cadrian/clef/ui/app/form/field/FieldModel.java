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
package net.cadrian.clef.ui.app.form.field;

import javax.swing.JComponent;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;

public interface FieldModel<T extends Bean, D, J extends JComponent> {

	FieldComponent<D, J> createComponent(T contextBean, ApplicationContext context) throws ModelException;

	String getName();

	String getTab();

	void save(T bean, D data);

	D load(T bean);

	default void created(final T contextBean, final ApplicationContext context, final FieldComponent<D, J> component) {
	}

	default void removed() {
		// default does nothing
	}

}
