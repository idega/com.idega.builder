/*
 * $Id: HorizontalAlignmentHandler.java,v 1.8 2007/05/24 11:31:12 valdas Exp $
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

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class HorizontalAlignmentHandler implements ICPropertyHandler {
  public final static String LEFT = "left";
  public final static String CENTER = "center";
  public final static String RIGHT = "right";

  /**
   *
   */
  public HorizontalAlignmentHandler() {
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
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Default");
    menu.addMenuElement(LEFT,"Left");
    menu.addMenuElement(CENTER,"Centered");
    menu.addMenuElement(RIGHT,"Right");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
