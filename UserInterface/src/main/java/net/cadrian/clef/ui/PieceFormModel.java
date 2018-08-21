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

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.form.FieldComponentFactory;
import net.cadrian.clef.ui.form.NumericFieldComponentFactory;
import net.cadrian.clef.ui.form.PropertiesComponentFactory;
import net.cadrian.clef.ui.form.TextAreaComponentFactory;
import net.cadrian.clef.ui.form.TextFieldComponentFactory;

class PieceFormModel extends BeanFormModel<Piece, Work> {

	private static final Map<String, FieldComponentFactory<?, ? extends JComponent, Work>> COMPONENT_FACTORIES = new LinkedHashMap<>();
	static {
		final TextFieldComponentFactory<Work> nameFactory = new TextFieldComponentFactory<>(true);
		final NumericFieldComponentFactory<Work> versionFactory = new NumericFieldComponentFactory<>(false);
		final NumericFieldComponentFactory<Work> durationFactory = new NumericFieldComponentFactory<>(true);
		final PropertiesComponentFactory<Work> propertiesFactory = new PropertiesComponentFactory<>(Entity.piece, true);
		final TextAreaComponentFactory<Work> notesFactory = new TextAreaComponentFactory<>(true);
		COMPONENT_FACTORIES.put("Name", nameFactory);
		COMPONENT_FACTORIES.put("Version", versionFactory);
		COMPONENT_FACTORIES.put("Duration", durationFactory);
		COMPONENT_FACTORIES.put("Notes", notesFactory);
		COMPONENT_FACTORIES.put("Properties", propertiesFactory);
	}

	PieceFormModel() {
		super(Piece.class, COMPONENT_FACTORIES);
	}

}
