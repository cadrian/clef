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

public class PropertiesComponentFactory implements FieldComponentFactory<Collection<? extends Property>, JSplitPane> {

	private static class PropertiesComponent implements FieldComponent<Collection<? extends Property>, JSplitPane> {

		private final JSplitPane component;
		private final DefaultListModel<Property> model = new DefaultListModel<>();
		private final JList<Property> list;

		private Property current;
		private JTextArea content;

		PropertiesComponent() {
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
			component.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent e) {
					saveProperty();
				}
			});
		}

		void saveProperty() {
			if (current != null && content != null) {
				current.setValue(content.getText());
			}
		}

		void loadProperty(final Property selected) {
			current = selected;
			content = new JTextArea(selected.getValue());
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

	@Override
	public FieldComponent<Collection<? extends Property>, JSplitPane> createComponent() {
		return new PropertiesComponent();
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<Collection> getDataType() {
		return Collection.class;
	}

}
