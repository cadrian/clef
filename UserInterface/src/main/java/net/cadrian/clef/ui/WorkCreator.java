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