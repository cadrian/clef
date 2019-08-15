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

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Activity;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

class ActivityComponent implements FieldComponent<Activity, JComboBox<Activity>> {

	private final JComboBox<Activity> component;
	private Activity savedData;

	private static class CellRenderer implements ListCellRenderer<Activity> {

		private final DefaultListCellRenderer def = new DefaultListCellRenderer();
		private JList<? extends Activity> listCache;
		private JList<String> stringsCache;

		@Override
		public Component getListCellRendererComponent(final JList<? extends Activity> list, final Activity value,
				final int index, final boolean isSelected, final boolean cellHasFocus) {
			final JList<String> strings;
			if (list == listCache) {
				strings = stringsCache;
			} else {
				final ListModel<? extends Activity> model = list.getModel();
				final int n = model.getSize();
				final String[] values = new String[n];
				for (int i = 0; i < n; i++) {
					final Activity element = list.getModel().getElementAt(i);
					values[i] = element == null ? "" : element.getName();
				}
				strings = new JList<>(values);
				listCache = list;
				stringsCache = strings;
			}
			return def.getListCellRendererComponent(strings, value == null ? "" : value.getName(), index, isSelected,
					cellHasFocus);
		}
	}

	ActivityComponent(final boolean writable, final Beans beans) {
		final Collection<? extends Activity> activities = beans.getActivities();
		final List<? extends Activity> activitiesWithNull = activities == null ? new ArrayList<>()
				: new ArrayList<>(activities);
		activitiesWithNull.add(0, null);
		component = new JComboBox<>(activitiesWithNull.toArray(new Activity[activitiesWithNull.size()]));
		component.setRenderer(new CellRenderer());
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
