/*
 * $Id: IBTemplateChooserWindow.java,v 1.7 2001/12/13 11:25:41 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.TreeViewer;
import com.idega.presentation.text.Link;
import com.idega.builder.data.IBDomain;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.BuilderLogic;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.3
 */
public class IBTemplateChooserWindow extends AbstractChooserWindow {
  private static final int _width = 300;
  private static final int _height = 500;

  /**
   *
   */
  public IBTemplateChooserWindow() {
    /**
     * @todo Setja inn IWResourceBundle hérna í staðinn fyrir þessa texta.
     */
    setName("Template chooser");
    setWidth(_width);
    setHeight(_height);
  }

  /**
   *
   */
  public void displaySelection(IWContext iwc) {
    add("Select a template");

    try {
      int i_page_id = BuilderLogic.getInstance().getCurrentDomain(iwc).getStartTemplateID();

      TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_page_id,iwc),iwc);

      add(viewer);
      viewer.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME,iwc);
      viewer.setToMaintainParameter(VALUE_PARAMETER_NAME,iwc);

      Link prototype = new Link();
      viewer.setToUseOnClick();
      //sets the hidden input and textinput of the choosing page
      viewer.setOnClick(SELECT_FUNCTION_NAME+"("+viewer.ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME+","+viewer.ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME+")");
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
}