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
package net.cadrian.clef.model.bean;

import java.util.Collection;

public interface Piece extends PropertyBean {

	Work getWork();

	String getName();

	void setName(String name);

	Long getVersion();

	Piece getPrevious();

	void setPrevious(Piece piece);

	Long getDuration();

	void setDuration(Long duration);

	String getNotes();

	void setNotes(String notes);

	Collection<? extends Session> getSessions();

}
