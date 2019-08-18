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

import java.util.Arrays;
import java.util.List;

import net.cadrian.clef.database.io.ClobField;
import net.cadrian.clef.database.io.Condition;
import net.cadrian.clef.database.io.Field;
import net.cadrian.clef.database.io.StringField;

public class PropertyDescriptor extends AbstractBean<PropertyDescriptor> {

	private static final String TABLE_NAME = "property_descriptor";
	private static final List<Field<PropertyDescriptor>> FIELDS = Arrays.asList(
			new StringField<>("entity", PropertyDescriptor::getEntity, PropertyDescriptor::setEntity, Condition.EQ),
			new StringField<>("type", PropertyDescriptor::getType, PropertyDescriptor::setType, Condition.EQ),
			new StringField<>("name", PropertyDescriptor::getName, PropertyDescriptor::setName, Condition.EQ),
			new ClobField<>("description", PropertyDescriptor::getDescription, PropertyDescriptor::setDescription));

	private String entity;
	private String type;
	private String name;
	private String description;

	public PropertyDescriptor() {
		this(null);
	}

	public PropertyDescriptor(final Long id) {
		super(id);
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(final String entity) {
		this.entity = entity;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String value) {
		description = value;
	}

	@Override
	List<Field<PropertyDescriptor>> getFields() {
		return FIELDS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	PropertyDescriptor createBean(final Long id) {
		return new PropertyDescriptor(id);
	}

}
