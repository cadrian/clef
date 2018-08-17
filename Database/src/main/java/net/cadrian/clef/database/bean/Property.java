package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.ClobField;
import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.StringField;

public class Property extends AbstractBean<Property> {

	private static final String TABLE_NAME = "property";
	private static final List<Field<Property>> FIELDS = Arrays.asList(
			new StringField<>("name", Property::getName, Property::setName, Condition.EQ),
			new ClobField<>("value", Property::getValue, Property::setValue));

	private String name;
	private String value;

	public Property() {
		this(null);
	}

	public Property(final Long id) {
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	List<Field<Property>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Property createBean(final Long id) {
		return new Property(id);
	}

}
