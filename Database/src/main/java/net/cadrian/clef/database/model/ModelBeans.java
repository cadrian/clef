/*
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
 *
 */
package net.cadrian.clef.database.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
import net.cadrian.clef.database.model.bean.PropertyDescriptorBean;
import net.cadrian.clef.database.model.bean.SessionBean;
import net.cadrian.clef.database.model.bean.WorkBean;
import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public class ModelBeans implements Beans {

	private final DatabaseBeansHolder db;

	private final DatabaseBeans<net.cadrian.clef.database.bean.Author> authorsDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Work> worksDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Piece> piecesDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Session> sessionsDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Pricing> pricingsDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.Property> propertiesDatabase;
	private final DatabaseBeans<net.cadrian.clef.database.bean.PropertyDescriptor> propertyDescriptorsDatabase;

	private final Map<Long, AuthorBean> authorsCache = new HashMap<>();
	private final Map<Long, WorkBean> worksCache = new HashMap<>();
	private final Map<Long, PieceBean> piecesCache = new HashMap<>();
	private final Map<Long, SessionBean> sessionsCache = new HashMap<>();
	private final Map<Long, PricingBean> pricingsCache = new HashMap<>();
	private final Map<Long, PropertyBean> propertiesCache = new HashMap<>();
	private final Map<Long, PropertyDescriptorBean> propertyDescriptorsCache = new HashMap<>();

	public ModelBeans(final DatabaseManager manager) {
		try {
			authorsDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Author.class);
			worksDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Work.class);
			piecesDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Piece.class);
			sessionsDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Session.class);
			pricingsDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Pricing.class);
			propertiesDatabase = manager.getDatabaseBeans(net.cadrian.clef.database.bean.Property.class);
			propertyDescriptorsDatabase = manager
					.getDatabaseBeans(net.cadrian.clef.database.bean.PropertyDescriptor.class);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}

		db = new DatabaseBeansHolder() {

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Pricing> getPricings() {
				return pricingsDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Author> getAuthors() {
				return authorsDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Work> getWorks() {
				return worksDatabase;
			}

			@Override
			public DatabaseBeans<net.cadrian.clef.database.bean.Piece> getPieces() {
				return piecesDatabase;
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
			public DatabaseBeans<net.cadrian.clef.database.bean.PropertyDescriptor> getPropertyDescriptors() {
				return propertyDescriptorsDatabase;
			}

			@Override
			public PricingBean getPricing(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getPricings(Collections.singleton(id)).get(0);
			}

			@Override
			public AuthorBean getAuthor(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getAuthors(Collections.singleton(id)).get(0);
			}

			@Override
			public WorkBean getWork(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getWorks(Collections.singleton(id)).get(0);
			}

			@Override
			public PieceBean getPiece(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getPieces(Collections.singleton(id)).get(0);
			}

			@Override
			public SessionBean getSession(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getSessions(Collections.singleton(id)).get(0);
			}

			@Override
			public PropertyBean getProperty(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getProperties(Collections.singleton(id)).get(0);
			}

			@Override
			public PropertyDescriptorBean getPropertyDescriptor(final Long id) {
				if (id == null) {
					return null;
				}
				return ModelBeans.this.getPropertyDescriptors(Collections.singleton(id)).get(0);
			}

			@Override
			public Collection<PricingBean> getPricings(final Collection<Long> ids) {
				return ModelBeans.this.getPricings(ids);
			}

			@Override
			public Collection<AuthorBean> getAuthors(final Collection<Long> ids) {
				return ModelBeans.this.getAuthors(ids);
			}

			@Override
			public Collection<WorkBean> getWorks(final Collection<Long> ids) {
				return ModelBeans.this.getWorks(ids);
			}

			@Override
			public Collection<PieceBean> getPieces(final Collection<Long> ids) {
				return ModelBeans.this.getPieces(ids);
			}

			@Override
			public Collection<SessionBean> getSessions(final Collection<Long> ids) {
				return ModelBeans.this.getSessions(ids);
			}

			@Override
			public Collection<PropertyBean> getProperties(final Collection<Long> ids) {
				return ModelBeans.this.getProperties(ids);
			}

			@Override
			public Collection<PropertyDescriptorBean> getPropertyDescriptors(final Collection<Long> ids) {
				return ModelBeans.this.getPropertyDescriptors(ids);
			}
		};
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
	public Piece createPieceVersion(final Piece piece) {
		final PieceBean result;
		try {
			final net.cadrian.clef.database.bean.Piece template = new net.cadrian.clef.database.bean.Piece();
			template.setWorkId(((WorkBean) piece.getWork()).getId());
			template.setPreviousId(((PieceBean) piece).getId());
			final net.cadrian.clef.database.bean.Piece bean = db.getPieces().insert(template);
			result = new PieceBean(bean, db);
			piecesCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public boolean movePiece(final Piece piece, final Work targetWork) {
		PieceBean dbPiece = (PieceBean) piece;
		final WorkBean dbWork = (WorkBean) targetWork;

		try {
			while (dbPiece != null) {
				final net.cadrian.clef.database.bean.Piece bean = new net.cadrian.clef.database.bean.Piece(
						dbPiece.getId());
				bean.setWorkId(dbWork.getId());
				db.getPieces().update(bean);
				bean.setWorkId(null);
				final net.cadrian.clef.database.bean.Piece updatedBean = db.getPieces().readOne(bean);
				piecesCache.put(dbPiece.getId(), new PieceBean(updatedBean, db));
				dbPiece = (PieceBean) dbPiece.getPrevious();
			}
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}

		return true;
	}

	@Override
	public Session createSession(final Piece piece) {
		final SessionBean result;
		try {
			final net.cadrian.clef.database.bean.Session template = new net.cadrian.clef.database.bean.Session();
			template.setPieceId(((PieceBean) piece).getId());
			final Timestamp now = new Timestamp(System.currentTimeMillis());
			template.setStart(now);
			template.setStop(now);
			final net.cadrian.clef.database.bean.Session bean = db.getSessions().insert(template);
			result = new SessionBean(bean, db);
			sessionsCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Property createProperty(final PropertyDescriptor propertyDescriptor) {
		final PropertyBean result;
		try {
			final net.cadrian.clef.database.bean.Property template = new net.cadrian.clef.database.bean.Property();
			template.setPropertyDescriptorId(((PropertyDescriptorBean) propertyDescriptor).getId());
			final net.cadrian.clef.database.bean.Property bean = db.getProperties().insert(template);
			result = new PropertyBean(bean, db);
			propertiesCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public PropertyDescriptor createPropertyDescriptor(final PropertyDescriptor.Entity entity,
			final PropertyDescriptor.Type type) {
		final PropertyDescriptorBean result;
		try {
			final net.cadrian.clef.database.bean.PropertyDescriptor template = new net.cadrian.clef.database.bean.PropertyDescriptor();
			template.setEntity(entity.name());
			template.setType(type.name());
			final net.cadrian.clef.database.bean.PropertyDescriptor bean = db.getPropertyDescriptors().insert(template);
			result = new PropertyDescriptorBean(bean, db);
			propertyDescriptorsCache.put(bean.getId(), result);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Collection<? extends Pricing> getPricings() {
		final Collection<? extends Pricing> result;
		try {
			final net.cadrian.clef.database.bean.Pricing template = new net.cadrian.clef.database.bean.Pricing();
			final Map<Long, net.cadrian.clef.database.bean.Pricing> pricings = db.getPricings().readMany(template,
					true);
			result = db.getPricings(pricings.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Collection<? extends Author> getAuthors() {
		final Collection<? extends Author> result;
		try {
			final net.cadrian.clef.database.bean.Author template = new net.cadrian.clef.database.bean.Author();
			final Map<Long, net.cadrian.clef.database.bean.Author> authors = db.getAuthors().readMany(template, true);
			result = db.getAuthors(authors.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Collection<? extends Work> getWorks() {
		final Collection<? extends Work> result;
		try {
			final net.cadrian.clef.database.bean.Work template = new net.cadrian.clef.database.bean.Work();
			final Map<Long, net.cadrian.clef.database.bean.Work> works = db.getWorks().readMany(template, true);
			result = db.getWorks(works.keySet());
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

	@Override
	public Collection<? extends Piece> getPieces() {
		final Collection<? extends Piece> result;
		try {
			final net.cadrian.clef.database.bean.Piece template = new net.cadrian.clef.database.bean.Piece();
			final Map<Long, net.cadrian.clef.database.bean.Piece> pieces = db.getPieces().readMany(template, true);
			result = db.getPieces(pieces.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Collection<? extends Session> getSessions() {
		final Collection<? extends Session> result;
		try {
			final net.cadrian.clef.database.bean.Session template = new net.cadrian.clef.database.bean.Session();
			final Map<Long, net.cadrian.clef.database.bean.Session> sessions = db.getSessions().readMany(template,
					true);
			result = db.getSessions(sessions.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
		return result;
	}

	@Override
	public Collection<? extends PropertyDescriptor> getPropertyDescriptors(final Entity entity) {
		final Collection<? extends PropertyDescriptor> result;
		try {
			final net.cadrian.clef.database.bean.PropertyDescriptor template = new net.cadrian.clef.database.bean.PropertyDescriptor();
			template.setEntity(entity.name());
			final Map<Long, net.cadrian.clef.database.bean.PropertyDescriptor> propertyDescriptors = db
					.getPropertyDescriptors().readMany(template, true);
			result = db.getPropertyDescriptors(propertyDescriptors.keySet());
		} catch (final DatabaseException e) {
			throw new ModelException(e);
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
					final net.cadrian.clef.database.bean.Pricing bean = pricingsDatabase.readOne(template);
					if (bean == null) {
						return Arrays.asList((PricingBean) null);
					}
					pricing = new PricingBean(bean, db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(pricing);
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
					final net.cadrian.clef.database.bean.Author bean = authorsDatabase.readOne(template);
					if (bean == null) {
						return Arrays.asList((AuthorBean) null);
					}
					author = new AuthorBean(bean, db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(author);
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
					final net.cadrian.clef.database.bean.Work bean = worksDatabase.readOne(template);
					if (bean == null) {
						return Arrays.asList((WorkBean) null);
					}
					work = new WorkBean(bean, db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(work);
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
					final net.cadrian.clef.database.bean.Piece bean = piecesDatabase.readOne(template);
					if (bean == null) {
						return Arrays.asList((PieceBean) null);
					}
					piece = new PieceBean(bean, db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(piece);
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
					final net.cadrian.clef.database.bean.Session bean = sessionsDatabase.readOne(template);
					if (bean == null) {
						return Arrays.asList((SessionBean) null);
					}
					session = new SessionBean(bean, db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(session);
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
					final net.cadrian.clef.database.bean.Property bean = propertiesDatabase.readOne(template);
					if (bean == null) {
						return null;
					}
					property = new PropertyBean(bean, db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(property);
		}
		return result;
	}

	List<PropertyDescriptorBean> getPropertyDescriptors(final Collection<Long> ids) {
		final List<PropertyDescriptorBean> result = new ArrayList<>();
		for (final Long id : ids) {
			PropertyDescriptorBean propertyDescriptor = propertyDescriptorsCache.get(id);
			if (propertyDescriptor == null) {
				final net.cadrian.clef.database.bean.PropertyDescriptor template = new net.cadrian.clef.database.bean.PropertyDescriptor(
						id);
				try {
					final net.cadrian.clef.database.bean.PropertyDescriptor bean = propertyDescriptorsDatabase
							.readOne(template);
					if (bean == null) {
						return null;
					}
					propertyDescriptor = new PropertyDescriptorBean(bean, db);
				} catch (final DatabaseException e) {
					throw new ModelException(e);
				}
			}
			result.add(propertyDescriptor);
		}
		return result;
	}

}
