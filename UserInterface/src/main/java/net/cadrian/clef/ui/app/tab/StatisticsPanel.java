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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.tools.StatisticsComputation;

public class StatisticsPanel extends JPanel {

	private final class AllPiecesIterableProvider implements StatisticsComputation.IterableProvider {
		private final ApplicationContext context;

		private AllPiecesIterableProvider(final ApplicationContext context) {
			this.context = context;
		}

		@Override
		public Iterable<Work> getWorks() {
			return new ArrayList<>(context.getBeans().getWorks());
		}

		@Override
		public Iterable<Piece> getPieces(final Work work) {
			return new ArrayList<>(work.getPieces());
		}

		@Override
		public Iterable<Session> getSessions(final Piece piece) {
			return new ArrayList<>(piece.getSessions());
		}
	}

	private final class RefreshComponentListener extends ComponentAdapter {
		@Override
		public void componentShown(final ComponentEvent e) {
			computation.refresh();
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsPanel.class);

	private static final long serialVersionUID = -2023860576290261246L;

	private final StatisticsComputation computation;

	public StatisticsPanel(final ApplicationContext context) {
		super(new GridBagLayout());

		final JPanel titledPanel = new JPanel(new BorderLayout());
		final JPanel panel = new JPanel(new GridBagLayout());

		final JLabel meanPerWork = new JLabel();
		final JLabel stdevPerWork = new JLabel();
		final JLabel meanPerPiece = new JLabel();
		final JLabel stdevPerPiece = new JLabel();
		final Map<String, JLabel> labels = new LinkedHashMap<>();
		labels.put("MeanPerWork", meanPerWork);
		labels.put("StdDeviationPerWork", stdevPerWork);
		labels.put("MeanPerPiece", meanPerPiece);
		labels.put("StdDeviationPerPiece", stdevPerPiece);
		addLabels(context, labels, panel);

		panel.setBorder(BorderFactory.createEtchedBorder());

		titledPanel.add(new JLabel(context.getPresentation().getMessage("StatisticsMessage")), BorderLayout.NORTH);
		titledPanel.add(panel, BorderLayout.CENTER);

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		add(titledPanel, constraints);

		addComponentListener(new RefreshComponentListener());

		computation = new StatisticsComputation(new AllPiecesIterableProvider(context), meanPerWork, stdevPerWork,
				meanPerPiece, stdevPerPiece);

		computation.refresh();
	}

	private void addLabels(final ApplicationContext context, final Map<String, JLabel> labels, final JPanel panel) {
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

	public void refresh() {
		computation.refresh();
	}

}
