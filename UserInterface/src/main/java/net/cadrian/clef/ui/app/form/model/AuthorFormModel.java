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

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.properties.PropertiesComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextAreaComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextFieldComponentFactory;

public class AuthorFormModel extends BeanFormModel<Author> {

	private static final Collection<FieldComponentFactory<Author, ?, ? extends JComponent>> COMPONENT_FACTORIES = Arrays
			.asList(new TextFieldComponentFactory<>(Author.class, "Name", true),
					new TextAreaComponentFactory<>(Author.class, "Notes", true),
					new PropertiesComponentFactory<>(Author.class, "Properties", Entity.author, true));

	public AuthorFormModel(final Class<Author> beanType) {
		super(COMPONENT_FACTORIES);
	}

}
