/*
 * $Id: ComponentBasedPage.java,v 1.1 2004/12/20 08:55:06 tryggvil Exp $
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
 * 
 *  Last modified: $Date: 2004/12/20 08:55:06 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
public interface ComponentBasedPage {
	
	public Page getNewPageCloned();
	
	public Page getPage(IWContext iwc);
	
	public Page getNewPage(IWContext iwc);
	
	public UIComponent createComponent(FacesContext context);
	
}
