package net.cadrian.clef.ui;

import net.cadrian.clef.model.Beans;

public interface ApplicationContext {

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

}
