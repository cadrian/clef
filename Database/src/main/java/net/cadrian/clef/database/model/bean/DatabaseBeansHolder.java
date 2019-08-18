/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
 * This file is part of Clef.
 *
 * Clef is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Clef is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Clef.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.cadrian.clef.database.model.bean;

import java.util.Collection;

import net.cadrian.clef.database.DatabaseBeans;
import net.cadrian.clef.database.bean.Activity;
import net.cadrian.clef.database.bean.Author;
import net.cadrian.clef.database.bean.Piece;
import net.cadrian.clef.database.bean.Pricing;
import net.cadrian.clef.database.bean.Property;
import net.cadrian.clef.database.bean.PropertyDescriptor;
import net.cadrian.clef.database.bean.Session;
import net.cadrian.clef.database.bean.Work;

public interface DatabaseBeansHolder {

	DatabaseBeans<Pricing> getPricings();

	DatabaseBeans<Activity> getActivities();

	DatabaseBeans<Author> getAuthors();

	DatabaseBeans<Work> getWorks();

	DatabaseBeans<Piece> getPieces();

	DatabaseBeans<Session> getSessions();

	DatabaseBeans<Property> getProperties();

	DatabaseBeans<PropertyDescriptor> getPropertyDescriptors();

	PricingBean getPricing(Long id);

	Collection<PricingBean> getPricings(Collection<Long> ids);

	AuthorBean getAuthor(Long id);

	Collection<AuthorBean> getAuthors(Collection<Long> ids);

	WorkBean getWork(Long id);

	Collection<WorkBean> getWorks(Collection<Long> ids);

	PieceBean getPiece(Long id);

	Collection<PieceBean> getPieces(Collection<Long> ids);

	ActivityBean getActivity(Long id);

	Collection<ActivityBean> getActivities(Collection<Long> ids);

	SessionBean getSession(Long id);

	Collection<SessionBean> getSessions(Collection<Long> ids);

	PropertyBean getProperty(Long id);

	Collection<PropertyBean> getProperties(Collection<Long> ids);

	PropertyDescriptorBean getPropertyDescriptor(Long id);

	Collection<PropertyDescriptorBean> getPropertyDescriptors(Collection<Long> ids);

}
