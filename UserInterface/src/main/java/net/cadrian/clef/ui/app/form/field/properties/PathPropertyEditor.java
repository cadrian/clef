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

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.widget.DefaultDownloadFilter;
import net.cadrian.clef.ui.widget.DownloadFilter;

class PathPropertyEditor extends AbstractFilePropertyEditor {

	PathPropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
		super(context, writable, property);
		content.setFile(property.getValue());
	}

	@Override
	public void save() {
		property.setValue(content.getFile().getAbsolutePath());
		content.markSave();
	}

	@Override
	protected DownloadFilter getDownloadFilter(final ApplicationContext context) {
		return new DefaultDownloadFilter();
	}
}
