package com.idega.builder.handler;

import java.util.List;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.DropdownMenu;
/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 *
 *	This	 class	 is a handler for a string property. You can choose the
 *	desired	 string	 from a drop down menu. The content of the drop down 	menu
 *	can	 be set by the presentation object itself depending of	 its property
 *	values	 that are	 already	 set.
 *
 *	see also interface com.idega.builder.handler.SpecifiedChoiceProvider
 *	
 *
 */
public class DropDownMenuSpecifiedChoiceHandler implements PropertyHandler {

  private DropdownMenu menu;
  	
  /**
	 * @see com.idega.builder.handler.PropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}
  
	/**
	 * @see com.idega.builder.handler.PropertyHandler#getHandlerObject(String, String, IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
    menu = new DropdownMenu(name);
    menu.addMenuElement("","Select:");
    menu.setSelectedElement(stringValue);
    return(menu);
  }
    

	/**
	 * @see com.idega.builder.handler.PropertyHandler#onUpdate(String[], IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
	}
  


  
}
