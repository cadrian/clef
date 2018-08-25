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

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.numeric.NumericFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.properties.PropertiesComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextAreaComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextFieldComponentFactory;

public class PieceFormModel extends BeanFormModel<Piece> {

	private static final Map<String, FieldComponentFactory<Piece, ?, ? extends JComponent>> COMPONENT_FACTORIES = new LinkedHashMap<>();
	static {
		final TextFieldComponentFactory<Piece> nameFactory = new TextFieldComponentFactory<>(true);
		final NumericFieldComponentFactory<Piece> versionFactory = new NumericFieldComponentFactory<>(false);
		final NumericFieldComponentFactory<Piece> durationFactory = new NumericFieldComponentFactory<>(true);
		final PropertiesComponentFactory<Piece> propertiesFactory = new PropertiesComponentFactory<>(Entity.piece,
				true);
		final TextAreaComponentFactory<Piece> notesFactory = new TextAreaComponentFactory<>(true);
		COMPONENT_FACTORIES.put("Name", nameFactory);
		COMPONENT_FACTORIES.put("Version", versionFactory);
		COMPONENT_FACTORIES.put("Duration", durationFactory);
		COMPONENT_FACTORIES.put("Notes", notesFactory);
		COMPONENT_FACTORIES.put("Properties", propertiesFactory);
	}

	public PieceFormModel() {
		super(Piece.class, COMPONENT_FACTORIES);
	}

}
