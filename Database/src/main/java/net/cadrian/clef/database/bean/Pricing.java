package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.Field;

public class Pricing extends AbstractBean<Pricing> {

	private static final String TABLE_NAME = "pricing";
	private static final List<Field<Pricing>> FIELDS = Arrays.asList();

	public Pricing() {
		this(null);
	}

	public Pricing(final Long id) {
		super(id);
	}

	@Override
	List<Field<Pricing>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	Pricing createBean(final Long id) {
		return new Pricing(id);
	}

}
