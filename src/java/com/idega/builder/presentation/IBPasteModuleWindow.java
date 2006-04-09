/*
 * $Id: IBPasteModuleWindow.java,v 1.7 2006/04/09 11:43:34 laddi Exp $
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
public class IBPasteModuleWindow extends IBAdminWindow {
  private static final String IC_OBJECT_ID_PARAMETER = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String IB_PAGE_PARAMETER = BuilderConstants.IB_PAGE_PARAMETER;
  private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
  private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String ACTION_PASTE = BuilderLogic.ACTION_PASTE;
  private static final String ACTION_PASTE_ABOVE = BuilderLogic.ACTION_PASTE_ABOVE;
  private static final String IB_LABEL_PARAMETER = BuilderLogic.IB_LABEL_PARAMETER;
	
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
    String label = iwc.getParameter(IB_LABEL_PARAMETER);

    if (control == null) {
			close();
		}

    setParentToReload();

    if (control.equals(ACTION_PASTE)) {
      pasteObject(iwc,ib_page_id,ib_parent_id,label);
    }
    else if (control.equals(ACTION_PASTE_ABOVE)) {
      pasteObject(ib_page_id,ib_parent_id,ic_object_id,iwc);
    }

    close();
  }

  /**
   *
   */
  public void pasteObject(IWContext iwc, String pageKey, String parentID, String label) {
    BuilderLogic.getInstance().pasteModuleIntoRegion(iwc,pageKey,parentID,label);
  }

  /**
   *
   */
  public void pasteObject(String pageKey, String parentID, String objectID,IWContext iwc) {
    BuilderLogic.getInstance().pasteModuleAbove(iwc,pageKey,parentID,objectID);
  }
}
