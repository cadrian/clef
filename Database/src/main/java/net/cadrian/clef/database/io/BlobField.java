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
package net.cadrian.clef.database.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.io.BlobField.BlobStream;

public class BlobField<T extends DatabaseBean> extends AbstractField<T, BlobStream> {

	public interface BlobStream extends Closeable {
		void save(InputStream stream) throws DatabaseException;

		default void read(final OutputStream stream) throws DatabaseException {
		}
	}

	private static class ReadableBlobStream implements BlobStream {

		private final Blob blob;

		ReadableBlobStream(final Blob blob) {
			this.blob = blob;
		}

		@Override
		public void save(final InputStream stream) throws DatabaseException {
			throw new DatabaseException("readable BLOB");
		}

		@Override
		public void read(final OutputStream stream) throws DatabaseException {
			final byte[] buffer = new byte[4096];
			try (InputStream in = blob.getBinaryStream()) {
				try {
					while (true) {
						final int n = in.read(buffer);
						if (n == -1) {
							return;
						}
						stream.write(buffer, 0, n);
					}
				} finally {
					blob.free();
				}
			} catch (IOException | SQLException e) {
				throw new DatabaseException(e);
			}
		}

		@Override
		public void close() throws IOException {
			try {
				blob.free();
			} catch (final SQLException e) {
				throw new IOException(e);
			}
		}

	}

	private static class BlobOutputStream extends OutputStream {

		private final Blob blob;
		private final byte[] buffer = new byte[4096];
		private int index;
		private long pos;

		BlobOutputStream(final Blob blob) {
			this.blob = blob;
		}

		@Override
		public void write(final int b) throws IOException {
			if (index == 4095) {
				flush();
			}
			buffer[index++] = (byte) b;
		}

		@Override
		public void flush() throws IOException {
			try {
				final int written = blob.setBytes(pos, buffer, 0, index);
				if (written != index) {
					throw new IOException("BLOB not completely written");
				}
				pos += written;
				index = 0;
			} catch (final SQLException e) {
				throw new IOException(e);
			}
		}

		@Override
		public void close() throws IOException {
			try {
				blob.free();
			} catch (final SQLException e) {
				throw new IOException(e);
			}
		}

	}

	public BlobField(final String name, final FieldGetter<T, BlobStream> getter,
			final FieldSetter<T, BlobStream> setter) {
		super(name, getter, setter);
	}

	@Override
	public String getCondition(final T bean) throws DatabaseException {
		throw new DatabaseException("Unexpected read condition on BLOB");
	}

	@Override
	public void setConditionValue(final int index, final T bean, final PreparedStatement ps) throws DatabaseException {
		try {
			final Blob blob = ps.getConnection().createBlob();
			final BlobStream value = getter.get(bean);
			try (BlobOutputStream out = new BlobOutputStream(blob)) {
				value.read(out);
				out.flush();
				ps.setBlob(index, blob);
			} finally {
				value.close();
			}
		} catch (SQLException | IOException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public void setValue(final ResultSet rs, final T newBean) throws DatabaseException {
		try {
			final Blob blob = rs.getBlob(getName());
			if (blob != null) {
				setter.set(newBean, new ReadableBlobStream(blob));
			}
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

}
