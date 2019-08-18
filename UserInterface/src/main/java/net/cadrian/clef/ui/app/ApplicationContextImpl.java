/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
package net.cadrian.clef.ui.app;

import java.util.HashMap;
import java.util.Map;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;

class ApplicationContextImpl implements ApplicationContext {

	private final Beans beans;
	private final PresentationImpl presentation;
	private boolean closing;

	private final Map<AdvancedConfigurationEntry, AdvancedConfiguration<?>> listeners = new HashMap<>();

	ApplicationContextImpl(final Beans beans, final PresentationImpl presentation) {
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

	public void setClosing(final boolean closing) {
		this.closing = closing;
	}

	@Override
	public boolean applicationIsClosing() {
		return closing;
	}

}
