/*
 * $Id: IBCreatePageWindow.java,v 1.34 2002/05/10 15:55:26 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainBMPBean;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
*/
public class IBPageWindow extends IWAdminWindow {
	
	
  protected static final String PAGE_NAME_PARAMETER   = "ib_page_name";
  protected static final String PAGE_CHOOSER_NAME     = IBPropertyHandler.PAGE_CHOOSER_NAME;
  protected static final String TEMPLATE_CHOOSER_NAME = IBPropertyHandler.TEMPLATE_CHOOSER_NAME;
  protected static final String PAGE_TYPE             = "ib_page_type";
  protected static final String IW_BUNDLE_IDENTIFIER  = "com.idega.builder";
  protected static final String PAGE_FORMAT = "ib_page_format";

  public IBPageWindow() {
    setWidth(330);
    setHeight(230);
    setScrollbar(false);
    this.setResizable(true);
  }


  /*
   *
   */
  protected IBPageChooser getPageChooser(String name, IWContext iwc) {
    IBPageChooser chooser = new IBPageChooser(name);
    chooser.setInputStyle(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

    try {
      ICPage current = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc);
      if (current.getType().equals(ICPageBMPBean.PAGE)) {
				chooser.setSelectedPage(current.getID(),current.getName());
			}
			else {
      	ICDomain domain = ICDomainBMPBean.getDomain(1);
      	ICPage top = domain.getStartPage();
      	if (top != null) {
					chooser.setSelectedPage(top.getID(),top.getName());
				}
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    return(chooser);
  }

  /**
   *
   */
  protected IBTemplateChooser getTemplateChooser(String name, IWContext iwc, String type){
    IBTemplateChooser chooser = new IBTemplateChooser(name);
    chooser.setInputStyle(IWConstants.BUILDER_FONT_STYLE_INTERFACE);

    try {
      String templateId = iwc.getParameter(TEMPLATE_CHOOSER_NAME);
      if (templateId == null || templateId.equals("")) {
      	ICPage current = BuilderLogic.getInstance().getCurrentIBPageEntity(iwc);
      	if (current.getType().equals(ICPageBMPBean.TEMPLATE)) {
					chooser.setSelectedPage(current);
				}
				else {
      	  if (type.equals(IBPageHelper.TEMPLATE)) {
	          ICDomain domain = BuilderLogic.getInstance().getCurrentDomain();
    	      ICPage top = domain.getStartTemplate();
    	      if (top != null) {
							chooser.setSelectedPage(top);
						}
      	  }
      	}
      }
      else {
      	ICPage top = ((com.idega.core.builder.data.ICPageHome)com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(templateId));
      	if (top != null) {
					chooser.setSelectedPage(top);
				}
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    return chooser;
  }

  /**
   *
   */
  public String getBundleIdentifier() {
    return IW_BUNDLE_IDENTIFIER;
  }
}