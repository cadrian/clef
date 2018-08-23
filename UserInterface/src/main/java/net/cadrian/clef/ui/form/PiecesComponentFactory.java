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
package net.cadrian.clef.ui.form;

import java.util.Collection;

import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.BeanComparators;
import net.cadrian.clef.ui.BeanCreator;
import net.cadrian.clef.ui.BeanFormModel;
import net.cadrian.clef.ui.BeanGetter;
import net.cadrian.clef.ui.DataPane;

public class PiecesComponentFactory
		extends AbstractFieldComponentFactory<Collection<Piece>, DataPane<Piece, Work>, Work> {

	private static class PiecesComponent implements FieldComponent<Collection<Piece>, DataPane<Piece, Work>> {

		private final DataPane<Piece, Work> component;

		public PiecesComponent(final ApplicationContext context, final BeanGetter<Piece> beanGetter,
				final BeanCreator<Piece> beanCreator, final BeanFormModel<Piece, Work> beanFormModel) {
			component = new DataPane<>(context, false, beanGetter, beanCreator,
					(p1, p2) -> BeanComparators.comparePieces(p1, p2), beanFormModel);
		}

		@Override
		public DataPane<Piece, Work> getComponent() {
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

	private final BeanFormModel<Piece, Work> beanFormModel;

	public PiecesComponentFactory(final BeanFormModel<Piece, Work> beanFormModel, final String tab) {
		super(false, tab);
		this.beanFormModel = beanFormModel;
	}

	@Override
	public FieldComponent<Collection<Piece>, DataPane<Piece, Work>> createComponent(final ApplicationContext context,
			final Work contextBean) {
		final BeanGetter<Piece> beanGetter = () -> contextBean.getPieces();
		final BeanCreator<Piece> beanCreator = new BeanCreator<Piece>() {

			@Override
			public Piece createBean() {
				return context.getBeans().createPiece(contextBean);
			}
		};
		return new PiecesComponent(context, beanGetter, beanCreator, beanFormModel);
	}

	@Override
	public Class<?> getDataType() {
		return Collection.class;
	}

}
