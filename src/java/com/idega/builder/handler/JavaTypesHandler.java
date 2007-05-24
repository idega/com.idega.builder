/*
 * $Id: JavaTypesHandler.java,v 1.6 2007/05/24 11:31:12 valdas Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class JavaTypesHandler implements ICPropertyHandler {


  public static final String STRING_TYPE = "java.lang.String";
  public static final String INTEGER_TYPE = "java.lang.Integer";
  public static final String BOOLEAN_TYPE = "java.lang.Boolean";
  public static final String FLOAT_TYPE = "java.lang.Float";
  public static final String DOUBLE_TYPE = "java.lang.Double";
  public static final String DATE_TYPE = "java.util.Date";
  public static final String TIMESTAMP_TYPE = "java.sql.TimeStamp";
  public static final String TIME_TYPE = "java.sql.Time";



  /**
   *
   */
  public JavaTypesHandler() {
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
    DropdownMenu menu = new DropdownMenu(name);
    menu.addMenuElement("","None");

    menu.addMenuElement(STRING_TYPE,"String");
    menu.addMenuElement(BOOLEAN_TYPE,"Boolean (True/False)");
    menu.addMenuElement(INTEGER_TYPE,"Integer (whole number)");
    menu.addMenuElement(FLOAT_TYPE,"Float (fraction)");
    menu.addMenuElement(DOUBLE_TYPE,"Double (fraction)");
    menu.addMenuElement(DATE_TYPE,"Date (without time)");
    menu.addMenuElement(TIMESTAMP_TYPE,"Timestamp (Time and date)");
    menu.addMenuElement(TIME_TYPE,"Time (without date)");

    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
