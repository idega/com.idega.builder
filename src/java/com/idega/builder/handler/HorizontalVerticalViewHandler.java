/*
 * $Id: HorizontalVerticalViewHandler.java,v 1.6 2004/06/28 14:07:21 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;

/**
 * @author <a href="aron@idega.is">Aron Birkir</a>
 * @version 1.0
 */
public class HorizontalVerticalViewHandler implements ICPropertyHandler {
  public final static int HORIZONTAL = 1;
  public final static int VERTICAL = 2;

  /**
   *
   */
  public HorizontalVerticalViewHandler() {
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
    menu.addMenuElement("","Select:");
    menu.addMenuElement(String.valueOf(HORIZONTAL) ,"HORIZONTAL");
    menu.addMenuElement(String.valueOf(VERTICAL),"VERTICAL");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
