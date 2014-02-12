/**
 * $Id: BuilderDomainViewNode.java,v 1.2 2007/07/27 15:42:50 civilis Exp $
 * Created in 2007 by tryggvil
 *
 * Copyright (C) 2000-2007 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.CachedBuilderPage;
import com.idega.builder.business.PageCacher;
import com.idega.core.builder.business.BuilderPageException;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.view.DefaultViewNode;
import com.idega.core.view.ViewNode;
import com.idega.core.view.ViewNodeBase;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
import com.idega.util.RequestUtil;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

/**
 * <p>
 * Root node for builder pages for each domain
 * </p>
 *  Last modified: $Date: 2007/07/27 15:42:50 $ by $Author: civilis $
 *
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
 */
public class BuilderDomainViewNode extends DefaultViewNode {

	private ICDomain domain;

	/**
	 * @param viewId
	 * @param parent
	 */
	public BuilderDomainViewNode(BuilderRootViewNode parent,ICDomain domain) {
		super(domain.getPrimaryKey().toString(), parent);
		setDomain(domain);
	}

	@Override
	public void setViewHandler(ViewHandler viewHandler) {
		super.setViewHandler(viewHandler);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.view.DefaultViewNode#loadChild(java.lang.String)
	 */
	@Override
	protected ViewNode loadChild(String childId) throws BuilderPageException {
		ViewNode node = getPageCacher().getCachedBuilderPage(childId);
		if (node == null) {
			BuilderPageException be = new BuilderPageException("Page with ID=" + childId + " not found");
			be.setCode(BuilderPageException.CODE_NOT_FOUND);
			be.setPageUri(childId);
			throw be;
		}
		return node;
	}

	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}

	protected PageCacher getPageCacher(){
		return getBuilderLogic().getPageCacher();
	}

	@Override
	protected Map<String, ViewNode> getChildrenMap(){
		return getPageCacher().getPageCacheMap();
	}

	private ViewNode getDefaultNode(FacesContext context) throws BuilderPageException {
		IWContext iwc = IWContext.getIWContext(context);
		String pageKey = getBuilderLogic().getCurrentIBPage(iwc);
		String uri = iwc.getRequestURI();
		if (StringUtil.isEmpty(pageKey)) {
			pageKey = getBuilderLogic().getPageKeyByURICached(uri);
		}
		if (StringUtil.isEmpty(pageKey)) {
			String redirect = RequestUtil.getRedirectUriByApplicationProperty(iwc.getRequest(), HttpServletResponse.SC_NOT_FOUND);
			if (!StringUtil.isEmpty(redirect)) {
				try {
					Logger.getLogger(getClass().getName()).warning("Redirecting to " + redirect + " because page with URI " + uri + " can not be found");
					iwc.getResponse().sendRedirect(redirect);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			BuilderPageException be = new BuilderPageException("Page with URI " + uri + " not found");
			be.setCode(BuilderPageException.CODE_NOT_FOUND);
			be.setPageUri(uri);
			throw be;
		}

		ViewNode defaultChild = getChild(pageKey);
		if (defaultChild.equals(this)) {
			throw new RuntimeException("Page with id="+pageKey+" does not exist");
		}
		return defaultChild;
	}

	@Override
	public UIComponent createComponent(FacesContext context) {
		try {
			ViewNode defaultChild = getDefaultNode(context);
			return defaultChild.createComponent(context);
		} catch (BuilderPageException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isComponentBased() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ViewNode defaultChild = getDefaultNode(context);
			return defaultChild.isComponentBased();
		} catch (BuilderPageException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public ViewNodeBase getViewNodeBase() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ViewNode defaultChild = getDefaultNode(context);
			if (defaultChild != null) {
				return defaultChild.getViewNodeBase();
			}
		} catch (BuilderPageException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getResourceURI(){
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			ViewNode defaultChild = getDefaultNode(context);
			return defaultChild.getResourceURI();
		} catch (BuilderPageException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public ViewNode getChild(String childViewId) {
		//parse the url:
		if (isPageId(childViewId)) {
			return super.getChild(childViewId);
		} else {
			String newUrl = getUrlParsedInstandardFormat(childViewId);
			ViewNode viewNode = getViewNodeCached(newUrl);
			if (viewNode == null) {
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
		String pagesPrefix = CoreConstants.PAGES_URI_PREFIX;
		String requestUri = pageUri;
		if (!pageUri.startsWith(pagesPrefix)) {
			requestUri=pagesPrefix+pageUri;
		}
		//We have to add a /pages prefix because the method getPageKeyByURI() expects it
		String pageKey = getBuilderLogic().getPageKeyByURI(requestUri,getDomain());
		return super.getChild(pageKey);
	}

	/**
	 * @param newUrl
	 * @return
	 */
	private ViewNode getViewNodeCached(String newUrl) {
		//iterate over the viewnodes and check if the url exists:
		for (Iterator<ViewNode> valueIter = getPageCacher().getPageCacheMap().values().iterator(); valueIter.hasNext();) {
			ViewNode node = valueIter.next();
			CachedBuilderPage page = (CachedBuilderPage)node;
			if (newUrl != null) {
				if (newUrl.equals(page.getPageUri())) {
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
		if (StringUtil.isEmpty(childViewId)) {
			return false;
		}

		try {
			if (childViewId.endsWith(StringHandler.SLASH)) {
				//remove the potential '/' character in the ending:
				childViewId = childViewId.substring(0,childViewId.length()-1);
			}
			Integer.parseInt(childViewId);
			return true;
		} catch(NumberFormatException nfe){
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
		if (StringUtil.isEmpty(childViewId)) {
			return null;
		}

		if (childViewId.equals(StringHandler.SLASH)){
			return childViewId;
		} else {
			String returnUrl = childViewId;
			if(returnUrl.startsWith(StringHandler.SLASH)){
				//do nothing
			}
			else{
				returnUrl = StringHandler.SLASH+returnUrl;
			}
			if(returnUrl.endsWith(StringHandler.SLASH)){
				//do nothing
			}
			else{
				returnUrl = returnUrl+StringHandler.SLASH;
			}
			return returnUrl;
		}
	}


	public ICDomain getDomain() {
		return domain;
	}


	public void setDomain(ICDomain domain) {
		this.domain = domain;
	}
}