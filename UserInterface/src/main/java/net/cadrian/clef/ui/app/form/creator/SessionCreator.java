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
package net.cadrian.clef.ui.app.form.creator;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.bean.BeanComparators;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.app.form.BeanCreator;
import net.cadrian.clef.ui.tools.SortableListModel;
import net.cadrian.clef.ui.widget.ClefTools;

public class SessionCreator implements BeanCreator<Session> {

	private final class ClefToolsListenerImpl implements ClefTools.Listener {
		private final JList<Piece> pieces;
		private final JDialog params;
		private final AtomicBoolean added;
		private final JList<Work> works;

		private ClefToolsListenerImpl(JList<Piece> pieces, JDialog params, AtomicBoolean added, JList<Work> works) {
			this.pieces = pieces;
			this.params = params;
			this.added = added;
			this.works = works;
		}

		@Override
		public void toolCalled(final ClefTools tools, final ClefTools.Tool tool) {
			switch (tool) {
			case Add:
				final Work work = works.getSelectedValue();
				final Piece piece = pieces.getSelectedValue();
				if (work != null && piece != null) {
					added.set(true);
					params.setVisible(false);
				}
				break;
			default:
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionCreator.class);

	private final ApplicationContext context;

	public SessionCreator(final ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Session createBean() {
		final Presentation presentation = context.getPresentation();

		final Collection<? extends Work> allWorks = context.getBeans().getWorks();
		if (allWorks.isEmpty()) {
			JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
					presentation.getMessage("SessionCreatorNoWorksMessage"),
					presentation.getMessage("SessionCreatorNoWorksTitle"), JOptionPane.WARNING_MESSAGE);
			return null;
		}
		boolean foundPieces = false;
		for (final Work work : allWorks) {
			if (!work.getPieces().isEmpty()) {
				foundPieces = true;
				break;
			}
		}
		if (!foundPieces) {
			JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
					presentation.getMessage("SessionCreatorNoPiecesMessage"),
					presentation.getMessage("SessionCreatorNoPiecesTitle"), JOptionPane.WARNING_MESSAGE);
			return null;
		}

		final SortableListModel<Work> worksModel = new SortableListModel<>(BeanComparators::compareWorks, allWorks);
		final SortableListModel<Piece> piecesModel = new SortableListModel<>(BeanComparators::comparePieces);

		final JDialog params = new JDialog(presentation.getApplicationFrame(),
				presentation.getMessage("SessionCreatorTitle"), true);

		final JPanel paramsContent = new JPanel(new BorderLayout());
		params.getContentPane().add(paramsContent);
		paramsContent.add(new JLabel(presentation.getMessage("SessionCreatorMessage")), BorderLayout.NORTH);

		final JList<Work> works = new JList<>(worksModel);
		final JPanel worksPanel = new JPanel(new BorderLayout());
		worksPanel.add(
				presentation
						.bold(new JLabel(presentation.getMessage("SessionCreatorWorksTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		worksPanel.add(new JScrollPane(works), BorderLayout.CENTER);
		works.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					// TODO swing niceties using an async worker
					piecesModel.removeAll();
					for (final Piece piece : works.getSelectedValue().getPieces()) {
						piecesModel.add(piece);
						if (context.<Boolean>getValue(ApplicationContext.AdvancedConfigurationEntry.offlineMode)) {
							Piece p = piece.getPrevious();
							while (p != null) {
								piecesModel.add(p);
								p = p.getPrevious();
							}
						}
					}
				}
			}
		});

		final JList<Piece> pieces = new JList<>(piecesModel);
		final JPanel piecesPanel = new JPanel(new BorderLayout());
		piecesPanel.add(
				presentation
						.bold(new JLabel(presentation.getMessage("SessionCreatorPiecesTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		piecesPanel.add(new JScrollPane(pieces), BorderLayout.CENTER);

		final JPanel lists = new JPanel();
		lists.setLayout(new BoxLayout(lists, BoxLayout.X_AXIS));
		lists.add(worksPanel);
		lists.add(piecesPanel);

		paramsContent.add(lists, BorderLayout.CENTER);

		final ClefTools tools = new ClefTools(context, ClefTools.Tool.Add);

		final AtomicBoolean added = new AtomicBoolean(false);
		final AtomicBoolean worksSelected = new AtomicBoolean(false);
		final AtomicBoolean piecesSelected = new AtomicBoolean(false);

		tools.addListener(new ClefToolsListenerImpl(pieces, params, added, works));
		tools.getAction(ClefTools.Tool.Add).setEnabled(false);

		works.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					worksSelected.set(true);
					tools.getAction(ClefTools.Tool.Add).setEnabled(piecesSelected.get());
					added.set(false);
				}
			}
		});
		pieces.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					piecesSelected.set(true);
					tools.getAction(ClefTools.Tool.Add).setEnabled(worksSelected.get());
					added.set(false);
				}
			}
		});

		paramsContent.add(tools, BorderLayout.NORTH);

		params.pack();
		params.setLocationRelativeTo(presentation.getApplicationFrame());
		LOGGER.debug("Showing SessionCreator dialog");
		params.setVisible(true);

		final Piece piece = pieces.getSelectedValue();

		if (!added.get()) {
			LOGGER.debug("Not added, not creating session");
			return null;
		}
		if (piece == null) {
			LOGGER.debug("Missing data, not creating session");
			return null;
		}

		return context.getBeans().createSession(piece);
	}
}
