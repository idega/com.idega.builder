/*
 * $Id: IBPageName.java 1.1 29.10.2004 laddi Exp $
 * Created on 29.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.data;



import com.idega.core.builder.data.*;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.IDOEntity;


/**
 * Last modified: 29.10.2004 11:10:14 by laddi
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
public interface IBPageName extends IDOEntity {

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#setPageName
	 */
	public void setPageName(String name);

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#getPageName
	 */
	public String getPageName();

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#getLocaleId
	 */
	public int getLocaleId();

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#setLocaleId
	 */
	public void setLocaleId(int id);

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#setLocaleId
	 */
	public void setLocaleId(Integer id);

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#getLocale
	 */
	public ICLocale getLocale();

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#setLocale
	 */
	public void setLocale(ICLocale locale);

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#getPageId
	 */
	public int getPageId();

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#setPageId
	 */
	public void setPageId(int id);

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#setPageId
	 */
	public void setPageId(Integer id);

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#getPage
	 */
	public ICPage getPage();

	/**
	 * @see com.idega.builder.data.IBPageNameBMPBean#setPage
	 */
	public void setPage(ICPage page);

}
