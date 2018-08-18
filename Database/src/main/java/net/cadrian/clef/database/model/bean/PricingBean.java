package net.cadrian.clef.database.model.bean;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Pricing;
import net.cadrian.clef.model.ModelException;

public class PricingBean extends AbstractBean implements net.cadrian.clef.model.bean.Pricing {

	private final Pricing bean;

	public PricingBean(final Pricing bean, final DatabaseBeansHolder db) {
		super(db);
		this.bean = bean;
	}

	@Override
	public Long getId() {
		return bean.getId();
	}

	@Override
	public void delete() {
		try {
			db.getPricings().delete(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

}
