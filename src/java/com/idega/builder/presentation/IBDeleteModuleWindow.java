package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;

import com.idega.jmodule.object.ModuleInfo;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBDeleteModuleWindow extends IBAdminWindow{

  private static final String ic_object_id_parameter = BuilderLogic.IC_OBJECT_ID_PARAMETER;
  private static final String ib_parent_parameter = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String ib_page_parameter = BuilderLogic.IB_PAGE_PARAMETER;

  public IBDeleteModuleWindow() {
  }

  public void main(ModuleInfo modinfo){
      String ib_parent_id = modinfo.getParameter(ib_parent_parameter);
      String ib_page_id = modinfo.getParameter(ib_page_parameter);
      this.setParentToReload();
      String ic_object_id = modinfo.getParameter(ic_object_id_parameter);
      deleteObject(ib_page_id,ib_parent_id,ic_object_id);
      this.close();
  }

  public void deleteObject(String pageKey,String parentID,String objectID){
     BuilderLogic.getInstance().deleteModule(pageKey,parentID,Integer.parseInt(objectID));
  }

}