/*
 * $Id: IBPasteModuleWindow.java,v 1.5 2004/06/24 20:12:24 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.BuilderConstants;
import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPasteModuleWindow extends IBAdminWindow {
  private static final String IC_OBJECT_ID_PARAMETER = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String IB_PAGE_PARAMETER = BuilderConstants.IB_PAGE_PARAMETER;
  private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
  private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String ACTION_PASTE = BuilderLogic.ACTION_PASTE;
  private static final String ACTION_PASTE_ABOVE = BuilderLogic.ACTION_PASTE_ABOVE;

  /**
   *
   */
  public IBPasteModuleWindow() {
    setWidth(300);
    setHeight(200);
  }

  /**
   *
   */
  public void main(IWContext iwc) {
    setTitle("Paste module");

    String control = iwc.getParameter(IB_CONTROL_PARAMETER);
    String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);
    String ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);
    String ic_object_id = iwc.getParameter(IC_OBJECT_ID_PARAMETER);

    if (control == null)
      close();

    setParentToReload();

    if (control.equals(ACTION_PASTE)) {
      pasteObject(iwc,ib_page_id,ib_parent_id);
    }
    else if (control.equals(ACTION_PASTE_ABOVE)) {
      pasteObject(iwc,ib_page_id,ib_parent_id,ic_object_id);
    }

    close();
  }

  /**
   *
   */
  public void pasteObject(IWContext iwc, String pageKey, String parentID) {
    BuilderLogic.getInstance().pasteModule(iwc,pageKey,parentID);
  }

  /**
   *
   */
  public void pasteObject(IWContext iwc, String pageKey, String parentID, String objectID) {
    BuilderLogic.getInstance().pasteModule(iwc,pageKey,parentID,objectID);
  }
}
