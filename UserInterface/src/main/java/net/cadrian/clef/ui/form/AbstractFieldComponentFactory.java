package net.cadrian.clef.ui.form;

import javax.swing.JComponent;

abstract class AbstractFieldComponentFactory<D, J extends JComponent, C> implements FieldComponentFactory<D, J, C> {

	protected final boolean writable;
	private final String tab;

	public AbstractFieldComponentFactory(final boolean writable, final String tab) {
		this.writable = writable;
		this.tab = tab;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public String getTab() {
		return tab;
	}

}
