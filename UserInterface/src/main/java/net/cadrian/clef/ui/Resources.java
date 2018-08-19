package net.cadrian.clef.ui;

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Resources {

	private final ResourceBundle messages;

	public Resources(final ResourceBundle messages) {
		this.messages = messages;
	}

	public String getMessage(final String key) {
		return messages.getString(key);
	}

	public Icon getIcon(final String key) {
		return new ImageIcon(Resources.class.getResource("/icons/" + key + ".png"));
	}

}
