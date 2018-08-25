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

import java.lang.reflect.Method;
import java.util.Collection;

import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.BeanCreator;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.BeanGetter;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.SimpleFieldModel;
import net.cadrian.clef.ui.app.tab.DataPane;

class PiecesFieldModel extends SimpleFieldModel<Work, Collection<Piece>, DataPane<Piece>> {

	private final BeanFormModel<Piece> beanFormModel;

	protected PiecesFieldModel(final String name, final String tab, final Method getter, final Method setter,
			final FieldComponentFactory<Work, Collection<Piece>, DataPane<Piece>> componentFactory,
			final BeanFormModel<Piece> beanFormModel) {
		super(name, tab, getter, setter, componentFactory);
		this.beanFormModel = beanFormModel;
	}

	@Override
	protected FieldComponent<Collection<Piece>, DataPane<Piece>> createNewComponent(final Work contextBean,
			final ApplicationContext context) throws ModelException {
		final BeanGetter<Piece> beanGetter = () -> contextBean.getPieces();
		final BeanCreator<Piece> beanCreator = () -> context.getBeans().createPiece(contextBean);
		return new PiecesComponent(context, beanGetter, beanCreator, beanFormModel);
	}

}
