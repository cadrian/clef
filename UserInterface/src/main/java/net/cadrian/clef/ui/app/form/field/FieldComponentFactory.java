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
import net.cadrian.clef.ui.ApplicationContext;

public interface FieldComponentFactory<D, J extends JComponent, C extends Bean> {
	FieldComponent<D, J> createComponent(final ApplicationContext context, C contextBean);

	Class<?> getDataType();

	boolean isWritable();

	String getTab();
}
