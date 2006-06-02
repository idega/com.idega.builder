package com.idega.builder.data;


import java.util.Collection;
import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface IBPageNameHome extends IDOHome {

	public IBPageName create() throws CreateException;

	public IBPageName findByPrimaryKey(Object pk) throws FinderException;

	public Collection findAllByPageIdAndLocaleId(int pageId, int localeId) throws FinderException;

	public IBPageName findByPageIdAndLocaleId(int pageId, int localeId) throws FinderException;

	public Collection findAllByPageId(int pageId) throws FinderException;

	public Collection findAll() throws FinderException;
}