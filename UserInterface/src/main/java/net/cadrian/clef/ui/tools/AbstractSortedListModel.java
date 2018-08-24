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
package net.cadrian.clef.ui.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;

abstract class AbstractSortedListModel<E> extends AbstractListModel<E> {

	private static final long serialVersionUID = 8107218077478116856L;

	private final List<E> elements = new ArrayList<>();
	private final Comparator<E> comparator;

	public AbstractSortedListModel(final Comparator<E> comparator) {
		this.comparator = comparator;
	}

	public AbstractSortedListModel(final Comparator<E> comparator, final Collection<? extends E> init) {
		this.comparator = comparator;
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
		final int search = Collections.binarySearch(elements, element, comparator);
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
		final int search = Collections.binarySearch(elements, element, comparator);
		if (search >= 0) {
			remove(search);
		}
	}

	public void remove(final int index) {
		elements.remove(index);
		fireIntervalRemoved(this, index, index);
	}

	public void removeAll() {
		final int size = elements.size();
		if (size > 0) {
			elements.clear();
			fireIntervalRemoved(this, 0, size - 1);
		}
	}

	public Collection<E> getElements() {
		return Collections.unmodifiableCollection(elements);
	}

}
