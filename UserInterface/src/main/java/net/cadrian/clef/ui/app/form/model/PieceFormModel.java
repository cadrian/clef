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
import java.util.Date;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.numeric.DurationFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.piece.PieceVersionComponentFactory;
import net.cadrian.clef.ui.app.form.field.properties.PropertiesComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextAreaComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextFieldComponentFactory;

public class PieceFormModel extends BeanFormModel<Piece> {

	private static final Collection<FieldComponentFactory<Piece, ?, ? extends JComponent>> COMPONENT_FACTORIES = Arrays
			.asList(new TextFieldComponentFactory<>(Piece.class, "Name", true),
					new DurationFieldComponentFactory<>(Piece.class, "Duration", true),
					new TextAreaComponentFactory<>(Piece.class, "Notes", true),
					new PropertiesComponentFactory<>(Piece.class, "Properties", Entity.piece, true),
					new DurationFieldComponentFactory<>(Piece.class, "WorkTime", PieceFormModel::getWorkTime),
					new PieceVersionComponentFactory());

	public PieceFormModel() {
		super(COMPONENT_FACTORIES);
	}

	static Long getWorkTime(final Piece piece) {
		long result = 0;
		for (final Session session : piece.getSessions()) {
			final Date start = session.getStart();
			if (start != null) {
				final Date stop = session.getStop();
				if (stop != null) {
					result += (stop.getTime() - start.getTime()) / 1000;
				}
			}
		}
		return result;
	}

}
