package com.idega.builder.data;

import com.idega.core.builder.data.ICPage;
import com.idega.core.localisation.data.ICLocale;
import com.idega.data.IDOEntity;

public interface IBPageProperty  extends IDOEntity {
	public static final String KEY_DESCRIPTION = "pageDescription";
	public void setPropertyValue(String value);
	public String getPropertyValue();
	public int getLocaleId();
	public void setLocaleId(int id);
	public void setLocaleId(Integer id);
	public ICLocale getLocale();
	public void setLocale(ICLocale locale);
	public int getPageId();
	public void setPageId(int id);
	public void setPageId(Integer id);
	public ICPage getPage();
	public void setPage(ICPage page);
	public String getPropertyKey();
	public void setPropertyKey(String key);
}