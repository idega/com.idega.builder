/*
 * $Id: IBCopyModuleWindow.java,v 1.3 2001/12/17 16:11:30 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.data.IBObjectLibrary;
import com.idega.builder.business.BuilderLogic;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Form;
import com.idega.presentation.Table;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;

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
//  private static final String IB_LIBRARY_NAME = BuilderLogic.IB_LIBRARY_NAME;
//  private static final String CLIPBOARD = IBObjectLibrary.CLIPBOARD;
//  private static final String LIBRARY = IBObjectLibrary.LIBRARY;

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
    String ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);
    String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);
    String ic_object_id = iwc.getParameter(IC_OBJECT_ID_PARAMETER);
//    String libraryName = iwc.getParameter();

    if (control == null)
      close();

    setParentToReload();

    if (control.equals(ACTION_COPY)) {
      copyObject(ib_parent_id,ic_object_id,iwc.getUserId(),"C",null);
    }
    else if (control.equals(ACTION_LIBRARY)) {
      String name = "Test";
      copyObject(ib_parent_id,ic_object_id,iwc.getUserId(),"L",name);
    }
    close();
  }

  /**
   *
   */
  public void copyObject(String parentID,String objectID, int userId, String type, String name) {
//    BuilderLogic.getInstance().copyModule(parentID,Integer.parseInt(objectID),name);
  }
}