package com.idega.builder.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface BuilderSlideListenerHome extends IBOHome {

	public BuilderSlideListener create() throws CreateException, RemoteException;
}