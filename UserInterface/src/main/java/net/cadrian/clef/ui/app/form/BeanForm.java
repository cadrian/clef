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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import net.cadrian.clef.ui.app.form.field.FieldModel;

public class BeanForm<T extends Bean> extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanForm.class);

	private static final long serialVersionUID = -2371381405618937355L;

	private final T bean;
	private final Map<String, FieldView<T, ?, ?>> fields = new LinkedHashMap<>();

	public BeanForm(final ApplicationContext context, final T bean, final BeanFormModel<T> model,
			final List<String> tabs) {
		super(new BorderLayout());
		this.bean = Objects.requireNonNull(bean);

		for (final FieldModel<T, ?, ?> fieldModel : model.getFields().values()) {
			final FieldView<T, ?, ?> fieldView = getFieldView(context, fieldModel, bean);
			fields.put(fieldView.model.getName(), fieldView);
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
		for (final FieldView<T, ?, ?> fieldView : fields.values()) {
			if (tab == null || tab.equals(fieldView.model.getTab())) {
				final GridBagConstraints labelConstraints = new GridBagConstraints();
				labelConstraints.gridy = gridy;
				labelConstraints.anchor = GridBagConstraints.NORTHWEST;
				labelConstraints.insets = new Insets(8, 2, 2, 2);
				final String name = fieldView.model.getName();
				final String label = presentation.getMessage("Field." + name);
				LOGGER.debug("Label for {} is {}", name, label);
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
				LOGGER.debug("tab mismatch: {} vs {}", tab, fieldView.model.getTab());
			}
		}
	}

	private <D, J extends JComponent> FieldView<T, D, J> getFieldView(final ApplicationContext context,
			final FieldModel<T, D, J> model, final T bean) {
		final FieldComponent<D, J> component = model.createComponent(bean, context);
		return new FieldView<>(model, component);
	}

	public void load() {
		for (final FieldView<T, ?, ?> field : fields.values()) {
			field.load(bean);
		}
	}

	public void save() {
		for (final FieldView<T, ?, ?> field : fields.values()) {
			field.save(bean);
		}
	}

	public T getBean() {
		return bean;
	}

	public boolean isDirty() {
		for (final FieldView<T, ?, ?> field : fields.values()) {
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
