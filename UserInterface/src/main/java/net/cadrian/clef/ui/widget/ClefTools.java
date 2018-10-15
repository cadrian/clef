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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.cadrian.clef.ui.ApplicationContext;

public class ClefTools extends JToolBar {

	private static final long serialVersionUID = 5025926948132545775L;

	public static enum Tool {
		Filter(false), Add(true), Del(false), Move(false), Save(true);

		final boolean needsSeparatorBefore;

		Tool(final boolean needsSeparatorBefore) {
			this.needsSeparatorBefore = needsSeparatorBefore;
		}
	}

	public interface Listener {
		void toolCalled(ClefTools tools, Tool tool);
	}

	private final Map<Tool, Action> actions = new HashMap<>();
	private final Map<Tool, JButton> buttons = new HashMap<>();

	private final List<Listener> listeners = new ArrayList<>();

	public ClefTools(final ApplicationContext context, final Tool... tools) {
		super(SwingConstants.HORIZONTAL);
		setFloatable(false);

		Arrays.sort(tools);
		boolean first = true;
		for (final Tool tool : tools) {
			if (!first && tool.needsSeparatorBefore) {
				addSeparator();
			}
			final Action action = new AbstractAction(tool.name()) {
				private static final long serialVersionUID = -1937859824187249283L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					fireToolCalled(tool);
				}
			};
			actions.put(tool, action);
			buttons.put(tool, add(action));
			first = false;
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

	public Action getAction(final Tool tool) {
		return actions.get(tool);
	}

	public Point getLocation(final Tool tool) {
		final Point result;
		final JButton button = buttons.get(tool);
		if (button != null) {
			result = button.getLocation();
		} else {
			result = null;
		}
		return result;
	}

	public Dimension getSize(final Tool tool) {
		final Dimension result;
		final JButton button = buttons.get(tool);
		if (button != null) {
			result = button.getSize();
		} else {
			result = null;
		}
		return result;
	}

	public Point getLocationOnScreen(final Tool tool) {
		final Point result;
		final JButton button = buttons.get(tool);
		if (button != null) {
			result = button.getLocationOnScreen();
		} else {
			result = null;
		}
		return result;
	}

}
