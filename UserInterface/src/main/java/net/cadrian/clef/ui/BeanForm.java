package net.cadrian.clef.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;

public class BeanForm<T extends Bean> extends JPanel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanForm.class);

	private static final long serialVersionUID = -2371381405618937355L;

	private static class FieldModel<T extends Bean> {
		final String name;
		final Class<?> type;
		final Method getter;
		final Method setter;

		final JTextField view;

		FieldModel(final String name, final Method getter, final Method setter) {
			this.name = name;
			this.type = getter.getReturnType();
			this.getter = getter;
			this.setter = setter;

			view = new JTextField(); // TODO depends on actual type
		}

		void load(final T bean) {
			// TODO depends on actual type
			if (type == String.class) {
				try {
					view.setText((String) getter.invoke(bean));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.warn("Could not get value!!", e);
				}
			}
		}

		void save(final T bean) {
			if (setter != null) {
				// TODO depends on actual type
				if (type == String.class) {
					final String value = view.getText();
					try {
						setter.invoke(bean, value);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						LOGGER.warn("Could not set value!!", e);
					}
				}
			}
		}
	}

	private final T bean;
	private Map<String, FieldModel<T>> fields = new LinkedHashMap<>();

	public BeanForm(final T bean) {
		super(new GridBagLayout());
		this.bean = bean;

		detectGettableFields(bean);
		filterSettableFields(bean);

		int gridy = 0;
		for (FieldModel<T> field : fields.values()) {
			GridBagConstraints labelConstraints = new GridBagConstraints();
			labelConstraints.gridy = gridy;
			labelConstraints.anchor = GridBagConstraints.WEST;
			labelConstraints.insets = new Insets(2, 2, 2, 2);
			add(new JLabel(field.name), labelConstraints);

			GridBagConstraints fieldConstraints = new GridBagConstraints();
			fieldConstraints.gridx = 1;
			fieldConstraints.gridy = gridy;
			fieldConstraints.weightx = 1;
			// fieldConstraints.weighty = 1;
			fieldConstraints.insets = new Insets(2, 2, 2, 2);
			fieldConstraints.fill = GridBagConstraints.BOTH;
			add(field.view, fieldConstraints);

			gridy++;
		}
	}

	private void detectGettableFields(final T bean) {
		for (final Method method : bean.getClass().getMethods()) {
			final String methodName = method.getName();
			if (methodName.startsWith("get") && !methodName.equals("getId") && method.getParameterTypes().length == 0) {
				// this is a getter
				final String fieldName = methodName.substring(3);
				final FieldModel<T> tmp = new FieldModel<>(fieldName, method, null);
				fields.put(fieldName, tmp);
			}
		}
	}

	private void filterSettableFields(final T bean) {
		for (final Method method : bean.getClass().getMethods()) {
			final String methodName = method.getName();
			if (methodName.startsWith("set") && method.getParameterTypes().length == 1) {
				// this is a setter
				final String fieldName = methodName.substring(3);
				final FieldModel<T> tmp = fields.get(fieldName);
				if (tmp != null && tmp.getter.getReturnType().equals(method.getParameterTypes()[0])) {
					// the getter exists,
					// and the setter argument type matches the getter return type
					final FieldModel<T> fd = new FieldModel<>(fieldName, tmp.getter, method);
					fields.put(fieldName, fd);
				}
			}
		}
	}

	void load() {
		for (FieldModel<T> field : fields.values()) {
			field.load(bean);
		}
	}

	void save() {
		for (FieldModel<T> field : fields.values()) {
			field.save(bean);
		}
	}

}
