/*
 * $Id: ParagraphAlignmentHandler.java,v 1.3 2003/04/03 09:10:10 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ParagraphAlignmentHandler extends HorizontalAlignmentHandler {
  public final static String JUSTIFY = "justify";

  /**
   *
   */
  public ParagraphAlignmentHandler() {
  }

  /**
   *
   */
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Default");
    menu.addMenuElement(LEFT,"Left");
    menu.addMenuElement(CENTER,"Centered");
    menu.addMenuElement(RIGHT,"Right");
    menu.addMenuElement(JUSTIFY,"Justified");
    menu.setSelectedElement(value);
    return(menu);
  }

}
