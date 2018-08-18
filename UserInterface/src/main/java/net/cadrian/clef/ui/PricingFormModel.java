package net.cadrian.clef.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.ui.form.FieldComponentFactory;

class PricingFormModel extends BeanFormModel<Pricing> {

	private static final Map<String, FieldComponentFactory<?, ? extends JComponent>> COMPONENT_FACTORIES = new LinkedHashMap<>();

	PricingFormModel(final Class<Pricing> beanType) {
		super(beanType, COMPONENT_FACTORIES);
	}

}
