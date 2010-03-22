package com.idega.builder.form.data;


import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.data.IDOFactory;

public class EmailedFormHomeImpl extends IDOFactory implements EmailedFormHome {
	public Class getEntityInterfaceClass() {
		return EmailedForm.class;
	}

	public EmailedForm create() throws CreateException {
		return (EmailedForm) super.createIDO();
	}

	public EmailedForm findByPrimaryKey(Object pk) throws FinderException {
		return (EmailedForm) super.findByPrimaryKeyIDO(pk);
	}
}