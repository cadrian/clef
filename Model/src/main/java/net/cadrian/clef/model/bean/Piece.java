package net.cadrian.clef.model.bean;

import java.util.Collection;

public interface Piece extends PropertyBean {

	Work getWork();

	String getName();

	void setName(String name);

	int getVersion();

	Piece getPrevious();

	void setPrevious(Piece piece);

	long getDuration();

	void setDuration(long duration);

	String getNotes();

	void setNotes(String notes);

	Collection<Session> getSessions();

}
