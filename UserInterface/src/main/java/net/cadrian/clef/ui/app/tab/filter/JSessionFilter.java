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
package net.cadrian.clef.ui.app.tab.filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.BeanComparators;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.tools.SortableListModel;
import net.cadrian.clef.ui.widget.ClefTools;

public class JSessionFilter extends JBeanFilter<Session> {

	private final class WorksListSelectionListener implements ListSelectionListener {
		private final ApplicationContext context;
		private final SortableListModel<Piece> piecesModel;

		private WorksListSelectionListener(ApplicationContext context, SortableListModel<Piece> piecesModel) {
			this.context = context;
			this.piecesModel = piecesModel;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				final Work work = works.getSelectedValue();
				if (work == null) {
					piecesModel.removeAll();
				} else {
					if (context.<Boolean>getValue(AdvancedConfigurationEntry.offlineMode)) {
						final List<Piece> workPieces = new ArrayList<>();
						for (Piece piece : work.getPieces()) {
							do {
								workPieces.add(piece);
								piece = piece.getPrevious();
							} while (piece != null);
						}
						piecesModel.replaceAll(workPieces);
					} else {
						piecesModel.replaceAll(work.getPieces());
					}
				}
			}
		}
	}

	private final class AuthorsListSelectionListener implements ListSelectionListener {
		private final SortableListModel<Work> worksModel;
		private final Collection<? extends Work> allWorks;
		private final ApplicationContext context;

		private AuthorsListSelectionListener(SortableListModel<Work> worksModel, Collection<? extends Work> allWorks,
				ApplicationContext context) {
			this.worksModel = worksModel;
			this.allWorks = allWorks;
			this.context = context;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				final Author author = authors.getSelectedValue();
				if (author == null) {
					worksModel.replaceAll(allWorks);
				} else {
					final List<Work> authorWorks = new ArrayList<>();
					for (final Work work : context.getBeans().getWorks()) {
						if (author.equals(work.getAuthor())) {
							authorWorks.add(work);
						}
					}
					worksModel.replaceAll(authorWorks);
				}
			}
		}
	}

	private final class ClefToolsListenerImpl implements ClefTools.Listener {
		@Override
		public void toolCalled(final ClefTools tools, final ClefTools.Tool tool) {
			switch (tool) {
			case Del:
				pieces.clearSelection();
				works.clearSelection();
				authors.clearSelection();
				break;
			case Filter:
				final ActionEvent e = new ActionEvent(JSessionFilter.this, ActionEvent.ACTION_PERFORMED, tool.name());
				fireActionPerformed(e);
				break;
			default:
			}
		}
	}

	private static final long serialVersionUID = 6199756832553677405L;

	private final JList<Author> authors;
	private final JList<Work> works;
	private final JList<Piece> pieces;

	public JSessionFilter(final ApplicationContext context) {
		super(new BorderLayout());

		final Beans beans = context.getBeans();
		final Presentation presentation = context.getPresentation();

		final Collection<? extends Author> allAuthors = beans.getAuthors();
		final SortableListModel<Author> authorsModel = new SortableListModel<>(BeanComparators::compareAuthors,
				allAuthors);
		authors = new JList<>(authorsModel);

		final JPanel authorsPanel = new JPanel(new BorderLayout());
		authorsPanel.add(
				presentation
						.bold(new JLabel(presentation.getMessage("SessionFilterAuthorsTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		authorsPanel.add(new JScrollPane(authors), BorderLayout.CENTER);

		final Collection<? extends Work> allWorks = context.getBeans().getWorks();
		final SortableListModel<Work> worksModel = new SortableListModel<>(BeanComparators::compareWorks, allWorks);
		works = new JList<>(worksModel);

		final JPanel worksPanel = new JPanel(new BorderLayout());
		worksPanel.add(
				presentation
						.bold(new JLabel(presentation.getMessage("SessionFilterWorksTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		worksPanel.add(new JScrollPane(works), BorderLayout.CENTER);

		final SortableListModel<Piece> piecesModel = new SortableListModel<>(BeanComparators::comparePieces);
		pieces = new JList<>(piecesModel);
		final JPanel piecesPanel = new JPanel(new BorderLayout());
		piecesPanel.add(
				presentation
						.bold(new JLabel(presentation.getMessage("SessionFilterPiecesTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		piecesPanel.add(new JScrollPane(pieces), BorderLayout.CENTER);

		authors.addListSelectionListener(new AuthorsListSelectionListener(worksModel, allWorks, context));
		works.addListSelectionListener(new WorksListSelectionListener(context, piecesModel));

		final JPanel lists = new JPanel();
		lists.setLayout(new BoxLayout(lists, BoxLayout.X_AXIS));
		lists.add(authorsPanel);
		lists.add(worksPanel);
		lists.add(piecesPanel);

		final ClefTools tools = new ClefTools(context, ClefTools.Tool.Del, ClefTools.Tool.Filter);
		tools.addListener(new ClefToolsListenerImpl());

		add(lists, BorderLayout.CENTER);
		add(tools, BorderLayout.NORTH);

		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

		setSize(getLayout().preferredLayoutSize(this));
	}

	@Override
	public boolean isBeanVisible(final Session bean) {
		if (!isVisible(bean.getPiece(), pieces.getSelectedValuesList())) {
			return false;
		}
		if (!isVisible(bean.getPiece().getWork(), works.getSelectedValuesList())) {
			return false;
		}
		if (!isVisible(bean.getPiece().getWork().getAuthor(), authors.getSelectedValuesList())) {
			return false;
		}
		return true;
	}

	private static <T> boolean isVisible(final T value, final List<T> values) {
		final boolean result;
		if (values.isEmpty()) {
			// nothing selected, as if everything was selected
			result = true;
		} else {
			result = values.contains(value);
		}
		return result;
	}

}
