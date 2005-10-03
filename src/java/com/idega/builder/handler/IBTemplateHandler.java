/*
 * $Id: IBTemplateHandler.java,v 1.10 2005/10/03 14:50:15 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.builder.presentation.IBTemplateChooser;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageUpdater;
import com.idega.core.builder.presentation.ICPropertyHandler;
import java.util.Map;

/**
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBTemplateHandler implements ICPropertyHandler {
  /**
   *
   */
  public IBTemplateHandler() {
  }

  /**
   *
   */
  public List getDefaultHandlerTypes() {
    return(null);
  }

  /**
   *
   */
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    IBTemplateChooser chooser = new IBTemplateChooser(name);

    try {
      if (value != null && !value.equals("")) {
        Map tree = PageTreeNode.getTree(iwc);
        if (tree != null) {
          PageTreeNode node = (PageTreeNode)tree.get(Integer.valueOf(value));
          if (node != null)
            chooser.setSelectedPage(node.getNodeID(),node.getNodeName());
        }
      }
    }
    catch(NumberFormatException e) {
      e.printStackTrace();
    }
    return(chooser);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
    try {
      if (values != null) {
        String value = values[0];

        if (value != null && !value.equals("")) {
          BuilderLogic instance = BuilderLogic.getInstance();
          String currPage = instance.getCurrentIBPage(iwc);
          if (currPage != null) {
            int p = Integer.parseInt(currPage);
            int v = Integer.parseInt(value);
            instance.changeTemplateId(value,iwc);
            IBPageUpdater.updateTemplateId(p,v);
            try{
	            Page template = instance.getIBXMLPage(value).getPopulatedPage();
	            if (template != null) {
	              if (template.isLocked())
	                instance.lockRegion(currPage,"-1");
	              else
	                instance.unlockRegion(currPage,"-1",null);
	            }
            }
            catch(ClassCastException ce){
            		//this happens when the template is of type html or not ibxml
            }
          }

        }
      }
    }
    catch(NumberFormatException e) {
      e.printStackTrace();
    }
  }
}
