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

import java.util.Collection;

import net.cadrian.clef.model.PropertyBean;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;

public interface Work extends PropertyBean {

	@Override
	default PropertyDescriptor.Entity getEntity() {
		return Entity.work;
	}

	Pricing getPricing();

	Author getAuthor();

	String getName();

	void setName(String name);

	String getNotes();

	void setNotes(String notes);

	Collection<? extends Piece> getPieces();

}
