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
