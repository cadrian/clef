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
package net.cadrian.clef.ui.widget;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext;

public class VersionSpinner extends JPanel {

	private static final long serialVersionUID = 8419922467823938576L;

	private static final Logger LOGGER = LoggerFactory.getLogger(VersionSpinner.class);

	private final class LeftAction extends AbstractAction {
		private final Controller controller;
		private static final long serialVersionUID = -7912301007428815095L;

		private LeftAction(final Controller controller) {
			super("SpinLeft");
			this.controller = controller;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			controller.previous();
			refreshNow();
		}
	}

	private final class RightAction extends AbstractAction {
		private final Controller controller;
		private static final long serialVersionUID = -7135069953331039207L;

		private RightAction(final Controller controller) {
			super("SpinRight");
			this.controller = controller;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			controller.next();
			refreshNow();
		}
	}

	private final class CreateAction extends AbstractAction {
		private final Controller controller;
		private static final long serialVersionUID = 7246511819586352379L;

		private CreateAction(final Controller controller) {
			super("Add");
			this.controller = controller;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			controller.create();
			if (controller.hasNext()) {
				controller.next();
			}
			refreshNow();
		}
	}

	private final class Refresher implements Runnable {
		@Override
		public void run() {
			refreshNow();
		}
	}

	public interface Controller {
		boolean hasPrevious();

		boolean hasNext();

		int getCurrent();

		void previous();

		void next();

		void create();
	}

	private final Controller controller;
	private final JLabel version;
	private final Action leftAction;
	private final Action rightAction;
	private final Action createAction;

	public VersionSpinner(final ApplicationContext context, final Controller controller) {
		super(new BorderLayout());
		this.controller = controller;

		final String versionString = Integer.toString(controller.getCurrent());
		LOGGER.debug("version: {}", versionString);
		version = new JLabel(versionString, SwingConstants.CENTER);

		leftAction = new LeftAction(controller);
		rightAction = new RightAction(controller);
		createAction = new CreateAction(controller);

		final JToolBar leftButtons = new JToolBar(SwingConstants.HORIZONTAL);
		leftButtons.setFloatable(false);
		leftButtons.add(leftAction);
		final JToolBar rightButtons = new JToolBar(SwingConstants.HORIZONTAL);
		rightButtons.setFloatable(false);
		rightButtons.add(rightAction);
		rightButtons.add(createAction);

		add(version, BorderLayout.CENTER);
		add(context.getPresentation().awesome(leftButtons), BorderLayout.WEST);
		add(context.getPresentation().awesome(rightButtons), BorderLayout.EAST);

		refreshNow();
	}

	private void refreshNow() {
		final String versionString = Integer.toString(controller.getCurrent());
		LOGGER.debug("version: {}", versionString);
		version.setText(versionString);
		leftAction.setEnabled(controller.hasPrevious());
		rightAction.setEnabled(controller.hasNext());
		createAction.setEnabled(!controller.hasNext());
	}

	public void refresh() {
		SwingUtilities.invokeLater(new Refresher());
	}

}
