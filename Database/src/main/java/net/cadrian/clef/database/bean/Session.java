/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
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
			new LongField<>("activity_id", Session::getActivityId, Session::setActivityId, Condition.EQ),
			new TimestampField<>("start", Session::getStart, Session::setStart, Condition.EQ),
			new TimestampField<>("stop", Session::getStop, Session::setStop, Condition.EQ),
			new StringField<>("notes", Session::getNotes, Session::setNotes, Condition.EQ));

	private Long pieceId;
	private Long activityId;
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

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(final Long activityId) {
		this.activityId = activityId;
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
