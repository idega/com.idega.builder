/*
 * $Id: IBCopyModuleWindow.java,v 1.7 2003/04/03 09:10:10 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBCopyModuleWindow extends IBAdminWindow {
  private static final String IC_OBJECT_ID_PARAMETER = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String IB_PAGE_PARAMETER = BuilderLogic.IB_PAGE_PARAMETER;
  private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
  private static final String ACTION_COPY = BuilderLogic.ACTION_COPY;
  private static final String ACTION_LIBRARY = BuilderLogic.ACTION_LIBRARY;
  private static final String IB_LIBRARY_NAME = BuilderLogic.IB_LIBRARY_NAME;

  /**
   *
   */
  public IBCopyModuleWindow() {
    setWidth(300);
    setHeight(200);
  }

  /**
   *
   */
  public void main(IWContext iwc) {
    setTitle("Copy module");

    String control = iwc.getParameter(IB_CONTROL_PARAMETER);
    String ic_object_id = iwc.getParameter(IC_OBJECT_ID_PARAMETER);
    String libraryName = iwc.getParameter(IB_LIBRARY_NAME);
    String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);


    if (control == null)
      close();

    if (iwc.getSessionAttribute(BuilderLogic.CLIPBOARD) == null)
      setParentToReload();

    if (control.equals(ACTION_COPY)) {
      copyObject(iwc,ib_page_id,ic_object_id);
    }
    else if (control.equals(ACTION_LIBRARY)) {
      copyObjectToLibrary(iwc,ic_object_id,iwc.getUserId(),libraryName);
    }

    close();
  }

  /**
   *
   */
  public void copyObject(IWContext iwc, String pageKey, String objectID) {
    BuilderLogic.getInstance().copyModule(iwc,pageKey,Integer.parseInt(objectID));
  }

  /**
   *
   */
  public void copyObjectToLibrary(IWContext iwc, String objectID, int userId, String name) {
//    BuilderLogic.getInstance().copyModule(parentID,Integer.parseInt(objectID),name);
  }
}
