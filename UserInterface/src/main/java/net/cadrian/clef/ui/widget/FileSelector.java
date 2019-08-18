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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;

public class FileSelector extends JPanel {

	private final class BrowseAction extends AbstractAction {
		private final ApplicationContext context;
		private static final long serialVersionUID = -3091414439330672834L;

		private BrowseAction(final ApplicationContext context) {
			super("Browse");
			this.context = context;
		}

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
	}

	private final class DownloadAction extends AbstractAction {
		private final ApplicationContext context;
		private final DownloadFilter downloadFilter;
		private static final long serialVersionUID = 1306117769999262324L;

		private DownloadAction(final ApplicationContext context, final DownloadFilter downloadFilter) {
			super("Download");
			this.context = context;
			this.downloadFilter = downloadFilter;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				final File filteredFile = downloadFilter.download(file);
				Desktop.getDesktop().open(filteredFile);
			} catch (final IOException x) {
				LOGGER.error("Error while opening file: {}", file, x);
				final Presentation presentation = context.getPresentation();
				JOptionPane.showMessageDialog(presentation.getApplicationFrame(),
						presentation.getMessage("FileOpenFailedMessage", file.getPath()),
						presentation.getMessage("FileOpenFailedTitle"), JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private static final long serialVersionUID = -606371277654849846L;

	private static final Logger LOGGER = LoggerFactory.getLogger(FileSelector.class);

	// app-wide
	private static File lastDirectory;

	private final JTextField display;
	private final Action download;
	private final Action browse;
	private File file;
	private boolean dirty;

	public FileSelector(final ApplicationContext context, final boolean writable, final DownloadFilter downloadFilter) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		display = new JTextField();
		display.setEditable(writable);

		download = new DownloadAction(context, downloadFilter);
		browse = new BrowseAction(context);

		download.setEnabled(true);
		browse.setEnabled(writable);

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);
		buttons.add(browse);
		buttons.addSeparator();
		buttons.add(download);

		add(display);
		add(context.getPresentation().awesome(buttons));
	}

	public File getFile() {
		return file;
	}

	public void setFile(final File file) {
		this.file = file;
		display.setText(file.getAbsolutePath());
	}

	public void setFile(final String path) {
		if (path != null) {
			file = new File(path);
			display.setText(path);
		}
	}

	public boolean isDirty() {
		if (dirty) {
			LOGGER.debug("dirty");
		}
		return dirty;
	}

	public void markSave() {
		dirty = false;
	}

}
