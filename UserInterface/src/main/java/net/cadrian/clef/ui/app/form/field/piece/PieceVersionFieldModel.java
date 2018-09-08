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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldSetter;
import net.cadrian.clef.ui.app.tab.DataPane;
import net.cadrian.clef.ui.widget.VersionSpinner;

class PieceVersionFieldModel extends AbstractSimpleFieldModel<Piece, Long, VersionSpinner> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PieceVersionFieldModel.class);

	protected PieceVersionFieldModel(final String tab, final FieldGetter<Piece, Long> getter,
			final FieldSetter<Piece, Long> setter,
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
		LOGGER.debug("Created: {}", contextBean);

		final PiecesComponent piecesComponent = (PiecesComponent) AbstractSimpleFieldModel
				.<Work, Collection<Piece>, DataPane<Piece>>getCachedComponent(contextBean.getWork(), "Pieces");
		LOGGER.debug("  >> piecesComponent={}", piecesComponent);
		((PieceVersionComponent) component).setPiecesComponent(piecesComponent);

		final Piece previous = contextBean.getPrevious();
		LOGGER.debug("  >> previous={}", previous);
		if (previous != null) {
			final PieceVersionComponent cachedPreviousComponent = (PieceVersionComponent) AbstractSimpleFieldModel
					.<Piece, Long, VersionSpinner>getCachedComponent(previous, "Version");
			if (cachedPreviousComponent == null) {
				LOGGER.debug("    (no cached component yet)");
				final PieceVersionComponent previousComponent = (PieceVersionComponent) createComponent(previous,
						context);
				((PieceVersionComponent) component).setPrevious(previousComponent);
				created(previous, context, previousComponent);
				previousComponent.setNext((PieceVersionComponent) component);
			} else {
				LOGGER.debug("    (already created: {})", cachedPreviousComponent);
			}
		}

		final Piece next = contextBean.getNext();
		LOGGER.debug("  >> next={}", previous);
		if (next != null) {
			final PieceVersionComponent cachedNextComponent = (PieceVersionComponent) AbstractSimpleFieldModel
					.<Piece, Long, VersionSpinner>getCachedComponent(next, "Version");
			if (cachedNextComponent == null) {
				LOGGER.debug("    (no cached component yet)");
				final PieceVersionComponent nextComponent = (PieceVersionComponent) createComponent(next, context);
				((PieceVersionComponent) component).setNext(nextComponent);
				created(next, context, nextComponent);
				nextComponent.setPrevious((PieceVersionComponent) component);
			} else {
				LOGGER.debug("    (already created: {})", cachedNextComponent);
			}
		}

		LOGGER.debug("  >> done for {}", contextBean);
	}

}
