package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.builder.presentation.TableRowColumnPropertyPresentation;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class TableRowsHandler implements PropertyHandler {


  public TableRowsHandler(){
  }

  public List getDefaultHandlerTypes() {
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    TableRowColumnPropertyPresentation menu = new TableRowColumnPropertyPresentation(name,value,iwc);
    return menu;
  }



}