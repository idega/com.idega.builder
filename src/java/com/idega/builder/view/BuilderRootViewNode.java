/*
 * $Id: BuilderRootViewNode.java,v 1.3 2005/03/01 23:25:03 tryggvil Exp $
 * Created on 16.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.view;

import java.util.Iterator;
import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.CachedBuilderPage;
import com.idega.builder.business.PageCacher;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.util.StringHandler;


/**
 * 
 *  Last modified: $Date: 2005/03/01 23:25:03 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.3 $
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
		ViewNode node =  getPageCacher().getCachedBuilderPage(childId);
		if(node==null){
			throw new RuntimeException("Page with id="+childId+" not found");
		}
		return node;
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
	
	
	public ViewNode getChild(String childViewId) {
		//parse the url:
		if(isPageId(childViewId)){
			return super.getChild(childViewId);
		}
		else{
			String newUrl = getUrlParsedInstandardFormat(childViewId);
			ViewNode viewNode = getViewNodeCached(newUrl);
			if(viewNode==null){
				viewNode = getViewNodeLoadedFromDB(newUrl);
			}
			return viewNode;
		}
	}

	/**
	 * @param viewNode
	 * @return
	 */
	private ViewNode getViewNodeLoadedFromDB(String pageUri) {
		String pageKey = getBuilderLogic().getPageKeyByURI(pageUri);
		return super.getChild(pageKey);
	}

	/**
	 * @param newUrl
	 * @return
	 */
	private ViewNode getViewNodeCached(String newUrl) {
		//iterate over the viewnodes and check if the url exists:
		Iterator valueIter = getPageCacher().getPageCacheMap().values().iterator();
		while (valueIter.hasNext()) {
			ViewNode node = (ViewNode) valueIter.next();
			CachedBuilderPage page = (CachedBuilderPage)node;
			if(newUrl!=null){
				if(newUrl.equals(page.getPageUri())){
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * @param childViewId
	 * @return
	 */
	private boolean isPageId(String childViewId) {
		try{
			if(childViewId.endsWith(StringHandler.SLASH)){
				//remove the potential '/' character in the ending:
				childViewId = childViewId.substring(0,childViewId.length()-1);
			}
			Integer.parseInt(childViewId);
			return true;
		}
		catch(NumberFormatException nfe){
			return false;
		}
	}

	/**
	 * Parses the string and returns it in the standard format with always begins and ends with a '/' character.
	 * e.g. the input 'mypage/mysubpage' is converted to '/mypage/mysubpage/'
	 * @param childViewId
	 * @return
	 */
	private String getUrlParsedInstandardFormat(String childViewId) {
		if(childViewId.equals(StringHandler.SLASH)){
			return childViewId;
		}
		else{
			String returnUrl = childViewId;
			if(returnUrl.startsWith(StringHandler.SLASH)){
				//do nothing
			}
			else{
				returnUrl = "/"+returnUrl;
			}
			if(returnUrl.endsWith(StringHandler.SLASH)){
				//do nothing
			}
			else{
				returnUrl = returnUrl+"/";
			}
			return returnUrl;
		}
	}
}
