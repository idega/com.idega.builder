/*
 * $Id: IBPageNameHome.java 1.1 29.10.2004 laddi Exp $
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

import com.idega.data.IDOHome;


/**
 * Last modified: 29.10.2004 11:10:43 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface IBPageNameHome extends IDOHome {

	public IBPageName create() throws javax.ejb.CreateException;

	public IBPageName findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#ejbFindAllByPageIdAndLocaleId
	 */
	public Collection findAllByPageIdAndLocaleId(int pageId, int localeId) throws FinderException;

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#ejbFindByPageIdAndLocaleId
	 */
	public IBPageName findByPageIdAndLocaleId(int pageId, int localeId) throws FinderException;

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

}
