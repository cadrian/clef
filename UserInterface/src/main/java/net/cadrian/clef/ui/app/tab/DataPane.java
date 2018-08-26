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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.app.form.BeanCreator;
import net.cadrian.clef.ui.app.form.BeanForm;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.BeanGetter;
import net.cadrian.clef.ui.tools.SortableListModel;

public class DataPane<T extends Bean> extends JSplitPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataPane.class);

	private static final long serialVersionUID = -6198568152980667836L;

	private final SortableListModel<T> model;
	private final JList<T> list;
	private final JPanel current = new JPanel(new BorderLayout());

	private final Action addAction;
	private final Action delAction;
	private final Action saveAction;

	private final BeanGetter<T> beanGetter;
	private final BeanCreator<T> beanCreator;
	private final ApplicationContext context;
	private final BeanFormModel<T> beanFormModel;
	private final List<String> tabs;

	private final Map<T, BeanForm<T>> formCache = new WeakHashMap<>();

	private BeanForm<T> currentForm;

	public DataPane(final ApplicationContext context, final boolean showSave, final Class<T> beanType,
			final BeanGetter<T> beanGetter, final BeanCreator<T> beanCreator, final Comparator<T> beanComparator,
			final BeanFormModel<T> beanFormModel) {
		this(context, showSave, beanType, beanGetter, beanCreator, beanComparator, beanFormModel, null);
	}

	public DataPane(final ApplicationContext context, final boolean showSave, final Class<T> beanType,
			final BeanGetter<T> beanGetter, final BeanCreator<T> beanCreator, final Comparator<T> beanComparator,
			final BeanFormModel<T> beanFormModel, final List<String> tabs) {
		super(JSplitPane.HORIZONTAL_SPLIT);
		this.beanGetter = Objects.requireNonNull(beanGetter);
		this.beanCreator = Objects.requireNonNull(beanCreator);
		this.context = Objects.requireNonNull(context);
		this.beanFormModel = Objects.requireNonNull(beanFormModel);
		this.tabs = tabs;

		model = new SortableListModel<>(beanComparator);
		list = new JList<>(model);

		final JPanel left = new JPanel(new BorderLayout());

		left.add(new JScrollPane(list), BorderLayout.CENTER);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					LOGGER.debug("value still adjusting");
					current.setEnabled(false);
				} else {
					installBeanForm(list.getSelectedValue());
				}
			}
		});

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);

		addAction = new AbstractAction("Add") {
			private static final long serialVersionUID = -5722810007033837355L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				addData();
			}
		};

		delAction = new AbstractAction("Del") {
			private static final long serialVersionUID = -8206872556606892261L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				delData();
			}
		};

		if (showSave) {
			saveAction = new AbstractAction("Save") {
				private static final long serialVersionUID = -8659808353683696964L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					saveData();
				}
			};

			saveAction.setEnabled(false);
		} else {
			saveAction = null;
		}

		delAction.setEnabled(false);

		buttons.add(addAction);
		if (saveAction != null) {
			buttons.add(saveAction);
		}
		buttons.add(new JSeparator(SwingConstants.VERTICAL));
		buttons.add(delAction);
		left.add(context.getPresentation().awesome(buttons), BorderLayout.NORTH);

		setLeftComponent(left);
		setRightComponent(current);

		refreshList(null);
	}

	public Collection<T> getList() {
		final List<T> result = new ArrayList<>();
		final int n = model.getSize();
		for (int i = 0; i < n; i++) {
			result.add(model.getElementAt(i));
		}
		return result;
	}

	public T getSelection() {
		return list.getSelectedValue();
	}

	public void select(T version, boolean refresh) {
		LOGGER.debug("Select version: {}", version);
		if (refresh) {
			refreshList(version);
		} else {
			installBeanForm(version);
		}
	}

	private void installBeanForm(final T selected) {
		current.removeAll();
		if (selected != null) {
			LOGGER.debug("Selected: {} [{}]", selected, selected.hashCode());
			currentForm = formCache.get(selected);
			if (currentForm == null) {
				currentForm = new BeanForm<>(context, selected, beanFormModel, tabs);
				formCache.put(selected, currentForm);
			}
			current.add(new JScrollPane(currentForm), BorderLayout.CENTER);
			currentForm.load();
			delAction.setEnabled(true);
			if (saveAction != null) {
				saveAction.setEnabled(true);
			}
		} else {
			LOGGER.debug("Selected nothing");
			currentForm = null;
			current.add(new JPanel());
			delAction.setEnabled(false);
			if (saveAction != null) {
				saveAction.setEnabled(false);
			}
		}
		current.setEnabled(true);
		current.revalidate();
	}

	void addData() {
		final SwingWorker<Void, T> worker = new SwingWorker<Void, T>() {

			private int index = -1;

			@Override
			protected Void doInBackground() throws Exception {
				try {
					final T newBean = beanCreator.createBean();
					if (newBean == null) {
						LOGGER.info("null bean, aborting creation");
					} else {
						publish(newBean);
					}
				} catch (final ModelException e) {
					LOGGER.error("Error while adding data", e);
					final Presentation presentation = context.getPresentation();
					JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
							presentation.getMessage("CreateFailedMessage"),
							presentation.getMessage("CreateFailedTitle"), JOptionPane.WARNING_MESSAGE);
				}
				return null;
			}

			@Override
			protected void process(final java.util.List<T> chunks) {
				for (final T bean : chunks) {
					LOGGER.debug("Adding element: {}", bean);
					index = model.add(bean);
				}
			};

			@Override
			protected void done() {
				LOGGER.debug("Selecting last element");
				list.setSelectedIndex(index);
			}
		};

		worker.execute();
	}

	void delData() {
		final T bean = list.getSelectedValue();
		final Presentation presentation = context.getPresentation();
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(presentation.getApplicationFrame(),
				presentation.getMessage("ConfirmDeleteMessage", bean), presentation.getMessage("ConfirmDeleteTitle"),
				JOptionPane.YES_NO_OPTION)) {
			try {
				bean.delete();
			} catch (final ModelException e) {
				LOGGER.error("Error while deleting data", e);
				JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
						presentation.getMessage("DeleteFailedMessage"), presentation.getMessage("DeleteFailedTitle"),
						JOptionPane.WARNING_MESSAGE);
			} finally {
				refreshList(null);
			}
		}
	}

	public void saveData() {
		final T selected = list.getSelectedValue();
		try {
			if (currentForm != null) {
				currentForm.save();
			}
		} catch (final ModelException e) {
			LOGGER.error("Error while saving data", e);
			final Presentation presentation = context.getPresentation();
			JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
					presentation.getMessage("SaveFailedMessage"), presentation.getMessage("SaveFailedTitle"),
					JOptionPane.WARNING_MESSAGE);
		} finally {
			refreshList(selected);
		}
	}

	void refreshList(final T selected) {
		if (context.applicationIsClosing()) {
			return;
		}
		final SwingWorker<Void, T> worker = new SwingWorker<Void, T>() {
			private int selectedIndex = -1;

			@Override
			protected Void doInBackground() throws Exception {
				try {
					for (final T bean : beanGetter.getAllBeans()) {
						publish(bean);
					}
				} catch (final ModelException e) {
					LOGGER.error("Error while refreshing data", e);
					JOptionPane.showMessageDialog(context.getPresentation().getApplicationFrame(),
							context.getPresentation().getMessage("RefreshFailedMessage"),
							context.getPresentation().getMessage("RefreshFailedTitle"), JOptionPane.WARNING_MESSAGE);
				}
				return null;
			}

			@Override
			protected void process(final java.util.List<T> chunks) {
				for (final T bean : chunks) {
					LOGGER.debug("Adding element: {} (selected is {})", bean, selected);
					final int index = model.add(bean);
					if (bean.equals(selected)) {
						selectedIndex = index;
					}
				}
			};

			@Override
			protected void done() {
				LOGGER.debug("Selecting element #{}", selectedIndex);
				list.setSelectedIndex(selectedIndex);
			}
		};

		LOGGER.debug("Removing all elements");
		model.removeAll();
		worker.execute();
	}

	public boolean isDirty() {
		final boolean result;
		if (currentForm == null) {
			result = false;
		} else {
			result = currentForm.isDirty();
			if (result) {
				LOGGER.debug("dirty: {}", currentForm);
			}
		}
		return result;
	}

}