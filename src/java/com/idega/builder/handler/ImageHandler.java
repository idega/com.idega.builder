/*
 * $Id: ImageHandler.java,v 1.6 2004/06/15 18:25:32 thomas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import com.idega.builder.business.IBImageInserter;
import com.idega.builder.business.IBClassesFactory;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ImageHandler implements PropertyHandler {
  /**
   *
   */
  public ImageHandler() {
  	// default constructor
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
  	IBClassesFactory builderClassesFactory = new IBClassesFactory();
  	IBImageInserter inserter = builderClassesFactory.createImageInserterImpl();
  	inserter.setImSessionImageName(name);
  	inserter.setHasUseBox(false);
  	inserter.setNullImageIDDefault();
    try {
      inserter.setImageId(Integer.parseInt(value));
    }
    catch(NumberFormatException e) {
    	// thomas: can we really ignore this?
    }
    // IBImageInserter extends PresentationObjectType
    return (PresentationObject) inserter;
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  	// do nothing
  }
}
