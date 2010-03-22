package com.idega.builder.form.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface EmailedFormBusinessHome extends IBOHome {
	public EmailedFormBusiness create() throws CreateException, RemoteException;
}