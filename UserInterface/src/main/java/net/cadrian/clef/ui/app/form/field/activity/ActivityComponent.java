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
package net.cadrian.clef.ui.app.form.field.activity;

import java.util.Collection;

import javax.swing.JComboBox;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Activity;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

class ActivityComponent implements FieldComponent<Activity, JComboBox<Activity>> {

	private final JComboBox<Activity> component;
	private Activity savedData;

	ActivityComponent(final boolean writable, final Beans beans) {
		final Collection<? extends Activity> activities = beans.getActivities();
		component = new JComboBox<>(activities.toArray(new Activity[activities.size()]));
		component.setEditable(false);
		component.setEnabled(writable);
	}

	@Override
	public JComboBox<Activity> getComponent() {
		return component;
	}

	@Override
	public Activity getData() {
		savedData = (Activity) component.getSelectedItem();
		return savedData;
	}

	@Override
	public void setData(final Activity data) {
		savedData = data;
		component.setSelectedItem(data);
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public boolean isDirty() {
		return savedData != component.getSelectedItem();
	}

}
