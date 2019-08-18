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
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.PropertyBean;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.PropertyDescriptor.Type;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.widget.ClefTools;
import net.cadrian.clef.ui.widget.ClefTools.Tool;

public class ConfigurationPanel extends JTabbedPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationPanel.class);

	private static final long serialVersionUID = -2023860576290261246L;

	private static final int COLUMN_NAME = 0;
	private static final int COLUMN_TYPE = 1;
	private static final int COLUMN_DESCRIPTION = 2;

	private static final Class<?>[] COLUMN_TYPES = { String.class, LocalizedType.class, String.class };

	private final class OfflineModeChangeListener implements ChangeListener {
		private final JToggleButton offlineModeButton;
		private final ApplicationContext context;

		private OfflineModeChangeListener(final JToggleButton offlineModeButton, final ApplicationContext context) {
			this.offlineModeButton = offlineModeButton;
			this.context = context;
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			context.setValue(AdvancedConfigurationEntry.offlineMode, offlineModeButton.isSelected());
		}
	}

	private final class TableSelectionListener implements ListSelectionListener {
		private final PropertyDescriptorTableModel model;
		private final ClefTools tools;
		private final JTable table;

		private TableSelectionListener(final PropertyDescriptorTableModel model, final ClefTools tools,
				final JTable table) {
			this.model = model;
			this.tools = tools;
			this.table = table;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			tools.getAction(ClefTools.Tool.Del).setEnabled(model.canDelRow(table.getSelectedRow()));
		}
	}

	private final class ClefToolsListenerImpl implements ClefTools.Listener {
		private final JTable table;
		private final PropertyDescriptorTableModel model;

		private ClefToolsListenerImpl(final JTable table, final PropertyDescriptorTableModel model) {
			this.table = table;
			this.model = model;
		}

		@Override
		public void toolCalled(final ClefTools tools, final Tool tool) {
			switch (tool) {
			case Add:
				model.addRow(table.getSelectedRow());
				break;
			case Del:
				model.delRow(table.getSelectedRow());
				break;
			case Save:
				model.save();
				break;
			default:
			}
		}
	}

	private static class ConfigurableBeanDescription {
		final Entity entity;
		final String name;

		ConfigurableBeanDescription(final Entity entity, final Class<? extends PropertyBean> type) {
			this.entity = entity;
			name = type.getSimpleName();
		}
	}

	private static class LocalizedType {
		final PropertyDescriptor.Type type;
		private final String string;

		private LocalizedType(final PropertyDescriptor.Type type, final Presentation presentation) {
			this.type = type;
			string = presentation.getMessage("PropertyType." + type);
		}

		@Override
		public String toString() {
			return string;
		}

		public static LocalizedType[] values(final Presentation presentation) {
			final Type[] types = PropertyDescriptor.Type.values();
			final List<LocalizedType> result = new ArrayList<>(types.length);
			for (final Type type : types) {
				result.add(new LocalizedType(type, presentation));
			}
			Collections.sort(result, (t1, t2) -> t1.string.compareTo(t2.string));
			return result.toArray(new LocalizedType[types.length]);
		}

		@Override
		public boolean equals(final Object obj) {
			return ((LocalizedType) obj).type == type;
		}

		@Override
		public int hashCode() {
			return type.hashCode();
		}
	}

	private static final List<ConfigurableBeanDescription> CONFIGURABLE_BEANS = Arrays.asList(
			new ConfigurableBeanDescription(Entity.pricing, Pricing.class),
			new ConfigurableBeanDescription(Entity.author, Author.class),
			new ConfigurableBeanDescription(Entity.work, Work.class),
			new ConfigurableBeanDescription(Entity.piece, Piece.class),
			new ConfigurableBeanDescription(Entity.session, Session.class));

	private final List<PropertyDescriptorTableModel> models = new ArrayList<>();

	public ConfigurationPanel(final ApplicationContext context) {
		super();

		final Presentation presentation = context.getPresentation();
		for (final ConfigurableBeanDescription configurableBean : CONFIGURABLE_BEANS) {
			addTab(presentation.getMessage("Configuration." + configurableBean.name),
					configurationPanel(context, configurableBean));
		}

		addTab(presentation.getMessage("AdvancedConfiguration"), advancedConfigurationPanel(context));
	}

	private JPanel configurationPanel(final ApplicationContext context,
			final ConfigurableBeanDescription configurableBean) {
		final JPanel result = new JPanel(new BorderLayout());

		final PropertyDescriptorTableModel model = new PropertyDescriptorTableModel(context, configurableBean);
		models.add(model);

		final JTable table = new JTable(model);

		final JComboBox<LocalizedType> typeCombo = new JComboBox<>(LocalizedType.values(context.getPresentation()));
		table.getColumnModel().getColumn(COLUMN_TYPE).setCellEditor(new DefaultCellEditor(typeCombo));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		result.add(new JScrollPane(table), BorderLayout.CENTER);

		final ClefTools tools = new ClefTools(context, ClefTools.Tool.Add, ClefTools.Tool.Del, ClefTools.Tool.Save);
		tools.addListener(new ClefToolsListenerImpl(table, model));
		tools.getAction(ClefTools.Tool.Save).setEnabled(false);
		tools.getAction(ClefTools.Tool.Del).setEnabled(false);

		result.add(tools, BorderLayout.NORTH);

		table.getSelectionModel().addListSelectionListener(new TableSelectionListener(model, tools, table));

		return result;
	}

	private JPanel advancedConfigurationPanel(final ApplicationContext context) {
		final JPanel result = new JPanel(new BorderLayout());

		final JToggleButton offlineModeButton = context.getPresentation().awesome(new JToggleButton("Attention"));
		offlineModeButton.setForeground(Color.RED);
		final JLabel offlineModeLabel = new JLabel(context.getPresentation().getMessage("OfflineModeLabel"));

		final JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
		content.add(offlineModeButton);
		content.add(offlineModeLabel);

		offlineModeButton.addChangeListener(new OfflineModeChangeListener(offlineModeButton, context));

		result.add(content, BorderLayout.CENTER);

		return result;
	}

	private static class PropertyDescriptorTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -4338724388501210776L;

		private static class EditableBean implements Comparable<EditableBean> {
			private PropertyDescriptor bean;
			private String name;
			private LocalizedType type;
			private String description;
			private boolean dirty;

			EditableBean(final Presentation presentation, final PropertyDescriptor bean) {
				this.bean = bean;
				name = bean == null ? "" : bean.getName();
				type = new LocalizedType(bean == null ? Type.string : bean.getType(), presentation);
				description = bean == null ? "" : bean.getDescription();
				dirty = false;
			}

			@Override
			public int compareTo(final EditableBean o) {
				final String n = bean == null ? "" : bean.getName();
				final String on = o.bean == null ? "" : o.bean.getName();
				return (n == null ? "" : n).compareTo(on == null ? "" : on);
			}

			public PropertyDescriptor getBean() {
				return bean;
			}

			public void setBean(final PropertyDescriptor bean) {
				this.bean = bean;
				dirty = true;
			}

			public String getName() {
				return name;
			}

			public void setName(final String value) {
				name = value;
				dirty = true;
			}

			public LocalizedType getType() {
				return type;
			}

			public void setType(final LocalizedType type) {
				this.type = type;
			}

			public String getDescription() {
				return description;
			}

			public void setDescription(final String description) {
				this.description = description;
				dirty = true;
			}

			public void save() {
				if (dirty) {
					bean.setName(name);
					bean.setType(type.type);
					bean.setDescription(description);
					dirty = false;
				}
			}

			public boolean isDirty() {
				if (dirty) {
					LOGGER.debug("dirty: {}", bean);
				}
				return dirty;
			}

			@Override
			public String toString() {
				if (bean != null) {
					return bean.toString();
				}
				return "";
			}
		}

		private final ApplicationContext context;
		private final ConfigurableBeanDescription configurableBean;

		private final String[] columnNames;

		final List<EditableBean> editableBeans = new ArrayList<>();
		final List<EditableBean> deletedBeans = new ArrayList<>();

		PropertyDescriptorTableModel(final ApplicationContext context,
				final ConfigurableBeanDescription configurableBean) {
			this.context = context;
			this.configurableBean = configurableBean;
			columnNames = new String[] { context.getPresentation().getMessage("Name"),
					context.getPresentation().getMessage("Type"), context.getPresentation().getMessage("Description") };
			refresh();
		}

		private void refresh() {
			final Collection<? extends PropertyDescriptor> propertyDescriptors = context.getBeans()
					.getPropertyDescriptors(configurableBean.entity);
			final Presentation presentation = context.getPresentation();

			for (final PropertyDescriptor bean : propertyDescriptors) {
				final EditableBean editableBean = new EditableBean(presentation, bean);
				editableBeans.add(editableBean);
			}
			Collections.sort(editableBeans);
		}

		public void addRow(int row) {
			if (row == -1) {
				row = editableBeans.size();
			}
			editableBeans.add(new EditableBean(context.getPresentation(), null));
			fireTableRowsInserted(row, row);
		}

		public boolean canDelRow(final int row) {
			if (row != -1) {
				final EditableBean deleted = editableBeans.get(row);
				return canBeDeleted(deleted.getBean());
			}
			return false;
		}

		public void delRow(final int row) {
			if (row != -1) {
				final EditableBean deleted = editableBeans.get(row);
				final PropertyDescriptor bean = deleted.getBean();
				if (canBeDeleted(bean)) {
					editableBeans.remove(row);
					deletedBeans.add(deleted);
					fireTableRowsDeleted(row, row);
				} else {
					final String name = bean.getName();
					LOGGER.error("Cannot delete: there are occurrences of the {} property for entity {}", name,
							configurableBean.entity);
					final Presentation presentation = context.getPresentation();
					JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
							presentation.getMessage("CannotDeletePropertyMessage", name),
							presentation.getMessage("CannotDeletePropertyTitle"), JOptionPane.WARNING_MESSAGE);
				}
			}
		}

		private boolean canBeDeleted(final PropertyDescriptor bean) {
			if (bean != null) {
				if (bean.countUsages() > 0) {
					return false;
				}
			}
			return true;
		}

		public void save() {
			if (canBeSaved()) {
				try {
					for (final EditableBean editableBean : deletedBeans) {
						final PropertyDescriptor bean = editableBean.getBean();
						if (bean != null) {
							bean.delete();
						}
					}
					for (final EditableBean editableBean : editableBeans) {
						if (editableBean.getBean() == null) {
							final PropertyDescriptor bean = context.getBeans()
									.createPropertyDescriptor(configurableBean.entity, editableBean.getType().type);
							editableBean.setBean(bean);
						}
						editableBean.save();
					}
					deletedBeans.clear();
				} catch (final ModelException e) {
					LOGGER.error("Save failed", e);
					final Presentation presentation = context.getPresentation();
					JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
							presentation.getMessage("SaveFailedMessage"), presentation.getMessage("SaveFailedTitle"),
							JOptionPane.WARNING_MESSAGE);
				}
			}
		}

		private boolean canBeSaved() {
			final Set<String> names = new HashSet<>();
			for (final EditableBean editableBean : editableBeans) {
				final String name = editableBean.getName();
				if (names.contains(name)) {
					LOGGER.error("Cannot save: the {} property is defined twice for entity {}", name,
							configurableBean.entity);
					final Presentation presentation = context.getPresentation();
					JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
							presentation.getMessage("CannotSavePropertiesMessage", name),
							presentation.getMessage("CannotSavePropertiesTitle"), JOptionPane.WARNING_MESSAGE);
					return false;
				} else {
					names.add(name);
				}
			}
			return true;
		}

		@Override
		public int getRowCount() {
			return editableBeans.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				return editableBeans.get(rowIndex).getName();
			case COLUMN_TYPE:
				return editableBeans.get(rowIndex).getType();
			case COLUMN_DESCRIPTION:
				return editableBeans.get(rowIndex).getDescription();
			}
			return null;
		}

		@Override
		public String getColumnName(final int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(final int column) {
			return COLUMN_TYPES[column];
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
			case COLUMN_NAME:
				editableBeans.get(rowIndex).setName((String) aValue);
				break;
			case COLUMN_TYPE:
				editableBeans.get(rowIndex).setType((LocalizedType) aValue);
				break;
			case COLUMN_DESCRIPTION:
				editableBeans.get(rowIndex).setDescription((String) aValue);
				break;
			}
		}

		public boolean isDirty() {
			if (!deletedBeans.isEmpty()) {
				LOGGER.debug("dirty: there are deleted beans");
				return true;
			}
			for (final EditableBean editableBean : editableBeans) {
				if (editableBean.isDirty()) {
					LOGGER.debug("dirty: {}", editableBean);
					return true;
				}
			}
			return false;
		}

	}

	public boolean isDirty() {
		for (final PropertyDescriptorTableModel model : models) {
			if (model.isDirty()) {
				return true;
			}
		}
		return false;
	}

	public void saveData() {
		for (final PropertyDescriptorTableModel model : models) {
			model.save();
		}
	}

}
