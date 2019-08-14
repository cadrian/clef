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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Session;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Activity;
import net.cadrian.clef.tools.Converters;

public class SessionBean extends AbstractPropertyBean implements net.cadrian.clef.model.bean.Session {

	private final Session bean;

	public SessionBean(final Session bean, final DatabaseBeansHolder db) {
		super(bean, db);
		this.bean = bean;
	}

	@Override
	public Piece getPiece() {
		return db.getPiece(bean.getPieceId());
	}

	@Override
	public void setPiece(final Piece piece) {
		bean.setPieceId(((PieceBean) piece).getId());
		update();
	}

	@Override
	public Activity getActivity() {
		return db.getActivity(bean.getActivityId());
	}

	@Override
	public void setActivity(final Activity activity) {
		bean.setActivityId(((ActivityBean) activity).getId());
		update();
	}

	@Override
	public Date getStart() {
		return bean.getStart();
	}

	@Override
	public void setStart(final Date start) {
		final Timestamp ts = new Timestamp(start == null ? System.currentTimeMillis() : start.getTime());
		bean.setStart(ts);
		update();
	}

	@Override
	public Date getStop() {
		return bean.getStop();
	}

	@Override
	public void setStop(final Date stop) {
		final Timestamp ts = new Timestamp(stop == null ? System.currentTimeMillis() : stop.getTime());
		bean.setStop(ts);
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
	void update() {
		try {
			db.getSessions().update(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void delete() {
		try {
			db.getSessions().delete(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		final Date now = new Date();
		final Date start0 = getStart();
		final Date start = start0 == null ? now : start0;
		final Date stop0 = getStop();
		final Date stop = stop0 == null ? now : stop0;
		final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		result.append(df.format(start)).append(" - ").append(getPiece().getName());
		if (stop != null) {
			final long duration = (stop.getTime() - start.getTime()) / 1000;
			result.append(" (").append(Converters.formatTime(duration)).append(")");
		}
		return result.toString();
	}

}
