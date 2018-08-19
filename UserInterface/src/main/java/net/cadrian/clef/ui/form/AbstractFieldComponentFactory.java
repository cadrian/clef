package net.cadrian.clef.ui.form;

import javax.swing.JComponent;

abstract class AbstractFieldComponentFactory<D, J extends JComponent> implements FieldComponentFactory<D, J> {

	protected final boolean writable;

	public AbstractFieldComponentFactory(final boolean writable) {
		this.writable = writable;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

}
