package net.cadrian.clef.model.bean;

import java.util.Collection;

public interface Work extends PropertyBean {

	Pricing getPricing();

	Author getAuthor();

	String getName();

	void setName(String name);

	String getNotes();

	void setNotes(String notes);

	Collection<? extends Piece> getPieces();

}
