/*
 * $Id: FileHandler.java,v 1.3 2002/04/06 19:07:39 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.block.media.presentation.ImageInserter;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class FileHandler implements PropertyHandler {
  /**
   *
   */
  public FileHandler() {
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
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    ImageInserter po = new ImageInserter(name,false);
    try {
      po.setImageId(Integer.parseInt(value));
    }
    catch(NumberFormatException e) {
    }

    return(po);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
