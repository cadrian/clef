package net.cadrian.clef.database.model.bean;

import net.cadrian.clef.database.bean.Pricing;

public class PricingBean extends AbstractBean implements net.cadrian.clef.model.bean.Pricing {

	private final Pricing bean;
	@SuppressWarnings("unused")
	private final DatabaseBeansHolder db;

	public PricingBean(final Pricing bean, final DatabaseBeansHolder db) {
		this.bean = bean;
		this.db = db;
	}

	@Override
	public Long getId() {
		return bean.getId();
	}

}
