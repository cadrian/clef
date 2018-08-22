package net.cadrian.clef.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class SortedListModel<E extends Comparable<E>> extends AbstractListModel<E> {

	private static final long serialVersionUID = -8334332941742382417L;

	private final List<E> elements = new ArrayList<>();

	public SortedListModel() {
	}

	public SortedListModel(final Collection<? extends E> init) {
		elements.addAll(init);
		fireIntervalAdded(this, 0, elements.size() - 1);
	}

	@Override
	public int getSize() {
		return elements.size();
	}

	@Override
	public E getElementAt(final int index) {
		return elements.get(index);
	}

	public void replaceAll(final Collection<? extends E> init) {
		final int oldSize = elements.size();
		elements.clear();
		elements.addAll(init);
		final int size = elements.size();
		if (size < oldSize) {
			if (size > 0) {
				fireContentsChanged(this, 0, size - 1);
			}
			if (oldSize > 0) {
				fireIntervalRemoved(this, size, oldSize - 1);
			}
		} else {
			if (oldSize > 0) {
				fireContentsChanged(this, 0, oldSize - 1);
			}
			if (size > 0) {
				fireIntervalAdded(this, oldSize, size - 1);
			}
		}
	}

	public int add(final E element) {
		final int result;
		final int search = Collections.binarySearch(elements, element);
		if (search < 0) {
			result = -search - 1;
			elements.add(result, element);
		} else {
			result = search;
			elements.set(result, element);
		}
		fireIntervalAdded(this, result, result);
		return result;
	}

	public void remove(final E element) {
		final int search = Collections.binarySearch(elements, element);
		if (search >= 0) {
			remove(search);
		}
	}

	public void remove(final int index) {
		elements.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	public Collection<E> getElements() {
		return Collections.unmodifiableCollection(elements);
	}

}
