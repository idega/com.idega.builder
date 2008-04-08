package com.idega.builder.data;


import java.util.Collection;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOEntity;
import com.idega.data.IDOFactory;

public class IBPageNameHomeImpl extends IDOFactory implements IBPageNameHome {

	public Class getEntityInterfaceClass() {
		return IBPageName.class;
	}

	public IBPageName create() throws CreateException {
		return (IBPageName) super.createIDO();
	}

	public IBPageName findByPrimaryKey(Object pk) throws FinderException {
		return (IBPageName) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllByPageIdAndLocaleId(int pageId, int localeId) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((IBPageNameBMPBean) entity).ejbFindAllByPageIdAndLocaleId(pageId, localeId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public IBPageName findByPageIdAndLocaleId(int pageId, int localeId) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((IBPageNameBMPBean) entity).ejbFindByPageIdAndLocaleId(pageId, localeId);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllByPageId(int pageId) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((IBPageNameBMPBean) entity).ejbFindAllByPageId(pageId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAll() throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((IBPageNameBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Collection findAllByPhrase(String phrase, Locale locale) throws FinderException {
		IDOEntity entity = this.idoCheckOutPooledEntity();
		Collection ids = ((IBPageNameBMPBean) entity).ejbFindAllByPhrase(phrase, locale);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}