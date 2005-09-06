/*
 * $Id: ComponentBasedPage.java,v 1.2 2005/09/06 12:19:59 tryggvil Exp $
 * Created on 19.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;


/**
 * <p>
 * This is an interface for declaring a "component-based" Builder page that is rendered through JSF.<br/>
 * This means that the page is entirely based up on and rendered through JSF components or existing classes, 
 * The opposite of this is e.g. a JSP based Builder page. <br/>
 * Old style IBXML pages are populated into a com.idega.presentation.Page and rended as such.
 * </p>
 *  Last modified: $Date: 2005/09/06 12:19:59 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public interface ComponentBasedPage {
	
	public Page getNewPageCloned();
	
	public Page getPage(IWContext iwc);
	
	public Page getNewPage(IWContext iwc);
	
	public UIComponent createComponent(FacesContext context);
	
}
