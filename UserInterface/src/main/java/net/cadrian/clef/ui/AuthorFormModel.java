package net.cadrian.clef.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.ui.form.FieldComponentFactory;
import net.cadrian.clef.ui.form.PropertiesComponentFactory;
import net.cadrian.clef.ui.form.TextAreaComponentFactory;
import net.cadrian.clef.ui.form.TextFieldComponentFactory;

class AuthorFormModel extends BeanFormModel<Author> {

	private static final Map<String, FieldComponentFactory<?, ? extends JComponent>> COMPONENT_FACTORIES = new LinkedHashMap<>();
	static {
		final TextFieldComponentFactory nameFactory = new TextFieldComponentFactory();
		final TextAreaComponentFactory notesFactory = new TextAreaComponentFactory();
		final PropertiesComponentFactory propertiesFactory = new PropertiesComponentFactory();
		COMPONENT_FACTORIES.put("Name", nameFactory);
		COMPONENT_FACTORIES.put("Notes", notesFactory);
		COMPONENT_FACTORIES.put("Properties", propertiesFactory);
	}

	AuthorFormModel(final Class<Author> beanType) {
		super(beanType, COMPONENT_FACTORIES);
	}

}
