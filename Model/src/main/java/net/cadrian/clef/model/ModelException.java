package net.cadrian.clef.model;

public class ModelException extends RuntimeException {

	private static final long serialVersionUID = 2404476401209345358L;

	public ModelException() {
	}

	public ModelException(final String msg) {
		super(msg);
	}

	public ModelException(final Throwable cause) {
		super(cause);
	}

	public ModelException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

}
