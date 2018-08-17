package net.cadrian.clef.database.io;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

public interface Field<T extends DatabaseBean> {

	String getName();

	boolean hasCondition(T bean) throws DatabaseException;

	String getCondition(T bean) throws DatabaseException;

	void setConditionValue(int index, T bean, PreparedStatement ps) throws DatabaseException;

	void setValue(ResultSet rs, T newBean) throws DatabaseException;

}
