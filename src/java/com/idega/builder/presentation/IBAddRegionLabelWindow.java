/*

 * $Id: IBAddRegionLabelWindow.java,v 1.3 2002/04/06 19:07:39 tryggvil Exp $

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

import com.idega.presentation.IWContext;

import com.idega.presentation.Table;

import com.idega.presentation.text.Text;

import com.idega.presentation.text.Link;

import com.idega.presentation.ui.Form;

import com.idega.presentation.ui.Window;

import com.idega.presentation.ui.Parameter;

import com.idega.presentation.ui.EntityUpdater;

import com.idega.presentation.ui.TextInput;

import com.idega.presentation.ui.SubmitButton;

import com.idega.builder.business.BuilderLogic;

import com.idega.idegaweb.IWResourceBundle;

import com.idega.data.EntityFinder;



/**

 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>

 * @version 1.0

 */



public class IBAddRegionLabelWindow extends IBAdminWindow {

  private static final String LABEL_PARAMETER   = "label_name";

  private static final String IB_PARENT_PARAMETER = BuilderLogic.IB_PARENT_PARAMETER;

  private static final String IB_PAGE_PARAMETER = BuilderLogic.IB_PAGE_PARAMETER;

  private static final String IB_CONTROL_PARAMETER = BuilderLogic.IB_CONTROL_PARAMETER;

  private static final String ACTION_LABEL = BuilderLogic.ACTION_LABEL;

  private static final String IB_LABEL_PARAMETER = BuilderLogic.IB_LABEL_PARAMETER;



  private static final String IW_BUNDLE_IDENTIFIER = BuilderLogic.IW_BUNDLE_IDENTIFIER;



  /**

   *

   */

  public IBAddRegionLabelWindow() {

  }



  /**

   *

   */

  public void main(IWContext iwc) throws Exception {

    super.addTitle("IBAddRegionLabelWindow");

    String ib_parent_id = iwc.getParameter(IB_PARENT_PARAMETER);

    String ib_page_id = iwc.getParameter(IB_PAGE_PARAMETER);

    String action = iwc.getParameter(IB_CONTROL_PARAMETER);

    String label = iwc.getParameter(IB_LABEL_PARAMETER);



    if (action.equalsIgnoreCase(ACTION_LABEL)) {

      IWResourceBundle iwrb = getBundle(iwc).getResourceBundle(iwc);

      Form form = new Form();

      form.addParameter(IB_PARENT_PARAMETER,ib_parent_id);

      form.addParameter(IB_PAGE_PARAMETER,ib_page_id);

      form.addParameter(IB_CONTROL_PARAMETER,action);



      setTitle(iwrb.getLocalizedString("region_label","Put label on region"));

      add(form);

      Table tab = new Table(2,2);

      form.add(tab);

      TextInput inputName = new TextInput(LABEL_PARAMETER);

      tab.add(iwrb.getLocalizedString("Label","Label"),1,1);

      tab.add(inputName,2,1);



      if (label != null)

        inputName.setValue(label);



      SubmitButton button = new SubmitButton("subbi",iwrb.getLocalizedString("save","Save"));

      tab.add(button,2,2);



      String submit = iwc.getParameter("subbi");



      if (submit != null) {

        label = iwc.getParameter(LABEL_PARAMETER);

        BuilderLogic.getInstance().labelRegion(ib_page_id,ib_parent_id,label);



        setParentToReload();

        close();

      }

    }

  }



  /**

   *

   */

  public String getBundleIdentifier(){

    return(IW_BUNDLE_IDENTIFIER);

  }

}
