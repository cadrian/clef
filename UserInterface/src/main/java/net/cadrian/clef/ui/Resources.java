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

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resources {

	private static final Logger LOGGER = LoggerFactory.getLogger(Resources.class);

	private final ResourceBundle messages;
	private final Font awesomeFont;

	public Resources(final ResourceBundle messages) {
		this.messages = messages;

		Font ft = null;
		for (final String f : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
			if (f.equals("FontAwesome")) {
				LOGGER.debug("Awesome!");
				ft = Font.decode(f);
				break;
			}
		}
		if (ft == null) {
			throw new RuntimeException("Missing some Awesome.");
		}
		awesomeFont = new Font(ft.getName(), ft.getStyle(), 20);
		LOGGER.debug("Font Awesome: {}", awesomeFont);
	}

	public String getMessage(final String key, final Object... args) {
		try {
			return String.format(messages.getString(key), args);
		} catch (final MissingResourceException e) {
			LOGGER.warn("Missing resource: {}", key);
			return key;
		}
	}

	public JButton awesome(final JButton button) {
		button.setText(getMessage("Awesome." + button.getText()));
		button.setFont(awesomeFont);
		return button;
	}

	public JToolBar awesome(final JToolBar toolbar) {
		toolbar.setFont(awesomeFont);
		for (final Component component : toolbar.getComponents()) {
			if (component instanceof JButton) {
				awesome((JButton) component);
			}
		}
		return toolbar;
	}

	public JLabel bolden(final JLabel label) {
		final Font font = label.getFont();
		label.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
		return label;
	}

}
