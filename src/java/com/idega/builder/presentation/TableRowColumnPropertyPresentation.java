package com.idega.builder.presentation;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.IWContext;

import com.idega.builder.business.IBPropertyHandler;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class TableRowColumnPropertyPresentation extends PresentationObjectContainer {

    int numberOfColumns;
    String previousSelected;
    DropdownMenu theMenu;
    IWContext _iwc;

    public TableRowColumnPropertyPresentation(String name,String value,IWContext iwc){
      theMenu = new DropdownMenu(name);
      add(theMenu);
      IBPropertyHandler.getInstance().setDropdownToChangeValue(theMenu);
      //theMenu.setToSubmit();
      previousSelected = value;
      _iwc=iwc;
    }

    public void setRowOrColumnCount(int count,IWContext iwc){
      numberOfColumns=count;
      this.theMenu.removeElements();
      this.theMenu.addMenuElement("","Select:");
      for (int i = 1; i <= count; i++) {
        String sCount = Integer.toString(i);
        theMenu.addMenuElement(sCount);
      }
      if(previousSelected!=null){
        theMenu.setSelectedElement(previousSelected);
      }
    }


}