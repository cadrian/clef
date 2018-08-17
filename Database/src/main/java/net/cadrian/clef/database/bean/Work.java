package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.LongField;
import net.cadrian.clef.database.io.StringField;

public class Work extends AbstractPropertyBean<Work> {

	private static final String TABLE_NAME = "work";
	private static final List<Field<Work>> FIELDS = Arrays.asList(
			new LongField<>("pricing_id", Work::getPricingId, Work::setPricingId, Condition.EQ),
			new LongField<>("author_id", Work::getAuthorId, Work::setAuthorId, Condition.EQ),
			new StringField<>("name", Work::getName, Work::setName, Condition.EQ),
			new StringField<>("notes", Work::getNotes, Work::setNotes, Condition.EQ));

	private Long pricingId;
	private Long authorId;
	private String name;
	private String notes;

	public Work() {
		this(null);
	}

	public Work(final Long id) {
		super(id);
	}

	public Long getPricingId() {
		return pricingId;
	}

	public void setPricingId(final Long pricingId) {
		this.pricingId = pricingId;
	}

	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(final Long pieceId) {
		authorId = pieceId;
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
	List<Field<Work>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Work createBean(final Long id) {
		return new Work(id);
	}

}
