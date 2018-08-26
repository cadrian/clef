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
package net.cadrian.clef.ui.tools;

import javax.swing.JLabel;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.tools.Converters;

public class StatisticsComputation {

	public interface IterableProvider {
		Iterable<Work> getWorks();

		Iterable<Piece> getPieces(Work work);
	}

	private final IterableProvider iterableProvider;

	private final JLabel meanPerWork;
	private final JLabel stdevPerWork;
	private final JLabel meanPerPiece;
	private final JLabel stdevPerPiece;

	public StatisticsComputation(final IterableProvider iterableProvider, final JLabel meanPerWork,
			final JLabel stdevPerWork, final JLabel meanPerPiece, final JLabel stdevPerPiece) {
		this.iterableProvider = iterableProvider;

		this.meanPerWork = meanPerWork;
		this.stdevPerWork = stdevPerWork;
		this.meanPerPiece = meanPerPiece;
		this.stdevPerPiece = stdevPerPiece;
	}

	public void refresh() {
		long nw = 0;
		long tw = 0;
		long np = 0;
		long tp = 0;

		for (final Work work : iterableProvider.getWorks()) {
			nw++;
			long twp = 0;
			for (final Piece piece : iterableProvider.getPieces(work)) {
				final Long duration = piece.getDuration();
				if (duration != null) {
					twp += duration;
					tp += duration;
					np++;
				}
			}
			tw += twp;
		}

		final long mw = nw == 0 ? 0 : tw / nw;
		final long mp = np == 0 ? 0 : tp / np;

		long sw = 0;
		long sp = 0;

		for (final Work work : iterableProvider.getWorks()) {
			long twp = 0;
			for (final Piece piece : iterableProvider.getPieces(work)) {
				final Long duration = piece.getDuration();
				if (duration != null) {
					twp += duration;
					final long dp = mp - duration;
					sp += dp * dp;
				}
			}
			final long dp = mw - twp;
			sw += dp * dp;
		}

		sw = Math.round(Math.sqrt(sw));
		sp = Math.round(Math.sqrt(sp));

		if (meanPerWork != null) {
			meanPerWork.setText(Converters.formatTime(mw));
		}
		if (stdevPerWork != null) {
			stdevPerWork.setText(Converters.formatTime(sw));
		}
		if (meanPerPiece != null) {
			meanPerPiece.setText(Converters.formatTime(mp));
		}
		if (stdevPerPiece != null) {
			stdevPerPiece.setText(Converters.formatTime(sp));
		}
	}

}
