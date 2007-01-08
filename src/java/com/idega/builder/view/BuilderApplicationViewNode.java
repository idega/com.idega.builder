/*
 * $Id: BuilderApplicationViewNode.java,v 1.6 2007/01/08 14:30:35 valdas Exp $
 * Created on 25.11.2005 in project com.idega.builder
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.view;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.builder.app.IBApplication;
import com.idega.builder.business.BuilderLogic;
import com.idega.core.view.DefaultViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * TODO tryggvil Describe Type BuilderApplicationViewNode
 * </p>
 *  Last modified: $Date: 2007/01/08 14:30:35 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class BuilderApplicationViewNode extends DefaultViewNode {

	/**
	 * @param viewId
	 * @param parent
	 */
	public BuilderApplicationViewNode(String viewId) {
		super(viewId);
	}

	/**
	 * @param iwma
	 */
	public BuilderApplicationViewNode(IWMainApplication iwma) {
		super(iwma);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#createComponent(javax.faces.context.FacesContext)
	 */
	public UIComponent createComponent(FacesContext context) {
		return super.createComponent(context);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#getResourceURI()
	 */
	public String getResourceURI() {
		if(getBuilderLogic().isFirstBuilderRun()){
			//return getParent().getChild("initialsetup").getURI();
			return "/workspace/content/pages/tree";
		}
		else{
			return getIWMainApplication().getWindowOpenerURIWithoutContextPath(IBApplication.class);
		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#isComponentBased()
	 */
	public boolean isComponentBased() {
		return super.isComponentBased();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#isResourceBased()
	 */
	public boolean isResourceBased() {
		return true;
	}
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#getRedirectsToResourceUri()
	 */
	public boolean getRedirectsToResourceUri() {
		return true;
	}
}