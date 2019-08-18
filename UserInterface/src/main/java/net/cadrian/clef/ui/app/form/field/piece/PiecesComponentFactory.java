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
package net.cadrian.clef.ui.app.form.field.piece;

import java.util.Collection;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldModel;
import net.cadrian.clef.ui.app.form.field.FieldSetter;
import net.cadrian.clef.ui.app.tab.DataPane;

public class PiecesComponentFactory
		extends AbstractSimpleFieldComponentFactory<Work, Collection<Piece>, DataPane<Piece>> {

	private final BeanFormModel<Piece> beanFormModel;

	public PiecesComponentFactory(final BeanFormModel<Piece> beanFormModel, final String tab) {
		super(Work.class, "Pieces", false, tab);
		this.beanFormModel = beanFormModel;
	}

	@Override
	protected FieldModel<Work, Collection<Piece>, DataPane<Piece>> createModel(final String fieldName,
			final FieldGetter<Work, Collection<Piece>> getter, final FieldSetter<Work, Collection<Piece>> setter) {
		return new PiecesFieldModel(fieldName, tab, getter, setter, this, beanFormModel);
	}

}
