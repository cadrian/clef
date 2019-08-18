/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
package net.cadrian.clef.ui.app.tab;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.swing.Action;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.ApplicationContext.ApplicationContextListener;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.app.form.BeanCreator;
import net.cadrian.clef.ui.app.form.BeanFilter;
import net.cadrian.clef.ui.app.form.BeanForm;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.BeanGetter;
import net.cadrian.clef.ui.app.form.BeanMover;
import net.cadrian.clef.ui.app.tab.filter.JBeanFilter;
import net.cadrian.clef.ui.tools.SortableListModel;
import net.cadrian.clef.ui.widget.ClefTools;
import net.cadrian.clef.ui.widget.ClefTools.Tool;

public class DataPane<T extends Bean> extends JSplitPane {

	private final class ApplicationContextListenerImpl implements ApplicationContextListener<Boolean> {

		private ApplicationContextListenerImpl() {
		}

		@Override
		public void onAdvancedConfigurationChange(final AdvancedConfigurationEntry entry, final Boolean value) {
			try {
				LOGGER.debug("Will refresh configuration: offineMode={}", value);
				SwingUtilities.invokeLater(() -> saveData(false));
			} catch (final RuntimeException e) {
				LOGGER.error("Error during configuration refresh", e);
				throw e;
			}
		}

		@Override
		public String toString() {
			return "ApplicationContextListenerImpl: " + beanType.getName();
		}
	}

	private final class BeanFilterActionListener implements ActionListener {
		private final class PaneRefresher implements Runnable {
			@Override
			public void run() {
				layeredPane.repaint();
				filterAction.setEnabled(true);
			}
		}

		private final JBeanFilter<T> beanFilterComponent;
		private final JLayeredPane layeredPane;
		private final Action filterAction;

		private BeanFilterActionListener(final JBeanFilter<T> beanFilterComponent, final JLayeredPane layeredPane,
				final Action filterAction) {
			this.beanFilterComponent = beanFilterComponent;
			this.layeredPane = layeredPane;
			this.filterAction = filterAction;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			layeredPane.remove(beanFilterComponent);
			SwingUtilities.invokeLater(new PaneRefresher());
			refreshList(list.getSelectedValue());
		}
	}

