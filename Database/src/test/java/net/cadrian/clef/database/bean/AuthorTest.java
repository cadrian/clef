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
package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.cadrian.clef.database.AbstractDatabaseTestHarness;
import net.cadrian.clef.database.DatabaseBeans;
import net.cadrian.clef.database.DatabaseException;

public class AuthorTest extends AbstractDatabaseTestHarness {

	private Long fooPropertyDescriptorId;

	@Override
	@Before
	public void setup() throws DatabaseException {
		super.setup();

		final DatabaseBeans<PropertyDescriptor> propertyDescriptors = new DatabaseBeans<>(getDataSource(),
				PropertyDescriptor.class);
		final PropertyDescriptor template = new PropertyDescriptor();
		template.setEntity("author");
		template.setName("FOO");
		final PropertyDescriptor inserted = propertyDescriptors.insert(template);
		fooPropertyDescriptorId = inserted.getId();
	}

	@Test
	public void testLoad() throws DatabaseException {
		final DatabaseBeans<Author> authors = new DatabaseBeans<>(getDataSource(), Author.class);
		final Author template = new Author();
		final int count = authors.count(template);
		Assert.assertEquals(0, count);
	}

	@Test
	public void testSave() throws DatabaseException {
		final DatabaseBeans<Author> authors = new DatabaseBeans<>(getDataSource(), Author.class);
		final DatabaseBeans<Property> properties = new DatabaseBeans<>(getDataSource(), Property.class);

		insertTestAuthor(authors, properties);

		final Author authorTemplate = new Author();
		authorTemplate.setName("POLOP");
		final int count = authors.count(authorTemplate);
		Assert.assertEquals(1, count);

		final Author checkAuthor = authors.readOne(authorTemplate);
		Assert.assertNotNull(checkAuthor.getId());
		Assert.assertEquals("POLOP", checkAuthor.getName());
		final Collection<Long> authorProperties = checkAuthor.getProperties();
		Assert.assertEquals(1, authorProperties.size());

		final Property propertyTemplate = new Property(authorProperties.iterator().next());
		final Property checkAuthorProperty = properties.readOne(propertyTemplate);
		Assert.assertEquals(fooPropertyDescriptorId, checkAuthorProperty.getPropertyDescriptorId());
		Assert.assertEquals("BAR", checkAuthorProperty.getValue());
	}

	@Test
	public void testDelete() throws DatabaseException {
		final DatabaseBeans<Author> authors = new DatabaseBeans<>(getDataSource(), Author.class);
		final DatabaseBeans<Property> properties = new DatabaseBeans<>(getDataSource(), Property.class);

		insertTestAuthor(authors, properties);

		final Author authorTemplate = new Author();
		authorTemplate.setName("POLOP");
		final Author checkAuthor = authors.readOne(authorTemplate);
		Assert.assertNotNull(checkAuthor.getId());

		authors.delete(checkAuthor);

		final int count = authors.count(authorTemplate);
		Assert.assertEquals(0, count);
	}

	private void insertTestAuthor(final DatabaseBeans<Author> authors, final DatabaseBeans<Property> properties)
			throws DatabaseException {
		Author newAuthor = new Author(null);
		newAuthor.setName("POLOP");
		Property newAuthorProperty = new Property();
		newAuthorProperty.setPropertyDescriptorId(fooPropertyDescriptorId);
		newAuthorProperty.setValue("BAR");

		newAuthorProperty = properties.insert(newAuthorProperty);
		Assert.assertNotNull(newAuthorProperty.getId());
		newAuthor.setProperties(Arrays.asList(newAuthorProperty.getId()));

		newAuthor = authors.insert(newAuthor);
		Assert.assertNotNull(newAuthor.getId());
	}

}
