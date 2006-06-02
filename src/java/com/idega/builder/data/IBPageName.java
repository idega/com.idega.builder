package com.idega.builder.data;


import com.idega.core.localisation.data.ICLocale;
import com.idega.core.builder.data.ICPage;
import com.idega.data.IDOEntity;

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