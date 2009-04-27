/*
 * $Id: IBTemplateChooserWindow.java,v 1.17 2009/04/27 14:52:25 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderConstants;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.ui.TreeViewer;

/**
 * @author <a href="mailto:palli@idega.is">Pall Helgason</a>
 * @version 1.3
 */
public class IBTemplateChooserWindow extends AbstractChooserWindow {
  private static final int _width = 280;
  private static final int _height = 400;
  private static final String _linkStyle = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";

  /**
   *
   */
  public IBTemplateChooserWindow() {
    /**
     * @todo Setja inn IWResourceBundle h�rna � sta�inn fyrir �essa texta.
     */
    setTitle("Template chooser");
    setWidth(_width);
    setHeight(_height);
    this.setCellpadding(5);
    setScrollbar(true);
  }

  /**
   *
   */
  @Override
public void displaySelection(IWContext iwc) {
    IWResourceBundle iwrb = iwc.getIWMainApplication().getBundle(BuilderConstants.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);
    addTitle(iwrb.getLocalizedString("select_template","Select template"),IWConstants.BUILDER_FONT_STYLE_TITLE);
    setStyles();

    Text text = new Text(iwrb.getLocalizedString("select_template","Select template")+":");
      text.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    add(text);

    try {
      TreeViewer viewer = com.idega.builder.business.IBPageHelper.getInstance().getTemplateTreeViewer(iwc);

      add(viewer);
      viewer.setToMaintainParameter(FORM_ID_PARAMETER,iwc);
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
      viewer.setOnClick(SELECT_FUNCTION_NAME+"("+TreeViewer.ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME+","+TreeViewer.ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME+")");

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
