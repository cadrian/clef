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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	FilePropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
		super(context, writable, property);
		final String value = property.getValue();

		try (ByteArrayInputStream in = new ByteArrayInputStream(value.getBytes());
				InputStream b64 = BASE64_DECODER.wrap(in);
				SpecialByteArrayOutputStream pathout = new SpecialByteArrayOutputStream()) {
			String path = null;
			while (true) {
				final int b = b64.read();
				if (b > 0) {
					pathout.write(b);
				} else {
					path = pathout.toString();
					break;
				}
			}
			content.setFile(path);
		} catch (final IOException e) {
			LOGGER.error("error while reading file, not reading data", e);
		}
	}

	@Override
	public void save() {
		final File file = content.getFile();
		final String path = file.getAbsolutePath();
		String value = null;

		// TODO support big files (1.5Gb max for now -- because of BASE64)

		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
				ByteArrayOutputStream valueout = new ByteArrayOutputStream();
				OutputStream b64 = BASE64_ENCODER.wrap(valueout)) {
			b64.write(path.getBytes());
			b64.write(0);
			final byte[] buffer = new byte[4096];
			int n;
			while ((n = in.read(buffer)) >= 0) {
				b64.write(buffer, 0, n);
			}
			value = new String(valueout.toByteArray());
		} catch (final IOException e) {
			LOGGER.error("error while reading file, not saving data", e);
			value = path;
		}

		property.setValue(value);
		content.markSave();
	}

	private static class SpecialByteArrayOutputStream extends ByteArrayOutputStream {
		FilePropertyDownloadFilter getDownloadFilter(final String path) {
			return new FilePropertyDownloadFilter(path, buf, count);
		}
	}

	@Override
	protected DownloadFilter getDownloadFilter(final ApplicationContext context) {
		final String value = property.getValue();
		try (ByteArrayInputStream in = new ByteArrayInputStream(value.getBytes());
				InputStream b64 = BASE64_DECODER.wrap(in);
				SpecialByteArrayOutputStream pathout = new SpecialByteArrayOutputStream();
				SpecialByteArrayOutputStream dataout = new SpecialByteArrayOutputStream();) {
			int state = 0;
			while (state >= 0) {
				final int b = b64.read();
				switch (b) {
				case -1:
					state = -1;
					break;
				case 0:
					switch (state) {
					case 0:
						state = 1;
						break;
					case 1:
						dataout.write(b);
						break;
					}
					break;
				default:
					switch (state) {
					case 0:
						pathout.write(b);
						break;
					case 1:
						dataout.write(b);
						break;
					}
				}
			}
			return dataout.getDownloadFilter(pathout.toString());
		} catch (final IOException e) {
			LOGGER.error("error while reading file, not reading data", e);
		}

		return new DefaultDownloadFilter();
	}

}
