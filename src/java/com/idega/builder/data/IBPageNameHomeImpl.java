/*
 * $Id: IBPageNameHomeImpl.java 1.1 29.10.2004 laddi Exp $
 * Created on 29.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.data;

import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.data.IDOFactory;


/**
 * Last modified: 29.10.2004 11:11:28 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public class IBPageNameHomeImpl extends IDOFactory implements IBPageNameHome {

	protected Class getEntityInterfaceClass() {
		return IBPageName.class;
	}

	public IBPageName create() throws javax.ejb.CreateException {
		return (IBPageName) super.createIDO();
	}

	public IBPageName findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (IBPageName) super.findByPrimaryKeyIDO(pk);
	}

	public Collection findAllByPageIdAndLocaleId(int pageId, int localeId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((IBPageNameBMPBean) entity).ejbFindAllByPageIdAndLocaleId(pageId, localeId);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public IBPageName findByPageIdAndLocaleId(int pageId, int localeId) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((IBPageNameBMPBean) entity).ejbFindByPageIdAndLocaleId(pageId, localeId);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAll() throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((IBPageNameBMPBean) entity).ejbFindAll();
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

}
