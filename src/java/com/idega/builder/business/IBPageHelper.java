/*
 * $Id: IBPageHelper.java,v 1.34 2004/05/11 14:22:28 gummi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.builder.data.IBStartPage;
import com.idega.builder.data.IBStartPageHome;
import com.idega.builder.dynamicpagetrigger.business.DPTCopySession;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDORuntimeException;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.TreeViewer;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLElement;
/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageHelper {
	public static final String PAGE = com.idega.builder.data.IBPageBMPBean.PAGE;
	public static final String TEMPLATE = com.idega.builder.data.IBPageBMPBean.TEMPLATE;
	public static final String DRAFT = com.idega.builder.data.IBPageBMPBean.DRAFT;
	public static final String FOLDER = com.idega.builder.data.IBPageBMPBean.FOLDER;
	public static final String DPT_PAGE = com.idega.builder.data.IBPageBMPBean.DPT_PAGE;
	public static final String DPT_TEMPLATE = com.idega.builder.data.IBPageBMPBean.DPT_TEMPLATE;
	private final String LINK_STYLE = "font-family:Arial,Helvetica,sans-serif;font-size:8pt;color:#000000;text-decoration:none;";
	private final int PAGEVIEWER = 0;
	private final int TEMPLATEVIEWER = 1;
	private static IBPageHelper _instance = null;
	private IBPageHelper() {
	}
	public static IBPageHelper getInstance() {
		if (_instance == null)
			_instance = new IBPageHelper();
		return _instance;
	}
	/**
	 * Creates a new IBPage. Sets its name and type and stores it to the database.
	 * If the parentId and the tree parameter are valid it also stores the page in
	 * the cached IWContext tree.
	 *
	 * @param parentId The id of the parent of this page
	 * @param name The name this page is to be given
	 * @param type The type of the page, ie. PAGE, TEMPLATE, DRAFT, ...
	 * @param templateId The id of the page this page is extending, if any
	 * @param tree A map of PageTreeNode objects representing the whole page tree
	 *
	 * @return The id of the new IBPage
	 */
	public int createNewPage(String parentId, String name, String type, String templateId, Map tree) {
		return createNewPage(parentId, name, type, templateId, tree, null, null);
	}
	/**
	 * Creates a new IBPage. Sets its name and type and stores it to the database.
	 * If the parentId and the tree parameter are valid it also stores the page in
	 * the cached IWContext tree.
	 *
	 * @param parentId The id of the parent of this page
	 * @param name The name this page is to be given
	 * @param type The type of the page, ie. PAGE, TEMPLATE, DRAFT, ...
	 * @param templateId The id of the page this page is extending, if any
	 * @param tree A map of PageTreeNode objects representing the whole page tree
	 * @param creatorContext the context of the User that created the page
	 *
	 * @return The id of the new IBPage
	 */
	public int createNewPage(String parentId, String name, String type, String templateId, Map tree, IWUserContext creatorContext) {
		return createNewPage(parentId, name, type, templateId, tree, creatorContext, null);
	}
	/**
	 * Creates a new IBPage. Sets its name and type and stores it to the database.
	 * If the parentId and the tree parameter are valid it also stores the page in
	 * the cached IWContext tree.
	 *
	 * @param parentId The id of the parent of this page
	 * @param name The name this page is to be given
	 * @param type The type of the page, ie. PAGE, TEMPLATE, DRAFT, ...
	 * @param templateId The id of the page this page is extending, if any
	 * @param tree A map of PageTreeNode objects representing the whole page tree
	 * @param creatorContext the context of the User that created the page
	 * @param subType Subtype of the current page
	 *
	 * @return The id of the new IBPage
	 */
	public int createNewPage(String parentId, String name, String type, String templateId, Map tree, IWUserContext creatorContext, String subType) {
		return createNewPage(parentId, name, type, templateId, tree, creatorContext, subType, -1);
	}
	/**
	 * Creates a new IBPage. Sets its name and type and stores it to the database.
	 * If the parentId and the tree parameter are valid it also stores the page in
	 * the cached IWContext tree.
	 *
	 * @param parentId The id of the parent of this page
	 * @param name The name this page is to be given
	 * @param type The type of the page, ie. PAGE, TEMPLATE, DRAFT, ...
	 * @param templateId The id of the page this page is extending, if any
	 * @param tree A map of PageTreeNode objects representing the whole page tree
	 * @param creatorContext the context of the User that created the page
	 * @param subType Subtype of the current page
	 * @param domainId The id of the domain if you are creating a top level page
	 *
	 * @return The id of the new IBPage
	 */
	public int createNewPage(String parentId, String name, String type, String templateId, Map tree, IWUserContext creatorContext, String subType, int domainId){
		ICPage ibPage = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).createLegacy();
		if (name == null)
			name = "Untitled";
		ibPage.setName(name);
		ICFile file;
		try {
			file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
		} catch (IDOLookupException e1) {
			e1.printStackTrace();
			return -1;
		} catch (CreateException e1) {
			e1.printStackTrace();
			return -1;
		}
		file.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_XML);
		ibPage.setFile(file);
		if (type.equals(PAGE)) {
			ibPage.setType(com.idega.builder.data.IBPageBMPBean.PAGE);
		}
		else if (type.equals(TEMPLATE)) {
			ibPage.setType(com.idega.builder.data.IBPageBMPBean.TEMPLATE);
		}
		else if (type.equals(DRAFT)) {
			ibPage.setType(com.idega.builder.data.IBPageBMPBean.DRAFT);
		}
		else if (type.equals(DPT_PAGE)) {
			ibPage.setType(com.idega.builder.data.IBPageBMPBean.DPT_PAGE);
		}
		else if (type.equals(DPT_TEMPLATE)) {
			ibPage.setType(com.idega.builder.data.IBPageBMPBean.DPT_TEMPLATE);
		}
		else if (type.equals(com.idega.builder.data.IBPageBMPBean.FOLDER)) {
			ibPage.setType(com.idega.builder.data.IBPageBMPBean.FOLDER);
		}
		else {
			ibPage.setType(com.idega.builder.data.IBPageBMPBean.PAGE);
		}
		int tid = -1;
		try {
			tid = Integer.parseInt(templateId);
			ibPage.setTemplateId(tid);
		}
		catch (java.lang.NumberFormatException e) {
//			e.printStackTrace();
		}
		if (subType != null)
			ibPage.setSubType(subType);
		try {
			ibPage.insert();
			if (creatorContext != null) {
				ibPage.setOwner(creatorContext);
			}
			if (parentId != null) {
				ICPage ibPageParent = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(parentId));
				ibPageParent.addChild(ibPage);
			}
			else {
				IBStartPageHome home = (IBStartPageHome) IDOLookup.getHome(IBStartPage.class);
				IBStartPage page = home.create();
				page.setPageId(ibPage.getID());
				if (type.equals(PAGE)) {
					page.setPageTypePage();
				}
				else if (type.equals(TEMPLATE)) {
					page.setPageTypeTemplate();
				}
				else if (type.equals(DRAFT)) {
					page.setPageTypePage();
				}
				else if (type.equals(DPT_PAGE)) {
					page.setPageTypePage();
				}
				else if (type.equals(DPT_TEMPLATE)) {
					page.setPageTypeTemplate();
				}
				else if (type.equals(com.idega.builder.data.IBPageBMPBean.FOLDER)) {
					page.setPageTypePage();
				}
				else {
					page.setPageTypePage();
				}
				page.setDomainId(domainId);
				page.store();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return (-1);
		}
		
		if (tid != -1) {
//			System.out.println("Creating page = " + ibPage.getName());
			IBXMLPage currentXMLPage = BuilderLogic.getInstance().getIBXMLPage(ibPage.getID());
			Page current = currentXMLPage.getPopulatedPage();
			List children = current.getChildrenRecursive();
//			System.out.println("children size = " + children.size());
			if (children != null) {
				Iterator it = children.iterator();
				boolean copyInstancePermissions = false;
				try {
					copyInstancePermissions = ((DPTCopySession)IBOLookup.getSessionInstance(creatorContext,DPTCopySession.class)).doCopyInstancePermissions();
				} catch (IBOLookupException e2) {
					e2.printStackTrace();
				} catch (RemoteException e2) {
					e2.printStackTrace();
				}
				while (it.hasNext()) {
					PresentationObject obj = (PresentationObject) it.next();
					boolean ok = changeInstanceId(obj, currentXMLPage, copyInstancePermissions,creatorContext);
					if (!ok) {
//						System.out.println("Unable to change instance id's for page = " + ibPage.getName());
						return (-1);
					}
				}
			}
			
			currentXMLPage.update();
		}
		int id = ibPage.getID();
		if (tree != null) {
			PageTreeNode node = new PageTreeNode(id, name);
			if (parentId != null) {
				PageTreeNode parent = (PageTreeNode) tree.get(Integer.valueOf(parentId));
				if (parent != null)
					parent.addChild(node);
			}
			tree.put(new Integer(node.getNodeID()), node);
		}
		if ((templateId != null) && (!templateId.equals(""))) {
			IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(templateId);
			xml.addUsingTemplate(Integer.toString(id));
			Page templateParent = xml.getPopulatedPage();
			if (!templateParent.isLocked()) {
				BuilderLogic.getInstance().unlockRegion(Integer.toString(id), "-1", null);
			}
		}
		return (id);
	}
	public boolean addElementToPage(ICPage ibPage, int[] templateObjInstID, IWUserContext iwuc) {
		System.out.println("addElementToPage begins");
		if (templateObjInstID != null) {
			IBXMLPage currentXMLPage = BuilderLogic.getInstance().getIBXMLPage(ibPage.getID());
			Page current = currentXMLPage.getPopulatedPage();
			List children = current.getChildrenRecursive();
			if (children != null) {
				boolean copyInstancePermissions = false;
				try {
					copyInstancePermissions = ((DPTCopySession)IBOLookup.getSessionInstance(iwuc,DPTCopySession.class)).doCopyInstancePermissions();
				} catch (IBOLookupException e2) {
					e2.printStackTrace();
				} catch (RemoteException e2) {
					e2.printStackTrace();
				}
				Iterator it = children.iterator();
				while (it.hasNext()) {
					PresentationObject obj = (PresentationObject) it.next();
					for (int i = 0; i < templateObjInstID.length; i++) {
						if (obj.getICObjectInstanceID() == templateObjInstID[i]) {
							boolean ok = changeInstanceId(obj, currentXMLPage, copyInstancePermissions,iwuc);
							if (!ok) {
								System.out.println("addElementToPage - changeInstanceId failed");
								return false;
							}
						}
					}
				}
			}
			else {
				//				System.out.println("addElementToPage - children null");
				return false;
			}
		}
		else {
			//			System.out.println("addElementToPage - templateObjInstID null");
			return false;
		}
		//		System.out.println("addElementToPage ends");
		return true;
	}
	public boolean addElementToPage(ICPage ibPage, int templateObjInstID, IWUserContext iwuc) {
		int[] ids = new int[1];
		ids[0] = templateObjInstID;
		return addElementToPage(ibPage, ids,iwuc);
	}
	private boolean changeInstanceId(PresentationObject obj, IBXMLPage xmlpage, boolean copyPermissions, IWUserContext iwuc) {
		if (obj.getChangeInstanceIDOnInheritance()) {
			try {
				int object_id = obj.getICObjectID();
				int ic_instance_id = obj.getICObjectInstanceID();
				DPTCopySession cSession = (DPTCopySession)IBOLookup.getSessionInstance(iwuc,DPTCopySession.class);
				ICObjectInstanceHome icObjInstHome = ((ICObjectInstanceHome) IDOLookup.getHomeLegacy(ICObjectInstance.class));
				Object instanceKey = new Integer(ic_instance_id);
				Object instancePK = cSession.getNewValue(ICObjectInstance.class,instanceKey);
				if(instancePK == null) {
					ICObjectInstance instance = null;
					try {
						ICObjectInstance inst = obj.getICObjectInstance();
						instance = icObjInstHome.createLegacy();
						instance.setICObjectID(object_id);
						if (inst != null) {
							instance.setParentInstanceID(inst.getID());
						}
						instance.setIBPageByKey(xmlpage.getKey());
						instance.insert();
						instancePK = instance.getPrimaryKey();
						cSession.setNewValue(ICObjectInstance.class,instanceKey,instancePK);
						if (copyPermissions) {
							AccessControl.copyObjectInstancePermissions(String.valueOf(ic_instance_id), String.valueOf(instance.getID()));
						}
					}
					catch (Exception e) {
						System.out.println("changeInstanceId - exception");
						e.printStackTrace();
						return false;
					}
					if (obj instanceof Builderaware) {
							boolean ok = ((Builderaware) obj).copyBlock(instance.getID(),cSession);
							if (!ok) {
								System.err.println("changeInstanceId - copyBlock failed");
								return false;
							}
					}
				}
				XMLElement element = new XMLElement(XMLConstants.CHANGE_IC_INSTANCE_ID);
				XMLAttribute from = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_FROM, Integer.toString(ic_instance_id));
				XMLAttribute to = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_TO, String.valueOf(instancePK));
				element.setAttribute(from);
				element.setAttribute(to);
				XMLWriter.addNewElement(xmlpage, -1, element);
			} catch (IBOLookupException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	/**
	 *
	 */
	public boolean checkDeletePage(String pageId) {
		IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(pageId);
		boolean okToDelete = true;
		if (xml.getType().equals(IBXMLPage.TYPE_TEMPLATE)) {
			List map = xml.getUsingTemplate();
			if ((map == null) || (map.isEmpty())) {
				okToDelete = true;
			}
			else {
				okToDelete = false;
			}
		}
		else {
			okToDelete = true;
		}
		return (okToDelete);
	}
	/**
	 *
	 */
	public boolean checkDeleteChildrenOfPage(String pageId) {
		try {
			ICPage page = ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(pageId));
			if (page.getType().equals(com.idega.builder.data.IBPageBMPBean.PAGE))
				return true;
			else if (page.getType().equals(com.idega.builder.data.IBPageBMPBean.DRAFT))
				return true;
			else if (page.getType().equals(com.idega.builder.data.IBPageBMPBean.DPT_PAGE))
				return true;
			else {
				Iterator it = page.getChildren();
				if (it != null) {
					while (it.hasNext()) {
						ICPage child = (ICPage) it.next();
						IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(child.getID());
						List map = xml.getUsingTemplate();
						if ((map != null) || (!map.isEmpty())) {
							return false;
						}
						boolean check = true;
						if (child.getChildCount() != 0)
							check = checkDeleteChildrenOfPage(Integer.toString(child.getID()));
						if (!check)
							return false;
					}
				}
				return true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Moves the page by id pageId to under the page with id newParentPageId
	 * @return true if the move was successful, false otherwise
	 */
	public boolean movePage(int pageId, int newParentPageId, int userId) {
		return movePage(pageId, newParentPageId, null, userId);
	}
	/**
	 * Moves the page by id pageId to under the page with id newParentPageId
	 * @return true if the move was successful, false otherwise
	 */
	public boolean movePage(int pageId, int newParentPageId, Map pageTreeCacheMap, int userId) {
		try {
			/**
			 * @todo Implement authentication check
			 */
			if (pageId == newParentPageId) {
				throw new Exception("Cannot move page under itself");
			}
			ICPage ibpage = getIBPageHome().findByPrimaryKey(new Integer(pageId));
			if (!ibpage.isPage()) {
				throw new Exception("Method only implemented for regular pages not templates");
			}
			ICPage parent = (ICPage) ibpage.getParentNode();
			ICPage newParent = getIBPageHome().findByPrimaryKey(new Integer(newParentPageId));
			parent.removeChild(ibpage);
			newParent.addChild(ibpage);
			if (pageTreeCacheMap != null) {
				PageTreeNode parentNode = (PageTreeNode) pageTreeCacheMap.get(parent.getIDInteger());
				PageTreeNode childNode = (PageTreeNode) pageTreeCacheMap.get(ibpage.getIDInteger());
				PageTreeNode newParentNode = (PageTreeNode) pageTreeCacheMap.get(newParent.getIDInteger());
				parentNode.removeChild(childNode);
				newParentNode.addChild(childNode);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * Checks if the page can be moved regularly. This is typically only implemented for regular pages.
	 * @return true the method movePage handles the move of the page by id pageID
	 */
	public boolean checkIfMayMovePage(int pageId, int userId) {
		/**
		 * @todo Implement authentication check
		 */
		try {
			ICPage ibpage = getIBPageHome().findByPrimaryKey(new Integer(pageId));
			return ibpage.isPage();
		}
		catch (Exception e) {
			return false;
		}
	}
	/**
	 *
	 */
	public boolean deletePage(String pageId, boolean deleteChildren, Map tree, int userId) {
		javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
		try {
			t.begin();
			ICPage ibpage = getIBPageHome().findByPrimaryKey(Integer.parseInt(pageId));
			ICPage parent = (ICPage) ibpage.getParentNode();
			parent.removeChild(ibpage);
			ibpage.delete(userId);
			int templateId = ibpage.getTemplateId();
			if (templateId > 0) {
				BuilderLogic.getInstance().getIBXMLPage(templateId).removeUsingTemplate(pageId);
			}
			if (deleteChildren) {
				deleteAllChildren(ibpage, tree, userId);
				if (tree != null) {
					PageTreeNode parentNode = (PageTreeNode) tree.get(parent.getIDInteger());
					PageTreeNode childNode = (PageTreeNode) tree.get(ibpage.getIDInteger());
					parentNode.removeChild(childNode);
					tree.remove(ibpage.getIDInteger());
				}
			}
			else {
				parent.moveChildrenFrom(ibpage);
				if (tree != null) {
					PageTreeNode parentNode = (PageTreeNode) tree.get(parent.getIDInteger());
					PageTreeNode childNode = (PageTreeNode) tree.get(ibpage.getIDInteger());
					Iterator it = childNode.getChildren();
					if (it != null) {
						while (it.hasNext()) {
							parentNode.addChild((PageTreeNode) it.next());
						}
					}
					parentNode.removeChild(childNode);
					tree.remove(ibpage.getIDInteger());
				}
			}
		}
		catch (Exception e) {
			try {
				t.rollback();
			}
			catch (javax.transaction.SystemException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			return false;
		}
		finally {
			try {
				t.commit();
			}
			catch (Exception e) {
				try {
					t.rollback();
				}
				catch (javax.transaction.SystemException ex) {
					ex.printStackTrace();
				}
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	/**
	 *
	 */
	private void deleteAllChildren(ICPage page, Map tree, int userId) throws java.sql.SQLException {
		Iterator it = page.getChildren();
		if (it != null) {
			while (it.hasNext()) {
				ICPage child = (ICPage) it.next();
				if (child.getChildCount() != 0)
					deleteAllChildren(child, tree, userId);
				child.delete(userId);
				int templateId = child.getTemplateId();
				if (templateId > 0) {
					BuilderLogic.getInstance().getIBXMLPage(templateId).removeUsingTemplate(Integer.toString(child.getID()));
				}
				page.removeChild(child);
				if (tree != null)
					tree.remove(child.getIDInteger());
			}
		}
	}
	public TreeViewer getPageTreeViewer(IWContext iwc) {
		return getTreeViewer(iwc, PAGEVIEWER);
	}
	public TreeViewer getTemplateTreeViewer(IWContext iwc) {
		return getTreeViewer(iwc, TEMPLATEVIEWER);
	}
	private TreeViewer getTreeViewer(IWContext iwc, int type) {
		com.idega.core.builder.data.ICDomain domain = BuilderLogic.getCurrentDomain(iwc);
		int id = -1;
		if (type == PAGEVIEWER) {
			id = domain.getStartPageID();
		}
		else {
			id = domain.getStartTemplateID();
		}
		TreeViewer viewer = TreeViewer.getTreeViewerInstance(new PageTreeNode(id, iwc), iwc);
		try {
			java.util.Collection coll = null;
			if (type == PAGEVIEWER)
				coll = getStartPages(domain);
			else
				coll = getTemplateStartPages(domain);
			java.util.Iterator it = coll.iterator();
			while (it.hasNext()) {
				com.idega.builder.data.IBStartPage startPage = (com.idega.builder.data.IBStartPage) it.next();
				if (startPage.getPageId() != id)
					viewer.addFirstLevelNode(new PageTreeNode(startPage.getPageId(), iwc));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		viewer.setNodeActionParameter(com.idega.builder.business.BuilderLogic.IB_PAGE_PARAMETER);
		Link l = new Link();
		l.setNoTextObject(true);
		l.maintainParameter(Page.IW_FRAME_CLASS_PARAMETER, iwc);
		l.addParameter("reload", "t");
		viewer.setToMaintainParameter(Page.IW_FRAME_CLASS_PARAMETER, iwc);
		viewer.setTreeStyle(LINK_STYLE);
		viewer.setLinkPrototype(l);
		return viewer;
	}
	
	/**
	 * @return list of PageTreeNode
	 */
	public List getFirstLevelPageTreeNodesDomainFirst(IWContext iwc) throws IDOLookupException, FinderException {
		return getFirstLevelPageTreeNodesDomainPageFirstDependingOnType(iwc, PAGEVIEWER);
	}
	
	/** 
	 * @return list of PageTreeNode
	 */
	public List getFirstLevelPageTreeNodesTemplateDomainFirst(IWContext iwc) throws IDOLookupException, FinderException {
		return getFirstLevelPageTreeNodesDomainPageFirstDependingOnType(iwc, TEMPLATEVIEWER);
	}

	private List getFirstLevelPageTreeNodesDomainPageFirstDependingOnType(IWContext iwc, int type) throws IDOLookupException, FinderException {
		ICDomain domain = BuilderLogic.getCurrentDomain(iwc);
		int domainStartPageId = (PAGEVIEWER == type) ? domain.getStartPageID() : domain.getStartTemplateID();
		Collection startPages = (PAGEVIEWER == type) ? getStartPages(domain) : getTemplateStartPages(domain);
		List pages = new ArrayList(1 +  startPages.size());
		pages.add(new PageTreeNode(domainStartPageId, iwc));
		Iterator iterator = startPages.iterator();
		while (iterator.hasNext()) {
			IBStartPage startPage = (IBStartPage) iterator.next();
			int id = startPage.getPageId();
			// do not add the domain start page again
			if (id != domainStartPageId) {
				pages.add(new PageTreeNode(id, iwc));
			}
		}
		return pages;
	}
		
	private Collection getStartPages(ICDomain domain) throws IDOLookupException, FinderException {
		return ((IBStartPageHome) IDOLookup.getHome(IBStartPage.class)).findAllPagesByDomain(((Integer) domain.getPrimaryKeyValue()).intValue());
	}
	
	private Collection getTemplateStartPages(ICDomain domain) throws IDOLookupException, FinderException {
		return ((IBStartPageHome) IDOLookup.getHome(IBStartPage.class)).findAllTemplatesByDomain(((Integer) domain.getPrimaryKeyValue()).intValue());
	}
	
	protected ICPageHome getIBPageHome() {
		try {
			return (ICPageHome) IDOLookup.getHome(ICPage.class);
		}
		catch (Exception e) {
			throw new IDORuntimeException(e);
		}
	}
}