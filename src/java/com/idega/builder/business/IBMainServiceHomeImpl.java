package com.idega.builder.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class IBMainServiceHomeImpl extends IBOHomeImpl implements IBMainServiceHome {
	public Class getBeanInterfaceClass() {
		return IBMainService.class;
	}

	public IBMainService create() throws CreateException {
		return (IBMainService) super.createIBO();
	}
}