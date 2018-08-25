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

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.numeric.NumericFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.properties.PropertiesComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextAreaComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextFieldComponentFactory;

public class PieceFormModel extends BeanFormModel<Piece> {

	private static final Collection<FieldComponentFactory<Piece, ?, ? extends JComponent>> COMPONENT_FACTORIES = Arrays
			.asList(new TextFieldComponentFactory<>(Piece.class, "Name", true),
					new NumericFieldComponentFactory<>(Piece.class, "Version", false),
					new NumericFieldComponentFactory<>(Piece.class, "Duration", true),
					new TextAreaComponentFactory<>(Piece.class, "Notes", true),
					new PropertiesComponentFactory<>(Piece.class, "Properties", Entity.piece, true));

	public PieceFormModel() {
		super(COMPONENT_FACTORIES);
	}

}
