/*
 * $Id: FontSizeHandler.java,v 1.7 2007/05/24 11:31:12 valdas Exp $
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
import com.idega.presentation.text.Text;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class FontSizeHandler implements ICPropertyHandler {
  /**
   *
   */
  public FontSizeHandler() {
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
    menu.addMenuElement(Text.FONT_SIZE_7_HTML_1,"7  points");
    menu.addMenuElement(Text.FONT_SIZE_10_HTML_2,"10 points");
    menu.addMenuElement(Text.FONT_SIZE_12_HTML_3,"12 points");
    menu.addMenuElement(Text.FONT_SIZE_14_HTML_4,"14 points");
    menu.addMenuElement(Text.FONT_SIZE_18_HTML_5,"18 points");
    menu.addMenuElement(Text.FONT_SIZE_24_HTML_6,"24 points");
    menu.addMenuElement(Text.FONT_SIZE_34_HTML_7,"34 points");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
