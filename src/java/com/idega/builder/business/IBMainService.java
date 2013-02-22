package com.idega.builder.business;


import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import com.idega.business.IBOService;
import com.idega.core.builder.business.BuilderPageWriterService;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.io.serialization.ObjectWriter;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;

public interface IBMainService extends IBOService, BuilderService, BuilderPageWriterService {
	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPage
	 */
	@Override
	public Page getPage(String pageID) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentDomain
	 */
	@Override
	public ICDomain getCurrentDomain() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	@Override
	public String getPageURI(int pageKey) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	@Override
	public String getPageURI(String pageId) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageURI
	 */
	@Override
	public String getPageURI(ICPage page) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageURI
	 */
	@Override
	public String getCurrentPageURI(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPageId
	 */
	@Override
	public int getRootPageId() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPageKey
	 */
	@Override
	public String getRootPageKey() throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getRootPage
	 */
	@Override
	public ICPage getRootPage() throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageId
	 */
	@Override
	public int getCurrentPageId(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPageKey
	 */
	@Override
	public String getCurrentPageKey(IWContext iwc) throws RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCurrentPage
	 */
	@Override
	public ICPage getCurrentPage(IWContext iwc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageTree
	 */
	@Override
	public ICTreeNode getPageTree(int startNodeId, int userId) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageTree
	 */
	@Override
	public ICTreeNode getPageTree(int startNodeId) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#unload
	 */
	@Override
	public void unload();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageKeyByRequestURIAndServerName
	 */
	@Override
	public String getPageKeyByRequestURIAndServerName(String pageRequestUri, String serverName);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getCopyOfUIComponentFromIBXML
	 */
	@Override
	public UIComponent getCopyOfUIComponentFromIBXML(UIComponent component);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#write
	 */
	@Override
	public Object write(ICPage page, ObjectWriter writer, IWContext iwc) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#movePage
	 */
	@Override
	public boolean movePage(int newParentId, int nodeId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTopLevelPages
	 */
	@Override
	public Collection<PageTreeNode> getTopLevelPages(IWContext iwc);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTemplateKey
	 */
	@Override
	public String getTemplateKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPageKey
	 */
	@Override
	public String getPageKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getHTMLTemplateKey
	 */
	@Override
	public String getHTMLTemplateKey();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getTopLevelTemplateId
	 */
	@Override
	public String getTopLevelTemplateId(Collection<?> templates);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createNewPage
	 */
	@Override
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map<Integer, ? extends ICTreeNode> tree,
			IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createPageOrTemplateToplevelOrWithParent
	 */
	@Override
	public int createPageOrTemplateToplevelOrWithParent(String name, String parentId, String type, String templateId,
			Map<Integer, ? extends ICTreeNode> tree, IWContext creatorContext);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setProperty
	 */
	@Override
	public boolean setProperty(String pageKey, String instanceId, String propertyName, String[] propertyValues, IWMainApplication iwma);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getICPage
	 */
	@Override
	public ICPage getICPage(String key);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#deletePage
	 */
	@Override
	public boolean deletePage(String pageId, boolean deleteChildren, Map<Integer, ? extends ICTreeNode> tree, int userId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#checkDeletePage
	 */
	@Override
	public boolean checkDeletePage(String pageId, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#clearAllCachedPages
	 */
	@Override
	public void clearAllCachedPages();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setTemplateId
	 */
	@Override
	public void setTemplateId(String pageKey, String newTemplateId);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getIBXMLFormat
	 */
	@Override
	public String getIBXMLFormat();

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#getPropertyValues
	 */
	@Override
	public String[] getPropertyValues(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] selectedValues, boolean returnSelectedValueIfNothingFound);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#removeProperty
	 */
	@Override
	public boolean removeProperty(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] values);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#changePageUriByTitle
	 */
	@Override
	public boolean changePageUriByTitle(String parentId, ICPage page, String pageTitle, int domainId);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#movePageToTopLevel
	 */
	@Override
	public boolean movePageToTopLevel(int pageID, IWContext iwc);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#createTopLevelPageFromExistingPage
	 */
	@Override
	public void createTopLevelPageFromExistingPage(int pageID, ICDomain domain, IWUserContext creatorContext);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#isPageTopLevelPage
	 */
	@Override
	public boolean isPageTopLevelPage(int pageID, ICDomain domain);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#unlockRegion
	 */
	@Override
	public boolean unlockRegion(String pageKey, String parentObjectInstanceID, String label);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#setCurrentPageId
	 */
	@Override
	public void setCurrentPageId(IWContext iwc, String pageKey);

	/**
	 * @see com.idega.builder.business.IBMainServiceBean#addPropertyToModule
	 */
	@Override
	public boolean addPropertyToModule(String pageKey, String moduleId, String propName, String propValue);

	@Override
	public boolean addPropertyToModules(String pageKey, List<String> moduleIds, String propName, String propValue);

	@Override
	public List<String> getModuleId(String pageKey, String moduleClass);

	@Override
	public boolean isPropertySet(String pageKey, String instanceId, String propertyName, IWMainApplication iwma);

	@Override
	public boolean isPropertyValueSet(String pageKey, String moduleId, String propertyName, String propertyValue);

	@Override
	public boolean removeValueFromModuleProperty(String pageKey, String moduleId, String propertyName, String valueToRemove);

	@Override
	public boolean removeValueFromModulesProperties(String pageKey, List<String> moduleIds, String propertyName, String valueToRemove);

	@Override
	public boolean removeBlockObjectFromCache(IWContext iwc, String cacheKey);

	@Override
	public void startBuilderSession(IWContext iwc);
}