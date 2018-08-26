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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.cadrian.clef.database.AbstractDatabaseTestHarness;
import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.PropertyDescriptor;
import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.PropertyDescriptor.Type;
import net.cadrian.clef.model.bean.Work;

public class ModelBeansTest extends AbstractDatabaseTestHarness {

	private ModelBeans beans;
	private PropertyDescriptor copyrightPropertyDescriptor;

	@Before
	@Override
	public void setup() throws DatabaseException {
		super.setup();
		beans = new ModelBeans(getManager());
		copyrightPropertyDescriptor = beans.createPropertyDescriptor(Entity.piece, Type.string);
	}

	@Test
	public void test() {
		final Author author = beans.createAuthor();
		final Pricing pricing = beans.createPricing();
		author.setName("POLOP");

		final Work work = beans.createWork(author, pricing);
		work.setName("WORK BY POLOP");

		final Collection<? extends Work> worksByPolop = beans.getWorksBy(author);
		Assert.assertEquals(1, worksByPolop.size());
		Assert.assertSame(work, worksByPolop.iterator().next());

		final Piece lullaby = beans.createPiece(work);
		lullaby.setName("LULLABY");
		lullaby.setDuration(42L); // 00:42 -- enough to sleep deeply
		final Piece waltz = beans.createPiece(work);
		waltz.setName("WALTZ");
		waltz.setDuration(3 * 60L + 37L); // 03:37 -- enough to kiss a girl

		final Piece lullaby2 = beans.createPieceVersion(lullaby);
		lullaby.setName("LULLABY");
		lullaby.setDuration(53L); // 00:53 -- a bit longer

		Assert.assertSame(lullaby, lullaby2.getPrevious());
		Assert.assertSame(lullaby2, lullaby.getNext());

		final Collection<? extends Piece> pieces = work.getPieces();
		Assert.assertEquals(2, pieces.size());
		Assert.assertFalse(pieces.contains(lullaby));
		Assert.assertTrue(pieces.contains(lullaby2));
		Assert.assertTrue(pieces.contains(waltz));

		final Property copyright = beans.createProperty(copyrightPropertyDescriptor);
		copyright.setValue("This is a test, it has no rights");

		final Set<Property> propertiesToSet = new HashSet<>();
		propertiesToSet.add(copyright);
		lullaby.setProperties(propertiesToSet);

		final Collection<? extends Property> properties = lullaby.getProperties();
		Assert.assertEquals(1, properties.size());
		Assert.assertSame(copyright, properties.iterator().next());
	}

}
