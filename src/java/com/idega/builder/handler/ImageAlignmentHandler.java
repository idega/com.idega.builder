/*
 * $Id: ImageAlignmentHandler.java,v 1.7 2008/10/23 11:43:58 laddi Exp $
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
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ImageAlignmentHandler implements ICPropertyHandler {
  /**
   *
   */
  public ImageAlignmentHandler() {
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
    menu.addMenuElement("","Select:");
    menu.addMenuElement(Image.ALIGNMENT_TOP,"Top");
    menu.addMenuElement(Image.ALIGNMENT_BOTTOM,"Bottom");
    menu.addMenuElement(Image.ALIGNMENT_LEFT,"Left");
    menu.addMenuElement(Image.ALIGNMENT_MIDDLE,"Middle");
    menu.addMenuElement(Image.ALIGNMENT_RIGHT,"Right");
    menu.addMenuElement(Image.ALIGNMENT_ABSOLUTE_BOTTOM,"* Absolute bottom");
    menu.addMenuElement(Image.ALIGNMENT_ABSOLUTE_MIDDLE,"* Absolute middle");
    menu.addMenuElement(Image.ALIGNMENT_BASELINE,"* Baseline");
    menu.addMenuElement(Image.ALIGNMENT_TEXT_TOP,"* Text top");
    menu.setSelectedElement(value);

    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
