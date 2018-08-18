package net.cadrian.clef.database.model.bean;

import net.cadrian.clef.model.Bean;

abstract class AbstractBean implements Bean {

	protected final DatabaseBeansHolder db;

	public abstract Long getId();

	AbstractBean(final DatabaseBeansHolder db) {
		this.db = db;
	}

}
