package net.cadrian.clef.model;

import java.util.Collection;

import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public interface Beans {

	Session createSession(Piece piece);

	Property createProperty(String name);

	Piece createPiece(Work work);

	Work createWork(Author author, Pricing pricing);

	Author createAuthor();

	Pricing createPricing();

	Collection<? extends Work> getWorksBy(Author author);

	Collection<? extends Work> getWorksPriced(Pricing pricing);

	Collection<? extends Work> getWorks();

	Collection<? extends Piece> getPieces();

	Collection<? extends Author> getAuthors();

	Collection<? extends Pricing> getPricings();

	Collection<? extends Session> getSessions();

}