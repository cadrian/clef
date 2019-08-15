package net.cadrian.clef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrLogger extends AbstractOutErrLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrLogger.class);

	ErrLogger() {
	}

	@Override
	protected void log(final Object logged) {
		LOGGER.error("STDERR: {}", logged);
	}

}