	private final class DataAdder extends SwingWorker<Void, T> {
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
						presentation.getMessage("CreateFailedMessage"), presentation.getMessage("CreateFailedTitle"),
						JOptionPane.WARNING_MESSAGE);
			}
			return null;
		}

		@Override
		protected void process(final java.util.List<T> chunks) {
			for (final T bean : chunks) {
				LOGGER.debug("Adding element: {}", bean);
				index = model.add(bean);
			}
		}

		@Override
		protected void done() {
			LOGGER.debug("Selecting last element");
			list.setSelectedIndex(index);
		}
	}

	private final class BeanFormInstaller implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				LOGGER.debug("value still adjusting");
				current.setEnabled(false);
			} else {
				installBeanForm(list.getSelectedValue(), getTab());
			}
		}
	}

	private final class ClefToolsListenerImpl implements ClefTools.Listener {
		private final ApplicationContext context;

		private ClefToolsListenerImpl(final ApplicationContext context) {
			this.context = context;
		}

		@Override
		public void toolCalled(final ClefTools tools, final Tool tool) {
			switch (tool) {
			case Add:
				addData();
				break;
			case Del:
				delData();
				break;
			case Save:
				saveData();
				break;
			case Move:
				move();
				break;
			case Filter:
				final Point frameLocationOnScreen = context.getPresentation().getApplicationFrame().getLayeredPane()
						.getLocationOnScreen();
				final Point toolLocationOnScreen = tools.getLocationOnScreen(tool);
				final Dimension toolSize = tools.getSize(tool);
				final int x = toolLocationOnScreen.x - frameLocationOnScreen.x + toolSize.width / 2;
				final int y = toolLocationOnScreen.y - frameLocationOnScreen.y + toolSize.height / 2;
				final Point position = new Point(x, y);
				showFilter(position, tools.getAction(tool));
				break;
			}
		}
	}

	private static final class RefreshListWorker<T extends Bean> extends SwingWorker<Void, T> {
		private static final Logger LOGGER = LoggerFactory.getLogger(RefreshListWorker.class);

		private final BeanGetter<T> beanGetter;
		private final BeanFilter<T> beanFilter;
		private final ApplicationContext context;

		private final SortableListModel<T> model;
		private final JList<T> list;

		private final T selected;
		private int selectedIndex = -1;

		private RefreshListWorker(final ApplicationContext context, final BeanGetter<T> beanGetter,
				final BeanFilter<T> beanFilter, final SortableListModel<T> model, final JList<T> list,
				final T selected) {
			this.context = context;
			this.beanGetter = beanGetter;
			this.beanFilter = beanFilter;
			this.model = model;
			this.list = list;
			this.selected = selected;
		}

		@Override
		protected Void doInBackground() throws Exception {
			try {
				LOGGER.info("Refreshing data...");
				if (beanFilter == null) {
					for (final T bean : beanGetter.getAllBeans()) {
						publish(bean);
					}
				} else {
					for (final T bean : beanGetter.getAllBeans()) {
						if (beanFilter.isVisible(bean)) {
							publish(bean);
						}
					}
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
				if (bean.isVersionOf(selected)) {
					selectedIndex = index;
				}
			}
		}

		@Override
		protected void done() {
			LOGGER.debug("Selecting element #{}", selectedIndex);
			list.setSelectedIndex(selectedIndex);
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DataPane.class);

	public static final String DEFAULT_TAB = "Description";
	private static final List<String> DEFAULT_TABS = Arrays.asList(DEFAULT_TAB);

	private static final long serialVersionUID = -6198568152980667836L;

	private final SortableListModel<T> model;
	private final JList<T> list;
	private final JPanel current = new JPanel(new BorderLayout());
	private final ClefTools tools;

	private final Class<T> beanType;
	private final BeanGetter<T> beanGetter;
	private final BeanCreator<T> beanCreator;
	private final BeanFilter<T> beanFilter;
	private final BeanMover<T> beanMover;
	private final ApplicationContext context;
	private final BeanFormModel<T> beanFormModel;
	private final List<String> tabs;

	private final Map<T, BeanForm<T>> formCache = new WeakHashMap<>();

	private final ApplicationContextListener<Boolean> applicationContextListener;

	private BeanForm<T> currentForm;
	private String lastTab;

	public DataPane(final ApplicationContext context, final boolean showSave, final Class<T> beanType,
			final BeanGetter<T> beanGetter, final BeanCreator<T> beanCreator, final BeanFilter<T> beanFilter,
			final BeanMover<T> beanMover, final Comparator<T> beanComparator, final BeanFormModel<T> beanFormModel,
			final String... tabs) {
		super(JSplitPane.HORIZONTAL_SPLIT);
		this.beanType = beanType;
		this.beanGetter = Objects.requireNonNull(beanGetter);
		this.beanCreator = Objects.requireNonNull(beanCreator);
		this.beanFilter = beanFilter;
		this.beanMover = beanMover;
		this.context = Objects.requireNonNull(context);
		this.beanFormModel = Objects.requireNonNull(beanFormModel);
		this.tabs = tabs.length == 0 ? DEFAULT_TABS : Arrays.asList(tabs);

		model = new SortableListModel<>(beanComparator);
		list = new JList<>(model);

		final JPanel left = new JPanel(new BorderLayout());

		left.add(new JScrollPane(list), BorderLayout.CENTER);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		list.getSelectionModel().addListSelectionListener(new BeanFormInstaller());

		final List<Tool> toolsList = new ArrayList<>(Arrays.asList(Tool.values()));
		if (!showSave) {
			toolsList.remove(Tool.Save);
		}
		if (beanFilter == null) {
			toolsList.remove(Tool.Filter);
		}
		if (beanMover == null) {
			toolsList.remove(Tool.Move);
		}
		tools = new ClefTools(context, toolsList.toArray(new Tool[toolsList.size()]));
		tools.addListener(new ClefToolsListenerImpl(context));
		tools.getAction(ClefTools.Tool.Del).setEnabled(false);
		if (showSave) {
			final Action saveAction = tools.getAction(ClefTools.Tool.Save);
			saveAction.setEnabled(false);
			getInputMap().put(KeyStroke.getKeyStroke("control S"), "save");
			getActionMap().put("save", saveAction);
		}
		if (beanMover != null) {
			tools.getAction(ClefTools.Tool.Move).setEnabled(false);
		}

		left.add(tools, BorderLayout.NORTH);

		setLeftComponent(left);
		setRightComponent(current);

		applicationContextListener = new ApplicationContextListenerImpl();
		context.addApplicationContextListener(AdvancedConfigurationEntry.offlineMode, applicationContextListener);

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

	public void refresh() {
		refreshList(getSelection());
	}

	public void select(final T pieceVersion, final boolean refresh) {
		LOGGER.debug("Select piece version: {}", pieceVersion);
		if (refresh) {
			refreshList(pieceVersion);
		} else {
			installBeanForm(pieceVersion, getTab());
		}
	}

	private void installBeanForm(final T selected, final String tab) {
		current.removeAll();
		final Action delAction = tools.getAction(ClefTools.Tool.Del);
		final Action saveAction = tools.getAction(ClefTools.Tool.Save);
		if (selected != null) {
			LOGGER.debug("Selected: {} [{}]", selected, selected.hashCode());
			currentForm = formCache.get(selected);
			if (currentForm == null) {
				currentForm = new BeanForm<>(context, selected, beanFormModel, tabs);
				formCache.put(selected, currentForm);
			}
			if (tab != null) {
				currentForm.setTab(tab);
			}
			current.add(new JScrollPane(currentForm), BorderLayout.CENTER);
			currentForm.load();
			delAction.setEnabled(true);
			if (saveAction != null) {
				saveAction.setEnabled(true);
			}
			if (beanMover != null && beanMover.canMove(selected)) {
				final Action moveAction = tools.getAction(ClefTools.Tool.Move);
				moveAction.setEnabled(true);
			}
		} else {
			LOGGER.debug("Selected nothing");
			currentForm = null;
			current.add(new JPanel());
			delAction.setEnabled(false);
			if (saveAction != null) {
				saveAction.setEnabled(false);
			}
			if (beanMover != null) {
				final Action moveAction = tools.getAction(ClefTools.Tool.Move);
				moveAction.setEnabled(false);
			}
		}
		current.setEnabled(true);
		current.revalidate();
	}

	void addData() {
		final SwingWorker<Void, T> worker = new DataAdder();
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
		saveData(true);
	}

	private void saveData(final boolean refresh) {
		LOGGER.debug("{}: <-- {}", beanType.getName(), refresh);
		final T selected = list.getSelectedValue();
		LOGGER.debug("selected={}", selected);
		final String tab = getTab();
		try {
			formCache.remove(selected);
			if (currentForm != null) {
				if (currentForm.isDirty()) {
					LOGGER.debug("{}: Saving {}", beanType.getName(), currentForm);
					currentForm.save();
				} else {
					LOGGER.debug("{}: NOT saving {} (not dirty)", beanType.getName(), currentForm);
				}
				currentForm = null;
			}
		} catch (final ModelException e) {
			LOGGER.error("{}: Error while saving data", beanType.getName(), e);
			final Presentation presentation = context.getPresentation();
			JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
					presentation.getMessage("SaveFailedMessage"), presentation.getMessage("SaveFailedTitle"),
					JOptionPane.WARNING_MESSAGE);
		} finally {
			if (refresh) {
				LOGGER.debug("{}: Refreshing list", beanType.getName());
				refreshList(selected);
			} else {
				LOGGER.debug("{}: Installing new bean form", beanType.getName());
				installBeanForm(selected, tab);
			}
		}
		LOGGER.debug("{}: -->", beanType.getName());
	}

	private void showFilter(final Point position, final Action filterAction) {
		LOGGER.debug("Showing filter");
		filterAction.setEnabled(false);
		final JLayeredPane layeredPane = context.getPresentation().getApplicationFrame().getLayeredPane();
		final JBeanFilter<T> beanFilterComponent = beanFilter.getFilterComponent(context);
		beanFilterComponent
				.addActionListener(new BeanFilterActionListener(beanFilterComponent, layeredPane, filterAction));
		beanFilterComponent.setLocation(position);
		layeredPane.add(beanFilterComponent, JLayeredPane.MODAL_LAYER, 1000);
	}

	private void move() {
		beanMover.move(getSelection());
		refreshList(null);
	}

	String getTab() {
		final String result;
		if (currentForm == null) {
			result = lastTab;
		} else {
			result = currentForm.getTab();
			lastTab = result;
		}
		LOGGER.debug("tab: {}", result);
		return result;
	}

	void refreshList(final T selected) {
		if (context.applicationIsClosing()) {
			return;
		}
		final SwingWorker<Void, T> worker = new RefreshListWorker<>(context, beanGetter, beanFilter, model, list,
				selected);
		LOGGER.debug("Removing all elements");
		model.removeAll();
		LOGGER.debug("Adding elements using getter: {} and filter: {}", beanGetter, beanFilter);
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

	public void removed() {
		context.removeApplicationContextListener(AdvancedConfigurationEntry.offlineMode, applicationContextListener);
	}

}
