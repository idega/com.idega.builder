package com.idega.builder.business;


public class IBPageExportBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements IBPageExportBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return IBPageExportBusiness.class;
 }


 public IBPageExportBusiness create() throws javax.ejb.CreateException{
  return (IBPageExportBusiness) super.createIBO();
 }



}