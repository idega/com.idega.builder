package com.idega.builder.business;


public interface IBMainServiceHome extends com.idega.business.IBOHome
{
 public IBMainService create() throws javax.ejb.CreateException, java.rmi.RemoteException;

}