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
package net.cadrian.clef;

import java.io.IOException;
import java.io.OutputStream;

abstract class AbstractOutErrLogger extends OutputStream {

	private final StringBuilder buffer = new StringBuilder();

	AbstractOutErrLogger() {
	}

	@Override
	public void write(final byte[] b, final int s, final int l) throws IOException {
		synchronized (buffer) {
			for (int i = 0; i < l; i++) {
				write0(b[i + s]);
			}
		}
	}

	@Override
	public void write(final int c) throws IOException {
		synchronized (buffer) {
			write0(c);
		}
	}

	private void write0(final int c) {
		if (c == '\n') {
			log(buffer);
			buffer.setLength(0);
		} else {
			buffer.append((char) c);
		}
	}

	protected abstract void log(final Object logged);

}
