package com.idega.builder.dynamicpagetrigger.business;


public class DPTCopySessionHomeImpl extends com.idega.business.IBOHomeImpl implements DPTCopySessionHome
{
 protected Class getBeanInterfaceClass(){
  return DPTCopySession.class;
 }


 public DPTCopySession create() throws javax.ejb.CreateException{
  return (DPTCopySession) super.createIBO();
 }



}