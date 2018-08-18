package net.cadrian.clef.ui;

import java.util.ResourceBundle;

import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Session;

class SessionCreator implements DataPane.BeanCreator<Session> {

	private final Beans beans;
	private final ResourceBundle messages;

	public SessionCreator(final Beans beans, final ResourceBundle messages) {
		this.beans = beans;
		this.messages = messages;
	}

	@Override
	public Session createBean() {
		// TODO Auto-generated method stub
		// must ask for the right Piece to attach the Session to
		// (note: no Piece => return null + popup "create Piece first")
		// and the start date will be set to "now"
		return null;
	}
}