/*
 * $Id: LanguageHandler.java,v 1.5 2004/02/20 16:37:44 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.core.localisation.presentation.LocalePresentationUtil;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 *
 * This handler is to display a selection with the available Locales.
 * In the selection the keys (values) are the locale-stringrepresentations e.g. "en_US" for English/US
 */
public class LanguageHandler implements PropertyHandler {
  /**
   *
   */
  public LanguageHandler() {
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
    DropdownMenu menu = LocalePresentationUtil.getAvailableLocalesDropdown(iwc.getIWMainApplication(),name);
    menu.addMenuElementFirst("","Select:");
    menu.setSelectedElement(value);
    return(menu);
  }

  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
