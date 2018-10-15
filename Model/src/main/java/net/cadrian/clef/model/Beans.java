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
package net.cadrian.clef.model;

import java.util.Collection;

import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public interface Beans {

	Session createSession(Piece piece);

	Property createProperty(PropertyDescriptor propertyDescriptor);

	PropertyDescriptor createPropertyDescriptor(PropertyDescriptor.Entity entity, PropertyDescriptor.Type type);

	Piece createPiece(Work work);

	Piece createPieceVersion(Piece piece);

	Work createWork(Author author, Pricing pricing);

	Author createAuthor();

	Pricing createPricing();

	Collection<? extends Pricing> getPricings();

	Collection<? extends Author> getAuthors();

	Collection<? extends Work> getWorks();

	Collection<? extends Work> getWorksBy(Author author);

	Collection<? extends Work> getWorksPriced(Pricing pricing);

	Collection<? extends Piece> getPieces();

	Collection<? extends Session> getSessions();

	Collection<? extends PropertyDescriptor> getPropertyDescriptors(PropertyDescriptor.Entity entity);

	boolean movePiece(Piece piece, Work targetWork);

}
