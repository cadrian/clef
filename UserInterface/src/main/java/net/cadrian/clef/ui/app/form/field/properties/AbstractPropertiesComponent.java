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
package net.cadrian.clef.ui.app.form.field.properties;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
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

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.tools.SortedListModel;

abstract class AbstractPropertiesComponent implements FieldComponent<Collection<? extends Property>, JSplitPane> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPropertiesComponent.class);

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
			left.add(context.getPresentation().awesome(buttons), BorderLayout.NORTH);

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
			LOGGER.debug("dirty: there are deleted properties");
			return true;
		}
		for (final EditableProperty property : model.getElements()) {
			if (property.isDirty()) {
				return true;
			}
		}
		if (propertyEditor == null) {
			return false;
		}

		return propertyEditor.isDirty();
	}

	Collection<PropertyDescriptor> getAddableDescriptors() {
		final Set<PropertyDescriptor> descriptors = new HashSet<>(context.getBeans().getPropertyDescriptors(entity));

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