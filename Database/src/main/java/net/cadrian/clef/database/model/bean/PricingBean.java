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
package net.cadrian.clef.database.model.bean;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Pricing;
import net.cadrian.clef.model.ModelException;

public class PricingBean extends AbstractBean implements net.cadrian.clef.model.bean.Pricing {

	private final Pricing bean;

	public PricingBean(final Pricing bean, final DatabaseBeansHolder db) {
		super(db);
		this.bean = bean;
	}

	@Override
	public Long getId() {
		return bean.getId();
	}

	@Override
	public void delete() {
		try {
			db.getPricings().delete(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

}
