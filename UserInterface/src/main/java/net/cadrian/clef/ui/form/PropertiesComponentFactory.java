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
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
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

import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.ui.Resources;

public class PropertiesComponentFactory<C>
		extends AbstractFieldComponentFactory<Collection<? extends Property>, JSplitPane, C> {

	private static class PropertiesComponent implements FieldComponent<Collection<? extends Property>, JSplitPane> {

		private static class EditableProperty {
			private final PropertyDescriptor propertyDescriptor;
			private Property property;
			private String value;
			private boolean dirty;

			EditableProperty(final PropertyDescriptor propertyDescriptor) {
				this.propertyDescriptor = propertyDescriptor;
				this.property = null;
				this.value = null;
				this.dirty = false;
			}

			EditableProperty(Property property) {
				this.propertyDescriptor = property.getPropertyDescriptor();
				this.property = property;
				this.value = property.getValue();
				this.dirty = false;
			}

			public Property getProperty() {
				return property;
			}

			void setProperty(Property property) {
				this.property = property;
				this.dirty = true;
			}

			public String getValue() {
				return value;
			}

			public void setValue(String value) {
				this.value = value;
				this.dirty = true;
			}

			public void save() {
				if (dirty) {
					property.setValue(value);
					dirty = false;
				}
			}

		}

		private final JSplitPane component;
		private final DefaultListModel<EditableProperty> model = new DefaultListModel<>();
		private final JList<EditableProperty> list;
		private final boolean writable;

		private EditableProperty current;
		private JTextArea content;

		PropertiesComponent(final Resources rc, final boolean writable) {
			this.writable = writable;
			component = new JSplitPane();

			component.setBorder(BorderFactory.createEtchedBorder());

			list = new JList<>(model);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
			buttons.setFloatable(false);

			final Action addAction = new AbstractAction("Add") {
				private static final long serialVersionUID = -5722810007033837355L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					// TODO addData();
				}
			};

			final Action delAction = new AbstractAction("Del") {
				private static final long serialVersionUID = -8206872556606892261L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					// TODO delData();
				}
			};

			final Action saveAction = new AbstractAction("Save") {
				private static final long serialVersionUID = -8659808353683696964L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					// TODO saveData();
				}
			};

			final JPanel left = new JPanel(new BorderLayout());
			left.add(new JScrollPane(list), BorderLayout.CENTER);

			buttons.add(addAction);
			buttons.add(saveAction);
			buttons.add(new JSeparator());
			buttons.add(delAction);
			left.add(rc.awesome(buttons), BorderLayout.SOUTH);

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

		@Override
		public JSplitPane getComponent() {
			return component;
		}

		@Override
		public Collection<? extends Property> getData() {
			// TODO must save properties first

			final List<Property> result = new ArrayList<>();
			final Enumeration<EditableProperty> elements = model.elements();
			while (elements.hasMoreElements()) {
				result.add(elements.nextElement().getProperty());
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

	}

	public PropertiesComponentFactory(final boolean writable) {
		this(writable, null);
	}

	public PropertiesComponentFactory(final boolean writable, final String tab) {
		super(writable, tab);
	}

	@Override
	public FieldComponent<Collection<? extends Property>, JSplitPane> createComponent(final Resources rc,
			final C context, JFrame parent) {
		return new PropertiesComponent(rc, writable);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<Collection> getDataType() {
		return Collection.class;
	}

}
