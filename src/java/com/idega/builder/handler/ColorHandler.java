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

public class ColorHandler implements PropertyHandler {

  public ColorHandler() {
  }
  public List getDefaultHandlerTypes(){
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Select:");
    menu.addMenuElement("#000000","Black");
    menu.addMenuElement("#FFFFFF","White");
    menu.addMenuElement("#DDDDDD","Light Gray");
    menu.addMenuElement("#FF0000","Red");
    menu.addMenuElement("#00FF00","Green");
    menu.addMenuElement("#0000FF","Blue");
    menu.setSelectedElement(value);
    return menu;
  }
}