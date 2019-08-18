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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Piece;
import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public class PieceBean extends AbstractPropertyBean implements net.cadrian.clef.model.bean.Piece {

	private static final Logger LOGGER = LoggerFactory.getLogger(PieceBean.class);

	private final Piece bean;

	/**
	 * "next" version cache. Only available after the next's "previous" was computed
	 * -- but that's OK since a piece can only be accessed via its latest version
	 * (from work) and through "previous" links first. "Next" links are simply a
	 * convenience.
	 */
	private volatile PieceBean next;

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
	public Long getVersion() {
		final net.cadrian.clef.model.bean.Piece previous = getPrevious();
		if (previous == null) {
			return 1L;
		}
		return 1 + previous.getVersion();
	}

	@Override
	public net.cadrian.clef.model.bean.Piece getPrevious() {
		final Long previousId = bean.getPreviousId();
		if (previousId == null) {
			return null;
		}
		LOGGER.debug("looking for previous piece {}", previousId);
		final PieceBean result = db.getPiece(previousId);
		if (result != null) {
			result.next = this;
		}
		return result;
	}

	@Override
	public void setPrevious(final net.cadrian.clef.model.bean.Piece piece) {
		bean.setPreviousId(((PieceBean) piece).getId());
		if (piece != null) {
			((PieceBean) piece).next = this;
		}
		update();
	}

	@Override
	public net.cadrian.clef.model.bean.Piece getNext() {
		return next;
	}

	@Override
	public Long getDuration() {
		return bean.getDuration();
	}

	@Override
	public void setDuration(final Long duration) {
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
	public boolean isVersionOf(final Bean bean) {
		// short-cuts to avoid creating a Set in most usual cases
		if (bean == null) {
			return false;
		}
		if (super.isVersionOf(bean)) {
			return true;
		}
		final Set<net.cadrian.clef.model.bean.Piece> checked = new HashSet<>(Collections.singleton(this));
		return checkIsVersionOf(bean, checked);
	}

	private boolean checkIsVersionOf(final Bean bean, final Set<net.cadrian.clef.model.bean.Piece> checked) {
		if (!checked.add(this)) {
			if (super.isVersionOf(bean)) {
				return true;
			}
			final net.cadrian.clef.model.bean.Piece previous = getPrevious();
			if (previous != null && (((PieceBean) previous).checkIsVersionOf(bean, checked)
					|| ((PieceBean) bean).checkIsVersionOf(previous, checked))) {
				return true;
			}
			final net.cadrian.clef.model.bean.Piece next = getNext();
			if (next != null && (((PieceBean) next).checkIsVersionOf(bean, checked)
					|| ((PieceBean) bean).checkIsVersionOf(next, checked))) {
				return true;
			}
		}
		return false;
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
