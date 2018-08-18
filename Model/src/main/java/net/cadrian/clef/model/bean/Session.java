package net.cadrian.clef.model.bean;

import java.util.Date;

public interface Session extends PropertyBean {

	Piece getPiece();

	void setPiece(Piece piece);

	Date getStart();

	void setStart(Date start);

	Date getStop();

	void setStop(Date stop);

	String getNotes();

	void setNotes(String notes);

}
