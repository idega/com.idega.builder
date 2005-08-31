/*
 * Created on 28.7.2003 by  tryggvil in project com.project
 */
package com.idega.builder.business;

import java.rmi.RemoteException;
import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.data.ICTreeNode;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

/**
 * IBMainServiceBean : Implementation of BuilderService and simplified interface to BuilderLogic
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBMainServiceBean extends IBOServiceBean implements IBMainService,BuilderService
{
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getPage(java.lang.String)
	 */
	public Page getPage(String pageID) throws RemoteException {
		return getBuilderLogic().getPageCacher().getComponentBasedPage(pageID).getNewPageCloned();
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getCurrentDomain()
	 */
	public ICDomain getCurrentDomain()
	{
		return getBuilderLogic().getCurrentDomain(this.getIWApplicationContext());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getIBPageURL(int)
	 */
	public String getPageURI(int pageKey)
	{
		return getBuilderLogic().getIBPageURL(getIWApplicationContext(),pageKey);
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getIBPageURL(String)
	 */
	public String getPageURI(String pageId)
	{
		return getPageURI(Integer.parseInt(pageId));
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getIBPageURL(String)
	 */
	public String getPageURI(ICPage page)
	{
		if (page != null) {
			return getPageURI(((Number)page.getPrimaryKey()).intValue());
		}
		else {
			return getPageURI(-1);
		}
	}	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getIBPageURL(int)
	 */
	public String getCurrentPageURI(IWContext iwc)
	{
		return getBuilderLogic().getIBPageURL(iwc, getCurrentPageId(iwc));
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getRootPageId()
	 */
	public int getRootPageId()
	{
		return getBuilderLogic().getStartPageId(getIWApplicationContext());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getRootPageId()
	 */
	public String getRootPageKey()
	{
		return getBuilderLogic().getStartPageKey(getIWApplicationContext());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getRootPage()
	 */
	public ICPage getRootPage()throws RemoteException
	{
		int pageId = getRootPageId();
		try
		{
			return getIBPageHome().findByPrimaryKey(pageId);
		}
		catch (FinderException e)
		{
			e.printStackTrace();
			throw new RemoteException("IBMainServiceBean.getRootPage(): Exception getting the Root page for pageId="+pageId+" : Exception was : "+e.getMessage());
		}
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getCurrentPageId(com.idega.presentation.IWContext)
	 */
	public int getCurrentPageId(IWContext iwc)
	{
		return getBuilderLogic().getCurrentIBPageID(iwc);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getCurrentPageId(com.idega.presentation.IWContext)
	 */
	public String getCurrentPageKey(IWContext iwc)
	{
		return getBuilderLogic().getCurrentIBPage(iwc);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getCurrentPage(com.idega.presentation.IWContext)
	 */
	public ICPage getCurrentPage(IWContext iwc)throws RemoteException
	{
		int pageId = getCurrentPageId(iwc);
		try
		{
			return getIBPageHome().findByPrimaryKey(pageId);
		}
		catch (FinderException e)
		{
			e.printStackTrace();
			throw new RemoteException("IBMainServiceBean.getRootPage(): Exception getting the Root page for pageId="+pageId+" : Exception was : "+e.getMessage());
		}
	}
	
	private BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
	
	private ICPageHome getIBPageHome() throws RemoteException{
		return (ICPageHome)this.getIDOHome(ICPage.class);
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getPageTree(int, int)
	 */
	public ICTreeNode getPageTree(int startNodeId, int userId) throws RemoteException
	{
		// TODO Implement access control by userId
		return new PageTreeNode(startNodeId,this.getIWApplicationContext());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getPageTree(int)
	 */
	public ICTreeNode getPageTree(int startNodeId) throws RemoteException
	{
		return getPageTree(startNodeId,-1);
	}
	
	/**
	 * Unloads all the resources associated with the Builder
	 */
	public void unload(){
		BuilderLogic.unload();
	}
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getPageKeyByRequestURI(java.lang.String)
	 */
	public String getPageKeyByRequestURIAndServerName(String pageRequestUri,String serverName) {
		return getBuilderLogic().getPageKeyByURIAndServerName(pageRequestUri,serverName);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getCopyOfUIComponentFromIBXML(UIComponent)
	 */
	public UIComponent getCopyOfUIComponentFromIBXML(UIComponent component) {
		return getBuilderLogic().getCopyOfUIComponentFromIBXML(component);
		
	}
	
}
