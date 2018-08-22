package net.cadrian.clef.ui;

import java.util.Collection;
import java.util.Comparator;

public class SortableListModel<E> extends AbstractSortedListModel<E> {

	private static final long serialVersionUID = 9181985135265163745L;

	public SortableListModel(final Comparator<E> comparator) {
		super(comparator);
	}

	public SortableListModel(final Comparator<E> comparator, final Collection<? extends E> init) {
		super(comparator, init);
	}

}
