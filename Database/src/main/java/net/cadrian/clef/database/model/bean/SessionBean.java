package net.cadrian.clef.database.model.bean;

import java.sql.Timestamp;
import java.util.Date;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Session;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Piece;

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
	public Date getStart() {
		return bean.getStart();
	}

	@Override
	public void setStart(final Date start) {
		final Timestamp ts = new Timestamp(start.getTime());
		bean.setStart(ts);
		update();
	}

	@Override
	public Date getStop() {
		return bean.getStop();
	}

	@Override
	public void setStop(final Date stop) {
		final Timestamp ts = new Timestamp(stop.getTime());
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

}
