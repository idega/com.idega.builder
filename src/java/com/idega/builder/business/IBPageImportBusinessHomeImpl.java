package com.idega.builder.business;


public class IBPageImportBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements IBPageImportBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return IBPageImportBusiness.class;
 }


 public IBPageImportBusiness create() throws javax.ejb.CreateException{
  return (IBPageImportBusiness) super.createIBO();
 }



}