/*
 * $Id: IBLockRegionWindow.java,v 1.3 2001/09/28 15:39:45 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import java.util.List;
import java.util.Iterator;

import com.idega.builder.data.IBPage;
import com.idega.core.data.ICObjectInstance;
import com.idega.core.data.ICObject;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.textObject.Text;
import com.idega.jmodule.object.textObject.Link;
import com.idega.jmodule.object.interfaceobject.Form;
import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.object.interfaceobject.Parameter;
import com.idega.jmodule.object.interfaceobject.EntityUpdater;
import com.idega.builder.business.BuilderLogic;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.data.EntityFinder;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */

public class IBLockRegionWindow extends IBAdminWindow {
  private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;
  private static final String IB_PAGE_PARAMETER = BuilderLogic.IB_PAGE_PARAMETER;
  private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;
  private static final String ACTION_LOCK = BuilderLogic.ACTION_LOCK_REGION;
  private static final String ACTION_UNLOCK = BuilderLogic.ACTION_UNLOCK_REGION;
  private static final String IW_BUNDLE_IDENTIFIER=BuilderLogic.IW_BUNDLE_IDENTIFIER;

  /**
   *
   */
  public IBLockRegionWindow() {
  }

  /**
   *
   */
  public void main(ModuleInfo modinfo) throws Exception {
    super.addTitle("IBLockRegionWindow");
    String ib_parent_id = modinfo.getParameter(IB_PARENT_PARAMETER);
    String ib_page_id = modinfo.getParameter(IB_PAGE_PARAMETER);
    String action = modinfo.getParameter(IB_CONTROL_PARAMETER);
    setParentToReload();
    if (action.equalsIgnoreCase(ACTION_LOCK)) {
      lockRegion(ib_page_id,ib_parent_id);
    }
    else if (action.equalsIgnoreCase(ACTION_UNLOCK)) {
      unlockRegion(ib_page_id,ib_parent_id);
    }
    close();
  }

  /*
   *
   */
  private void lockRegion(String pageKey, String parentID) throws Exception {
     BuilderLogic.getInstance().lockRegion(pageKey,parentID);
  }

  /*
   *
   */
  private void unlockRegion(String pageKey, String parentID) throws Exception {
     BuilderLogic.getInstance().unlockRegion(pageKey,parentID);
  }

  /**
   *
   */
  public String getBundleIdentifier(){
    return(IW_BUNDLE_IDENTIFIER);
  }
}