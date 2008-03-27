/*
 * $Id: BuilderRootViewNode.java,v 1.9 2008/03/27 20:16:25 tryggvil Exp $
 * Created on 16.9.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.view;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.idega.core.builder.data.ICDomain;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.core.view.ViewNodeBase;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;


/**
 * <p>
 * This is the ViewNode that is by default mapped to the root of the builder viewnode hierarchy, i.e. the one that is by 
 * default mapped under '/pages/'. The instance of this class is the one that handles precicely this url, i.e. the 
 * one on the root for pages.
 * </p>
 *  Last modified: $Date: 2008/03/27 20:16:25 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.9 $
 */
public class BuilderRootViewNode extends DefaultViewNode {
	private Map domainNodeMap;

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
	
	public UIComponent createComponent(FacesContext context){
		ViewNode defaultChild = getDomainNode(context);
		return defaultChild.createComponent(context);
	}

	private ViewNode getDomainNode(FacesContext context) {
		
		ICDomain domain = getDomainForRequest(context);
		BuilderDomainViewNode builderNode = getDefaultNodeForDomain(domain);
		return builderNode;
	}

	private BuilderDomainViewNode getDefaultNodeForDomain(ICDomain domain) {
		Map map = getDomainNodeMap();
		String domainKey = domain.getServerName();
		BuilderDomainViewNode node = (BuilderDomainViewNode) map.get(domainKey);
		if(node==null){
			node = new BuilderDomainViewNode(this,domain);
			map.put(domainKey, node);
		}
		return node;
	}

	private ICDomain getDomainForRequest(FacesContext context) {
		IWContext iwc = IWContext.getIWContext(context);
		ICDomain domain = getIWMainApplication().getIWApplicationContext().getDomainByServerName(iwc.getServerName());
		return domain;
	}

	private Map getDomainNodeMap() {
		if(domainNodeMap==null){
			domainNodeMap=new HashMap();
		}
		return domainNodeMap;
	}
	
	public boolean isComponentBased(){
		FacesContext context = FacesContext.getCurrentInstance();
		ViewNode domainChild = getDomainNode(context);
		return domainChild.isComponentBased();
	}
	
	@Override
	public ViewNodeBase getViewNodeBase() {
		FacesContext context = FacesContext.getCurrentInstance();
		ViewNode domainChild = getDomainNode(context);
		return domainChild.getViewNodeBase();
	}
	
	public String getResourceURI(){
		FacesContext context = FacesContext.getCurrentInstance();
		ViewNode defaultChild = getDomainNode(context);
		return defaultChild.getResourceURI();
	}
	
	public ViewNode getChild(String childViewId){
		FacesContext context = FacesContext.getCurrentInstance();
		if(context!=null){
			ViewNode defaultChild = getDomainNode(context);
			return defaultChild.getChild(childViewId);
		}
		return null;
	}
}
