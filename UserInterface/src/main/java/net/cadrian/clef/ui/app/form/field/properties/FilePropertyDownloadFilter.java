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
package net.cadrian.clef.ui.app.form.field.properties;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.widget.DownloadFilter;

class FilePropertyDownloadFilter implements DownloadFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilePropertyDownloadFilter.class);

	private final String ext;
	private final byte[] data;
	private final int count;

	public FilePropertyDownloadFilter(final String path, final byte[] data, final int count) {
		final int dot = path.lastIndexOf('.');
		final int sep = path.lastIndexOf(File.separatorChar);
		if (dot > sep) {
			ext = path.substring(dot);
		} else {
			ext = "";
		}

		this.data = data;
		this.count = count;
	}

	@Override
	public File download(final File file) throws IOException {
		final File temp = File.createTempFile("clef", ext);
		try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(temp))) {
			out.write(data, 0, count);
		}
		temp.deleteOnExit();
		LOGGER.debug("Opening {}", temp);
		return temp;
	}

}
