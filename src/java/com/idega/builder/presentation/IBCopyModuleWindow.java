/*
 * $Id: IBCopyModuleWindow.java,v 1.1 2001/11/01 17:21:07 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

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
  private static final String ic_object_id_parameter = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String ib_parent_parameter = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String ib_page_parameter = BuilderLogic.IB_PAGE_PARAMETER;

  private static final String IB_CONFIRM = "ib_del_confirm";

  public IBCopyModuleWindow() {
    setWidth(300);
    setHeight(200);
  }

  public void main(IWContext iwc) {
    setTitle("Copy module");

    String ib_parent_id = iwc.getParameter(ib_parent_parameter);
    String ib_page_id = iwc.getParameter(ib_page_parameter);
    setParentToReload();
    String ic_object_id = iwc.getParameter(ic_object_id_parameter);

    boolean doConfirm = !iwc.isParameterSet(IB_CONFIRM);
    if (doConfirm) {
      add(getConfirmBox(iwc));
    }
    else {
      copyObject(ib_page_id,ib_parent_id,ic_object_id);
      close();
    }
  }

  public void copyObject(String pageKey,String parentID,String objectID) {
//     BuilderLogic.getInstance().deleteModule(pageKey,parentID,Integer.parseInt(objectID));
  }

  public PresentationObject getConfirmBox(IWContext iwc) {
    Table t = new Table(1,2);
    Form f = new Form();

    f.maintainParameter(ic_object_id_parameter);
    f.maintainParameter(ib_parent_parameter);
    f.maintainParameter(ib_page_parameter);

    f.add(t);
    t.setWidth("100%");
    t.setHeight("150");
    t.setAlignment(com.idega.idegaweb.IWConstants.CENTER_ALIGNMENT);

    Text confirmText = new Text("Copy this module to clipboard or library");
    t.add(confirmText,1,1);

    SubmitButton button = new SubmitButton(IB_CONFIRM,"Yes");

    Table innerTable = new Table(2,1);
    innerTable.setAlignment(com.idega.idegaweb.IWConstants.CENTER_ALIGNMENT);
    innerTable.add(button,1,1);
    t.add(innerTable,1,2);

    return(f);
  }
}