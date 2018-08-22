package net.cadrian.clef.ui;

import java.util.Date;

import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

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
