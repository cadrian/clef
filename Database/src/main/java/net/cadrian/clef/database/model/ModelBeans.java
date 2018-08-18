package net.cadrian.clef.database.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.cadrian.clef.database.DatabaseBeans;
import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.DatabaseManager;
import net.cadrian.clef.database.model.bean.AuthorBean;
import net.cadrian.clef.database.model.bean.DatabaseBeansHolder;
import net.cadrian.clef.database.model.bean.PieceBean;
import net.cadrian.clef.database.model.bean.PricingBean;
import net.cadrian.clef.database.model.bean.PropertyBean;
import net.cadrian.clef.database.model.bean.SessionBean;
import net.cadrian.clef.database.model.bean.WorkBean;
import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public class ModelBeans implements Beans {

	private final DatabaseBeansHolder db;

	private final DatabaseBeans<net.cadrian.clef.database.bean.Work> worksDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Session> sessionsDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Property> propertiesDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Pricing> pricingsDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Piece> piecesDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Author> authorsDatabase;

	private final Map<Long, AuthorBean> authorsCache = new HashMap<>();
	private final Map<Long, SessionBean> sessionsCache = new HashMap<>();
	private final Map<Long, PieceBean> piecesCache = new HashMap<>();
	private final Map<Long, WorkBean> worksCache = new HashMap<>();
	private final Map<Long, PricingBean> pricingsCache = new HashMap<>();
	private final Map<Long, PropertyBean> propertiesCache = new HashMap<>();

	public ModelBeans(final DatabaseManager manager) {
		try {
			worksDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Work.class);
			sessionsDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Session.class);
			propertiesDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Property.class);
			pricingsDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Pricing.class);
			piecesDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Piece.class);
			authorsDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Author.class);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}

		db = new DatabaseBeansHolder() {

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Work> getWorks() {
				return worksDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Session> getSessions() {
				return sessionsDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Property> getProperties() {
				return propertiesDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Pricing> getPricings() {
				return pricingsDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Piece> getPieces() {
				return piecesDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Author> getAuthors() {
				return authorsDatabase;
			}

			@Override
			public AuthorBean getAuthor(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getAuthors(Collections.singleton(id)).get(0);
			}

			@Override
			public SessionBean getSession(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getSessions(Collections.singleton(id)).get(0);
			}

			@Override
			public PieceBean getPiece(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getPieces(Collections.singleton(id)).get(0);
			}

			@Override
			public WorkBean getWork(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getWorks(Collections.singleton(id)).get(0);
			}

			@Override
			public PricingBean getPricing(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getPricings(Collections.singleton(id)).get(0);
			}

			@Override
			public PropertyBean getProperty(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getProperties(Collections.singleton(id)).get(0);
			}

			@Override
			public Collection<AuthorBean> getAuthors(final Collection<Long> ids) {
				return ModelBeans.this.getAuthors(ids);
			}

			@Override
			public Collection<SessionBean> getSessions(final Collection<Long> ids) {
				return ModelBeans.this.getSessions(ids);
			}

			@Override
			public Collection<PieceBean> getPieces(final Collection<Long> ids) {
				return ModelBeans.this.getPieces(ids);
			}

			@Override
			public Collection<WorkBean> getWorks(final Collection<Long> ids) {
				return ModelBeans.this.getWorks(ids);
			}

			@Override
			public Collection<PricingBean> getPricings(final Collection<Long> ids) {
				return ModelBeans.this.getPricings(ids);
			}

			@Override
			public Collection<PropertyBean> getProperties(final Collection<Long> ids) {
				return ModelBeans.this.getProperties(ids);
			}
		};
	}

	@Override
	public Session createSession(final Piece piece) {
		final SessionBean result;
		try {
			final net.cadrian.clef.database.bean.Session template = new net.cadrian.clef.database.bean.Session();
			template.setPieceId(((PieceBean) piece).getId());
			final net.cadrian.clef.database.bean.Session bean = db.getSessions().insert(template);
			result = new SessionBean(bean, db);
			sessionsCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Property createProperty(final String name) {
		final PropertyBean result;
		try {
			final net.cadrian.clef.database.bean.Property template = new net.cadrian.clef.database.bean.Property();
			template.setName(name);
			final net.cadrian.clef.database.bean.Property bean = db.getProperties().insert(template);
			result = new PropertyBean(bean, db);
			propertiesCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Piece createPiece(final Work work) {
		final PieceBean result;
		try {
			final net.cadrian.clef.database.bean.Piece template = new net.cadrian.clef.database.bean.Piece();
			template.setWorkId(((WorkBean) work).getId());
			final net.cadrian.clef.database.bean.Piece bean = db.getPieces().insert(template);
			result = new PieceBean(bean, db);
			piecesCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Work createWork(final Author author, final Pricing pricing) {
		final WorkBean result;
		try {
			final net.cadrian.clef.database.bean.Work template = new net.cadrian.clef.database.bean.Work();
			template.setAuthorId(((AuthorBean) author).getId());
			template.setPricingId(((PricingBean) pricing).getId());
			final net.cadrian.clef.database.bean.Work bean = db.getWorks().insert(template);
			result = new WorkBean(bean, db);
			worksCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Author createAuthor() {
		final AuthorBean result;
		try {
			final net.cadrian.clef.database.bean.Author template = new net.cadrian.clef.database.bean.Author();
			final net.cadrian.clef.database.bean.Author bean = db.getAuthors().insert(template);
			result = new AuthorBean(bean, db);
			authorsCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Pricing createPricing() {
		final PricingBean result;
		try {
			final net.cadrian.clef.database.bean.Pricing template = new net.cadrian.clef.database.bean.Pricing();
			final net.cadrian.clef.database.bean.Pricing bean = db.getPricings().insert(template);
			result = new PricingBean(bean, db);
			pricingsCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Collection<? extends Work> getWorksBy(final Author author) {
		final Collection<? extends Work> result;
		try {
			final net.cadrian.clef.database.bean.Work template = new net.cadrian.clef.database.bean.Work();
			template.setAuthorId(((AuthorBean) author).getId());
			final Map<Long, net.cadrian.clef.database.bean.Work> works = db.getWorks().readMany(template, true);
			result = db.getWorks(works.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Collection<? extends Work> getWorksPriced(final Pricing pricing) {
		final Collection<? extends Work> result;
		try {
			final net.cadrian.clef.database.bean.Work template = new net.cadrian.clef.database.bean.Work();
			template.setPricingId(((PricingBean) pricing).getId());
			final Map<Long, net.cadrian.clef.database.bean.Work> works = db.getWorks().readMany(template, true);
			result = db.getWorks(works.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	List<AuthorBean> getAuthors(final Collection<Long> ids) {
		final List<AuthorBean> result = new ArrayList<>();
		for (final Long id : ids) {
			AuthorBean author = authorsCache.get(id);
			if (author == null) {
				final net.cadrian.clef.database.bean.Author template = new net.cadrian.clef.database.bean.Author(id);
				try {
					author = new AuthorBean(authorsDatabase.readOne(template), db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(author);
		}
		return result;
	}

	List<SessionBean> getSessions(final Collection<Long> ids) {
		final List<SessionBean> result = new ArrayList<>();
		for (final Long id : ids) {
			SessionBean session = sessionsCache.get(id);
			if (session == null) {
				final net.cadrian.clef.database.bean.Session template = new net.cadrian.clef.database.bean.Session(id);
				try {
					session = new SessionBean(sessionsDatabase.readOne(template), db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(session);
		}
		return result;
	}

	List<PieceBean> getPieces(final Collection<Long> ids) {
		final List<PieceBean> result = new ArrayList<>();
		for (final Long id : ids) {
			PieceBean piece = piecesCache.get(id);
			if (piece == null) {
				final net.cadrian.clef.database.bean.Piece template = new net.cadrian.clef.database.bean.Piece(id);
				try {
					piece = new PieceBean(piecesDatabase.readOne(template), db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(piece);
		}
		return result;
	}

	List<WorkBean> getWorks(final Collection<Long> ids) {
		final List<WorkBean> result = new ArrayList<>();
		for (final Long id : ids) {
			WorkBean work = worksCache.get(id);
			if (work == null) {
				final net.cadrian.clef.database.bean.Work template = new net.cadrian.clef.database.bean.Work(id);
				try {
					work = new WorkBean(worksDatabase.readOne(template), db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(work);
		}
		return result;
	}

	List<PricingBean> getPricings(final Collection<Long> ids) {
		final List<PricingBean> result = new ArrayList<>();
		for (final Long id : ids) {
			PricingBean pricing = pricingsCache.get(id);
			if (pricing == null) {
				final net.cadrian.clef.database.bean.Pricing template = new net.cadrian.clef.database.bean.Pricing(id);
				try {
					pricing = new PricingBean(pricingsDatabase.readOne(template), db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(pricing);
		}
		return result;
	}

	List<PropertyBean> getProperties(final Collection<Long> ids) {
		final List<PropertyBean> result = new ArrayList<>();
		for (final Long id : ids) {
			PropertyBean property = propertiesCache.get(id);
			if (property == null) {
				final net.cadrian.clef.database.bean.Property template = new net.cadrian.clef.database.bean.Property(
						id);
				try {
					property = new PropertyBean(propertiesDatabase.readOne(template), db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(property);
		}
		return result;
	}

}
