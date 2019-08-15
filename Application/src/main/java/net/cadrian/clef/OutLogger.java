package net.cadrian.clef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutLogger extends AbstractOutErrLogger {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutLogger.class);

	OutLogger() {
	}

	@Override
	protected void log(final Object logged) {
		LOGGER.info("STDOUT: {}", logged);
	}

}
