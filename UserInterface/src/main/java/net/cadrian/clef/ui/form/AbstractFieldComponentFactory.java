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
package net.cadrian.clef.ui.form;

import javax.swing.JComponent;

import net.cadrian.clef.model.Bean;

abstract class AbstractFieldComponentFactory<D, J extends JComponent, C extends Bean>
		implements FieldComponentFactory<D, J, C> {

	protected final boolean writable;
	private final String tab;

	public AbstractFieldComponentFactory(final boolean writable, final String tab) {
		this.writable = writable;
		this.tab = tab;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public String getTab() {
		return tab;
	}

}
