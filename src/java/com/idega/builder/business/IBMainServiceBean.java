/*
 * Created on 28.7.2003 by  tryggvil in project com.project
 */
package com.idega.builder.business;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;

import org.jdom.Document;

import com.idega.builder.app.IBApplication;
import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.data.IBPageBMPBean;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.business.BuilderPageWriterService;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.io.serialization.ObjectWriter;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.util.xml.XMLData;
import com.idega.xml.XMLElement;

/**
 * IBMainServiceBean : Implementation of BuilderService and simplified interface to BuilderLogic
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBMainServiceBean extends IBOServiceBean implements IBMainService, BuilderService, BuilderPageWriterService
{

	private static final long serialVersionUID = -6324845255940576630L;

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
			if(pageId==-1){
				return null;
			}
			else{
				return getIBPageHome().findByPrimaryKey(pageId);
			}
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
		
		ICDomain domain = getIWApplicationContext().getDomainByServerName(serverName);
		
		return getBuilderLogic().getPageKeyByURI(pageRequestUri,domain);
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.builder.business.BuilderService#getCopyOfUIComponentFromIBXML(UIComponent)
	 */
	public UIComponent getCopyOfUIComponentFromIBXML(UIComponent component) {
		return getBuilderLogic().getCopyOfUIComponentFromIBXML(component);
		
	}
	
	public Object write(ICPage page, ObjectWriter writer, IWContext iwc) throws RemoteException {
		ICFile file = page.getFile();
		// special case: file is empty 
		// do not create files of deleted pages
		if (file.isEmpty() && ! page.getDeleted()) {
			// file value is empty get a xml description of the page
			IBXMLPage xmlPage = getBuilderLogic().getPageCacher().getIBXML(this.getPrimaryKey().toString());
			XMLElement rootElement = xmlPage.getRootElement();
			if (rootElement == null) {
				return null;
			}
			// remove connection to document
			rootElement.detach();
			// convert to xml data, because for that class a writer already exists
			XMLData pageData = XMLData.getInstanceWithoutExistingFile();
			pageData.getDocument().setRootElement(rootElement);
			pageData.setName(page.getName());
			return writer.write(pageData, iwc);
		}
		// normal way to handle pages 
		return writer.write(page, iwc);
	}
	
	public boolean movePage(int newParentId, int nodeId, ICDomain domain) {
		return getBuilderLogic().movePage(newParentId, nodeId, domain);
	}
	
	public boolean changePageName(int ID, String newName) {
		return getBuilderLogic().changePageName(ID, newName);
	}

	public Collection getTopLevelPages(IWContext iwc){
		return getBuilderLogic().getTopLevelPages(iwc);
	}
	public Collection getTopLevelTemplates(IWContext iwc){
		return getBuilderLogic().getTopLevelTemplates(iwc);		
	}
	
	public String getTemplateKey() {
		return ICPageBMPBean.TEMPLATE;
	}
	
	public String getPageKey() {
		return ICPageBMPBean.PAGE;
	}
	
	public String getHTMLTemplateKey() {
		return IBPageBMPBean.FORMAT_HTML;
	}
	
	public Map getTree(IWContext iwc) {
		return PageTreeNode.getTree(iwc);
	}
	
	public Map getTree(IWApplicationContext iwac) {
		return PageTreeNode.getTree(iwac);
	}
	
	public String getTopLevelTemplateId(Collection templates) {
		return getBuilderLogic().getTopLevelTemplateId(templates);
	}
	
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup) {
		return getBuilderLogic().getIBPageHelper().createNewPage(parentId, name, type, templateId, pageUri, tree, creatorContext, subType, domainId, format, sourceMarkup);
	}
	
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup, String treeOrder){
		return getBuilderLogic().getIBPageHelper().createNewPage(parentId, name, type, templateId, pageUri, tree, creatorContext, subType, domainId, format, sourceMarkup, treeOrder);		
	}
	
	public int createPageOrTemplateToplevelOrWithParent(String name, String parentId, String type, String templateId, Map tree, IWContext creatorContext) {
		return getBuilderLogic().getIBPageHelper().createPageOrTemplateToplevelOrWithParent(name, parentId, type, templateId, tree, creatorContext);
	}
	
	public boolean setProperty(String pageKey, String instanceId, String propertyName, String[] propertyValues, IWMainApplication iwma) {
		return getBuilderLogic().setProperty(pageKey, instanceId, propertyName, propertyValues, iwma);
	}
	
	public ICPage getICPage(String key){
		return getBuilderLogic().getICPage(key);
	}
	
	public boolean deletePage(String pageId, boolean deleteChildren, Map tree, int userId, ICDomain domain) {
		return getBuilderLogic().getIBPageHelper().deletePage(pageId, deleteChildren, tree, userId, domain);
	}
	
	public boolean checkDeletePage(String pageId, ICDomain domain) {
		return getBuilderLogic().getIBPageHelper().checkDeletePage(pageId, domain);
	}
	
	public void clearAllCachedPages() {
		getBuilderLogic().clearAllCachedPages();
	}
	
	public void setTemplateId(String pageKey, String newTemplateId) {
		getBuilderLogic().setTemplateId(pageKey, newTemplateId);
	}
	
	public String getIBXMLFormat() {
		return getBuilderLogic().PAGE_FORMAT_IBXML;
	}
	
	public String[] getPropertyValues(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] selectedValues, boolean returnSelectedValueIfNothingFound) {
		return getBuilderLogic().getPropertyValues(iwma, pageKey, instanceId, propertyName, selectedValues, returnSelectedValueIfNothingFound);
	}
	
	public boolean removeProperty(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] values) {
		return getBuilderLogic().removeProperty(iwma, pageKey, instanceId, propertyName, values);
	}
	
	public boolean changePageUriByTitle(String parentId, ICPage page, String pageTitle, int domainId) {
		return getBuilderLogic().changePageUriByTitle(parentId, page, pageTitle, domainId);
	}
	
	public boolean movePageToTopLevel(int pageID, IWContext iwc) {
		return getBuilderLogic().movePageToTopLevel(pageID, iwc);
	}
	
	public void createTopLevelPageFromExistingPage(int pageID, ICDomain domain, IWUserContext creatorContext) {
		getBuilderLogic().getIBPageHelper().createTopLevelPageFromExistingPage(pageID, domain, creatorContext);
	}
	
	public boolean isPageTopLevelPage(int pageID, ICDomain domain) {
		return getBuilderLogic().getIBPageHelper().isPageTopLevelPage(pageID, domain);
	}
	
	public boolean unlockRegion(String pageKey, String parentObjectInstanceID, String label) {
		return getBuilderLogic().unlockRegion(pageKey, parentObjectInstanceID, label);
	}
	
	public void renameRegion(String pageKey, String region_id, String region_label, String new_region_id, String new_region_label) {
		getBuilderLogic().renameRegion(pageKey, region_id, region_label, new_region_id, new_region_label);
	}
	
	public void setCurrentPageId(IWContext iwc, String pageKey) {
		getBuilderLogic().setCurrentIBPage(iwc, pageKey);
	}
	
	public boolean addPropertyToModule(String pageKey, String moduleId, String propName, String propValue) {
		return getBuilderLogic().addPropertyToModule(pageKey, moduleId, propName, propValue);
	}
	
	public List<String> getModuleId(String pageKey, String moduleClass) {
		return getBuilderLogic().getModuleId(pageKey, moduleClass);
	}
	
	public boolean isPropertySet(String pageKey, String instanceId, String propertyName, IWMainApplication iwma) {
		return getBuilderLogic().isPropertySet(pageKey, instanceId, propertyName, iwma);
	}
	
	public boolean isPropertyValueSet(String pageKey, String moduleId, String propertyName, String propertyValue) {
		return getBuilderLogic().isPropertyValueSet(pageKey, moduleId, propertyName, propertyValue);
	}
	
	public boolean removeValueFromModuleProperty(String pageKey, String moduleId, String propertyName, String valueToRemove) {
		return getBuilderLogic().removeValueFromModuleProperty(pageKey, moduleId, propertyName, valueToRemove);
	}
	
	public void setTreeOrder(int id, int order){
		getBuilderLogic().setTreeOrder(id, order);
	}

	public int getTreeOrder(int id){
		return getBuilderLogic().getTreeOrder(id);
	}
	
	public void increaseTreeOrder(int id){
		getBuilderLogic().increaseTreeOrder(id);
	}

	public void decreaseTreeOrder(int id){
		getBuilderLogic().decreaseTreeOrder(id);
	}	
	
	public int setAsLastInLevel(boolean isTopLevel, String parentId){
		return getBuilderLogic().setAsLastInLevel(isTopLevel, parentId);
	}
	
	public String getProperty(String pageKey, String instanceId, String propertyName){
		return getBuilderLogic().getProperty(pageKey, instanceId, propertyName);
	}
	
	public String getPageKeyByURI(String requestURI){
		
		ICDomain domain = getIWApplicationContext().getDomain();
		
		return getBuilderLogic().getPageKeyByURI(requestURI,domain);
	}
	
	public String getExistingPageKeyByURI(String requestURI){

		ICDomain domain = getIWApplicationContext().getDomain();
		return getBuilderLogic().getExistingPageKeyByURI(requestURI,domain);
	}
	
	public String getRenderedComponent(UIComponent component, IWContext iwc, boolean cleanHtml) {
		return getBuilderLogic().getRenderedComponent(component, iwc, cleanHtml);
	}
	
	public Document getRenderedComponent(IWContext iwc, UIComponent object, boolean cleanHtml) {
		return getBuilderLogic().getRenderedComponent(iwc, object, cleanHtml);
	}
	
	public boolean removeBlockObjectFromCache(IWContext iwc, String cacheKey) {
		return getBuilderLogic().removeBlockObjectFromCache(iwc, cacheKey);
	}
	
	public void startBuilderSession(IWContext iwc) {
		IBApplication.startIBApplication(iwc);
	}
	
	public void clearAllCaches() {
		getBuilderLogic().clearAllCaches();
	}
	
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] properties) {
		return getBuilderLogic().setModuleProperty(pageKey, moduleId, propertyName, properties);
	}
	
	public boolean removeAllBlockObjectsFromCache(IWContext iwc) {
		return getBuilderLogic().removeAllBlockObjectsFromCache(iwc);
	}
	
	public String generateResourcePath(String base, String scope, String fileName) {
		return getBuilderLogic().generateResourcePath(base, scope, fileName);
	}
	
	public String getYearMonthPath() {
		return getBuilderLogic().getYearMonthPath();
	}
	
	public boolean setProperty(IWContext iwc, String pageKey, String instanceId, String propertyName, List<AdvancedProperty> properties) {
		return getBuilderLogic().setProperty(iwc, pageKey, instanceId, propertyName, properties);
	}
	
	public Document getRenderedModule(String pageKey, String componentId, boolean cleanHtml) {
		return getBuilderLogic().getRenderedModule(pageKey, componentId, cleanHtml);
	}
}
