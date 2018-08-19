package net.cadrian.clef.ui;

import java.util.Collection;

import net.cadrian.clef.model.Bean;

@FunctionalInterface
public interface BeanGetter<T extends Bean> {
	Collection<? extends T> getAllBeans();
}