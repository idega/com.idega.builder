package com.idega.builder.form.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class EmailedFormBusinessHomeImpl extends IBOHomeImpl implements
		EmailedFormBusinessHome {
	public Class getBeanInterfaceClass() {
		return EmailedFormBusiness.class;
	}

	public EmailedFormBusiness create() throws CreateException {
		return (EmailedFormBusiness) super.createIBO();
	}
}