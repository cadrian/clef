package net.cadrian.clef.database;

public class DatabaseException extends Exception {

	private static final long serialVersionUID = 6950724534695418382L;

	public DatabaseException() {
	}

	public DatabaseException(final String msg) {
		super(msg);
	}

	public DatabaseException(final Throwable cause) {
		super(cause);
	}

	public DatabaseException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

}
