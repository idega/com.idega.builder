package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class VerticalAlignmentHandler implements PropertyHandler {

  public VerticalAlignmentHandler(){
  }

  public List getDefaultHandlerTypes() {
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Default");
    menu.addMenuElement("top","Top");
    menu.addMenuElement("middle","Middle");
    menu.addMenuElement("bottom","Bottom");
    menu.setSelectedElement(value);
    return menu;
  }
}