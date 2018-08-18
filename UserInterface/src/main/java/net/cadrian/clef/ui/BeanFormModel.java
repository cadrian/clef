package net.cadrian.clef.ui;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.form.FieldComponentFactory;

abstract class BeanFormModel<T extends Bean> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanFormModel.class);

	static class FieldModel<T extends Bean, D, J extends JComponent> {
		final String name;
		final Class<D> type;
		final Method getter;
		final Method setter;
		final FieldComponentFactory<D, J> componentFactory;

		@SuppressWarnings("unchecked")
		FieldModel(final String name, final Method getter, final Method setter,
				final FieldComponentFactory<D, J> componentFactory) {
			this.name = name;
			this.type = (Class<D>) getter.getReturnType();
			this.getter = getter;
			this.setter = setter;
			this.componentFactory = componentFactory;
		}
	}

	private final Class<T> beanType;
	private final Map<String, FieldModel<T, ?, ?>> fields = new LinkedHashMap<>();

	BeanFormModel(final Class<T> beanType,
			final Map<String, FieldComponentFactory<?, ? extends JComponent>> componentFactories) {
		this.beanType = beanType;
		initFields(componentFactories);
	}

	private void initFields(final Map<String, FieldComponentFactory<?, ? extends JComponent>> componentFactories) {
		for (final Map.Entry<String, FieldComponentFactory<?, ? extends JComponent>> entry : componentFactories
				.entrySet()) {
			final String fieldName = entry.getKey();
			try {
				final FieldComponentFactory<?, ? extends JComponent> componentFactory = entry.getValue();
				final Class<?> dataType = componentFactory.getDataType();
				final Method getter = beanType.getMethod("get" + fieldName);
				if (!getter.getReturnType().equals(dataType)) {
					LOGGER.error("BUG: invalid field definition for {}", fieldName);
				} else {
					final Method setter = beanType.getMethod("set" + fieldName, dataType);
					final FieldModel<T, ?, ?> model = new FieldModel<>(fieldName, getter, setter, componentFactory);
					fields.put(fieldName, model);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				LOGGER.error("BUG: invalid field definition for {}", fieldName, e);
			}
		}
	}

	Map<String, FieldModel<T, ?, ?>> getFields() {
		return fields;
	}

}