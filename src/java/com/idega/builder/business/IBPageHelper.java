/*
 * $Id: IBPageHelper.java,v 1.21 2003/04/03 09:10:10 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.idega.block.IWBlock;
import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBPageHome;
import com.idega.builder.data.IBStartPages;
import com.idega.builder.data.IBStartPagesHome;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICObjectInstance;
import com.idega.data.IDOLookup;
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
	public int createNewPage(String parentId, String name, String type, String templateId, Map tree, IWUserContext creatorContext, String subType, int domainId) {
		IBPage ibPage = ((com.idega.builder.data.IBPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).createLegacy();
		if (name == null)
			name = "Untitled";
		ibPage.setName(name);
		ICFile file = ((com.idega.core.data.ICFileHome) com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();
		file.setMimeType(com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_XML);
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
			e.printStackTrace();
		}
		if (subType != null)
			ibPage.setSubType(subType);
		try {
			ibPage.insert();
			if (creatorContext != null) {
				ibPage.setOwner(creatorContext);
			}
			if (parentId != null) {
				IBPage ibPageParent = ((com.idega.builder.data.IBPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(parentId));
				ibPageParent.addChild(ibPage);
			}
			else {
				IBStartPagesHome home = (IBStartPagesHome) IDOLookup.getHome(IBStartPages.class);
				IBStartPages page = home.create();
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
			IBXMLPage currentXMLPage = BuilderLogic.getInstance().getIBXMLPage(ibPage.getID());
			Page current = currentXMLPage.getPopulatedPage();
			List children = current.getAllContainedObjectsRecursive();
			if (children != null) {
				Iterator it = children.iterator();
				while (it.hasNext()) {
					PresentationObject obj = (PresentationObject) it.next();
					boolean ok = changeInstanceId(obj, currentXMLPage, true);
					if (!ok)
						return (-1);
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
	public boolean addElementToPage(IBPage ibPage, int[] templateObjInstID) {
		//		System.out.println("addElementToPage begins");
		if (templateObjInstID != null) {
			IBXMLPage currentXMLPage = BuilderLogic.getInstance().getIBXMLPage(ibPage.getID());
			Page current = currentXMLPage.getPopulatedPage();
			List children = current.getAllContainedObjectsRecursive();
			if (children != null) {
				Iterator it = children.iterator();
				while (it.hasNext()) {
					PresentationObject obj = (PresentationObject) it.next();
					for (int i = 0; i < templateObjInstID.length; i++) {
						if (obj.getICObjectInstanceID() == templateObjInstID[i]) {
							boolean ok = changeInstanceId(obj, currentXMLPage, true);
							if (!ok) {
								//								System.out.println("addElementToPage - changeInstanceId failed");
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
	public boolean addElementToPage(IBPage ibPage, int templateObjInstID) {
		int[] ids = new int[1];
		ids[0] = templateObjInstID;
		return addElementToPage(ibPage, ids);
	}
	private boolean changeInstanceId(PresentationObject obj, IBXMLPage xmlpage, boolean copyPermissions) {
		//		System.out.println("changeInstanceId begins");
		//		System.out.println("obj.change = " + obj.getChangeInstanceIDOnInheritance());
		//		System.out.println("obj.getId = " + obj.getICObjectID());
		//		System.out.println("obj.getObjectInstanceId = " + obj.getICObjectInstanceID());
		if (obj.getChangeInstanceIDOnInheritance()) {
			int object_id = obj.getICObjectID();
			int ic_instance_id = obj.getICObjectInstanceID();
			ICObjectInstance instance = null;
			try {
				ICObjectInstance inst = obj.getICObjectInstance();
				instance = ((com.idega.core.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).createLegacy();
				instance.setICObjectID(object_id);
				if (inst != null) {
					instance.setParentInstanceID(inst.getID());
				}
				instance.setIBPageByKey(xmlpage.getKey());
				instance.insert();
				if (copyPermissions) {
					AccessControl.copyObjectInstancePermissions(Integer.toString(ic_instance_id), Integer.toString(instance.getID()));
				}
			}
			catch (SQLException e) {
				//				System.out.println("changeInstanceId - exception");
				e.printStackTrace();
				return false;
			}
			if (obj instanceof IWBlock) {
				boolean ok = ((IWBlock) obj).copyBlock(instance.getID());
				if (!ok) {
					//					System.out.println("changeInstanceId - copyBlock failed");
					return false;
				}
			}
			XMLElement element = new XMLElement(XMLConstants.CHANGE_IC_INSTANCE_ID);
			XMLAttribute from = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_FROM, Integer.toString(ic_instance_id));
			XMLAttribute to = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_TO, Integer.toString(instance.getID()));
			element.setAttribute(from);
			element.setAttribute(to);
			XMLWriter.addNewElement(xmlpage, -1, element);
		}
		//		System.out.println("changeInstanceId ends");
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
			IBPage page = ((com.idega.builder.data.IBPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(pageId));
			boolean okToDelete = true;
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
						IBPage child = (IBPage) it.next();
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
			IBPage ibpage = getIBPageHome().findByPrimaryKey(new Integer(pageId));
			if (!ibpage.isPage()) {
				throw new Exception("Method only implemented for regular pages not templates");
			}
			IBPage parent = (IBPage) ibpage.getParentNode();
			IBPage newParent = getIBPageHome().findByPrimaryKey(new Integer(newParentPageId));
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
			IBPage ibpage = getIBPageHome().findByPrimaryKey(new Integer(pageId));
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
			IBPage ibpage = getIBPageHome().findByPrimaryKey(Integer.parseInt(pageId));
			IBPage parent = (IBPage) ibpage.getParentNode();
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
			return true;
		}
	}
	/**
	 *
	 */
	private void deleteAllChildren(IBPage page, Map tree, int userId) throws java.sql.SQLException {
		Iterator it = page.getChildren();
		if (it != null) {
			while (it.hasNext()) {
				IBPage child = (IBPage) it.next();
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
		com.idega.builder.data.IBDomain domain = BuilderLogic.getInstance().getCurrentDomain(iwc);
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
				coll = ((com.idega.builder.data.IBStartPagesHome) com.idega.data.IDOLookup.getHome(com.idega.builder.data.IBStartPages.class)).findAllPagesByDomain(((Integer) domain.getPrimaryKeyValue()).intValue());
			else
				coll = ((com.idega.builder.data.IBStartPagesHome) com.idega.data.IDOLookup.getHome(com.idega.builder.data.IBStartPages.class)).findAllTemplatesByDomain(((Integer) domain.getPrimaryKeyValue()).intValue());
			java.util.Iterator it = coll.iterator();
			while (it.hasNext()) {
				com.idega.builder.data.IBStartPages startPage = (com.idega.builder.data.IBStartPages) it.next();
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
	protected IBPageHome getIBPageHome() {
		try {
			return (IBPageHome) IDOLookup.getHome(IBPage.class);
		}
		catch (Exception e) {
			throw new IDORuntimeException(e);
		}
	}
}