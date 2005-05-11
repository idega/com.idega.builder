/*
 * $Id: IBTemplateChooserTag.java,v 1.1 2005/05/11 18:25:44 gummi Exp $
 * Created on 28.4.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import javax.faces.webapp.UIComponentTag;


/**
 * 
 *  Last modified: $Date: 2005/05/11 18:25:44 $ by $Author: gummi $
 * 
 * @author <a href="mailto:gummi@idega.com">Gudmundur Agust Saemundsson</a>
 * @version $Revision: 1.1 $
 */
public class IBTemplateChooserTag extends UIComponentTag {

	/**
	 * 
	 */
	public IBTemplateChooserTag() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "IBTemplateChooser";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		// TODO Auto-generated method stub
		return null;
	}
}
