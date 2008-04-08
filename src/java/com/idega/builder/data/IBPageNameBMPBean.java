/*
 * $Id:$
 *
 * Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.builder.data.ICPage;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.util.CoreConstants;

/**
 * This class does something very clever.....
 * 
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageNameBMPBean extends GenericEntity implements IBPageName {
	private static final String TABLE_NAME = "IB_PAGE_NAME";
	private static final String PAGE_ID = "IB_PAGE_ID";
	private static final String LOCALE_ID = "IC_LOCALE_ID";
	private static final String PAGE_NAME = "PAGE_NAME";

	public IBPageNameBMPBean() {
		super();
	}

	public IBPageNameBMPBean(int id) throws SQLException {
		super(id);
	}

	/**
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return TABLE_NAME;
	}

	/**
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addManyToOneRelationship(PAGE_ID, "page id", ICPage.class);
		addManyToOneRelationship(LOCALE_ID, "Locale id", ICLocale.class);
		addAttribute(PAGE_NAME, "Localized page name", true, true, String.class);
	}

	public void setPageName(String name) {
		setColumn(PAGE_NAME, name);
	}

	public String getPageName() {
		return getStringColumnValue(PAGE_NAME);
	}

	public int getLocaleId() {
		return getIntColumnValue(LOCALE_ID);
	}

	public void setLocaleId(int id) {
		setColumn(LOCALE_ID, id);
	}

	public void setLocaleId(Integer id) {
		setColumn(LOCALE_ID, id);
	}

	public ICLocale getLocale() {
		return (ICLocale) getColumnValue(LOCALE_ID);
	}

	public void setLocale(ICLocale locale) {
		setColumn(LOCALE_ID, locale);
	}

	public int getPageId() {
		return getIntColumnValue(PAGE_ID);
	}

	public void setPageId(int id) {
		setColumn(PAGE_ID, id);
	}

	public void setPageId(Integer id) {
		setColumn(PAGE_ID, id);
	}

	public ICPage getPage() {
		return (ICPage) getColumnValue(PAGE_ID);
	}

	public void setPage(ICPage page) {
		setColumn(PAGE_ID, page);
	}
	
	public Collection ejbFindAllByPageIdAndLocaleId(int pageId, int localeId) throws FinderException {
    StringBuffer sql = new StringBuffer("select * from ");
    sql.append(getTableName());
    sql.append(" where ");
    sql.append(PAGE_ID);
    sql.append(" = ");
    sql.append(pageId);
    sql.append(" and ");
    sql.append(LOCALE_ID);
    sql.append(" = ");
    sql.append(localeId);
    
    return super.idoFindIDsBySQL(sql.toString());
	}
	
	public Integer ejbFindByPageIdAndLocaleId(int pageId, int localeId) throws FinderException {
    StringBuffer sql = new StringBuffer("select * from ");
    sql.append(getTableName());
    sql.append(" where ");
    sql.append(PAGE_ID);
    sql.append(" = ");
    sql.append(pageId);
    sql.append(" and ");
    sql.append(LOCALE_ID);
    sql.append(" = ");
    sql.append(localeId);
    
    return (Integer) super.idoFindOnePKBySQL(sql.toString());
	}
	
	
	public Collection ejbFindAllByPageId(int pageId) throws FinderException {
	    
		SelectQuery query = idoSelectPKQuery();
		Table table = idoQueryTable();
	    query.addCriteria(new MatchCriteria(table,PAGE_ID,MatchCriteria.EQUALS,pageId));
	    return idoFindPKsByQuery(query);
	}
	
	public Collection ejbFindAll() throws FinderException {
		return super.idoFindAllIDsBySQL();	
	}
	
	public Collection ejbFindAllByPhrase(String phrase, Locale locale) throws FinderException {
		IDOQuery query = idoQuery("select ").append(getIDColumnName()).append(" from ").append(getEntityName());
		query.appendWhereEquals(LOCALE_ID, ICLocaleBusiness.getLocaleId(locale)).appendAnd().append("lower(").append(PAGE_NAME).append(")").appendLike();
		query.appendSingleQuote().append(CoreConstants.PERCENT).append(phrase.toLowerCase(locale)).append(CoreConstants.PERCENT).appendSingleQuote();
		return idoFindPKsByQuery(query);
	}
}