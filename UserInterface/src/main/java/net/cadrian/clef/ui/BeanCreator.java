package net.cadrian.clef.ui;

import net.cadrian.clef.model.Bean;

@FunctionalInterface
public interface BeanCreator<T extends Bean> {
	T createBean();
}