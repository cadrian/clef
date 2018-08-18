package net.cadrian.clef.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.ui.form.DateComponentFactory;
import net.cadrian.clef.ui.form.FieldComponentFactory;
import net.cadrian.clef.ui.form.TextAreaComponentFactory;

class SessionFormModel extends BeanFormModel<Session> {

	private static final Map<String, FieldComponentFactory<?, ? extends JComponent>> COMPONENT_FACTORIES = new LinkedHashMap<>();
	static {
		final DateComponentFactory stopFactory = new DateComponentFactory();
		final TextAreaComponentFactory notesFactory = new TextAreaComponentFactory();
		COMPONENT_FACTORIES.put("Stop", stopFactory);
		COMPONENT_FACTORIES.put("Notes", notesFactory);
	}

	SessionFormModel(final Class<Session> beanType) {
		super(beanType, COMPONENT_FACTORIES);
	}

}
