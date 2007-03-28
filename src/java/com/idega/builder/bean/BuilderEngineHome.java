package com.idega.builder.bean;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface BuilderEngineHome extends IBOHome {
	public BuilderEngine create() throws CreateException, RemoteException;
}