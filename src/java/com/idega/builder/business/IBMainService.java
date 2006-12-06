package com.idega.builder.business;


import com.idega.core.builder.data.ICDomain;
import com.idega.presentation.IWContext;
import java.util.Map;
import com.idega.core.builder.business.BuilderPageWriterService;
import com.idega.core.builder.business.BuilderService;
import com.idega.idegaweb.IWMainApplication;
import java.rmi.RemoteException;
import javax.faces.component.UIComponent;
import com.idega.io.serialization.ObjectWriter;
import java.util.Collection;
import com.idega.business.IBOService;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.Page;
import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWUserContext;

public interface IBMainService extends IBOService, BuilderService, BuilderPageWriterService {
	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPage
	 */
	public Page getPage(String pageID) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentDomain
	 */
	public ICDomain getCurrentDomain() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	public String getPageURI(int pageKey) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	public String getPageURI(String pageId) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	public String getPageURI(ICPage page) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageURI
	 */
	public String getCurrentPageURI(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPageId
	 */
	public int getRootPageId() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPageKey
	 */
	public String getRootPageKey() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPage
	 */
	public ICPage getRootPage() throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageId
	 */
	public int getCurrentPageId(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageKey
	 */
	public String getCurrentPageKey(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPage
	 */
	public ICPage getCurrentPage(IWContext iwc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageTree
	 */
	public ICTreeNode getPageTree(int startNodeId, int userId) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageTree
	 */
	public ICTreeNode getPageTree(int startNodeId) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#unload
	 */
	public void unload();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageKeyByRequestURIAndServerName
	 */
	public String getPageKeyByRequestURIAndServerName(String pageRequestUri, String serverName);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCopyOfUIComponentFromIBXML
	 */
	public UIComponent getCopyOfUIComponentFromIBXML(UIComponent component);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#write
	 */
	public Object write(ICPage page, ObjectWriter writer, IWContext iwc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#movePage
	 */
	public boolean movePage(int newParentId, int nodeId);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#changePageName
	 */
	public boolean changePageName(int ID, String newName);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTopLevelPages
	 */
	public Collection getTopLevelPages(IWContext iwc);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTopLevelTemplates
	 */
	public Collection getTopLevelTemplates(IWContext iwc);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTemplateKey
	 */
	public String getTemplateKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageKey
	 */
	public String getPageKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getHTMLTemplateKey
	 */
	public String getHTMLTemplateKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTree
	 */
	public Map getTree(IWContext iwc);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTopLevelTemplateId
	 */
	public String getTopLevelTemplateId(Collection templates);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createNewPage
	 */
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createPageOrTemplateToplevelOrWithParent
	 */
	public int createPageOrTemplateToplevelOrWithParent(String name, String parentId, String type, String templateId, Map tree, IWContext creatorContext);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setProperty
	 */
	public boolean setProperty(String pageKey, String instanceId, String propertyName, String[] propertyValues, IWMainApplication iwma);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getICPage
	 */
	public ICPage getICPage(String key);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#deletePage
	 */
	public boolean deletePage(String pageId, boolean deleteChildren, Map tree, int userId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#checkDeletePage
	 */
	public boolean checkDeletePage(String pageId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#clearAllCachedPages
	 */
	public void clearAllCachedPages();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setTemplateId
	 */
	public void setTemplateId(String pageKey, String newTemplateId);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getIBXMLFormat
	 */
	public String getIBXMLFormat();
}