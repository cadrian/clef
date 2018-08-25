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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.widget.DefaultDownloadFilter;
import net.cadrian.clef.ui.widget.DownloadFilter;

class FilePropertyEditor extends AbstractFilePropertyEditor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilePropertyEditor.class);

	private static final Encoder BASE64_ENCODER = Base64.getEncoder();
	private static final Decoder BASE64_DECODER = Base64.getDecoder();

	private static int indexOfSep(final byte[] data) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == 0) {
				return i;
			}
		}
		return data.length;
	}

	FilePropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
		super(context, writable, property);
		final String value = property.getValue();
		final byte[] serializedData = BASE64_DECODER.decode(value.getBytes());
		final int sep = indexOfSep(serializedData);
		final String path = new String(serializedData, 0, sep);
		content.setFile(path);
	}

	@Override
	public void save() {
		final File file = content.getFile();
		final String path = file.getAbsolutePath();
		byte[] data = null;

		// TODO support big files (1.5Gb max for now -- because of BASE64)

		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			final byte[] buffer = new byte[4096];
			int n;
			while ((n = in.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			data = out.toByteArray();
		} catch (final IOException e) {
			LOGGER.error("error while reading file, not saving data", e);
		}

		byte[] serializedData;
		if (data == null) {
			serializedData = path.getBytes();
		} else {
			final byte[] pathBytes = path.getBytes();
			final int sep = pathBytes.length;
			serializedData = new byte[sep + 1 + data.length];
			System.arraycopy(pathBytes, 0, serializedData, 0, sep);
			serializedData[sep] = 0;
			System.arraycopy(data, 0, serializedData, sep + 1, data.length);
		}

		final String value = BASE64_ENCODER.encodeToString(serializedData);
		property.setValue(value);
		content.markSave();
	}

	@Override
	protected DownloadFilter getDownloadFilter(final ApplicationContext context) {
		final byte[] serializedData = BASE64_DECODER.decode(property.getValue().getBytes());
		final int sep = indexOfSep(serializedData);
		final String path = new String(serializedData, 0, sep);
		final int length = serializedData.length - sep - 1;
		if (length < 0) {
			return new DefaultDownloadFilter();
		}
		final byte[] data = new byte[length];
		System.arraycopy(serializedData, sep + 1, data, 0, length);
		return new FilePropertyDownloadFilter(path, data);
	}

}
