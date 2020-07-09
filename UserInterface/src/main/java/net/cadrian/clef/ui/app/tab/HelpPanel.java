package net.cadrian.clef.ui.app.tab;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.cadrian.clef.ui.ApplicationContext;

public class HelpPanel extends JPanel {

	public HelpPanel(ApplicationContext context) {
		super(new BorderLayout());

		final JLabel about = new JLabel(context.getPresentation().getMessage("Help.About"));
		add(about, BorderLayout.CENTER);
	}

	private static final long serialVersionUID = -5641043731634385082L;

}
