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

	boolean applicationIsClosing();

}
