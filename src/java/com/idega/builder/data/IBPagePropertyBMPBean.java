package com.idega.builder.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.GenericEntity;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.data.query.WildCardColumn;

public class IBPagePropertyBMPBean  extends GenericEntity implements IBPageProperty {
	private static final long serialVersionUID = -3337530653341604192L;
	private static final String TABLE_NAME = "IB_PAGE_PROPERTY";
	private static final String COLUMN_PAGE_ID = "IB_PAGE_ID";
	private static final String COLUMN_LOCALE_ID = "IC_LOCALE_ID";
	private static final String COLUMN_PROPERTY_VALUE = "PROPERTY_VALUE";
	private static final String COLUMN_PROPERTY_KEY = "PROPERTY_KEY";

	@Override
	public String getEntityName() {
		return TABLE_NAME;
	}

	@Override
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(COLUMN_PAGE_ID, "page id", ICPage.class);
		addManyToOneRelationship(COLUMN_LOCALE_ID, "Locale id", ICLocale.class);
		addAttribute(COLUMN_PROPERTY_VALUE, COLUMN_PROPERTY_VALUE, true, true, String.class);
		addAttribute(COLUMN_PROPERTY_KEY, COLUMN_PROPERTY_KEY, true, true, String.class);
	}

	@Override
	public int getLocaleId() {
		return getIntColumnValue(COLUMN_LOCALE_ID);
	}

	@Override
	public void setLocaleId(int id) {
		setColumn(COLUMN_LOCALE_ID, id);
	}

	@Override
	public void setLocaleId(Integer id) {
		setColumn(COLUMN_LOCALE_ID, id);
	}

	@Override
	public ICLocale getLocale() {
		return (ICLocale) getColumnValue(COLUMN_LOCALE_ID);
	}

	@Override
	public void setLocale(ICLocale locale) {
		setColumn(COLUMN_LOCALE_ID, locale);
	}

	@Override
	public int getPageId() {
		return getIntColumnValue(COLUMN_PAGE_ID);
	}

	@Override
	public void setPageId(int id) {
		setColumn(COLUMN_PAGE_ID, id);
	}

	@Override
	public void setPageId(Integer id) {
		setColumn(COLUMN_PAGE_ID, id);
	}

	@Override
	public ICPage getPage() {
		return (ICPage) getColumnValue(COLUMN_PAGE_ID);
	}

	@Override
	public void setPage(ICPage page) {
		setColumn(COLUMN_PAGE_ID, page);
	}
	
	public Object ejbFindProperty(int pageId, int localeId,String propertyKey) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);

		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_PAGE_ID), MatchCriteria.EQUALS, pageId));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_LOCALE_ID), MatchCriteria.EQUALS, localeId));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_PROPERTY_KEY), MatchCriteria.EQUALS, propertyKey));
		
		return idoFindOnePKByQuery(query);
	}
	public Collection<Object> ejbFindPropertiesForAllLocales(int pageId,String propertyKey) throws FinderException {
		Table table = new Table(this);
		SelectQuery query = new SelectQuery(table);

		query.addColumn(new WildCardColumn(table));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_PAGE_ID), MatchCriteria.EQUALS, pageId));
		query.addCriteria(new MatchCriteria(new Column(table, COLUMN_PROPERTY_KEY), MatchCriteria.EQUALS, propertyKey));
		
		return idoFindPKsByQuery(query);
	}

	@Override
	public void setPropertyValue(String value) {
		setColumn(COLUMN_PROPERTY_VALUE, value);
	}

	@Override
	public String getPropertyValue() {
		return getStringColumnValue(COLUMN_PROPERTY_VALUE);
	}

	@Override
	public String getPropertyKey() {
		return getStringColumnValue(COLUMN_PROPERTY_KEY);
	}

	@Override
	public void setPropertyKey(String key) {
		setColumn(COLUMN_PROPERTY_KEY, key);
	}
}