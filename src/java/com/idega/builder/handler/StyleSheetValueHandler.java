/*
 * $Id: StyleHandler.java,v 1.3 2002/04/06 19:07:39 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.Enumeration;
import java.util.List;

import com.idega.idegaweb.IWStyleManager;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.builder.presentation.IBStyleChooser;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class StyleSheetValueHandler implements PropertyHandler {
  /**
   *
   */
  public StyleSheetValueHandler() {
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
    String styleValue = getStyleValue(iwc);
    
    IBStyleChooser chooser = new IBStyleChooser(name,"font-size: 8pt; border: 1 solid #000000");
    chooser.setSelected(value);
    if ( styleValue != null )
	    chooser.setSelected(styleValue);
    
    return(chooser);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
  
  private String getStyleValue(IWContext iwc) {
  	Enumeration enum = iwc.getParameterNames();
  	while ( enum.hasMoreElements() ) {
  		String style = new IWStyleManager().getStyle(iwc.getParameter((String)enum.nextElement()));
  		if ( style != null )
  			return style;	
  	}	
  	return null;
  }
}
