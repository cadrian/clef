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
package net.cadrian.clef.ui.app.tab;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.tools.Converters;
import net.cadrian.clef.ui.ApplicationContext;

public class StatisticsPanel extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsPanel.class);

	private static final long serialVersionUID = -2023860576290261246L;

	private final ApplicationContext context;

	private final JLabel meanPerWork;
	private final JLabel stdevPerWork;
	private final JLabel meanPerPiece;
	private final JLabel stdevPerPiece;

	public StatisticsPanel(final ApplicationContext context) {
		super(new GridBagLayout());
		this.context = context;

		final JPanel titledPanel = new JPanel(new BorderLayout());
		final JPanel panel = new JPanel(new GridBagLayout());

		meanPerWork = new JLabel();
		stdevPerWork = new JLabel();
		meanPerPiece = new JLabel();
		stdevPerPiece = new JLabel();
		final Map<String, JLabel> labels = new LinkedHashMap<>();
		labels.put("MeanPerWork", meanPerWork);
		labels.put("StdDeviationPerWork", stdevPerWork);
		labels.put("MeanPerPiece", meanPerPiece);
		labels.put("StdDeviationPerPiece", stdevPerPiece);
		addLabels(labels, panel);

		panel.setBorder(BorderFactory.createEtchedBorder());

		titledPanel.add(new JLabel(context.getPresentation().getMessage("StatisticsMessage")), BorderLayout.NORTH);
		titledPanel.add(panel, BorderLayout.CENTER);

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		add(titledPanel, constraints);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(final ComponentEvent e) {
				refresh();
			}
		});

		refresh();
	}

	private void addLabels(final Map<String, JLabel> labels, final JPanel panel) {
		int gridy = 0;
		for (final Map.Entry<String, JLabel> entry : labels.entrySet()) {
			final GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.gridy = gridy;
			labelConstraints.anchor = GridBagConstraints.WEST;
			labelConstraints.insets = new Insets(4, 2, 4, 5);
			final String label = context.getPresentation().getMessage("Statistics." + entry.getKey());
			LOGGER.debug("Label for {} is {}", entry.getKey(), label);
			panel.add(context.getPresentation().bold(new JLabel(label)), labelConstraints);

			final GridBagConstraints fieldConstraints = new GridBagConstraints();
			fieldConstraints.gridx = 1;
			fieldConstraints.gridy = gridy;
			fieldConstraints.weightx = 1;
			fieldConstraints.insets = new Insets(4, 5, 4, 2);
			fieldConstraints.anchor = GridBagConstraints.WEST;
			panel.add(entry.getValue(), fieldConstraints);

			gridy++;
		}
	}

	private void refresh() {
		long nw = 0;
		long tw = 0;
		long np = 0;
		long tp = 0;

		final Beans beans = context.getBeans();

		for (final Work work : beans.getWorks()) {
			nw++;
			long twp = 0;
			for (final Piece piece : work.getPieces()) {
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

		for (final Work work : beans.getWorks()) {
			long twp = 0;
			for (final Piece piece : work.getPieces()) {
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

		meanPerWork.setText(Converters.formatTime(mw));
		stdevPerWork.setText(Converters.formatTime(sw));
		meanPerPiece.setText(Converters.formatTime(mp));
		stdevPerPiece.setText(Converters.formatTime(sp));
	}

}
