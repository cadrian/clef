package net.cadrian.clef.database.model;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.cadrian.clef.database.AbstractDatabaseTestHarness;
import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.Work;

public class ModelBeansTest extends AbstractDatabaseTestHarness {

	private ModelBeans beans;

	@Before
	@Override
	public void setup() throws DatabaseException {
		super.setup();
		beans = new ModelBeans(getManager());
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

		final Collection<? extends Piece> pieces = work.getPieces();
		Assert.assertEquals(2, pieces.size());
		Assert.assertTrue(pieces.contains(lullaby));
		Assert.assertTrue(pieces.contains(waltz));

		Property copyright = beans.createProperty("COPYRIGHT");
		copyright.setValue("This is a test, it has no rights");

		lullaby.setProperty(copyright);

		Assert.assertSame(copyright, lullaby.getProperty("COPYRIGHT"));
	}

}
