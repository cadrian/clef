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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.PropertyDescriptor.Type;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.SortedListModel;
import net.cadrian.clef.ui.rte.RichTextEditor;
import net.cadrian.clef.ui.widget.DateSelector;
import net.cadrian.clef.ui.widget.FileSelector;

public class PropertiesComponentFactory<C extends Bean>
		extends AbstractFieldComponentFactory<Collection<? extends Property>, JSplitPane, C> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesComponentFactory.class);

	static class EditableProperty implements Comparable<EditableProperty> {
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

		public boolean isDirty() {
			return dirty;
		}

		@Override
		public int compareTo(final EditableProperty o) {
			return propertyDescriptor.getName().compareTo(o.propertyDescriptor.getName());
		}

		@Override
		public String toString() {
			return propertyDescriptor.getName();
		}
	}

	private static abstract class AbstractPropertiesComponent
			implements FieldComponent<Collection<? extends Property>, JSplitPane> {

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

		protected final ApplicationContext context;
		protected final Entity entity;
		protected final boolean writable;

		private final JSplitPane component;
		private final Map<PropertyDescriptor, Property> deleted = new HashMap<>();
		private final SortedListModel<EditableProperty> model = new SortedListModel<>();
		private final JList<EditableProperty> list;

		private EditableProperty current;
		private PropertyEditor propertyEditor;

		AbstractPropertiesComponent(final ApplicationContext context, final Entity entity, final boolean writable) {
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
						final int index = addData();
						if (index >= 0) {
							list.setSelectedIndex(index);
						}
						setEnabled(!getAddableDescriptors().isEmpty());
					}
				};

				final Action delAction = new AbstractAction("Del") {
					private static final long serialVersionUID = -8206872556606892261L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						delData(list.getSelectedIndex());
						addAction.setEnabled(true);
					}
				};

				buttons.add(addAction);
				buttons.add(new JSeparator(SwingConstants.VERTICAL));
				buttons.add(delAction);
				left.add(context.getPresentation().awesome(buttons), BorderLayout.SOUTH);

				list.addListSelectionListener(new ListSelectionListener() {

					@Override
					public void valueChanged(final ListSelectionEvent e) {
						delAction.setEnabled(!list.isSelectionEmpty());
					}
				});

				addAction.setEnabled(!getAddableDescriptors().isEmpty());
				delAction.setEnabled(false);
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
			if (writable && propertyEditor != null) {
				propertyEditor.save();
			}
		}

		void loadProperty(final EditableProperty selected) {
			current = selected;
			if (selected == null) {
				propertyEditor = null;
				component.setRightComponent(new JPanel());
			} else {
				propertyEditor = getEditor(current);
				component.setRightComponent(propertyEditor.getEditor());
			}
		}

		protected abstract PropertyEditor getEditor(EditableProperty selected);

		@Override
		public JSplitPane getComponent() {
			return component;
		}

		@Override
		public Collection<? extends Property> getData() {
			saveProperty();
			final List<Property> result = new ArrayList<>();
			final Beans beans = context.getBeans();
			// property deletes are handled when saving the bean holding the properties
			deleted.clear();
			for (final EditableProperty property : model.getElements()) {
				if (property.getProperty() == null) {
					final Property bean = beans.createProperty(property.getPropertyDescriptor());
					property.setProperty(bean);
				}
				property.save(); // TODO risk here: if error, this property will stay dangling
				result.add(property.getProperty());
			}
			return result;
		}

		@Override
		public void setData(final Collection<? extends Property> data) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					final List<EditableProperty> properties = new ArrayList<>();
					for (final Property property : data) {
						properties.add(new EditableProperty(property));
					}
					model.replaceAll(properties);
				}
			});
		}

		@Override
		public double getWeight() {
			return 1;
		}

		@Override
		public boolean isDirty() {
			if (!deleted.isEmpty()) {
				return true;
			}
			for (final EditableProperty property : model.getElements()) {
				if (property.isDirty()) {
					return true;
				}
			}
			return propertyEditor == null ? false : propertyEditor.isDirty();
		}

		Collection<PropertyDescriptor> getAddableDescriptors() {
			final Set<PropertyDescriptor> descriptors = new HashSet<>(
					context.getBeans().getPropertyDescriptors(entity));

			LOGGER.debug("descriptors for {}: {}", entity, descriptors);

			for (final EditableProperty property : model.getElements()) {
				final PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
				LOGGER.debug("property: {} (descriptor: {}) => removed", property, propertyDescriptor);
				descriptors.remove(propertyDescriptor);
			}

			LOGGER.debug("remaining descriptors for {}: {}", entity, descriptors);

			return descriptors;
		}

		int addData() {
			final int result;

			final List<PropertyDescriptor> addableDescriptors = new ArrayList<>(getAddableDescriptors());
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
				result = model.add(addProperty);
			} else {
				result = -1;
			}

			return result;
		}

		void delData(final int index) {
			final EditableProperty property = model.getElementAt(index);
			final Property bean = property.getProperty();
			if (bean != null) {
				deleted.put(bean.getPropertyDescriptor(), bean);
			}
			model.remove(index);
		}

	}

	private interface PropertyEditor {
		JComponent getEditor();

		boolean isDirty();

		void save();
	}

	private static class StringPropertyEditor implements PropertyEditor {
		private final EditableProperty property;
		private final RichTextEditor content;

		StringPropertyEditor(final ApplicationContext context, final boolean writable,
				final EditableProperty property) {
			this.property = property;
			content = new RichTextEditor(context);
			content.replaceSelection(property.getValue());
			content.markSave();
			content.setEditable(writable);
		}

		@Override
		public JComponent getEditor() {
			return content;
		}

		@Override
		public boolean isDirty() {
			return content.isDirty();
		}

		@Override
		public void save() {
			property.setValue(content.getText());
			content.markSave();
		}
	}

	private static class DatePropertyEditor implements PropertyEditor {
		private final EditableProperty property;
		private final JPanel container;
		private final DateSelector content;

		DatePropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
			this.property = property;

			content = new DateSelector(context, writable);
			content.setDateString(property.getValue());

			container = new JPanel(new GridBagLayout());
			final GridBagConstraints constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1;
			container.add(content, constraints);
		}

		@Override
		public JComponent getEditor() {
			return container;
		}

		@Override
		public boolean isDirty() {
			return content.isDirty();
		}

		@Override
		public void save() {
			property.setValue(content.getDateString());
			content.markSave();
		}
	}

	private static abstract class AbstractFilePropertyEditor implements PropertyEditor {
		protected final EditableProperty property;
		private final JPanel container;
		protected final FileSelector content;

		// TODO add a button to download the content of the file, either from the file
		// system or from the database, depending on the type

		AbstractFilePropertyEditor(final ApplicationContext context, final boolean writable,
				final EditableProperty property) {
			this.property = property;
			content = new FileSelector(context, writable);

			container = new JPanel(new GridBagLayout());
			final GridBagConstraints constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.CENTER;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1;
			container.add(content, constraints);
		}

		@Override
		public JComponent getEditor() {
			return container;
		}

		@Override
		public boolean isDirty() {
			return content.isDirty();
		}
	}

	private static class PathPropertyEditor extends AbstractFilePropertyEditor {

		PathPropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
			super(context, writable, property);
			content.setFile(property.getValue());
		}

		@Override
		public void save() {
			property.setValue(content.getFile().getAbsolutePath());
			content.markSave();
		}
	}

	private static class FilePropertyEditor extends AbstractFilePropertyEditor {

		private static final Encoder BASE64_ENCODER = Base64.getEncoder();
		private static final Decoder BASE64_DECODER = Base64.getDecoder();

		private static int indexOfSep(final byte[] data) {
			for (int i = 0; i < data.length; i++) {
				if (data[i] == 0) {
					return i;
				}
			}
			return data.length;
		}

		FilePropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
			super(context, writable, property);
			final byte[] serializedData = BASE64_DECODER.decode(property.getValue());
			final int sep = indexOfSep(serializedData);
			final String path = new String(serializedData, 0, sep);
			content.setFile(path);
		}

		@Override
		public void save() {
			final File file = content.getFile();
			final String path = file.getAbsolutePath();
			byte[] data = null;

			// TODO support big files (1.5Gb max for now -- because of BASE64)

			try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
					ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				final byte[] buffer = new byte[4096];
				int n;
				while ((n = in.read(buffer)) >= 0) {
					out.write(buffer, 0, n);
				}
				data = out.toByteArray();
			} catch (final IOException e) {
				LOGGER.error("error while reading file, not saving data", e);
			}

			try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				out.write(path.getBytes());
				if (data != null) {
					out.write(0);
					out.write(data);
				}
			} catch (final IOException e) {
				LOGGER.error("error while serializing data", e);
			}
			property.setValue(BASE64_ENCODER.encodeToString(data));
			content.markSave();
		}

	}

	private static class PropertiesComponent extends AbstractPropertiesComponent {

		PropertiesComponent(final ApplicationContext context, final Entity entity, final boolean writable) {
			super(context, entity, writable);
		}

		@Override
		protected PropertyEditor getEditor(final EditableProperty selected) {
			final Type type = selected.getPropertyDescriptor().getType();
			switch (type) {
			case string:
				return new StringPropertyEditor(context, writable, selected);
			case date:
				return new DatePropertyEditor(context, writable, selected);
			case file:
				return new FilePropertyEditor(context, writable, selected);
			case path:
				return new PathPropertyEditor(context, writable, selected);
			default:
				return null;
			}
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
