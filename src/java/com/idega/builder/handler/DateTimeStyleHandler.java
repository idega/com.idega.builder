/*
 * $Id: DateTimeStyleHandler.java,v 1.3 2004/06/28 11:18:12 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.core.builder.data.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.IWTimestamp;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class DateTimeStyleHandler implements ICPropertyHandler {
  /**
   *
   */
  public DateTimeStyleHandler() {
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
    menu.addMenuElement(IWTimestamp.SHORT,"Short");
    menu.addMenuElement(IWTimestamp.MEDIUM,"Medium");
    menu.addMenuElement(IWTimestamp.LONG,"Long");
		menu.addMenuElement(IWTimestamp.FULL,"Full");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
