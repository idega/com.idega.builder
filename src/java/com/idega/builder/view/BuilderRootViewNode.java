/*
 * $Id: BuilderRootViewNode.java,v 1.1 2004/12/20 08:55:07 tryggvil Exp $
 * Created on 16.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.view;

import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.PageCacher;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;


/**
 * 
 *  Last modified: $Date: 2004/12/20 08:55:07 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class BuilderRootViewNode extends DefaultViewNode {

	private ViewHandler builderPageViewHandler;
	
	/**
	 * @param viewId
	 * @param parent
	 */
	public BuilderRootViewNode(String viewId, ViewNode parent) {
		super(viewId, parent);
	}

	/**
	 * @param iwma
	 */
	public BuilderRootViewNode(IWMainApplication iwma) {
		super(iwma);
	}


	/*public ViewHandler getViewHandler() {
		if(this.builderPageViewHandler==null){
			ViewNode parentNode = getParent();
			ViewHandler parentViewHandler = parentNode.getViewHandler();
			setViewHandler(new BuilderPageViewHandler(parentViewHandler));
		}
		return this.builderPageViewHandler;
	}*/
	
	public void setViewHandler(ViewHandler viewHandler) {
		this.builderPageViewHandler=viewHandler;
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#loadChild(java.lang.String)
	 */
	protected ViewNode loadChild(String childId) {
		//return super.loadChild(childId);
		return getPageCacher().getCachedBuilderPage(childId);
	}
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
	
	protected PageCacher getPageCacher(){
		return getBuilderLogic().getPageCacher();
	}
	
	protected Map getChildrenMap(){
		return getPageCacher().getPageCacheMap();
	}
	
	protected ViewNode getDefaultNode(FacesContext context){
		IWContext iwc = IWContext.getIWContext(context);
		String pageKey = getBuilderLogic().getCurrentIBPage(iwc);
		ViewNode defaultChild = getChild(pageKey);
		if(defaultChild.equals(this)){
			throw new RuntimeException("Page with id="+pageKey+" does not exit");
		}
		return defaultChild;
	}
	
	public UIComponent createComponent(FacesContext context){
		ViewNode defaultChild = getDefaultNode(context);
		return defaultChild.createComponent(context);
	}
	
	public boolean isComponentBased(){
		FacesContext context = FacesContext.getCurrentInstance();
		ViewNode defaultChild = getDefaultNode(context);
		return defaultChild.isComponentBased();
	}
	
	public boolean isResourceBased(){
		FacesContext context = FacesContext.getCurrentInstance();
		ViewNode defaultChild = getDefaultNode(context);
		return defaultChild.isResourceBased();
	}
	
	public String getResourceURI(){
		FacesContext context = FacesContext.getCurrentInstance();
		ViewNode defaultChild = getDefaultNode(context);
		return defaultChild.getResourceURI();
	}
	
}
