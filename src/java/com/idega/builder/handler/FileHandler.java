/*
 * $Id: FileHandler.java,v 1.5 2004/06/15 18:25:32 thomas Exp $
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
public class FileHandler implements PropertyHandler {
  /**
   *
   */
  public FileHandler() {
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
  	IBClassesFactory classesFactory = new IBClassesFactory();
  	IBImageInserter inserter = classesFactory.createImageInserterImpl();
  	inserter.setImSessionImageName(name);
  	inserter.setHasUseBox(false);
    try {
      inserter.setImageId(Integer.parseInt(value));
    }
    catch(NumberFormatException e) {
    	// ? thomas: can we really ignore that?
    }
    // IBImageInserter extends PresentationObjectType
    return (PresentationObject) inserter;
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  	// nothing to do
  }
}
