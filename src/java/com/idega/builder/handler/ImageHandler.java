/*
 * $Id: ImageHandler.java,v 1.9 2004/09/01 17:19:33 eiki Exp $
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
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class ImageHandler implements ICPropertyHandler {
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
    	//yes this case is handled in the inserter
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
