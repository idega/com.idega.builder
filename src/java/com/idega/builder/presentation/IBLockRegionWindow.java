/*
 * $Id: IBLockRegionWindow.java,v 1.1 2001/09/12 12:42:01 palli Exp $
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
  private static final String ic_object_id_parameter = BuilderLogic.ic_object_id_parameter;
  private static final String ib_parent_parameter = BuilderLogic.ib_parent_parameter;
  private static final String ib_page_parameter = BuilderLogic.ib_page_parameter;

  private static final String ib_control_parameter = BuilderLogic.ib_control_parameter;
  private static final String ACTION_LOCK = BuilderLogic.ACTION_LOCK_REGION;
  private static final String ACTION_UNLOCK = BuilderLogic.ACTION_UNLOCK_REGION;

  private static final String IW_BUNDLE_IDENTIFIER=BuilderLogic.IW_BUNDLE_IDENTIFIER;

  public IBLockRegionWindow() {
  }

  public void main(ModuleInfo modinfo) throws Exception {
    super.addTitle("IBLockRegionWindow");
    String ib_parent_id = modinfo.getParameter(ib_parent_parameter);
    String ib_page_id = modinfo.getParameter(ib_page_parameter);
    String action = modinfo.getParameter(ib_control_parameter);
    setParentToReload();
    String ic_object_id = modinfo.getParameter(ic_object_id_parameter);
    if (action.equalsIgnoreCase(ACTION_LOCK))
      lockRegion(ib_page_id,ib_parent_id,ic_object_id);
    else if (action.equalsIgnoreCase(ACTION_UNLOCK))
      unlockRegion(ib_page_id,ib_parent_id,ic_object_id);
    close();
  }

  private void lockRegion(String pageKey, String parentID, String objectID) throws Exception {
     BuilderLogic.getInstance().lockRegion(pageKey,parentID,Integer.parseInt(objectID));
  }

  private void unlockRegion(String pageKey, String parentID, String objectID) throws Exception {
     BuilderLogic.getInstance().unlockRegion(pageKey,parentID,Integer.parseInt(objectID));
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
}

