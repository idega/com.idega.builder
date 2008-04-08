package com.idega.builder.data;


import java.util.Collection;
import java.util.Locale;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;

public interface IBPageNameHome extends IDOHome {

	public IBPageName create() throws CreateException;

	public IBPageName findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAllByPageIdAndLocaleId(int pageId, int localeId) throws FinderException;

	public IBPageName findByPageIdAndLocaleId(int pageId, int localeId) throws FinderException;

	public Collection findAllByPageId(int pageId) throws FinderException;

	public Collection findAll() throws FinderException;
	
	public Collection findAllByPhrase(String phrase, Locale locale) throws FinderException;
}