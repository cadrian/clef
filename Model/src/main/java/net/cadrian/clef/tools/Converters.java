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
package net.cadrian.clef.tools;

public class Converters {

	private Converters() {
		// no instance
	}

	public static final String formatTime(final Long time) {
		final StringBuilder result = new StringBuilder();
		long s = time;
		long m = s / 60;
		s -= m * 60;
		final long h = m / 24;
		m -= h * 24;
		if (h < 10) {
			result.append("0");
		}
		result.append(h).append(":");
		if (m < 10) {
			result.append("0");
		}
		result.append(m).append(":");
		if (s < 10) {
			result.append("0");
		}
		result.append(s);
		return result.toString();
	}

	public static final Long parseTime(final String time) {
		String[] split = time.split(":");
		switch (split.length) {
		case 0: { // seconds
			return Long.valueOf(split[0]);
		}
		case 1: { // minutes and seconds
			long s = Long.valueOf(split[1]);
			long m = Long.valueOf(split[0]);
			return m * 60L + s;
		}
		default: { // hours, minutes, and seconds -- ignore extra
			long s = Long.valueOf(split[2]);
			long m = Long.valueOf(split[1]);
			long h = Long.valueOf(split[0]);
			return h * 3600L + m * 60L + s;
		}
		}
	}

}
