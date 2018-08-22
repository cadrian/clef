package net.cadrian.clef.ui;

import java.util.Collection;
import java.util.Comparator;

public class SortedListModel<E extends Comparable<E>> extends AbstractSortedListModel<E> {

	private static final long serialVersionUID = -8334332941742382417L;

	public SortedListModel() {
		super(Comparator.<E>naturalOrder());
	}

	public SortedListModel(final Collection<? extends E> init) {
		super(Comparator.<E>naturalOrder(), init);
	}

}
