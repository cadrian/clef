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
package net.cadrian.clef.database.model.bean;

import java.util.Collection;
import java.util.Map;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Piece;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public class PieceBean extends AbstractPropertyBean implements net.cadrian.clef.model.bean.Piece {

	private final Piece bean;

	public PieceBean(final Piece bean, final DatabaseBeansHolder db) {
		super(bean, db);
		this.bean = bean;
	}

	@Override
	public Work getWork() {
		return db.getWork(bean.getWorkId());
	}

	@Override
	public String getName() {
		return bean.getName();
	}

	@Override
	public void setName(final String name) {
		bean.setName(name);
		update();
	}

	@Override
	public int getVersion() {
		final net.cadrian.clef.model.bean.Piece previous = getPrevious();
		if (previous == null) {
			return 1;
		}
		return 1 + previous.getVersion();
	}

	@Override
	public net.cadrian.clef.model.bean.Piece getPrevious() {
		return db.getPiece(bean.getPreviousId());
	}

	@Override
	public void setPrevious(final net.cadrian.clef.model.bean.Piece piece) {
		bean.setPreviousId(((PieceBean) piece).getId());
		update();
	}

	@Override
	public long getDuration() {
		return bean.getDuration();
	}

	@Override
	public void setDuration(final long duration) {
		bean.setDuration(duration);
		update();
	}

	@Override
	public String getNotes() {
		return bean.getNotes();
	}

	@Override
	public void setNotes(final String notes) {
		bean.setNotes(notes);
		update();
	}

	@Override
	public Collection<? extends Session> getSessions() {
		final Collection<SessionBean> result;
		try {
			final net.cadrian.clef.database.bean.Session template = new net.cadrian.clef.database.bean.Session();
			template.setPieceId(getId());
			final Map<Long, net.cadrian.clef.database.bean.Session> sessions = db.getSessions().readMany(template,
					true);
			result = db.getSessions(sessions.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	void update() {
		try {
			db.getPieces().update(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void delete() {
		try {
			db.getPieces().delete(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public String toString() {
		final String name = getName();
		if (name == null || name.isEmpty()) {
			return "(no name)";
		}
		return name;
	}

}
