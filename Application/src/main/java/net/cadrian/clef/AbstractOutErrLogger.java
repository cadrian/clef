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
