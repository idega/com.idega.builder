package com.idega.builder.handler;


import java.util.Collection;

import com.idega.builder.business.IBPropertyHandler;
import com.idega.presentation.IWContext;

/**
 *@author     <a href="mailto:thomas@idega.is">Thomas Hilbig</a>
 *@version    1.0
 *
 *	Presentation objects that want to use the DropDownMenuSpecifiedChoiceHandler
 *	must implement this interface.
 *	The method below is used by the IBPropertyHandler to get a collection of
 *	strings from	the presentation object. The collection is
 *	set into the drop down menu of the handler.
 *
 */
public interface SpecifiedChoiceProvider {
  
  public Collection getSpecifiedChoice(
    IWContext iwc, 
    String ICObjectInstanceID, 
    String methodIdentifier, 
    IBPropertyHandler propertyHandler);
}
