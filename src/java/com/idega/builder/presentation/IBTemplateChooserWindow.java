/*
 * $Id: IBTemplateChooserWindow.java,v 1.2 2001/10/02 10:34:12 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.jmodule.object.interfaceobject.AbstractChooserWindow;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.interfaceobject.TreeViewer;
import com.idega.jmodule.object.textObject.Link;
import com.idega.builder.data.IBDomain;

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
    setName("Template Chooser");
    setWidth(_width);
    setHeight(_height);
    add("Select a page");
  }

  /**
   * @todo get a treeviewer with the top page selected by default
   * with better implementation ibdomain...
   */
  public void displaySelection(ModuleInfo modinfo) {
    try {
      int i_page_id = 2;

      TreeViewer viewer = TreeViewer.getTreeViewerInstance(new com.idega.builder.data.IBPage(i_page_id),modinfo);

      add(viewer);
      viewer.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER,modinfo);
      viewer.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER,modinfo);
      viewer.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME,modinfo);
      viewer.setToMaintainParameter(VALUE_PARAMETER_NAME,modinfo);

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