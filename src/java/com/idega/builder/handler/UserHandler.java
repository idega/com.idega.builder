package com.idega.builder.handler;

import java.util.List;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.user.presentation.UserChooser;

/**
 * @author gimmi
 */
public class UserHandler implements PropertyHandler{
  /**
   *
   */
  public UserHandler() {
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
    UserChooser chooser = new UserChooser(name);
    chooser.setSelected(value);
    return(chooser);
  }
  
  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
