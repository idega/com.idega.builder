package com.idega.builder.handler;

import java.util.List;
import com.idega.core.builder.presentation.ICPropertyHandler;
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
public class DropDownMenuSpecifiedChoiceHandler implements ICPropertyHandler {

  private DropdownMenu menu;
  	
  /**
	 * @see com.idega.core.builder.presentation.ICPropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}
  
	/**
	 * @see com.idega.core.builder.presentation.ICPropertyHandler#getHandlerObject(String, String, IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc, boolean oldGenerationHandler, String instanceId, String method) {
    this.menu = new DropdownMenu(name);
    this.menu.addMenuElement("","Select:");
    this.menu.setSelectedElement(stringValue);
    return(this.menu);
  }
    

	/**
	 * @see com.idega.core.builder.presentation.ICPropertyHandler#onUpdate(String[], IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
	}
  


  
}
