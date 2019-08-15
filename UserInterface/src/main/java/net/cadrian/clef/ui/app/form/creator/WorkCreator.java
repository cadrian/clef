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
import javax.swing.JFrame;
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

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.BeanComparators;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.app.form.BeanCreator;
import net.cadrian.clef.ui.tools.SortableListModel;
import net.cadrian.clef.ui.widget.ClefTools;

public class WorkCreator implements BeanCreator<Work> {

	private final class PricingsListSelectionListener implements ListSelectionListener {
		private final ClefTools tools;
		private final AtomicBoolean added;
		private final AtomicBoolean pricingsSelected;
		private final AtomicBoolean authorsSelected;

		private PricingsListSelectionListener(final ClefTools tools, final AtomicBoolean added,
				final AtomicBoolean pricingsSelected, final AtomicBoolean authorsSelected) {
			this.tools = tools;
			this.added = added;
			this.pricingsSelected = pricingsSelected;
			this.authorsSelected = authorsSelected;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				pricingsSelected.set(true);
				tools.getAction(ClefTools.Tool.Add).setEnabled(authorsSelected.get());
				added.set(false);
			}
		}
	}

	private final class AuthorsListSelectionListener implements ListSelectionListener {
		private final ClefTools tools;
		private final AtomicBoolean pricingsSelected;
		private final AtomicBoolean added;
		private final AtomicBoolean authorsSelected;

		private AuthorsListSelectionListener(final ClefTools tools, final AtomicBoolean pricingsSelected,
				final AtomicBoolean added, final AtomicBoolean authorsSelected) {
			this.tools = tools;
			this.pricingsSelected = pricingsSelected;
			this.added = added;
			this.authorsSelected = authorsSelected;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				authorsSelected.set(true);
				tools.getAction(ClefTools.Tool.Add).setEnabled(pricingsSelected.get());
				added.set(false);
			}
		}
	}

	private final class ClefToolsListenerImpl implements ClefTools.Listener {
		private final JList<Pricing> pricings;
		private final JDialog params;
		private final AtomicBoolean added;
		private final JList<Author> authors;

		private ClefToolsListenerImpl(final JList<Pricing> pricings, final JDialog params, final AtomicBoolean added,
				final JList<Author> authors) {
			this.pricings = pricings;
			this.params = params;
			this.added = added;
			this.authors = authors;
		}

		@Override
		public void toolCalled(final ClefTools tools, final ClefTools.Tool tool) {
			switch (tool) {
			case Add:
				final Author author = authors.getSelectedValue();
				final Pricing pricing = pricings.getSelectedValue();
				if (author != null && pricing != null) {
					added.set(true);
					params.setVisible(false);
				}
				break;
			default:
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkCreator.class);

	private final ApplicationContext context;

	public WorkCreator(final ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Work createBean() {
		final Beans beans = context.getBeans();
		final Presentation presentation = context.getPresentation();
		final JFrame parent = presentation.getApplicationFrame();
		final Collection<? extends Author> allAuthors = beans.getAuthors();
		if (allAuthors.isEmpty()) {
			JOptionPane.showMessageDialog(parent, presentation.getMessage("WorkCreatorNoAuthorsMessage"),
					presentation.getMessage("WorkCreatorNoAuthorsTitle"), JOptionPane.WARNING_MESSAGE);
			return null;
		}

		final Collection<? extends Pricing> allPricings = beans.getPricings();
		if (allPricings.isEmpty()) {
			JOptionPane.showMessageDialog(parent, presentation.getMessage("WorkCreatorNoPricingsMessage"),
					presentation.getMessage("WorkCreatorNoPricingsTitle"), JOptionPane.WARNING_MESSAGE);
			return null;
		}

		final SortableListModel<Author> authorsModel = new SortableListModel<>(BeanComparators::compareAuthors,
				allAuthors);
		final SortableListModel<Pricing> pricingsModel = new SortableListModel<>(BeanComparators::comparePricings,
				allPricings);

		final JDialog params = new JDialog(parent, presentation.getMessage("WorkCreatorTitle"), true);

		final JPanel paramsContent = new JPanel(new BorderLayout());
		params.getContentPane().add(paramsContent);
		paramsContent.add(new JLabel(presentation.getMessage("WorkCreatorMessage")), BorderLayout.NORTH);

		final JList<Author> authors = new JList<>(authorsModel);
		final JPanel authorsPanel = new JPanel(new BorderLayout());
		authorsPanel.add(
				presentation
						.bold(new JLabel(presentation.getMessage("WorkCreatorAuthorsTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		authorsPanel.add(new JScrollPane(authors), BorderLayout.CENTER);

		final JList<Pricing> pricings = new JList<>(pricingsModel);
		final JPanel pricingsPanel = new JPanel(new BorderLayout());
		pricingsPanel.add(
				presentation
						.bold(new JLabel(presentation.getMessage("WorkCreatorPricingsTitle"), SwingConstants.CENTER)),
				BorderLayout.NORTH);
		pricingsPanel.add(new JScrollPane(pricings), BorderLayout.CENTER);

		final JPanel lists = new JPanel();
		lists.setLayout(new BoxLayout(lists, BoxLayout.X_AXIS));
		lists.add(authorsPanel);
		lists.add(pricingsPanel);

		paramsContent.add(lists, BorderLayout.CENTER);

		final ClefTools tools = new ClefTools(context, ClefTools.Tool.Add);

		final AtomicBoolean added = new AtomicBoolean(false);
		final AtomicBoolean authorsSelected = new AtomicBoolean(false);
		final AtomicBoolean pricingsSelected = new AtomicBoolean(false);

		tools.addListener(new ClefToolsListenerImpl(pricings, params, added, authors));

		tools.getAction(ClefTools.Tool.Add).setEnabled(false);

		authors.addListSelectionListener(
				new AuthorsListSelectionListener(tools, pricingsSelected, added, authorsSelected));
		pricings.addListSelectionListener(
				new PricingsListSelectionListener(tools, added, pricingsSelected, authorsSelected));

		paramsContent.add(tools, BorderLayout.NORTH);

		params.pack();
		params.setLocationRelativeTo(parent);
		LOGGER.debug("Showing WorkCreator dialog");
		params.setVisible(true);

		final Author author = authors.getSelectedValue();
		final Pricing pricing = pricings.getSelectedValue();

		if (!added.get()) {
			LOGGER.debug("Not saved, not creating work");
			return null;
		}
		if (author == null || pricing == null) {
			LOGGER.debug("Missing data, not creating work");
			return null;
		}

		LOGGER.info("Creating new Work with author: {} and pricing: {}", author, pricing);
		return beans.createWork(author, pricing);
	}
}
