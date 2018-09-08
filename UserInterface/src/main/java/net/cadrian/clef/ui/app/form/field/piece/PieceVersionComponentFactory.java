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
package net.cadrian.clef.ui.app.form.field.piece;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldModel;
import net.cadrian.clef.ui.app.form.field.FieldSetter;
import net.cadrian.clef.ui.widget.VersionSpinner;

public class PieceVersionComponentFactory extends AbstractSimpleFieldComponentFactory<Piece, Long, VersionSpinner> {

	public PieceVersionComponentFactory() {
		this(null);
	}

	public PieceVersionComponentFactory(final String tab) {
		super(Piece.class, "Version", false, tab);
	}

	@Override
	protected FieldModel<Piece, Long, VersionSpinner> createModel(final String fieldName,
			final FieldGetter<Piece, Long> getter, final FieldSetter<Piece, Long> setter) {
		return new PieceVersionFieldModel(tab, getter, setter, this);
	}

}
