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

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.cadrian.clef.model.bean.Property;

public class PropertiesComponentFactory
		extends AbstractFieldComponentFactory<Collection<? extends Property>, JSplitPane> {

	private static class PropertiesComponent implements FieldComponent<Collection<? extends Property>, JSplitPane> {

		private final JSplitPane component;
		private final DefaultListModel<Property> model = new DefaultListModel<>();
		private final JList<Property> list;
		private final boolean writable;

		private Property current;
		private JTextArea content;

		PropertiesComponent(final boolean writable) {
			this.writable = writable;
			component = new JSplitPane();
			list = new JList<>(model);
			component.setLeftComponent(new JScrollPane(list));
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

		void loadProperty(final Property selected) {
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
			final List<Property> result = new ArrayList<>();
			final Enumeration<Property> elements = model.elements();
			while (elements.hasMoreElements()) {
				result.add(elements.nextElement());
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
						model.addElement(property);
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
		super(writable);
	}

	@Override
	public FieldComponent<Collection<? extends Property>, JSplitPane> createComponent() {
		return new PropertiesComponent(writable);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<Collection> getDataType() {
		return Collection.class;
	}

}
