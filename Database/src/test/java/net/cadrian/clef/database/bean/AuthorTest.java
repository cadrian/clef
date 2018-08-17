package net.cadrian.clef.database.bean;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.cadrian.clef.database.DatabaseBeans;
import net.cadrian.clef.database.DatabaseException;

public class AuthorTest extends AbstractDatabaseTestHarness {

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
		final List<Long> authorProperties = checkAuthor.getProperties();
		Assert.assertEquals(1, authorProperties.size());

		final Property propertyTemplate = new Property(authorProperties.get(0));
		final Property checkAuthorProperty = properties.readOne(propertyTemplate);
		Assert.assertEquals("FOO", checkAuthorProperty.getName());
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

		// TODO: how to check that all properties where effectively removed?
	}

	private void insertTestAuthor(final DatabaseBeans<Author> authors, final DatabaseBeans<Property> properties)
			throws DatabaseException {
		Author newAuthor = new Author(null);
		newAuthor.setName("POLOP");
		Property newAuthorProperty = new Property();
		newAuthorProperty.setName("FOO");
		newAuthorProperty.setValue("BAR");

		newAuthorProperty = properties.insert(newAuthorProperty);
		Assert.assertNotNull(newAuthorProperty.getId());
		newAuthor.setProperties(Arrays.asList(newAuthorProperty.getId()));

		newAuthor = authors.insert(newAuthor);
		Assert.assertNotNull(newAuthor.getId());
	}

}
