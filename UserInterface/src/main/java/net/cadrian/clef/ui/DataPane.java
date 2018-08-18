package net.cadrian.clef.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;

class DataPane<T extends Bean> extends JSplitPane {

	private static final long serialVersionUID = -6198568152980667836L;

	@FunctionalInterface
	public interface BeanGetter<T extends Bean> {
		Collection<? extends T> getAllBeans();
	}

	@FunctionalInterface
	public interface BeanCreator<T extends Bean> {
		T createBean();
	}

	private final DefaultListModel<T> model = new DefaultListModel<>();
	private final JList<T> list = new JList<>(model);
	private final JPanel current = new JPanel(new BorderLayout());

	private final Action addAction;
	private final Action delAction;
	private final Action saveAction;

	private final BeanGetter<T> beanGetter;
	private final BeanCreator<T> beanCreator;
	private final ResourceBundle messages;

	private BeanForm<T> currentForm;

	DataPane(final BeanGetter<T> beanGetter, final BeanCreator<T> beanCreator, final ResourceBundle messages) {
		super(JSplitPane.HORIZONTAL_SPLIT);
		this.beanGetter = beanGetter;
		this.beanCreator = beanCreator;
		this.messages = messages;

		final JPanel left = new JPanel(new BorderLayout());

		left.add(new JScrollPane(list), BorderLayout.CENTER);

		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					final T selected = list.getSelectedValue();
					current.removeAll();
					if (selected != null) {
						currentForm = new BeanForm<>(selected);
						current.add(new JScrollPane(currentForm), BorderLayout.CENTER);
						delAction.setEnabled(true);
					} else {
						currentForm = null;
						delAction.setEnabled(false);
						saveAction.setEnabled(false);
					}
				}
			}
		});

		final JToolBar buttons = new JToolBar(SwingConstants.CENTER);
		buttons.setFloatable(false);
		left.add(buttons, BorderLayout.PAGE_END);

		addAction = new AbstractAction(messages.getString("Add"), getIcon("Add")) {
			private static final long serialVersionUID = -5722810007033837355L;

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				addData();
			}
		};

		delAction = new AbstractAction(messages.getString("Del"), getIcon("Del")) {
			private static final long serialVersionUID = -8206872556606892261L;

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				delData();
			}
		};

		saveAction = new AbstractAction(messages.getString("Save"), getIcon("Save")) {
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

		refreshList();
	}

	private ImageIcon getIcon(final String name) {
		return new ImageIcon(DataPane.class.getResource("/icons/" + name + ".png"));
	}

	void addData() {
		final SwingWorker<Void, T> worker = new SwingWorker<Void, T>() {

			@Override
			protected Void doInBackground() throws Exception {
				try {
					publish(beanCreator.createBean());
				} catch (final ModelException e) {
					JOptionPane.showMessageDialog(DataPane.this, messages.getString("CreateFailedMessage"),
							messages.getString("CreateFailedTitle"), JOptionPane.WARNING_MESSAGE);
				}
				return null;
			}

			@Override
			protected void process(final java.util.List<T> chunks) {
				for (final T bean : chunks) {
					model.addElement(bean);
				}
				list.setSelectedIndex(model.getSize() - 1);
			};
		};

		worker.execute();
	}

	void delData() {
		if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, messages.getString("ConfirmDeleteMessage"),
				messages.getString("ConfirmDeleteTitle"), JOptionPane.YES_NO_OPTION)) {
			try {
				currentForm.delete();
			} catch (final ModelException e) {
				JOptionPane.showMessageDialog(DataPane.this, messages.getString("DeleteFailedMessage"),
						messages.getString("DeleteFailedTitle"), JOptionPane.WARNING_MESSAGE);
			} finally {
				refreshList();
			}
		}
	}

	void saveData() {
		try {
			currentForm.save();
		} catch (final ModelException e) {
			JOptionPane.showMessageDialog(DataPane.this, messages.getString("SaveFailedMessage"),
					messages.getString("SaveFailedTitle"), JOptionPane.WARNING_MESSAGE);
		} finally {
			list.setSelectedIndex(-1); // TODO is this valid? (select nothing)
			refreshList();
		}
	}

	void refreshList() {
		final SwingWorker<Void, T> worker = new SwingWorker<Void, T>() {

			@Override
			protected Void doInBackground() throws Exception {
				try {
					for (final T bean : beanGetter.getAllBeans()) {
						publish(bean);
					}
				} catch (final ModelException e) {
					JOptionPane.showMessageDialog(DataPane.this, messages.getString("RefreshFailedMessage"),
							messages.getString("RefreshFailedTitle"), JOptionPane.WARNING_MESSAGE);
				}
				return null;
			}

			@Override
			protected void process(final java.util.List<T> chunks) {
				for (final T bean : chunks) {
					model.addElement(bean);
				}
			};
		};

		model.removeAllElements();
		worker.execute();
	}

}
