/*
 * Created on Jun 21, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.builder.handler;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.builder.presentation.IBObjectChooser;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author aron 
 * @version 1.0
 */
public class ICObjectClassHandler implements PropertyHandler {
	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#getDefaultHandlerTypes()
	 */
	public List getDefaultHandlerTypes() {
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		IBObjectChooser chooser = new IBObjectChooser(name);
		chooser.setToUseClassValue(true);
			try {
				if (stringValue != null && !stringValue.equals("")) {
					ICObjectHome home = (ICObjectHome)IDOLookup.getHome(ICObject.class);
					chooser.setSelectedObject(home.findByClassName(stringValue));
				}
			}
			catch (IDOLookupException e) {
				e.printStackTrace();
			}
			catch (NumberFormatException e) {
				e.printStackTrace();
			}
			catch (FinderException e) {
				e.printStackTrace();
			}
		
		
		return chooser;
	}
	/* (non-Javadoc)
	 * @see com.idega.builder.handler.PropertyHandler#onUpdate(java.lang.String[], com.idega.presentation.IWContext)
	 */
	public void onUpdate(String[] values, IWContext iwc) {
		
	}
}
