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

import java.awt.Font;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resources {

	private static final Logger LOGGER = LoggerFactory.getLogger(Resources.class);

	private final ResourceBundle messages;

	public Resources(final ResourceBundle messages) {
		this.messages = messages;
	}

	public String getMessage(final String key) {
		try {
			return messages.getString(key);
		} catch (final MissingResourceException e) {
			LOGGER.warn("Missing resource: {}", key);
			return key;
		}
	}

	public Icon getIcon(final String key) {
		return new ImageIcon(Resources.class.getResource("/icons/" + key + ".png"));
	}

	public JLabel bolden(final JLabel label) {
		final Font font = label.getFont();
		label.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
		return label;
	}

}
