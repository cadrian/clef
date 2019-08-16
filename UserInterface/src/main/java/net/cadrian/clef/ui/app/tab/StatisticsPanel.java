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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.bean.Activity;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.tools.StatisticsComputation;

public class StatisticsPanel extends JPanel {

	private final class ActivitiesActionListener implements ActionListener {
		private final ActivitiesComboBoxModel model;

		private ActivitiesActionListener(final ActivitiesComboBoxModel model) {
			this.model = model;
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			iterableProvider.setActivity(model.getSelectedActivity());
			computation.refresh();
		}
	}

	private final class ActivityIterableProvider implements StatisticsComputation.IterableProvider {
		private final ApplicationContext context;
		private Activity activity;

		private ActivityIterableProvider(final ApplicationContext context) {
			this.context = context;
		}

		public void setActivity(final Activity activity) {
			this.activity = activity;
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
			final Collection<? extends Session> sessions = piece.getSessions();
			final List<Session> result = new ArrayList<>(sessions.size());
			for (final Session session : sessions) {
				final Activity sessionActivity = session.getActivity();
				if (activity == null || activity.equals(sessionActivity)) {
					result.add(session);
				}
			}
			return result;
		}
	}

	private final class RefreshComponentListener extends ComponentAdapter {
		@Override
		public void componentShown(final ComponentEvent e) {
			computation.refresh();
		}
	}

	private static final class ActivitiesComboBoxModel implements ComboBoxModel<String> {

		private final List<ListDataListener> listeners = new ArrayList<>();

		private final List<Activity> activities;
		private final List<String> activityNames;

		private Object selectedItem;
		private Activity selectedActivity;

		ActivitiesComboBoxModel(final ApplicationContext context) {
			activities = new ArrayList<>(context.getBeans().getActivities());
			activityNames = new ArrayList<>();
			activityNames.add(context.getPresentation().getMessage("AllActivities"));
			for (final Activity activity : activities) {
				activityNames.add(activity.getName());
			}
		}

		@Override
		public void addListDataListener(final ListDataListener listener) {
			listeners.add(listener);
		}

		@Override
		public String getElementAt(final int index) {
			return activityNames.get(index);
		}

		@Override
		public int getSize() {
			return activityNames.size();
		}

		@Override
		public void removeListDataListener(final ListDataListener listener) {
			listeners.remove(listener);
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		@Override
		public void setSelectedItem(final Object item) {
			selectedItem = item;
			final int i = activityNames.indexOf(item);
			if (i == 0) {
				selectedActivity = null;
			} else {
				selectedActivity = activities.get(i - 1);
			}
		}

		public Activity getSelectedActivity() {
			return selectedActivity;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsPanel.class);

	private static final long serialVersionUID = -2023860576290261246L;

	private final ActivityIterableProvider iterableProvider;
	private final StatisticsComputation computation;

	public StatisticsPanel(final ApplicationContext context) {
		super(new GridBagLayout());

		final JPanel titledPanel = new JPanel(new BorderLayout());
		final JPanel titlePanel = new JPanel(new BorderLayout());
		final JPanel panel = new JPanel(new GridBagLayout());

		final ActivitiesComboBoxModel model = new ActivitiesComboBoxModel(context);
		final JComboBox<String> activitiesCombo = new JComboBox<>(model);
		activitiesCombo.addActionListener(new ActivitiesActionListener(model));

		final JLabel totalDuration = new JLabel();
		final JLabel totalWorkTime = new JLabel();
		final JLabel meanPerWork = new JLabel();
		final JLabel stdevPerWork = new JLabel();
		final JLabel meanPerPiece = new JLabel();
		final JLabel stdevPerPiece = new JLabel();
		final Map<String, JLabel> labels = new LinkedHashMap<>();
		labels.put("TotalDuration", totalDuration);
		labels.put("TotalWorkTime", totalWorkTime);
		labels.put("MeanPerWork", meanPerWork);
		labels.put("StdDeviationPerWork", stdevPerWork);
		labels.put("MeanPerPiece", meanPerPiece);
		labels.put("StdDeviationPerPiece", stdevPerPiece);
		addLabels(context, labels, panel);

		panel.setBorder(BorderFactory.createEtchedBorder());

		titlePanel.add(new JLabel(context.getPresentation().getMessage("StatisticsMessage")), BorderLayout.NORTH);
		titlePanel.add(activitiesCombo, BorderLayout.SOUTH);

		titledPanel.add(titlePanel, BorderLayout.NORTH);
		titledPanel.add(panel, BorderLayout.CENTER);

		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		add(titledPanel, constraints);

		addComponentListener(new RefreshComponentListener());

		iterableProvider = new ActivityIterableProvider(context);
		computation = new StatisticsComputation(iterableProvider, totalDuration, totalWorkTime, meanPerWork,
				stdevPerWork, meanPerPiece, stdevPerPiece);

		computation.refresh();
		activitiesCombo.setSelectedIndex(0);
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
