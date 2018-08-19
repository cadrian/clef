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

import java.util.ResourceBundle;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Work;

class WorkCreator implements DataPane.BeanCreator<Work> {

	private final Beans beans;
	private final ResourceBundle messages;

	public WorkCreator(final Beans beans, final ResourceBundle messages) {
		this.beans = beans;
		this.messages = messages;
	}

	@Override
	public Work createBean() {
		// TODO Auto-generated method stub
		// must ask for the right Author and Pricing
		// (note: no Author / Pricing => return null + popup "create Author / Pricing
		// first")
		return null;
	}
}