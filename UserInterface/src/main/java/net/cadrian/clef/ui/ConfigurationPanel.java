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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.PropertyBean;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

class ConfigurationPanel extends JTabbedPane {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationPanel.class);

	private static final long serialVersionUID = -2023860576290261246L;

	private static class ConfigurableBeanDescription {
		final Entity entity;
		final String name;

		ConfigurableBeanDescription(final Entity entity, final Class<? extends PropertyBean> type) {
			this.entity = entity;
			name = type.getSimpleName();
		}
	}

	private static final List<ConfigurableBeanDescription> CONFIGURABLE_BEANS = Arrays.asList(
			new ConfigurableBeanDescription(Entity.author, Author.class),
			new ConfigurableBeanDescription(Entity.work, Work.class),
			new ConfigurableBeanDescription(Entity.piece, Piece.class),
			new ConfigurableBeanDescription(Entity.session, Session.class));

	ConfigurationPanel(final Resources rc, final Beans beans) {
		super();

		for (final ConfigurableBeanDescription configurableBean : CONFIGURABLE_BEANS) {
			addTab(rc.getMessage(configurableBean.name), configurationPanel(beans, rc, configurableBean));
		}
	}

	private JPanel configurationPanel(final Beans beans, final Resources rc,
			final ConfigurableBeanDescription configurableBean) {
		final JPanel result = new JPanel(new BorderLayout());

		final PropertyDescriptorTableModel model = new PropertyDescriptorTableModel(beans, rc, this, configurableBean);

		final JTable table = new JTable(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		result.add(new JScrollPane(table), BorderLayout.CENTER);

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);

		final Action addAction = new AbstractAction("Add") {
			private static final long serialVersionUID = -5722810007033837355L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int row = table.getSelectedRow();
				model.addRow(row);
			}
		};

		final Action delAction = new AbstractAction("Del") {
			private static final long serialVersionUID = -8206872556606892261L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int row = table.getSelectedRow();
				model.delRow(row);
			}
		};

		final Action saveAction = new AbstractAction("Save") {
			private static final long serialVersionUID = -8659808353683696964L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				model.save();
			}
		};

		buttons.add(addAction);
		buttons.add(saveAction);
		buttons.add(new JSeparator());
		buttons.add(delAction);
		result.add(rc.awesome(buttons), BorderLayout.SOUTH);

		return result;
	}

	private static class PropertyDescriptorTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -4338724388501210776L;

		private static class EditableBean implements Comparable<EditableBean> {
			private PropertyDescriptor bean;
			private String name;
			private String description;
			private boolean dirty;

			EditableBean(final PropertyDescriptor bean) {
				this.bean = bean;
				name = bean == null ? "" : bean.getName();
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
					bean.setDescription(description);
					dirty = false;
				}
			}
		}

		private final Beans beans;
		private final Resources rc;
		private final ConfigurableBeanDescription configurableBean;
		private final Component parent;

		private final String[] columnNames;

		final List<EditableBean> editableBeans = new ArrayList<>();
		final List<EditableBean> deletedBeans = new ArrayList<>();

		PropertyDescriptorTableModel(final Beans beans, final Resources rc, final Component parent,
				final ConfigurableBeanDescription configurableBean) {
			this.beans = beans;
			this.rc = rc;
			this.configurableBean = configurableBean;
			this.parent = parent;
			columnNames = new String[] { rc.getMessage("Name"), rc.getMessage("Description") };
			refresh();
		}

		private void refresh() {
			final Collection<? extends PropertyDescriptor> propertyDescriptors = beans
					.getPropertyDescriptors(configurableBean.entity);

			for (final PropertyDescriptor bean : propertyDescriptors) {
				final EditableBean editableBean = new EditableBean(bean);
				editableBeans.add(editableBean);
			}
			Collections.sort(editableBeans);
		}

		public void addRow(int row) {
			if (row == -1) {
				row = editableBeans.size();
			}
			editableBeans.add(new EditableBean(null));
			fireTableRowsInserted(row, row);
		}

		public void delRow(final int row) {
			if (row != -1) {
				final EditableBean deleted = editableBeans.get(row);
				if (canBeDeleted(deleted.getBean())) {
					editableBeans.remove(row);
					deletedBeans.add(deleted);
					fireTableRowsDeleted(row, row);
				}
			}
		}

		private boolean canBeDeleted(final PropertyDescriptor bean) {
			if (bean != null) {
				if (bean.countUsages() > 0) {
					final String name = bean.getName();
					LOGGER.error("Cannot delete: there are occurrences of the {} property for entity {}", name,
							configurableBean.entity);
					JOptionPane.showMessageDialog(parent, rc.getMessage("CannotDeletePropertyMessage", name),
							rc.getMessage("CannotDeletePropertyTitle"), JOptionPane.WARNING_MESSAGE);
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
							final PropertyDescriptor bean = beans.createPropertyDescriptor(configurableBean.entity);
							editableBean.setBean(bean);
						}
						editableBean.save();
					}
					deletedBeans.clear();
				} catch (final ModelException e) {
					LOGGER.error("Save failed", e);
					JOptionPane.showMessageDialog(parent, rc.getMessage("SaveFailedMessage"),
							rc.getMessage("SaveFailedTitle"), JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(parent, rc.getMessage("CannotSavePropertiesMessage", name),
							rc.getMessage("CannotSavePropertiesTitle"), JOptionPane.WARNING_MESSAGE);
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
			return 2;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
			case 0:
				return editableBeans.get(rowIndex).getName();
			case 1:
				return editableBeans.get(rowIndex).getDescription();
			}
			return null;
		}

		@Override
		public String getColumnName(final int column) {
			return columnNames[column];
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return true;
		}

		@Override
		public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
			case 0:
				editableBeans.get(rowIndex).setName((String) aValue);
				break;
			case 1:
				editableBeans.get(rowIndex).setDescription((String) aValue);
				break;
			}
		}

	}

}
