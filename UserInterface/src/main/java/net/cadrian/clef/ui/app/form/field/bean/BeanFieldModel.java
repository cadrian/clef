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
package net.cadrian.clef.ui.app.form.field.bean;

import java.util.Collection;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldSetter;
import net.cadrian.clef.ui.app.form.field.bean.BeanComponentFactory.ListGetter;

class BeanFieldModel<T extends Bean, D extends Bean> extends AbstractSimpleFieldModel<T, D, JComponent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanFieldModel.class);

	BeanFieldModel(final String name, final String tab, final FieldGetter<T, D> getter, final FieldSetter<T, D> setter,
			final FieldComponentFactory<T, D, JComponent> componentFactory) {
		super(name, tab, getter, setter, componentFactory);
	}

	@Override
	protected FieldComponent<D, JComponent> createNewComponent(final T contextBean, final ApplicationContext context)
			throws ModelException {
		LOGGER.debug("<-- {}", contextBean);
		final FieldComponent<D, JComponent> result;
		final boolean writable = componentFactory.isWritable()
				&& (Boolean) context.getValue(AdvancedConfigurationEntry.offlineMode);
		LOGGER.debug("writable: {}", writable);
		final ListGetter<D> listGetter = ((BeanComponentFactory<T, D>) componentFactory).getListGetter();
		LOGGER.debug("listGetter: {}", listGetter);
		final Collection<? extends D> beans = (writable && listGetter != null) ? listGetter.getBeans(context) : null;
		LOGGER.debug("beans: {}", beans);
		if (beans == null || beans.isEmpty()) {
			result = new BeanComponentText<>(writable);
		} else {
			result = new BeanComponentChoice<>(writable, beans);
		}
		LOGGER.debug("--> {}", result);
		return result;
	}

}
