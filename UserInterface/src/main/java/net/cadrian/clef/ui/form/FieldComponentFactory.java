package net.cadrian.clef.ui.form;

import javax.swing.JComponent;

public interface FieldComponentFactory<D, J extends JComponent> {
	FieldComponent<D, J> createComponent();

	Class<D> getDataType();
}