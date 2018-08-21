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
package net.cadrian.clef.ui.form;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;

public class PropertiesComponentFactory<C extends Bean>
		extends AbstractFieldComponentFactory<Collection<? extends Property>, JSplitPane, C> {

	private static class PropertiesComponent implements FieldComponent<Collection<? extends Property>, JSplitPane> {

		private static class AddPropertyChooser extends JDialog {

			private static final long serialVersionUID = 2515326043268533422L;

			private PropertyDescriptor selected;

			AddPropertyChooser(final ApplicationContext context, final Entity entity,
					final List<PropertyDescriptor> addableDescriptors) {
				super(context.getPresentation().getApplicationFrame(),
						context.getPresentation().getMessage("AddPropertyTitle"), true);

				final JPanel panel = new JPanel(new BorderLayout());
				getContentPane().add(panel);

				final Presentation presentation = context.getPresentation();
				final JLabel message = new JLabel(presentation.getMessage("AddPropertyMessage"));
				panel.add(message, BorderLayout.NORTH);

				final DefaultListModel<PropertyDescriptor> model = new DefaultListModel<>();
				for (final PropertyDescriptor propertyDescriptor : addableDescriptors) {
					model.addElement(propertyDescriptor);
				}
				final JList<PropertyDescriptor> list = new JList<>(model);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				panel.add(new JScrollPane(list), BorderLayout.CENTER);

				final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
				buttons.setFloatable(false);

				final Action addAction = new AbstractAction("Add") {
					private static final long serialVersionUID = -8659808353683696964L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						selected = list.getSelectedValue();
						setVisible(false);
					}
				};
				buttons.add(addAction);
				panel.add(presentation.awesome(buttons), BorderLayout.SOUTH);

				pack();
				setLocationRelativeTo(presentation.getApplicationFrame());
			}

			public PropertyDescriptor getSelected() {
				return selected;
			}
		}

		private static class EditableProperty {
			private final PropertyDescriptor propertyDescriptor;
			private Property property;
			private String value;
			private boolean dirty;

			EditableProperty(final PropertyDescriptor propertyDescriptor) {
				this.propertyDescriptor = propertyDescriptor;
				property = null;
				value = null;
				dirty = false;
			}

			EditableProperty(final Property property) {
				propertyDescriptor = property.getPropertyDescriptor();
				this.property = property;
				value = property.getValue();
				dirty = false;
			}

			public PropertyDescriptor getPropertyDescriptor() {
				return propertyDescriptor;
			}

			public Property getProperty() {
				return property;
			}

			void setProperty(final Property property) {
				this.property = property;
				dirty = true;
			}

			public String getValue() {
				return value;
			}

			public void setValue(final String value) {
				this.value = value;
				dirty = true;
			}

			public void save() {
				if (dirty) {
					property.setValue(value);
					dirty = false;
				}
			}

			@Override
			public String toString() {
				return propertyDescriptor.getName();
			}
		}

		private final ApplicationContext context;
		private final Entity entity;
		private final JSplitPane component;
		private final Map<PropertyDescriptor, Property> deleted = new HashMap<>();
		private final DefaultListModel<EditableProperty> model = new DefaultListModel<>();
		private final JList<EditableProperty> list;
		private final boolean writable;

		private EditableProperty current;
		private JTextArea content;

		PropertiesComponent(final ApplicationContext context, final Entity entity, final boolean writable) {
			this.context = context;
			this.entity = entity;
			this.writable = writable;
			component = new JSplitPane();

			component.setBorder(BorderFactory.createEtchedBorder());

			list = new JList<>(model);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			final JPanel left = new JPanel(new BorderLayout());
			left.add(new JScrollPane(list), BorderLayout.CENTER);

			if (writable) {
				final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
				buttons.setFloatable(false);

				final Action addAction = new AbstractAction("Add") {
					private static final long serialVersionUID = -5722810007033837355L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						addData(list.getSelectedIndex());
					}
				};

				final Action delAction = new AbstractAction("Del") {
					private static final long serialVersionUID = -8206872556606892261L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						delData(list.getSelectedIndex());
					}
				};

				buttons.add(addAction);
				buttons.add(new JSeparator());
				buttons.add(delAction);
				left.add(context.getPresentation().awesome(buttons), BorderLayout.SOUTH);
			}

			component.setLeftComponent(left);
			component.setRightComponent(new JPanel());
			list.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(final ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						saveProperty();
						loadProperty(list.getSelectedValue());
					}
				}
			});
		}

		void saveProperty() {
			if (writable && current != null && content != null) {
				current.setValue(content.getText());
			}
		}

		void loadProperty(final EditableProperty selected) {
			current = selected;
			if (selected == null) {
				component.setRightComponent(new JPanel());
			} else {
				content = new JTextArea(selected.getValue());
				if (writable) {
					content.addFocusListener(new FocusAdapter() {
						@Override
						public void focusLost(final FocusEvent e) {
							saveProperty();
						}
					});
				} else {
					content.setEditable(false);
				}
				component.setRightComponent(content);
			}
		}

		@Override
		public JSplitPane getComponent() {
			return component;
		}

		@Override
		public Collection<? extends Property> getData() {
			final List<Property> result = new ArrayList<>();
			final Beans beans = context.getBeans();
			for (final Property property : deleted.values()) {
				property.delete(); // TODO no!!!
			}
			deleted.clear();
			final Enumeration<EditableProperty> elements = model.elements();
			while (elements.hasMoreElements()) {
				final EditableProperty property = elements.nextElement();
				if (property.getProperty() == null) {
					final Property bean = beans.createProperty(property.getPropertyDescriptor());// TODO no!!!
					property.setProperty(bean);
				}
				property.save();
				result.add(property.getProperty());
			}
			return result;
		}

		@Override
		public void setData(final Collection<? extends Property> data) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					model.removeAllElements();
					for (final Property property : data) {
						model.addElement(new EditableProperty(property));
					}
				}
			});
		}

		@Override
		public double getWeight() {
			return 1;
		}

		void addData(final int index) {
			final Set<PropertyDescriptor> descriptors = new HashSet<>(
					context.getBeans().getPropertyDescriptors(entity));

			final Enumeration<EditableProperty> elements = model.elements();
			while (elements.hasMoreElements()) {
				final EditableProperty property = elements.nextElement();
				descriptors.remove(property.getPropertyDescriptor());
			}

			final List<PropertyDescriptor> addableDescriptors = new ArrayList<>(descriptors);
			addableDescriptors.sort((d1, d2) -> d1.getName().compareTo(d2.getName()));

			final AddPropertyChooser chooser = new AddPropertyChooser(context, entity, addableDescriptors);
			chooser.setVisible(true);

			final PropertyDescriptor propertyDescriptor = chooser.getSelected();
			if (propertyDescriptor != null) {
				final EditableProperty addProperty;
				final Property bean = deleted.remove(propertyDescriptor);
				if (bean != null) {
					addProperty = new EditableProperty(bean);
				} else {
					addProperty = new EditableProperty(propertyDescriptor);
				}
				addProperty.setValue("");

				if (index == -1) {
					model.addElement(addProperty);
				} else {
					model.add(index, addProperty);
				}
			}
		}

		void delData(final int index) {
			final EditableProperty property = model.getElementAt(index);
			final Property bean = property.getProperty();
			if (bean != null) {
				deleted.put(bean.getPropertyDescriptor(), bean);
			}
			model.removeElementAt(index);
		}

	}

	private final Entity entity;

	public PropertiesComponentFactory(final Entity entity, final boolean writable) {
		this(entity, writable, null);
	}

	public PropertiesComponentFactory(final Entity entity, final boolean writable, final String tab) {
		super(writable, tab);
		this.entity = entity;
	}

	@Override
	public FieldComponent<Collection<? extends Property>, JSplitPane> createComponent(final ApplicationContext context,
			final C contextBean) {
		return new PropertiesComponent(context, entity, writable);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<Collection> getDataType() {
		return Collection.class;
	}

}
