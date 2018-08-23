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
package net.cadrian.clef.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Work;

class WorkCreator implements BeanCreator<Work> {

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

		final SortableListModel<Author> authorsModel = new SortableListModel<>(
				(a1, a2) -> BeanComparators.compareAuthors(a1, a2), allAuthors);

		final SortableListModel<Pricing> pricingsModel = new SortableListModel<>(
				(p1, p2) -> BeanComparators.comparePricings(p1, p2), allPricings);

		final JDialog params = new JDialog(parent, presentation.getMessage("WorkCreatorTitle"), true);

		final JPanel paramsContent = new JPanel(new BorderLayout());
		params.getContentPane().add(paramsContent);
		paramsContent.add(new JLabel(presentation.getMessage("WorkCreatorMessage")), BorderLayout.NORTH);

		final JList<Author> authors = new JList<>(authorsModel);
		final JPanel authorsPanel = new JPanel(new BorderLayout());
		authorsPanel.add(presentation.bold(new JLabel(presentation.getMessage("WorkCreatorAuthorsTitle"))),
				BorderLayout.NORTH);
		authorsPanel.add(new JScrollPane(authors), BorderLayout.CENTER);

		final JList<Pricing> pricings = new JList<>(pricingsModel);
		final JPanel pricingsPanel = new JPanel(new BorderLayout());
		pricingsPanel.add(presentation.bold(new JLabel(presentation.getMessage("WorkCreatorPricingsTitle"))),
				BorderLayout.NORTH);
		pricingsPanel.add(new JScrollPane(pricings), BorderLayout.CENTER);

		final JPanel lists = new JPanel();
		lists.setLayout(new BoxLayout(lists, BoxLayout.X_AXIS));
		lists.add(authorsPanel);
		lists.add(pricingsPanel);

		paramsContent.add(lists, BorderLayout.CENTER);

		final AtomicBoolean saved = new AtomicBoolean(false);
		final Action addAction = new AbstractAction("Add") {
			private static final long serialVersionUID = -8659808353683696964L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Author author = authors.getSelectedValue();
				final Pricing pricing = pricings.getSelectedValue();
				if (author != null && pricing != null) {
					saved.set(true);
					params.setVisible(false);
				}
			}
		};
		addAction.setEnabled(false);

		final AtomicBoolean authorsSelected = new AtomicBoolean(false);
		final AtomicBoolean pricingsSelected = new AtomicBoolean(false);
		authors.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					authorsSelected.set(true);
					addAction.setEnabled(pricingsSelected.get());
					saved.set(false);
				}
			}
		});
		pricings.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					pricingsSelected.set(true);
					addAction.setEnabled(authorsSelected.get());
					saved.set(false);
				}
			}
		});

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);
		buttons.add(addAction);
		paramsContent.add(presentation.awesome(buttons), BorderLayout.SOUTH);

		params.pack();
		params.setLocationRelativeTo(parent);
		LOGGER.debug("Showing WorkCreator dialog");
		params.setVisible(true);

		final Author author = authors.getSelectedValue();
		final Pricing pricing = pricings.getSelectedValue();

		if (!saved.get()) {
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
