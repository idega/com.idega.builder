package com.idega.builder.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface IBMainServiceHome extends IBOHome {
	public IBMainService create() throws CreateException, RemoteException;
}