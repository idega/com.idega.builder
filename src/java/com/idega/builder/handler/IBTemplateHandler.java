/*
 * $Id: IBTemplateHandler.java,v 1.3 2001/12/12 21:06:32 palli Exp $
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
import com.idega.builder.presentation.IBTemplateChooser;
import com.idega.builder.business.PageTreeNode;
import java.util.Map;

/**
 * @author <a href="palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBTemplateHandler implements PropertyHandler {
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

    if (value != null && !value.equals("")) {
      Map tree = PageTreeNode.getTree(iwc);
      if (tree != null) {
        PageTreeNode node = (PageTreeNode)tree.get(new Integer(value));
        if (node != null)
          chooser.setSelectedPage(node.getNodeID(),node.getNodeName());
      }
    }
    return(chooser);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}