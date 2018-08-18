package net.cadrian.clef.ui.form;

import javax.swing.JComponent;

public interface FieldComponent<D, J extends JComponent> {
	J getComponent();

	D getData();

	void setData(D data);

	double getWeight();
}