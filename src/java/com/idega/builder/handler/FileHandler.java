/*
 * $Id: FileHandler.java,v 1.9 2007/05/24 11:31:12 valdas Exp $
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
public class FileHandler implements ICPropertyHandler {
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
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {
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
