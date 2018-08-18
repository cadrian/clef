package net.cadrian.clef.database.model.bean;

import java.util.Collection;
import java.util.Map;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Work;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;

public class WorkBean extends AbstractPropertyBean implements net.cadrian.clef.model.bean.Work {

	private final Work bean;

	public WorkBean(final Work bean, final DatabaseBeansHolder db) {
		super(bean, db);
		this.bean = bean;
	}

	@Override
	public Pricing getPricing() {
		return db.getPricing(bean.getPricingId());
	}

	@Override
	public Author getAuthor() {
		return db.getAuthor(bean.getAuthorId());
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
	public String getNotes() {
		return bean.getNotes();
	}

	@Override
	public void setNotes(final String notes) {
		bean.setNotes(notes);
		update();
	}

	@Override
	public Collection<? extends Piece> getPieces() {
		final Collection<PieceBean> result;
		try {
			final net.cadrian.clef.database.bean.Piece template = new net.cadrian.clef.database.bean.Piece();
			template.setWorkId(getId());
			final Map<Long, net.cadrian.clef.database.bean.Piece> pieces = db.getPieces().readMany(template, true);
			result = db.getPieces(pieces.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	void update() {
		try {
			db.getWorks().update(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

}
