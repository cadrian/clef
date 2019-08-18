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
package net.cadrian.clef.ui.app.form.field.stat;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

final class CommonStatisticsFieldComponent<T extends Bean, D extends StatisticsData<T>>
		implements FieldComponent<D, JPanel> {

	private final ApplicationContext context;
	private D data;

	private final JPanel component = new JPanel(new BorderLayout());

	CommonStatisticsFieldComponent(final ApplicationContext context) {
		this.context = context;
	}

	@Override
	public JPanel getComponent() {
		return component;
	}

	@Override
	public D getData() {
		SwingUtilities.invokeLater(() -> data.refresh());
		return data;
	}

	@Override
	public void setData(final D data) {
		this.data = data;
		SwingUtilities.invokeLater(() -> install(data));
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	private void install(final D data) {
		component.removeAll();
		component.add(data.getStatisticsComponent(context), BorderLayout.CENTER);
	}

}
