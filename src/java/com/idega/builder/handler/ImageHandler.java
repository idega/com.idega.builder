package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;

import com.idega.block.media.presentation.ImageInserter;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class ImageHandler implements PropertyHandler {

  public ImageHandler() {
  }
  public List getDefaultHandlerTypes() {
    return null;
  }
  public PresentationObject getHandlerObject(String name,String value,IWContext iwc){
    ImageInserter po = new ImageInserter(name,false);
    try{
      po.setImageId(Integer.parseInt(value));
    }
    catch(NumberFormatException e){

    }
    return po;
  }
}