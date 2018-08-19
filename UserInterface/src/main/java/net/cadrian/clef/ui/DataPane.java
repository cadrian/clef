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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;

public class DataPane<T extends Bean, C> extends JSplitPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataPane.class);

	private static final long serialVersionUID = -6198568152980667836L;

	private final DefaultListModel<T> model = new DefaultListModel<>();
	private final JList<T> list = new JList<>(model);
	private final JPanel current = new JPanel(new BorderLayout());

	private final Action addAction;
	private final Action delAction;
	private final Action saveAction;

	private final BeanGetter<T> beanGetter;
	private final BeanCreator<T> beanCreator;
	private final Resources rc;

	private BeanForm<T, C> currentForm;

	@FunctionalInterface
	public interface ContextGetter<T extends Bean, C> {
		C getContext(DataPane<T, C> pane);
	}

	public DataPane(final Resources rc, final BeanGetter<T> beanGetter, final BeanCreator<T> beanCreator,
			final BeanFormModel<T, C> beanFormModel) {
		this(rc, (pane) -> null, beanGetter, beanCreator, beanFormModel, null);
	}

	public DataPane(final Resources rc, final ContextGetter<T, C> contextGetter, final BeanGetter<T> beanGetter,
			final BeanCreator<T> beanCreator, final BeanFormModel<T, C> beanFormModel, final List<String> tabs) {
		super(JSplitPane.HORIZONTAL_SPLIT);
		this.beanGetter = beanGetter;
		this.beanCreator = beanCreator;
		this.rc = rc;

		final JPanel left = new JPanel(new BorderLayout());

		left.add(new JScrollPane(list), BorderLayout.CENTER);

		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					current.setEnabled(false);
				} else {
					final T selected = list.getSelectedValue();
					current.removeAll();
					if (selected != null) {
						LOGGER.debug("Selected: {} [{}]", selected, selected.hashCode());
						currentForm = new BeanForm<>(rc, contextGetter.getContext(DataPane.this), selected,
								beanFormModel, tabs);
						current.add(new JScrollPane(currentForm), BorderLayout.CENTER);
						currentForm.load();
						delAction.setEnabled(true);
						saveAction.setEnabled(true);
					} else {
						LOGGER.debug("Selected nothing");
						currentForm = null;
						current.add(new JPanel());
						delAction.setEnabled(false);
						saveAction.setEnabled(false);
					}
					current.setEnabled(true);
				}
			}
		});

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);
		left.add(buttons, BorderLayout.PAGE_END);

		addAction = new AbstractAction(rc.getMessage("Add"), rc.getIcon("Add")) {
			private static final long serialVersionUID = -5722810007033837355L;

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				addData();
			}
		};

		delAction = new AbstractAction(rc.getMessage("Del"), rc.getIcon("Del")) {
			private static final long serialVersionUID = -8206872556606892261L;

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				delData();
			}
		};

		saveAction = new AbstractAction(rc.getMessage("Save"), rc.getIcon("Save")) {
			private static final long serialVersionUID = -8659808353683696964L;

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				saveData();
			}
		};

		saveAction.setEnabled(false);
		delAction.setEnabled(false);

		buttons.add(addAction);
		buttons.add(saveAction);
		buttons.add(new JSeparator());
		buttons.add(delAction);

		setLeftComponent(left);
		setRightComponent(current);

		refreshList(null);
	}

	public Collection<T> getList() {
		List<T> result = new ArrayList<>();
		int n = model.getSize();
		for (int i = 0; i < n; i++) {
			result.add(model.getElementAt(i));
		}
		return result;
	}

	public T getSelection() {
		return list.getSelectedValue();
	}

	void addData() {
		final SwingWorker<Void, T> worker = new SwingWorker<Void, T>() {

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
					JOptionPane.showMessageDialog(DataPane.this, rc.getMessage("CreateFailedMessage"),
							rc.getMessage("CreateFailedTitle"), JOptionPane.WARNING_MESSAGE);
				}
				return null;
			}

			@Override
			protected void process(final java.util.List<T> chunks) {
				for (final T bean : chunks) {
					LOGGER.debug("Adding element: {}", bean);
					model.addElement(bean);
				}
				list.setSelectedIndex(model.getSize() - 1);
			};
		};

		worker.execute();
	}

	void delData() {
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, rc.getMessage("ConfirmDeleteMessage"),
				rc.getMessage("ConfirmDeleteTitle"), JOptionPane.YES_NO_OPTION)) {
			try {
				list.getSelectedValue().delete();
			} catch (final ModelException e) {
				JOptionPane.showMessageDialog(DataPane.this, rc.getMessage("DeleteFailedMessage"),
						rc.getMessage("DeleteFailedTitle"), JOptionPane.WARNING_MESSAGE);
			} finally {
				refreshList(null);
			}
		}
	}

	void saveData() {
		try {
			currentForm.save();
		} catch (final ModelException e) {
			JOptionPane.showMessageDialog(DataPane.this, rc.getMessage("SaveFailedMessage"),
					rc.getMessage("SaveFailedTitle"), JOptionPane.WARNING_MESSAGE);
		} finally {
			refreshList(list.getSelectedValue());
		}
	}

	void refreshList(final T selected) {
		final SwingWorker<Void, T> worker = new SwingWorker<Void, T>() {

			@Override
			protected Void doInBackground() throws Exception {
				try {
					for (final T bean : beanGetter.getAllBeans()) {
						publish(bean);
					}
				} catch (final ModelException e) {
					JOptionPane.showMessageDialog(DataPane.this, rc.getMessage("RefreshFailedMessage"),
							rc.getMessage("RefreshFailedTitle"), JOptionPane.WARNING_MESSAGE);
				}
				return null;
			}

			@Override
			protected void process(final java.util.List<T> chunks) {
				for (final T bean : chunks) {
					LOGGER.debug("Adding element: {}", bean);
					model.addElement(bean);
					list.setSelectedIndex(model.getSize() - 1);
				}
			};
		};

		LOGGER.debug("Removing all elements");
		model.removeAllElements();
		worker.execute();
	}

}
