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
