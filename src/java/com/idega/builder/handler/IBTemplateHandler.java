/*
 * $Id: IBTemplateHandler.java,v 1.2 2001/11/06 18:18:03 palli Exp $
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

  public IBTemplateHandler() {
  System.out.println("Getting to constructor");
  }

  public List getDefaultHandlerTypes() {
  System.out.println("Getting to getDefaultHandlerType");
    return(null);
  }

  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
  System.out.println("Getting to getHandlerObject");
  System.out.println("Value = " + value);
    IBTemplateChooser chooser = new IBTemplateChooser(name);

    if (value != null && !value.equals("")) {
      Map tree = PageTreeNode.getTree(iwc);
System.out.println("tree = " + tree);
      if (tree != null) {
        PageTreeNode node = (PageTreeNode)tree.get(new Integer(value));
System.out.println("node = " + node);
        if (node != null)
          chooser.setSelectedPage(node.getNodeID(),node.getNodeName());
      }
    }
    return(chooser);
  }

  public void main(IWContext iwc) throws Exception {
  System.out.println("Getting to main");
  }
}