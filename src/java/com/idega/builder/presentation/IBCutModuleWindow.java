/*
 * $Id: IBCutModuleWindow.java,v 1.5.2.1 2007/01/12 19:32:24 idegaweb Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBCutModuleWindow extends IBAdminWindow {
  private static final String IC_OBJECT_INSTANCE_ID_PARAMETER = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String IB_PAGE_PARAMETER = BuilderConstants.IB_PAGE_PARAMETER;
  private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
  private static final String ACTION_COPY = BuilderLogic.ACTION_COPY;

  /**
   *
   */
  public IBCutModuleWindow() {
    setWidth(300);
    setHeight(200);
  }

  /**
   *
   */
  public void main(IWContext iwc) {
    setTitle("Copy module");

    String control = iwc.getParameter(IB_CONTROL_PARAMETER);
    String instanceId = iwc.getParameter(IC_OBJECT_INSTANCE_ID_PARAMETER);
    String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);
    String ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);


    if (control == null) {
		close();
	}

    if (iwc.getSessionAttribute(BuilderLogic.CLIPBOARD) == null) {
		setParentToReload();
	}

    if (control.equals(ACTION_COPY)) {
      copyObject(iwc,ib_page_id,instanceId);
      deleteObject(ib_page_id,ib_parent_id,instanceId);
    }

    close();
  }

  /**
   *
   */
  public void copyObject(IWContext iwc, String pageKey, String instanceId) {
    BuilderLogic.getInstance().copyModule(iwc,pageKey,instanceId);
  }

  public void deleteObject(String pageKey,String parentID,String instanceId){
    BuilderLogic.getInstance().deleteModule(pageKey,parentID,instanceId);
  }

}
