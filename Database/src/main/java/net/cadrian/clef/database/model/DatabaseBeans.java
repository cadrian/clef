package net.cadrian.clef.database.model;

import java.util.Collection;

import net.cadrian.clef.database.DatabaseManager;
import net.cadrian.clef.model.Beans;
import net.cadrian.clef.model.bean.Author;
import net.cadrian.clef.model.bean.Piece;
import net.cadrian.clef.model.bean.Pricing;
import net.cadrian.clef.model.bean.Property;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.model.bean.Work;

public class DatabaseBeans implements Beans {

	private final DatabaseManager manager;

	public DatabaseBeans(final DatabaseManager manager) {
		this.manager = manager;
	}

	@Override
	public Session createSession(Piece piece) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Property createProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Piece createPiece(Work work) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Work createWork(Author author, Pricing pricing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Author createAuthor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pricing createPricing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Work> getWorksBy(Author author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Work> getWorksPriced(Pricing pricing) {
		// TODO Auto-generated method stub
		return null;
	}

}
