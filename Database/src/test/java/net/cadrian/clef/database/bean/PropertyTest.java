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

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.cadrian.clef.database.AbstractDatabaseTestHarness;
import net.cadrian.clef.database.DatabaseBeans;
import net.cadrian.clef.database.DatabaseException;

public class PropertyTest extends AbstractDatabaseTestHarness {

	@Test
	public void testLoad() throws DatabaseException {
		final DatabaseBeans<Property> properties = new DatabaseBeans<>(getDataSource(), Property.class);
		final Property template = new Property();
		final int count = properties.count(template);
		Assert.assertEquals(1, count);
		final Map<Long, Property> propertyBeans = properties.readMany(template, false);
		Assert.assertEquals(1, propertyBeans.size());
		final Property version = propertyBeans.values().iterator().next();
		Assert.assertEquals("VERSION", version.getName());
		Assert.assertEquals("1.0.0", version.getValue());
	}

	@Test
	public void testSave() throws DatabaseException {
		final DatabaseBeans<Property> properties = new DatabaseBeans<>(getDataSource(), Property.class);
		final Property newProperty = new Property();
		newProperty.setName("TEST");
		newProperty.setValue("FOOBAR");
		properties.insert(newProperty);

		final Property template = new Property();
		final int count = properties.count(template);
		Assert.assertEquals(2, count);

		template.setName("TEST");
		final Property testProperty = properties.readOne(template);
		Assert.assertNotNull(testProperty.getId());
		Assert.assertEquals("TEST", testProperty.getName());
		Assert.assertEquals("FOOBAR", testProperty.getValue());
	}

	@Test
	public void testDelete() throws DatabaseException {
		final DatabaseBeans<Property> properties = new DatabaseBeans<>(getDataSource(), Property.class);
		final Property template = new Property();
		template.setName("VERSION");
		final int count = properties.count(template);
		Assert.assertEquals(1, count);

		final Property version = properties.readOne(template);
		properties.delete(version);

		final int countAfterDelete = properties.count(template);
		Assert.assertEquals(0, countAfterDelete);
	}

}
