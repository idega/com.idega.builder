/*
 * $Id: ImageAlignmentHandler.java,v 1.6 2007/05/24 11:31:12 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.idegaweb.IWConstants;
import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.text.Text;
import com.idega.presentation.Image;
import com.idega.presentation.Table;

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
    Table table = new Table(1,3);
      table.setCellpadding(0);
      table.setCellspacing(0);
      table.setHeight(2,"5");

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
    table.add(menu,1,1);

    Text starText = new Text("* not supported by all browsers");
      starText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
    table.add(starText,1,3);

    return(table);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
