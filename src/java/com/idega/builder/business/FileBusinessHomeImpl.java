package com.idega.builder.business;


public class FileBusinessHomeImpl extends com.idega.business.IBOHomeImpl implements FileBusinessHome
{
 protected Class getBeanInterfaceClass(){
  return FileBusiness.class;
 }


 public FileBusiness create() throws javax.ejb.CreateException{
  return (FileBusiness) super.createIBO();
 }



}