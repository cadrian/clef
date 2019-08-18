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

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.Presentation;

class PresentationImpl implements Presentation {

	private static final Logger LOGGER = LoggerFactory.getLogger(PresentationImpl.class);

	private final ResourceBundle messages;
	private final JFrame frame;
	private final Font awesomeFont;

	PresentationImpl(final JFrame frame) {
		final Locale locale = Locale.getDefault();
		LOGGER.info("Current locale: {}", locale);
		messages = ResourceBundle.getBundle("ClefMessages");
		LOGGER.info("Messages bundle: {}", messages);

		this.frame = frame;

		Font ft = null;
		for (final String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
			if (f.equals("FontAwesome") || f.equals("Font Awesome 5 Free Regular")) {
				LOGGER.debug("Awesome!");
				ft = Font.decode(f);
				break;
			}
		}
		if (ft == null) {
			throw new RuntimeException("Missing some Awesome.");
		}
		awesomeFont = new Font(ft.getName(), ft.getStyle(), 16);
		LOGGER.debug("Font Awesome: {}", awesomeFont);

		final Enumeration<String> keys = messages.getKeys();
		while (keys.hasMoreElements()) {
			LOGGER.debug("bundle key: {}", keys.nextElement());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.cadrian.clef.ui.IPres#getMessage(java.lang.String, java.lang.Object)
	 */
	@Override
	public String getMessage(final String key, final Object... args) {
		try {
			return String.format(messages.getString(key), args);
		} catch (final MissingResourceException e) {
			LOGGER.warn("Missing resource: {}", key);
			return key;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.cadrian.clef.ui.IPres#awesome(B)
	 */
	@Override
	public <B extends AbstractButton> B awesome(final B button) {
		button.setText(getMessage("Awesome." + button.getText()));
		button.setFont(awesomeFont);
		return button;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.cadrian.clef.ui.IPres#awesome(javax.swing.JToolBar)
	 */
	@Override
	public JToolBar awesome(final JToolBar toolbar) {
		toolbar.setFont(awesomeFont);
		for (final Component component : toolbar.getComponents()) {
			if (component instanceof AbstractButton) {
				awesome((AbstractButton) component);
			}
		}
		return toolbar;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.cadrian.clef.ui.IPres#bold(javax.swing.JLabel)
	 */
	@Override
	public JLabel bold(final JLabel label) {
		final Font font = label.getFont();
		label.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
		return label;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.cadrian.clef.ui.IPres#getApplicationFrame()
	 */
	@Override
	public JFrame getApplicationFrame() {
		return frame;
	}

}
