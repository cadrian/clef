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
package net.cadrian.clef.ui.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.ApplicationContext.ApplicationContextListener;

class AdvancedConfiguration<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedConfiguration.class);

	private final AdvancedConfigurationEntry entry;
	private T value;
	private final List<ApplicationContextListener<T>> listeners = new ArrayList<>();

	AdvancedConfiguration(final AdvancedConfigurationEntry entry) {
		this.entry = entry;
	}

	void addApplicationContextListener(final ApplicationContextListener<T> listener) {
		LOGGER.debug(">>> {}", listener);
		listeners.add(listener);
	}

	void removeApplicationContextListener(final ApplicationContextListener<T> listener) {
		LOGGER.debug(">>> {}", listener);
		listeners.remove(listener);
	}

	public T getValue() {
		return value;
	}

	public void setValue(final T value) {
		LOGGER.debug(">>> {}", value);
		this.value = value;
		fireAdvancedConfigurationChanged();
	}

	private void fireAdvancedConfigurationChanged() {
		for (final ApplicationContextListener<T> listener : new ArrayList<>(listeners)) {
			LOGGER.debug(">>> {}", listener);
			listener.onAdvancedConfigurationChange(entry, value);
		}
	}
}
