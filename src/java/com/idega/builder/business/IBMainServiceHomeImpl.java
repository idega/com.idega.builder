package com.idega.builder.business;


public class IBMainServiceHomeImpl extends com.idega.business.IBOHomeImpl implements IBMainServiceHome
{
 protected Class getBeanInterfaceClass(){
  return IBMainService.class;
 }


 public IBMainService create() throws javax.ejb.CreateException{
  return (IBMainService) super.createIBO();
 }



}