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
package net.cadrian.clef.database.model.bean;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Work;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;

public class WorkBean extends AbstractPropertyBean implements net.cadrian.clef.model.bean.Work {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkBean.class);

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
	public void setPricing(Pricing pricing) {
		bean.setPricingId(((PricingBean) pricing).getId());
		update();
	}

	@Override
	public Author getAuthor() {
		return db.getAuthor(bean.getAuthorId());
	}

	@Override
	public void setAuthor(Author author) {
		bean.setAuthorId(((AuthorBean) author).getId());
		update();
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
			LOGGER.debug("{} => {}", getId(), pieces.keySet());
			result = db.getPieces(pieces.keySet());
			// filter out older versions
			final Set<PieceBean> old = new HashSet<>(result.size());
			for (final PieceBean piece : result) {
				final PieceBean previous = (PieceBean) piece.getPrevious();
				if (previous != null) {
					LOGGER.debug("old version: {}", previous);
					old.add(previous);
				}
			}
			final Iterator<PieceBean> it = result.iterator();
			while (it.hasNext()) {
				final PieceBean piece = it.next();
				if (old.contains(piece)) {
					LOGGER.debug("remove version: {}", piece);
					it.remove();
				}
			}
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

	@Override
	public void delete() {
		try {
			db.getWorks().delete(bean);
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
