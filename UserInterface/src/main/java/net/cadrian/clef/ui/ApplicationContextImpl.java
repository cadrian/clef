package net.cadrian.clef.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cadrian.clef.model.Beans;

class ApplicationContextImpl implements ApplicationContext {

	private final Beans beans;
	private final Presentation presentation;

	private static class AdvancedConfiguration<T> {
		private final AdvancedConfigurationEntry entry;
		private T value;
		private final List<ApplicationContextListener<T>> listeners = new ArrayList<>();

		private AdvancedConfiguration(final AdvancedConfigurationEntry entry) {
			this.entry = entry;
		}

		void addApplicationContextListener(final ApplicationContextListener<T> listener) {
			listeners.add(listener);
		}

		void removeApplicationContextListener(final ApplicationContextListener<T> listener) {
			listeners.remove(listener);
		}

		public T getValue() {
			return value;
		}

		public void setValue(final T value) {
			this.value = value;
			fireAdvancedConfigurationChanged();
		}

		private void fireAdvancedConfigurationChanged() {
			for (final ApplicationContextListener<T> listener : listeners) {
				listener.onAdvancedConfigurationChange(entry, value);
			}
		}
	}

	private final Map<AdvancedConfigurationEntry, AdvancedConfiguration<?>> listeners = new HashMap<>();

	ApplicationContextImpl(final Beans beans, final Presentation presentation) {
		this.beans = beans;
		this.presentation = presentation;
		for (final AdvancedConfigurationEntry entry : AdvancedConfigurationEntry.values()) {
			listeners.put(entry, new AdvancedConfiguration<>(entry));
		}
	}

	@Override
	public Beans getBeans() {
		return beans;
	}

	@Override
	public Presentation getPresentation() {
		return presentation;
	}

	@Override
	public <T> void addApplicationContextListener(final AdvancedConfigurationEntry entry,
			final ApplicationContextListener<T> listener) {
		@SuppressWarnings("unchecked")
		final AdvancedConfiguration<T> advancedConfiguration = (AdvancedConfiguration<T>) listeners.get(entry);
		advancedConfiguration.addApplicationContextListener(listener);
	}

	@Override
	public <T> void removeApplicationContextListener(final AdvancedConfigurationEntry entry,
			final ApplicationContextListener<T> listener) {
		@SuppressWarnings("unchecked")
		final AdvancedConfiguration<T> advancedConfiguration = (AdvancedConfiguration<T>) listeners.get(entry);
		advancedConfiguration.removeApplicationContextListener(listener);
	}

	@Override
	public <T> T getValue(final AdvancedConfigurationEntry entry) {
		@SuppressWarnings("unchecked")
		final AdvancedConfiguration<T> advancedConfiguration = (AdvancedConfiguration<T>) listeners.get(entry);
		return advancedConfiguration.getValue();
	}

	@Override
	public <T> void setValue(final AdvancedConfigurationEntry entry, final T value) {
		@SuppressWarnings("unchecked")
		final AdvancedConfiguration<T> advancedConfiguration = (AdvancedConfiguration<T>) listeners.get(entry);
		advancedConfiguration.setValue(value);
	}

}