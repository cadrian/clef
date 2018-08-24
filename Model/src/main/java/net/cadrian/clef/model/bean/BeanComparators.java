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
package net.cadrian.clef.model.bean;

import java.util.Date;

public class BeanComparators {

	private BeanComparators() {
		// no instance
	}

	public static int comparePricings(final Pricing p1, final Pricing p2) {
		final String name1 = p1.getName();
		final String name2 = p2.getName();
		return (name1 == null ? "" : name1).compareTo(name2 == null ? "" : name2);
	}

	public static int compareAuthors(final Author a1, final Author a2) {
		final String name1 = a1.getName();
		final String name2 = a2.getName();
		return (name1 == null ? "" : name1).compareTo(name2 == null ? "" : name2);
	}

	public static int compareWorks(final Work w1, final Work w2) {
		final String name1 = w1.getName();
		final String name2 = w2.getName();
		return (name1 == null ? "" : name1).compareTo(name2 == null ? "" : name2);
	}

	public static int comparePieces(final Piece p1, final Piece p2) {
		final String name1 = p1.getName();
		final String name2 = p2.getName();
		return (name1 == null ? "" : name1).compareTo(name2 == null ? "" : name2);
	}

	public static int compareSessions(final Session s1, final Session s2) {
		final Date start1 = s1.getStart();
		final Date start2 = s2.getStart();
		if (start1 == null) {
			if (start2 == null) {
				return 0;
			}
			return 1;
		}
		if (start2 == null) {
			return -1;
		}
		return start1.compareTo(start2);
	}

}
