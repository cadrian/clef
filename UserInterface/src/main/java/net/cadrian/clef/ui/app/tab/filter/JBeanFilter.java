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
package net.cadrian.clef.ui.app.tab.filter;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.cadrian.clef.model.Bean;

public abstract class JBeanFilter<T extends Bean> extends JPanel {

	private static final long serialVersionUID = 1810824695887700978L;

	private final List<ActionListener> listeners = new ArrayList<>();

	protected JBeanFilter(final LayoutManager layout) {
		super(layout);
	}

	public abstract boolean isBeanVisible(final T bean);

	public void addActionListener(final ActionListener listener) {
		listeners.add(listener);
	}

	public void removeActionListener(final ActionListener listener) {
		listeners.remove(listener);
	}

	protected final void fireActionPerformed(final ActionEvent e) {
		for (final ActionListener listener : listeners) {
			listener.actionPerformed(e);
		}
	}

}
