package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.text.Text;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class FontStyleHandler implements PropertyHandler {

  public FontStyleHandler() {
  }
  public List getDefaultHandlerTypes() {
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    /*DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","Select:");
    menu.addMenuElement(Text.FONT_FACE_STYLE_BOLD,"Bold");
    menu.addMenuElement(Text.FONT_FACE_STYLE_ITALIC,"Italic");
    //menu.addMenuElement(Text.FONT_FACE_STYLE_NORMAL,"Normal");
    menu.setSelectedElement(value);*/
    TextInput text = new TextInput(name);
    text.setContent(value);
    return text;
  }

}