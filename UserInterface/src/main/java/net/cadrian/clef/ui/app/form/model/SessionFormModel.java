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
package net.cadrian.clef.ui.app.form.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.date.DateComponentFactory;
import net.cadrian.clef.ui.app.form.field.properties.PropertiesComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextAreaComponentFactory;

public class SessionFormModel extends BeanFormModel<Session> {

	private static final Map<String, FieldComponentFactory<Session, ?, ? extends JComponent>> COMPONENT_FACTORIES = new LinkedHashMap<>();
	static {
		final DateComponentFactory<Session> startFactory = new DateComponentFactory<>(false);
		final DateComponentFactory<Session> stopFactory = new DateComponentFactory<>(true);
		final TextAreaComponentFactory<Session> notesFactory = new TextAreaComponentFactory<>(true);
		final PropertiesComponentFactory<Session> propertiesFactory = new PropertiesComponentFactory<>(Entity.session,
				true);
		COMPONENT_FACTORIES.put("Start", startFactory);
		COMPONENT_FACTORIES.put("Stop", stopFactory);
		COMPONENT_FACTORIES.put("Notes", notesFactory);
		COMPONENT_FACTORIES.put("Properties", propertiesFactory);
	}

	public SessionFormModel(final Class<Session> beanType) {
		super(beanType, COMPONENT_FACTORIES);
	}

}
