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
package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.LongField;
import net.cadrian.clef.database.io.StringField;

public class Piece extends AbstractPropertyBean<Piece> {

	private static final String TABLE_NAME = "piece";
	private static final List<Field<Piece>> FIELDS = Arrays.asList(
			new LongField<>("work_id", Piece::getWorkId, Piece::setWorkId, Condition.EQ),
			new LongField<>("previous_id", Piece::getPreviousId, Piece::setPreviousId, Condition.EQ),
			new LongField<>("duration", Piece::getDuration, Piece::setDuration, Condition.EQ),
			new StringField<>("name", Piece::getName, Piece::setName, Condition.EQ),
			new StringField<>("notes", Piece::getNotes, Piece::setNotes, Condition.EQ));

	private Long workId;
	private Long previousId;
	private Long duration;
	private String name;
	private String notes;

	public Piece() {
		this(null);
	}

	public Piece(final Long id) {
		super(id);
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(final Long pieceId) {
		workId = pieceId;
	}

	public Long getPreviousId() {
		return previousId;
	}

	public void setPreviousId(final Long previousId) {
		this.previousId = previousId;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(final Long duration) {
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(final String notes) {
		this.notes = notes;
	}

	@Override
	List<Field<Piece>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Piece createBean(final Long id) {
		return new Piece(id);
	}

}
