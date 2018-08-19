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
package net.cadrian.clef.ui;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Session;

class SessionCreator implements BeanCreator<Session> {

	private final Beans beans;
	private final Resources rc;

	public SessionCreator(final Resources rc, final Beans beans) {
		this.beans = beans;
		this.rc = rc;
	}

	@Override
	public Session createBean() {
		// TODO Auto-generated method stub
		// must ask for the right Piece to attach the Session to
		// (note: no Piece => return null + popup "create Piece first")
		// and the start date will be set to "now"
		return null;
	}
}
