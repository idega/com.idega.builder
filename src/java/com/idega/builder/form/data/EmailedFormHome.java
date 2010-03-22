package com.idega.builder.form.data;


import javax.ejb.CreateException;
import com.idega.data.IDOHome;
import javax.ejb.FinderException;

public interface EmailedFormHome extends IDOHome {
	public EmailedForm create() throws CreateException;

	public EmailedForm findByPrimaryKey(Object pk) throws FinderException;
}