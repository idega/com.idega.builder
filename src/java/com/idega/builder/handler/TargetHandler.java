/*
 * $Id: TargetHandler.java,v 1.4 2004/06/28 14:07:04 thomas Exp $
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
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.text.LinkContainer;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class TargetHandler implements ICPropertyHandler {
  /**
   *
   */
  public TargetHandler() {
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
    menu.addMenuElement(LinkContainer.TARGET_BLANK_WINDOW,"Blank window");
    menu.addMenuElement(LinkContainer.TARGET_NEW_WINDOW,"New window");
    menu.addMenuElement(LinkContainer.TARGET_PARENT_WINDOW,"Parent window");
    menu.addMenuElement(LinkContainer.TARGET_SELF_WINDOW,"Same window");
    menu.addMenuElement(LinkContainer.TARGET_TOP_WINDOW,"Top window");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
