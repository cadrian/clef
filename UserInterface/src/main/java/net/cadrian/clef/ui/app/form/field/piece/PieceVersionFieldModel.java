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

import java.lang.reflect.Method;
import java.util.Collection;

import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.tab.DataPane;
import net.cadrian.clef.ui.widget.VersionSpinner;

class PieceVersionFieldModel extends AbstractSimpleFieldModel<Piece, Long, VersionSpinner> {

	protected PieceVersionFieldModel(final String tab, final Method getter, final Method setter,
			final FieldComponentFactory<Piece, Long, VersionSpinner> componentFactory) {
		super("Version", tab, getter, setter, componentFactory);
	}

	@Override
	protected FieldComponent<Long, VersionSpinner> createNewComponent(final Piece contextBean,
			final ApplicationContext context) throws ModelException {
		return new PieceVersionComponent(contextBean, context);
	}

	@Override
	public void created(final Piece contextBean, final ApplicationContext context,
			final FieldComponent<Long, VersionSpinner> component) {

		final PiecesComponent piecesComponent = (PiecesComponent) AbstractSimpleFieldModel
				.<Work, Collection<Piece>, DataPane<Piece>>getCachedComponent(contextBean.getWork(), "Pieces");
		((PieceVersionComponent) component).setPiecesComponent(piecesComponent);

		final Piece previous = contextBean.getPrevious();
		if (previous != null) {
			final PieceVersionComponent previousComponent = (PieceVersionComponent) createNewComponent(previous,
					context);
			((PieceVersionComponent) component).setPrevious(previousComponent);
			created(previous, context, previousComponent);
		}
	}

}
