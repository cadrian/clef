package net.cadrian.clef.database.bean;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.LongField;
import net.cadrian.clef.database.io.StringField;
import net.cadrian.clef.database.io.TimestampField;

public class Session extends AbstractPropertyBean<Session> {

	private static final String TABLE_NAME = "session";
	private static final List<Field<Session>> FIELDS = Arrays.asList(
			new LongField<>("piece_id", Session::getPieceId, Session::setPieceId, Condition.EQ),
			new TimestampField<>("start", Session::getStart, Session::setStart, Condition.EQ),
			new TimestampField<>("stop", Session::getStop, Session::setStop, Condition.EQ),
			new StringField<>("notes", Session::getNotes, Session::setNotes, Condition.EQ));

	private Long pieceId;
	private Timestamp start;
	private Timestamp stop;
	private String notes;

	public Session() {
		this(null);
	}

	public Session(final Long id) {
		super(id);
	}

	public Long getPieceId() {
		return pieceId;
	}

	public void setPieceId(final Long pieceId) {
		this.pieceId = pieceId;
	}

	public Timestamp getStart() {
		return start;
	}

	public void setStart(final Timestamp start) {
		this.start = start;
	}

	public Timestamp getStop() {
		return stop;
	}

	public void setStop(final Timestamp stop) {
		this.stop = stop;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	@Override
	List<Field<Session>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Session createBean(final Long id) {
		return new Session(id);
	}

}
