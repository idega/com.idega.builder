/*
 * $Id: IBPageChooserWindow.java,v 1.10 2002/04/07 00:50:01 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.presentation.text.*;
import com.idega.idegaweb.IWConstants;
import com.idega.presentation.Table;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.TreeViewer;
import com.idega.builder.data.IBDomain;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.BuilderLogic;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @modified by <a href=eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 */
public class IBPageChooserWindow extends AbstractChooserWindow {

  private static final int _width = 280;
  private static final int _height = 400;
  private static final String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";

  /**
   *
   */
  public IBPageChooserWindow() {
    setTitle("Page chooser");
    setWidth(_width);
    setHeight(_height);
    setCellpadding(5);
    setScrollbar(true);
  }

  /**
   *
   */
  public void displaySelection(IWContext iwc) {
    IWResourceBundle iwrb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
    addTitle(iwrb.getLocalizedString("select_page","Select page"),IWConstants.BUILDER_FONT_STYLE_TITLE);
    setStyles();

    Text text = new Text(iwrb.getLocalizedString("select_page","Select page")+":");
      text.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    add(text);

    try {
      int i_page_id = BuilderLogic.getInstance().getCurrentDomain(iwc).getStartPageID();

      TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(i_page_id,iwc),iwc);

      add(viewer);
      viewer.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME,iwc);
      viewer.setToMaintainParameter(VALUE_PARAMETER_NAME,iwc);
      viewer.setDefaultOpenLevel(999);

      Link link = new Link();
	link.setNoTextObject(true);
      viewer.setLinkPrototype(link);
      viewer.setTreeStyle(_linkStyle);
      viewer.setToUseOnClick();
      //sets the hidden input and textinput of the choosing page
      viewer.setOnClick(SELECT_FUNCTION_NAME+"("+viewer.ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME+","+viewer.ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME+")");
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  private void setStyles() {
    String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
    String _linkHoverStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#FF8008;text-decoration:none;";
    if ( getParentPage() != null ) {
      getParentPage().setStyleDefinition("A",_linkStyle);
      //getParentPage().setStyleDefinition("A."+STYLE_NAME+":visited",_linkStyle);
      //getParentPage().setStyleDefinition("A."+STYLE_NAME+":active",_linkStyle);
      getParentPage().setStyleDefinition("A:hover",_linkHoverStyle);
    }
  }

}
