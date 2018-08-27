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
package net.cadrian.clef.ui.app.form.field.piece;

import java.util.Collection;

import net.cadrian.clef.model.bean.BeanComparators;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.BeanCreator;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.BeanGetter;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.tab.DataPane;

class PiecesComponent implements FieldComponent<Collection<Piece>, DataPane<Piece>> {

	final DataPane<Piece> component;

	public PiecesComponent(final ApplicationContext context, final BeanGetter<Piece> beanGetter,
			final BeanCreator<Piece> beanCreator, final BeanFormModel<Piece> beanFormModel) {
		component = new DataPane<>(context, false, Piece.class, beanGetter, beanCreator, BeanComparators::comparePieces,
				beanFormModel);
	}

	@Override
	public DataPane<Piece> getComponent() {
		return component;
	}

	@Override
	public Collection<Piece> getData() {
		component.saveData();
		return component.getList();
	}

	@Override
	public void setData(final Collection<Piece> data) {
		// ignored
	}

	@Override
	public double getWeight() {
		return 1;
	}

	@Override
	public boolean isDirty() {
		return component.isDirty();
	}

}
