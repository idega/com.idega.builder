package com.idega.builder.dynamicpagetrigger.business;


public class DPTTriggerBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements DPTTriggerBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return DPTTriggerBusiness.class;
 }


 public DPTTriggerBusiness create() throws javax.ejb.CreateException{
  return (DPTTriggerBusiness) super.createIBO();
 }



}