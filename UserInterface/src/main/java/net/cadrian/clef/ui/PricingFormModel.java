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
package net.cadrian.clef.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.ui.form.FieldComponentFactory;

class PricingFormModel extends BeanFormModel<Pricing> {

	private static final Map<String, FieldComponentFactory<?, ? extends JComponent>> COMPONENT_FACTORIES = new LinkedHashMap<>();

	PricingFormModel(final Class<Pricing> beanType) {
		super(beanType, COMPONENT_FACTORIES);
	}

}
