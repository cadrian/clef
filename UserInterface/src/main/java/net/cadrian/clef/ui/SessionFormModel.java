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
package net.cadrian.clef.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.ui.form.DateComponentFactory;
import net.cadrian.clef.ui.form.FieldComponentFactory;
import net.cadrian.clef.ui.form.PropertiesComponentFactory;
import net.cadrian.clef.ui.form.TextAreaComponentFactory;

class SessionFormModel extends BeanFormModel<Session, Void> {

	private static final Map<String, FieldComponentFactory<?, ? extends JComponent, Void>> COMPONENT_FACTORIES = new LinkedHashMap<>();
	static {
		final DateComponentFactory<Void> startFactory = new DateComponentFactory<>(false);
		final DateComponentFactory<Void> stopFactory = new DateComponentFactory<>(true);
		final TextAreaComponentFactory<Void> notesFactory = new TextAreaComponentFactory<>(true);
		final PropertiesComponentFactory<Void> propertiesFactory = new PropertiesComponentFactory<>(true);
		COMPONENT_FACTORIES.put("Start", startFactory);
		COMPONENT_FACTORIES.put("Stop", stopFactory);
		COMPONENT_FACTORIES.put("Notes", notesFactory);
		COMPONENT_FACTORIES.put("Properties", propertiesFactory);
	}

	SessionFormModel(final Class<Session> beanType) {
		super(beanType, COMPONENT_FACTORIES);
	}

}
