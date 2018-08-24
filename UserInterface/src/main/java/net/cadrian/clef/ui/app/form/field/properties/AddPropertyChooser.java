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
package net.cadrian.clef.ui.app.form.field.properties;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;

class AddPropertyChooser extends JDialog {

	private static final long serialVersionUID = 2515326043268533422L;

	private PropertyDescriptor selected;

	AddPropertyChooser(final ApplicationContext context, final Entity entity,
			final List<PropertyDescriptor> addableDescriptors) {
		super(context.getPresentation().getApplicationFrame(),
				context.getPresentation().getMessage("AddPropertyTitle"), true);

		final JPanel panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);

		final Presentation presentation = context.getPresentation();
		final JLabel message = new JLabel(presentation.getMessage("AddPropertyMessage"));
		panel.add(message, BorderLayout.NORTH);

		final DefaultListModel<PropertyDescriptor> model = new DefaultListModel<>();
		for (final PropertyDescriptor propertyDescriptor : addableDescriptors) {
			model.addElement(propertyDescriptor);
		}
		final JList<PropertyDescriptor> list = new JList<>(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);

		final Action addAction = new AbstractAction("Add") {
			private static final long serialVersionUID = -8659808353683696964L;

			@Override
			public void actionPerformed(final ActionEvent e) {
				selected = list.getSelectedValue();
				setVisible(false);
			}
		};
		buttons.add(addAction);
		panel.add(presentation.awesome(buttons), BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(presentation.getApplicationFrame());
	}

	public PropertyDescriptor getSelected() {
		return selected;
	}
}
