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
package net.cadrian.clef.ui.app.form;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

public class BeanForm<T extends Bean, C extends Bean> extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanForm.class);

	private static final long serialVersionUID = -2371381405618937355L;

	private static class FieldView<T extends Bean, D, J extends JComponent, C extends Bean> {
		final FieldModel<T, D, J, C> model;
		final FieldComponent<D, J> component;

		FieldView(final FieldModel<T, D, J, C> model, final FieldComponent<D, J> component) {
			if (model == null) {
				throw new NullPointerException("null model");
			}
			this.model = model;
			if (component == null) {
				throw new NullPointerException("null component");
			}
			this.component = component;
		}

		void load(final T bean) {
			try {
				@SuppressWarnings("unchecked")
				final D data = (D) model.getter.invoke(bean);
				component.setData(data);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("BUG: could not load value", e);
			}
		}

		void save(final T bean) {
			try {
				LOGGER.debug("saving {}.{}", bean, model.name);
				final D data = component.getData();
				if (model.setter == null) {
					LOGGER.info("no setter for {}", model.name);
				} else {
					model.setter.invoke(bean, data);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("BUG: could not save value", e);
			}
		}

		public boolean isDirty() {
			final boolean result = component.isDirty();
			if (result) {
				LOGGER.debug("dirty: {}", model.name);
			}
			return result;
		}
	}

	private final T bean;
	private final Map<String, FieldView<T, ?, ?, C>> fields = new LinkedHashMap<>();

	public BeanForm(final ApplicationContext context, final C contextBean, final T bean,
			final BeanFormModel<T, C> model, final List<String> tabs) {
		super(new BorderLayout());
		if (bean == null) {
			throw new NullPointerException("null bean");
		}
		this.bean = bean;

		for (final FieldModel<T, ?, ?, C> fieldModel : model.getFields().values()) {
			final FieldView<T, ?, ?, C> fieldView = getFieldView(context, contextBean, fieldModel);
			fields.put(fieldView.model.name, fieldView);
		}

		final Presentation presentation = context.getPresentation();

		if (tabs == null) {
			final JPanel panel = new JPanel(new GridBagLayout());
			addFields(presentation, panel, null);
			add(panel, BorderLayout.CENTER);
		} else {
			final JTabbedPane tabbedPane = new JTabbedPane();
			for (final String tab : tabs) {
				LOGGER.info("Adding tab: {}", tab);
				final JPanel panel = new JPanel(new GridBagLayout());
				addFields(presentation, panel, tab);
				tabbedPane.add(presentation.getMessage(tab), panel);
			}
			add(tabbedPane, BorderLayout.CENTER);
		}
	}

	private void addFields(final Presentation presentation, final JPanel panel, final String tab) {
		int gridy = 0;
		for (final FieldView<T, ?, ?, C> fieldView : fields.values()) {
			if (tab == null || tab.equals(fieldView.model.componentFactory.getTab())) {
				final GridBagConstraints labelConstraints = new GridBagConstraints();
				labelConstraints.gridy = gridy;
				labelConstraints.anchor = GridBagConstraints.NORTHWEST;
				labelConstraints.insets = new Insets(8, 2, 2, 2);
				final String label = presentation.getMessage("Field." + fieldView.model.name);
				LOGGER.debug("Label for {} is {}", fieldView.model.name, label);
				panel.add(presentation.bold(new JLabel(label)), labelConstraints);

				final GridBagConstraints fieldConstraints = new GridBagConstraints();
				fieldConstraints.gridx = 1;
				fieldConstraints.gridy = gridy;
				fieldConstraints.weightx = 1;
				fieldConstraints.weighty = fieldView.component.getWeight();
				fieldConstraints.insets = new Insets(2, 2, 2, 2);
				fieldConstraints.fill = GridBagConstraints.BOTH;
				panel.add(fieldView.component.getComponent(), fieldConstraints);

				gridy++;
			} else if (tab != null) {
				LOGGER.debug("tab mismatch: {} vs {}", tab, fieldView.model.componentFactory.getTab());
			}
		}
	}

	private <D, J extends JComponent> FieldView<T, D, J, C> getFieldView(final ApplicationContext context,
			final C contextBean, final FieldModel<T, D, J, C> model) {
		final FieldComponent<D, J> component = model.componentFactory.createComponent(context, contextBean);
		return new FieldView<>(model, component);
	}

	public void load() {
		for (final FieldView<T, ?, ?, C> field : fields.values()) {
			field.load(bean);
		}
	}

	public void save() {
		for (final FieldView<T, ?, ?, C> field : fields.values()) {
			field.save(bean);
		}
	}

	public T getBean() {
		return bean;
	}

	public boolean isDirty() {
		for (final FieldView<T, ?, ?, C> field : fields.values()) {
			if (field.isDirty()) {
				LOGGER.debug("dirty: {}", bean);
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "form:" + bean;
	}

}
