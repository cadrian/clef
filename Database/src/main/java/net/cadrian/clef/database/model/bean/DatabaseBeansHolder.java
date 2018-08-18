package net.cadrian.clef.database.model.bean;

import java.util.Collection;

import net.cadrian.clef.database.DatabaseBeans;
import net.cadrian.clef.database.bean.Author;
import net.cadrian.clef.database.bean.Piece;
import net.cadrian.clef.database.bean.Pricing;
import net.cadrian.clef.database.bean.Property;
import net.cadrian.clef.database.bean.Session;
import net.cadrian.clef.database.bean.Work;

public interface DatabaseBeansHolder {

	DatabaseBeans<Session> getSessions();

	DatabaseBeans<Piece> getPieces();

	DatabaseBeans<Work> getWorks();

	DatabaseBeans<Pricing> getPricings();

	DatabaseBeans<Author> getAuthors();

	DatabaseBeans<Property> getProperties();

	AuthorBean getAuthor(Long id);

	Collection<AuthorBean> getAuthors(Collection<Long> ids);

	SessionBean getSession(Long id);

	Collection<SessionBean> getSessions(Collection<Long> ids);

	PieceBean getPiece(Long id);

	Collection<PieceBean> getPieces(Collection<Long> ids);

	WorkBean getWork(Long id);

	Collection<WorkBean> getWorks(Collection<Long> ids);

	PricingBean getPricing(Long id);

	Collection<PricingBean> getPricings(Collection<Long> ids);

	PropertyBean getProperty(Long id);

	Collection<PropertyBean> getProperties(Collection<Long> ids);

}
