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
package net.cadrian.clef.ui.app.form.field.stat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.tools.Converters;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.tools.StatisticsComputation;
import net.cadrian.clef.ui.tools.StatisticsComputation.IterableProvider;

class WorkStatisticsComponent extends JPanel {

	private static final long serialVersionUID = -1755628526357896602L;

	private final Work bean;

	private final JLabel totalPieces;
	private final JLabel totalDuration;
	private final JLabel totalWorkTime;

	private final JLabel totalLostPieces;
	private final JLabel lostDuration;
	private final JLabel lostWorkTime;

	private final JLabel meanWorkPerMinute;
	private final JLabel stdevWorkPerMinute;
	private final JLabel meanWorkPerMinuteWithLost;
	private final JLabel stdevWorkPerMinuteWithLost;

	private final StatisticsComputation totalComputation;
	private final StatisticsComputation lostComputation;

	WorkStatisticsComponent(final Work bean, final ApplicationContext context) {
		super(new GridBagLayout());

		this.bean = bean;

		totalPieces = new JLabel();
		totalDuration = new JLabel();
		totalWorkTime = new JLabel();

		totalLostPieces = new JLabel();
		lostDuration = new JLabel();
		lostWorkTime = new JLabel();

		meanWorkPerMinute = new JLabel();
		stdevWorkPerMinute = new JLabel();
		meanWorkPerMinuteWithLost = new JLabel();
		stdevWorkPerMinuteWithLost = new JLabel();

		final Color livePieces = new Color(0xd0ffe0);
		final Color lostPieces = new Color(0xffd0e0);

		int gridy = 0;
		addLabel(context, gridy++, "TotalPieces", totalPieces, livePieces);
		addLabel(context, gridy++, "TotalDuration", totalDuration, livePieces);
		addLabel(context, gridy++, "TotalWorkTime", totalWorkTime, livePieces);
		addLabel(context, gridy++, "TotalLostPieces", totalLostPieces, lostPieces);
		addLabel(context, gridy++, "LostDuration", lostDuration, lostPieces);
		addLabel(context, gridy++, "LostWorkTime", lostWorkTime, lostPieces);
		addLabel(context, gridy++, "MeanWorkPerMinute", meanWorkPerMinute, livePieces);
		addLabel(context, gridy++, "StdDeviationPerMinute", stdevWorkPerMinute, livePieces);
		addLabel(context, gridy++, "MeanWorkPerMinuteWithLost", meanWorkPerMinuteWithLost, lostPieces);
		addLabel(context, gridy++, "StdDeviationPerMinuteWithLost", stdevWorkPerMinuteWithLost, lostPieces);

		totalComputation = new StatisticsComputation(new IterableProvider() {

			@Override
			public Iterable<Work> getWorks() {
				return Arrays.asList(bean);
			}

			@Override
			public Iterable<Piece> getPieces(final Work work) {
				return WorkStatisticsComponent.this.getPieces(work);
			}
		}, null, null, meanWorkPerMinute, stdevWorkPerMinute);

		lostComputation = new StatisticsComputation(new IterableProvider() {

			@Override
			public Iterable<Work> getWorks() {
				return Arrays.asList(bean);
			}

			@Override
			public Iterable<Piece> getPieces(final Work work) {
				return getAllPieces(work);
			}
		}, null, null, meanWorkPerMinuteWithLost, stdevWorkPerMinuteWithLost);

		refresh();
	}

	private void addLabel(final ApplicationContext context, final int gridy, final String titleKey, final JLabel label,
			final Color color) {
		final JLabel title = context.getPresentation()
				.bold(new JLabel(context.getPresentation().getMessage("Statistics.Work." + titleKey)));

		final GridBagConstraints labelConstraints = new GridBagConstraints();
		labelConstraints.gridy = gridy;
		labelConstraints.anchor = GridBagConstraints.WEST;
		labelConstraints.ipady = 10;
		labelConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(title, labelConstraints);

		final GridBagConstraints fieldConstraints = new GridBagConstraints();
		fieldConstraints.gridx = 1;
		fieldConstraints.gridy = gridy;
		fieldConstraints.weightx = 1;
		fieldConstraints.ipady = 10;
		fieldConstraints.anchor = GridBagConstraints.WEST;
		fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		add(label, fieldConstraints);

		title.setBorder(new EmptyBorder(2, 12, 2, 0));
		label.setBorder(new EmptyBorder(2, 0, 2, 12));

		if (color != null) {
			title.setOpaque(true);
			title.setBackground(color);
			label.setOpaque(true);
			label.setBackground(color);
		}
	}

	void refresh() {
		final List<Piece> pieces = getPieces(bean);
		final List<Piece> allPieces = getAllPieces(bean);

		long duration = 0;
		long workTime = 0;
		long allDuration = 0;
		long allWorkTime = 0;

		for (final Piece piece : pieces) {
			duration += piece.getDuration();
			for (final Session session : piece.getSessions()) {
				final Date start = session.getStart();
				final Date stop = session.getStop();
				if (start != null && stop != null) {
					long wt = stop.getTime() - start.getTime();
					if (wt > 0) {
						workTime += wt;
					}
				}
			}
		}

		for (final Piece piece : allPieces) {
			allDuration += piece.getDuration();
			for (final Session session : piece.getSessions()) {
				final Date start = session.getStart();
				final Date stop = session.getStop();
				if (start != null && stop != null) {
					long wt = stop.getTime() - start.getTime();
					if (wt > 0) {
						allWorkTime += wt;
					}
				}
			}
		}

		workTime /= 1000L;
		allWorkTime /= 1000L;

		totalPieces.setText(Integer.toString(pieces.size()));
		totalDuration.setText(Converters.formatTime(duration));
		totalWorkTime.setText(Converters.formatTime(workTime));

		totalLostPieces.setText(Integer.toString(allPieces.size() - pieces.size()));
		lostDuration.setText(Converters.formatTime(allDuration - duration));
		lostWorkTime.setText(Converters.formatTime(allWorkTime - workTime));

		totalComputation.refresh();
		lostComputation.refresh();
	}

	private List<Piece> getPieces(final Work work) {
		return new ArrayList<>(work.getPieces());
	}

	private List<Piece> getAllPieces(final Work work) {
		final List<Piece> result = new ArrayList<>();
		for (Piece piece : work.getPieces()) {
			do {
				result.add(piece);
				piece = piece.getPrevious();
			} while (piece != null);
		}
		return result;
	}

}
