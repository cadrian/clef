package net.cadrian.clef.ui.widget;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import net.cadrian.clef.ui.ApplicationContext;

public class FileSelector extends JPanel {

	private static final long serialVersionUID = -606371277654849846L;

	// app-wide
	private static File lastDirectory;

	private final JTextField display;
	private final Action browse;
	private File file;
	private boolean dirty;

	public FileSelector(final ApplicationContext context, final boolean writable) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		display = new JTextField();
		display.setEditable(writable);

		browse = new AbstractAction("Browse") {
			private static final long serialVersionUID = -3091414439330672834L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser chooser = new JFileChooser();
				if (lastDirectory != null) {
					chooser.setCurrentDirectory(lastDirectory);
				}
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setMultiSelectionEnabled(false);
				if (file != null) {
					chooser.setSelectedFile(file);
				}
				final int returnVal = chooser.showOpenDialog(context.getPresentation().getApplicationFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					lastDirectory = chooser.getCurrentDirectory();
					file = chooser.getSelectedFile();
					display.setText(file.getAbsolutePath());
					dirty = true;
				}
			}
		};

		browse.setEnabled(writable);

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);
		buttons.add(browse);

		add(display);
		add(context.getPresentation().awesome(buttons));
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		display.setText(file.getAbsolutePath());
	}

	public void setFile(String path) {
		if (path != null) {
			this.file = new File(path);
			display.setText(path);
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	public void markSave() {
		dirty = false;
	}

}
