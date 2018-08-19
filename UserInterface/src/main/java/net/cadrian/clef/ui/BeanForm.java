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
import java.awt.Font;
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
import net.cadrian.clef.ui.BeanFormModel.FieldModel;
import net.cadrian.clef.ui.form.FieldComponent;

class BeanForm<T extends Bean, C> extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanForm.class);

	private static final long serialVersionUID = -2371381405618937355L;

	private static class FieldView<T extends Bean, D, J extends JComponent, C> {
		final BeanFormModel.FieldModel<T, D, J, C> model;
		final FieldComponent<D, J> component;

		FieldView(final BeanFormModel.FieldModel<T, D, J, C> model, final FieldComponent<D, J> component) {
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
				if (model.setter == null) {
					LOGGER.info("no setter for {}", model.name);
				} else {
					model.setter.invoke(bean, component.getData());
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("BUG: could not save value", e);
			}
		}
	}

	private final T bean;
	private final Map<String, FieldView<T, ?, ?, C>> fields = new LinkedHashMap<>();

	public BeanForm(final Resources rc, final C context, final T bean, final BeanFormModel<T, C> model,
			final List<String> tabs) {
		super(new BorderLayout());
		if (bean == null) {
			throw new NullPointerException("null bean");
		}
		this.bean = bean;

		for (final FieldModel<T, ?, ?, C> fieldModel : model.getFields().values()) {
			final FieldView<T, ?, ?, C> fieldView = getFieldView(rc, context, fieldModel);
			fields.put(fieldView.model.name, fieldView);
		}

		if (tabs == null) {
			final JPanel panel = new JPanel(new GridBagLayout());
			addFields(rc, panel, null);
			add(panel, BorderLayout.CENTER);
		} else {
			final JTabbedPane tabbedPane = new JTabbedPane();
			for (final String tab : tabs) {
				LOGGER.info("Adding tab: {}", tab);
				final JPanel panel = new JPanel(new GridBagLayout());
				addFields(rc, panel, tab);
				tabbedPane.add(rc.getMessage(tab), panel);
			}
			add(tabbedPane, BorderLayout.CENTER);
		}
	}

	private void addFields(final Resources rc, final JPanel panel, final String tab) {
		int gridy = 0;
		for (final FieldView<T, ?, ?, C> fieldView : fields.values()) {
			if (tab == null || tab.equals(fieldView.model.componentFactory.getTab())) {
				final GridBagConstraints labelConstraints = new GridBagConstraints();
				labelConstraints.gridy = gridy;
				labelConstraints.anchor = GridBagConstraints.NORTHWEST;
				labelConstraints.insets = new Insets(8, 2, 2, 2);
				final String label = rc.getMessage("Field." + fieldView.model.name);
				LOGGER.debug("Label for {} is {}", fieldView.model.name, label);
				final JLabel jLabel = new JLabel(label);
				final Font font = jLabel.getFont();
				final Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());
				jLabel.setFont(boldFont);
				panel.add(jLabel, labelConstraints);

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

	private <D, J extends JComponent> FieldView<T, D, J, C> getFieldView(final Resources rc, final C context,
			final FieldModel<T, D, J, C> model) {
		final FieldComponent<D, J> component = model.componentFactory.createComponent(rc, context);
		return new FieldView<>(model, component);
	}

	void load() {
		for (final FieldView<T, ?, ?, C> field : fields.values()) {
			field.load(bean);
		}
	}

	void save() {
		for (final FieldView<T, ?, ?, C> field : fields.values()) {
			field.save(bean);
		}
	}

	public T getBean() {
		return bean;
	}

}
