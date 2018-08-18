package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.StringField;

public class Author extends AbstractPropertyBean<Author> {

	private static final String TABLE_NAME = "author";
	private static final List<Field<Author>> FIELDS = Arrays.asList(
			new StringField<>("name", Author::getName, Author::setName, Condition.EQ),
			new StringField<>("notes", Author::getNotes, Author::setNotes, Condition.EQ));

	private String name;
	private String notes;

	public Author() {
		this(null);
	}

	public Author(final Long id) {
		super(id);
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
	List<Field<Author>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Author createBean(final Long id) {
		return new Author(id);
	}

}
