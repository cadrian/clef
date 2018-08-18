package net.cadrian.clef.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.BeanFormModel.FieldModel;
import net.cadrian.clef.ui.form.FieldComponent;

class BeanForm<T extends Bean> extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanForm.class);

	private static final long serialVersionUID = -2371381405618937355L;

	private static class FieldView<T extends Bean, D, J extends JComponent> {
		final BeanFormModel.FieldModel<T, D, J> model;
		final FieldComponent<D, J> component;

		FieldView(final BeanFormModel.FieldModel<T, D, J> model, final FieldComponent<D, J> component) {
			this.model = model;
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
				model.setter.invoke(bean, component.getData());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("BUG: could not save value", e);
			}
		}

	}

	private final T bean;
	private final Map<String, FieldView<T, ?, ?>> fields = new LinkedHashMap<>();

	public BeanForm(final T bean, final BeanFormModel<T> model) {
		super(new GridBagLayout());
		this.bean = bean;

		for (final FieldModel<T, ?, ?> fieldModel : model.getFields().values()) {
			if (fieldModel.setter != null) {
				final FieldView<T, ?, ?> fieldView = getFieldView(fieldModel);
				fields.put(fieldView.model.name, fieldView);
			}
		}

		int gridy = 0;
		for (final FieldView<T, ?, ?> fieldView : fields.values()) {
			final GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.gridy = gridy;
			labelConstraints.anchor = GridBagConstraints.WEST;
			labelConstraints.insets = new Insets(2, 2, 2, 2);
			add(new JLabel(fieldView.model.name), labelConstraints);

			final GridBagConstraints fieldConstraints = new GridBagConstraints();
			fieldConstraints.gridx = 1;
			fieldConstraints.gridy = gridy;
			fieldConstraints.weightx = 1;
			fieldConstraints.weighty = fieldView.component.getWeight();
			fieldConstraints.insets = new Insets(2, 2, 2, 2);
			fieldConstraints.fill = GridBagConstraints.BOTH;
			add(fieldView.component.getComponent(), fieldConstraints);

			gridy++;
		}
	}

	private <D, J extends JComponent> FieldView<T, D, J> getFieldView(final FieldModel<T, D, J> model) {
		final FieldComponent<D, J> component = model.componentFactory.createComponent();
		return new FieldView<>(model, component);
	}

	void load() {
		for (final FieldView<T, ?, ?> field : fields.values()) {
			field.load(bean);
		}
	}

	void save() {
		for (final FieldView<T, ?, ?> field : fields.values()) {
			field.save(bean);
		}
	}

}
