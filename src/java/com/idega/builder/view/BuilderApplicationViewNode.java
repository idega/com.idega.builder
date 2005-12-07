/*
 * $Id: BuilderApplicationViewNode.java,v 1.1 2005/12/07 11:35:50 tryggvil Exp $
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
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * TODO tryggvil Describe Type BuilderApplicationViewNode
 * </p>
 *  Last modified: $Date: 2005/12/07 11:35:50 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class BuilderApplicationViewNode extends DefaultViewNode {

	/**
	 * @param viewId
	 * @param parent
	 */
	public BuilderApplicationViewNode(String viewId) {
		super(viewId);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param iwma
	 */
	public BuilderApplicationViewNode(IWMainApplication iwma) {
		super(iwma);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#createComponent(javax.faces.context.FacesContext)
	 */
	public UIComponent createComponent(FacesContext context) {
		// TODO Auto-generated method stub
		return super.createComponent(context);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#getResourceURI()
	 */
	public String getResourceURI() {
		if(getBuilderLogic().isFirstBuilderRun()){
			return getParent().getChild("initialsetup").getURI();
		}
		else{
			return getIWMainApplication().getWindowOpenerURI(IBApplication.class);
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
