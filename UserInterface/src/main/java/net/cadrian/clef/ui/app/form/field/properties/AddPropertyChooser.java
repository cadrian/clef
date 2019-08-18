/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
package net.cadrian.clef.ui.app.form.field.properties;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.Presentation;
import net.cadrian.clef.ui.widget.ClefTools;

class AddPropertyChooser extends JDialog {

	private final class ClefToolsListenerImpl implements ClefTools.Listener {
		private final JList<PropertyDescriptor> list;

		private ClefToolsListenerImpl(final JList<PropertyDescriptor> list) {
			this.list = list;
		}

		@Override
		public void toolCalled(final ClefTools tools, final ClefTools.Tool tool) {
			switch (tool) {
			case Add:
				selected = list.getSelectedValue();
				setVisible(false);
				break;
			default:
			}
		}
	}

	private static final long serialVersionUID = 2515326043268533422L;

	private PropertyDescriptor selected;

	AddPropertyChooser(final ApplicationContext context, final Entity entity,
			final List<PropertyDescriptor> addableDescriptors) {
		super(context.getPresentation().getApplicationFrame(), context.getPresentation().getMessage("AddPropertyTitle"),
				true);

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

		final ClefTools tools = new ClefTools(context, ClefTools.Tool.Add);
		tools.addListener(new ClefToolsListenerImpl(list));

		panel.add(tools, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(presentation.getApplicationFrame());
	}

	public PropertyDescriptor getSelected() {
		return selected;
	}
}
