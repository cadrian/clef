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
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.widget.VersionSpinner;
import net.cadrian.clef.ui.widget.VersionSpinner.Controller;

class PieceVersionComponent implements FieldComponent<Long, VersionSpinner> {

	private final class VersionSpinnerController implements Controller {
		private final Piece piece;
		private final ApplicationContext context;

		private VersionSpinnerController(final Piece piece, final ApplicationContext context) {
			this.piece = piece;
			this.context = context;
		}

		@Override
		public boolean hasPrevious() {
			return previous != null;
		}

		@Override
		public void previous() {
			if (piecesComponent != null) {
				piecesComponent.component.select(previous.piece, false);
			}
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public void next() {
			if (piecesComponent != null) {
				piecesComponent.component.select(next.piece, false);
			}
		}

		@Override
		public int getCurrent() {
			return count;
		}

		@Override
		public void create() {
			if (piecesComponent != null) {
				piecesComponent.component.select(context.getBeans().createPieceVersion(piece), true);
			}
		}
	}

	private final VersionSpinner component;
	private final Piece piece;

	private PieceVersionComponent previous;
	private PieceVersionComponent next;
	private PiecesComponent piecesComponent;
	private final int count;

	PieceVersionComponent(final Piece piece, final ApplicationContext context) {
		this.piece = piece;

		count = count(piece);

		component = new VersionSpinner(context, new VersionSpinnerController(piece, context));
	}

	private int count(final Piece piece) {
		Piece countPiece = piece;
		int current = 0;
		do {
			current++;
			countPiece = countPiece.getPrevious();
		} while (countPiece != null);
		return current;
	}

	@Override
	public VersionSpinner getComponent() {
		return component;
	}

	@Override
	public Long getData() {
		return Long.valueOf(count);
	}

	@Override
	public void setData(final Long data) {
		// ignored
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public boolean isDirty() {
		// TODO must go through all versions
		return false;
	}

	void setPrevious(final PieceVersionComponent previous) {
		this.previous = previous;
		component.refresh();
	}

	void setNext(final PieceVersionComponent next) {
		this.next = next;
		component.refresh();
	}

	public void setPiecesComponent(final PiecesComponent piecesComponent) {
		this.piecesComponent = piecesComponent;
		component.refresh();
	}

}
