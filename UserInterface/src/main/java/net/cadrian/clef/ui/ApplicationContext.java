package net.cadrian.clef.ui;

import net.cadrian.clef.model.Beans;

public interface ApplicationContext {

	enum AdvancedConfigurationEntry {
		allowStartWrite;
	}

	interface ApplicationContextListener<T> {
		void onAdvancedConfigurationChange(AdvancedConfigurationEntry entry, T value);
	}

	/**
	 * Bean model access
	 *
	 * @return
	 */
	Beans getBeans();

	/**
	 * UI resources access
	 *
	 * @return
	 */
	Presentation getPresentation();

	<T> void addApplicationContextListener(AdvancedConfigurationEntry entry, ApplicationContextListener<T> listener);

	<T> void removeApplicationContextListener(AdvancedConfigurationEntry entry, ApplicationContextListener<T> listener);

	<T> T getValue(AdvancedConfigurationEntry entry);

	<T> void setValue(AdvancedConfigurationEntry entry, T value);

}
