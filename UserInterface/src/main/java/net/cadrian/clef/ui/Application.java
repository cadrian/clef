package net.cadrian.clef.ui;

import javax.swing.JFrame;

import net.cadrian.clef.model.Beans;

public class Application extends JFrame {

	private static final long serialVersionUID = 6368233544150671678L;

	private final Beans beans;

	public Application(Beans beans) {
		this.beans = beans;
		setTitle("Clef");
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

}
