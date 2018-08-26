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
package net.cadrian.clef.ui.app.form.field.stat;

import javax.swing.JPanel;

import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Work;
import net.cadrian.clef.ui.app.form.field.FieldModel;

public class WorkStatisticsComponentFactory extends AbstractStatisticsComponentFactory<Work, WorkStatisticsData> {

	@Override
	public FieldModel<Work, WorkStatisticsData, JPanel> createModel() throws ModelException {
		return new CommonStatisticsFieldModel<>(Work.class, WorkStatisticsData.class);
	}

	@Override
	public Class<Work> getBeanType() {
		return Work.class;
	}

}
