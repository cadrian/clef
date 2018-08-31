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
package net.cadrian.clef.ui.widget;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.cadrian.clef.ui.ApplicationContext;

public class ClefTools extends JToolBar {

	private static final long serialVersionUID = 5025926948132545775L;

	public static enum Tool {
		Add, Save, Del, Filter;
	}

	public interface Listener {
		void toolCalled(ClefTools tools, Tool tool);
	}

	private final Map<Tool, Action> actions = new HashMap<>();

	private final List<Listener> listeners = new ArrayList<>();

	public ClefTools(final ApplicationContext context, final Tool... tools) {
		super(SwingConstants.HORIZONTAL);
		setFloatable(false);

		Arrays.sort(tools);
		boolean needSeparator = false;
		for (final Tool tool : tools) {
			final Action action;
			switch (tool) {
			case Add:
				action = new AbstractAction("Add") {
					private static final long serialVersionUID = -5722810007033837355L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						fireToolCalled(Tool.Add);
					}
				};
				break;
			case Save:
				if (needSeparator) {
					addSeparator();
				}
				action = new AbstractAction("Save") {
					private static final long serialVersionUID = -8659808353683696964L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						fireToolCalled(Tool.Save);
					}
				};
				action.setEnabled(false);
				break;
			case Del:
				action = new AbstractAction("Del") {
					private static final long serialVersionUID = -8206872556606892261L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						fireToolCalled(Tool.Del);
					}
				};
				action.setEnabled(false);
				break;
			case Filter:
				if (needSeparator) {
					addSeparator();
				}
				action = new AbstractAction("Filter") {
					private static final long serialVersionUID = -8659808353683696964L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						fireToolCalled(Tool.Filter);
					}
				};
				break;
			default:
				throw new IllegalArgumentException("Unknown tool: " + tool);
			}
			actions.put(tool, action);
			add(action);
			needSeparator = true;
		}

		context.getPresentation().awesome(this);
	}

	private void fireToolCalled(final Tool tool) {
		for (final Listener listener : listeners) {
			listener.toolCalled(this, tool);
		}
	}

	public void addListener(final Listener listener) {
		listeners.add(listener);
	}

	public Action getAction(Tool tool) {
		return actions.get(tool);
	}

}
