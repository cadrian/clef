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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.tools.Converters;

public class StatisticsComputation {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsComputation.class);

	public interface IterableProvider {
		Iterable<Work> getWorks();

		Iterable<Piece> getPieces(Work work);

		Iterable<Session> getSessions(Piece piece);
	}

	private final IterableProvider iterableProvider;

	private final JLabel totalDuration;
	private final JLabel totalWorkTime;
	private final JLabel meanPerWork;
	private final JLabel stdevPerWork;
	private final JLabel meanPerPiece;
	private final JLabel stdevPerPiece;

	public StatisticsComputation(final IterableProvider iterableProvider, final JLabel totalDuration,
			final JLabel totalWorkTime, final JLabel meanPerWork, final JLabel stdevPerWork, final JLabel meanPerPiece,
			final JLabel stdevPerPiece) {
		this.iterableProvider = iterableProvider;

		this.totalDuration = totalDuration;
		this.totalWorkTime = totalWorkTime;
		this.meanPerWork = meanPerWork;
		this.stdevPerWork = stdevPerWork;
		this.meanPerPiece = meanPerPiece;
		this.stdevPerPiece = stdevPerPiece;
	}

	public void refresh() {
		double dw = 0; // duration of works (in seconds)
		long tw = 0; // work time for works
		double dp = 0; // duration of pieces (in seconds)
		long tp = 0; // work time for pieces

		for (final Work work : iterableProvider.getWorks()) {
			long tws = 0;
			double dw0 = 0;
			for (final Piece piece : iterableProvider.getPieces(work)) {
				long tps = 0;
				for (final Session session : iterableProvider.getSessions(piece)) {
					final long t = (session.getStop().getTime() - session.getStart().getTime()) / 1000;
					LOGGER.debug("work time for session {}: {} ({})", session.toString(), t, Converters.formatTime(t));
					tps += t;
				}
				LOGGER.debug("work time for piece {}: {} ({})", piece.getName(), tps, Converters.formatTime(tps));
				tp += tps;
				tws += tps;
				final Long dpL = piece.getDuration();
				if (dpL != null && dpL > 0) {
					final double dp0 = dpL.doubleValue();
					dw0 += dp0;
					dp += dp0;
				}
			}
			LOGGER.debug("work time for work {}: {} ({})", work.getName(), tws, Converters.formatTime(tws));
			tw += tws;
			dw += dw0;
		}

		LOGGER.debug("total work time for pieces: {} ({})", tp, Converters.formatTime(tp));
		LOGGER.debug("total work time for works:  {} ({})", tw, Converters.formatTime(tw));
		LOGGER.debug("total duration for pieces: {} ({})", dp, Converters.formatTime(Math.round(dp)));
		LOGGER.debug("total duration for works:  {} ({})", dw, Converters.formatTime(Math.round(dw)));

		final double mw = dw == 0 ? 0 : tw / dw; // mean work per minute for works
		final double mp = dp == 0 ? 0 : tp / dp; // mean work per minute for pieces

		LOGGER.debug("mean work time for pieces: {} ({})", mw, Converters.formatTime(Math.round(mp)));
		LOGGER.debug("mean work time for works:  {} ({})", mw, Converters.formatTime(Math.round(mw)));

		double sw = 0; // std deviation of work per minute for works
		double sp = 0; // std deviation of work per minute for pieces

		for (final Work work : iterableProvider.getWorks()) {
			long tws = 0;
			double dw0 = 0;
			for (final Piece piece : iterableProvider.getPieces(work)) {
				long tps = 0;
				for (final Session session : iterableProvider.getSessions(piece)) {
					final long t = (session.getStop().getTime() - session.getStart().getTime()) / 1000;
					tps += t;
				}
				tws += tps;
				final Long dpL = piece.getDuration();
				if (dpL != null && dpL > 0) {
					final double dp0 = dpL.doubleValue();
					dw0 += dp0;
					final double ddp = mp - tps * 60 / dp0;
					sp += ddp * ddp;
				}
			}
			final double ddw = mw - tws * 60 / dw0;
			sw += ddw * ddw;
		}

		final long dwL = Math.round(dw);
		final long mwL = Math.round(mw);
		final long mpL = Math.round(mp);
		final long swL = Math.round(Math.sqrt(sw));
		final long spL = Math.round(Math.sqrt(sp));

		if (totalDuration != null) {
			totalDuration.setText(Converters.formatTime(dwL).toString());
		}
		if (totalWorkTime != null) {
			totalWorkTime.setText(Converters.formatTime(tw).toString());
		}
		if (meanPerWork != null) {
			meanPerWork.setText(Converters.formatTime(mwL).toString());
		}
		if (stdevPerWork != null) {
			stdevPerWork.setText(Converters.formatTime(swL).toString());
		}
		if (meanPerPiece != null) {
			meanPerPiece.setText(Converters.formatTime(mpL).toString());
		}
		if (stdevPerPiece != null) {
			stdevPerPiece.setText(Converters.formatTime(spL).toString());
		}
	}

}
