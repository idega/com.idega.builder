package com.idega.builder.data;

import java.util.Collection;

import javax.ejb.CreateException;

import com.idega.data.IDOHome;

public interface IBPagePropertyHome  extends IDOHome {
	public IBPageProperty create() throws CreateException;
	public IBPageProperty getProperty(int pageId, int localeId,String propertyKey);
	public Collection<IBPageProperty> getPropertiesForAllLocales(int pageId,String propertyKey);
}