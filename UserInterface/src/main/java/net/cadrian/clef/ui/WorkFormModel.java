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

import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.form.BeanComponentFactory;
import net.cadrian.clef.ui.form.FieldComponentFactory;
import net.cadrian.clef.ui.form.PiecesComponentFactory;
import net.cadrian.clef.ui.form.PropertiesComponentFactory;
import net.cadrian.clef.ui.form.TextAreaComponentFactory;
import net.cadrian.clef.ui.form.TextFieldComponentFactory;

class WorkFormModel extends BeanFormModel<Work, Work> {

	private static final Map<String, FieldComponentFactory<?, ? extends JComponent, Work>> COMPONENT_FACTORIES = new LinkedHashMap<>();
	static {
		final TextFieldComponentFactory<Work> nameFactory = new TextFieldComponentFactory<>(true, "Description");
		final TextAreaComponentFactory<Work> notesFactory = new TextAreaComponentFactory<>(true, "Description");
		final PropertiesComponentFactory<Work> propertiesFactory = new PropertiesComponentFactory<>(Entity.work, true,
				"Description");
		final BeanComponentFactory<Author, Work> authorFactory = new BeanComponentFactory<>(Author.class,
				"Description");
		final BeanComponentFactory<Pricing, Work> pricingFactory = new BeanComponentFactory<>(Pricing.class,
				"Description");
		final PiecesComponentFactory piecesFactory = new PiecesComponentFactory(new PieceFormModel(), "Pieces");
		COMPONENT_FACTORIES.put("Author", authorFactory);
		COMPONENT_FACTORIES.put("Pricing", pricingFactory);
		COMPONENT_FACTORIES.put("Name", nameFactory);
		COMPONENT_FACTORIES.put("Notes", notesFactory);
		COMPONENT_FACTORIES.put("Properties", propertiesFactory);
		COMPONENT_FACTORIES.put("Pieces", piecesFactory);
	}

	WorkFormModel(final Class<Work> beanType) {
		super(beanType, COMPONENT_FACTORIES);
	}

}
