/*
 * $Id: BorderStyleHandler.java,v 1.1 2004/08/05 15:05:46 laddi Exp $
 *
 * Copyright (C) 2004 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.text.StyleConstants;

/**
 * @author <a href="laddi@idega.is">Thorhallur Helgason</a>
 * @version 1.0
 */
public class BorderStyleHandler implements ICPropertyHandler {

	public static final String BORDER_NONE = "none";
	public static final String BORDER_DOTTED = "dotted";
	public static final String BORDER_DASHED = "dashed";
	public static final String BORDER_SOLID = "solid";
	public static final String BORDER_DOUBLE = "double";
	public static final String BORDER_GROOVE = "groove";
	public static final String BORDER_RIDGE = "ridge";
	public static final String BORDER_INSET = "inset";
	public static final String BORDER_OUTSET = "outset";

  /**
   *
   */
  public BorderStyleHandler() {
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
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Default");
    menu.addMenuElement(StyleConstants.BORDER_NONE,"None");
    menu.addMenuElement(StyleConstants.BORDER_DOTTED,"Dotted");
    menu.addMenuElement(StyleConstants.BORDER_DASHED,"Dashed");
    menu.addMenuElement(StyleConstants.BORDER_SOLID,"Solid");
    menu.addMenuElement(StyleConstants.BORDER_DOUBLE,"Double");
    menu.addMenuElement(StyleConstants.BORDER_GROOVE,"Groove");
    menu.addMenuElement(StyleConstants.BORDER_RIDGE,"Ridge");
    menu.addMenuElement(StyleConstants.BORDER_INSET,"Inset");
    menu.addMenuElement(StyleConstants.BORDER_OUTSET,"Outset");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
