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
package net.cadrian.clef.model.bean;

import net.cadrian.clef.model.Bean;

public interface PropertyDescriptor extends Bean {

	public enum Entity {
		// keep meta out (low-level management only)
		pricing, author, work, piece, session, activity;
	}

	public enum Type {
		string, date, path, file;
	}

	Entity getEntity();

	Type getType();

	void setType(Type type);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	int countUsages();

}
