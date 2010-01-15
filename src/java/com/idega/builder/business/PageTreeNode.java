/*
 * $Id: PageTreeNode.java,v 1.40 2008/06/10 12:53:07 valdas Exp $
 *
 * Copyright (C) 2001-2006 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import com.idega.builder.data.IBPageName;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.data.ICPage;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.data.ICTreeNode;
import com.idega.core.localisation.business.ICLocaleBusiness;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;

/**
 * <p>
 * Data structure that holds an in memory cache of the Builder 
 * Page tree structure.
 * </p>
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>,
 * <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>, 
 * 
 * @version 1.0
 */
public class PageTreeNode implements ICTreeNode,Serializable {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -6879671702204042851L;
	private static final String CACHE_NAME = "BuilderPageTree";
	//private static final String PAGE_TREE = "ib_page_node_tree";
	//private static final String NAME_TREE = "ib_page_node_tree_names";

	private int _id = -1;
	private String _name = null;
	//protected PageTreeNode _parent = null;
	//protected List _children = null;
	private List childPageIds;
	//protected Object _extra = null;
	private int _order = -1;
	private transient IWApplicationContext applicationContext;
	private boolean _isCategory = false;
	private boolean _isHidden = false;
	//protected ICPage _page;
	private transient Map pageNames;
	private Integer parentId;

	protected PageTreeNode(int id, String name) {
		this(id, name, -1, false, false);
	}
	
	protected PageTreeNode(Integer pageId, String name) {
		this(pageId.intValue(), name, -1, false, false);
	}
	
	protected PageTreeNode(Integer pageId, String name, int order) {
		this(pageId.intValue(), name, order, false, false);
	}
	
	protected PageTreeNode(int id, String name, int order) {
		this(id, name, order, false, false);
	}

	protected PageTreeNode(Integer id, String name, boolean isCategory, boolean isHidden) {
		this(id.intValue(), name, -1, isCategory, isHidden);
	}
	
	protected PageTreeNode(int id, String name, boolean isCategory, boolean isHidden) {
		this(id, name, -1, isCategory, isHidden);
	}

	protected PageTreeNode(Integer pageId, String name, int order, boolean isCategory, boolean isHidden) {
		this(pageId.intValue(),name,order,isCategory, isHidden);
	}
	
	protected PageTreeNode(int id, String name, int order, boolean isCategory, boolean isHidden) {
		this._id = id;
		this._name = name;
		this.childPageIds = new ArrayList();
		//this._extra = null;
		this._order = order;
		this._isCategory = isCategory;
		this._isHidden = isHidden;
	}

	/**
	 *
	 */
	public PageTreeNode(int id, IWApplicationContext iwc) {
		setApplicationContext(iwc);
		Map tree = PageTreeNode.getTree(iwc);
		PageTreeNode node = (PageTreeNode) tree.get(new Integer(id));
		node = fixTreeOrder(node);
		if (node != null) {
			copyNode(node);
		}
		else {
			this._id = id;
			this.childPageIds = new Vector();
		}
	}
	
	public PageTreeNode(PageTreeNode clonedNode){
		copyNode(clonedNode);
	}
	
	/**
	 * <p>
	 * Copies all the properties from node clonedNode to 
	 * this node
	 * </p>
	 * @param clonedNode
	 */
	public void copyNode(PageTreeNode clonedNode) {
		this._id = clonedNode._id;
		this._name = clonedNode._name;
		this.parentId = clonedNode.parentId;
		this.childPageIds = clonedNode.childPageIds;
		this._order = clonedNode._order;
		this._isCategory = clonedNode._isCategory;
		this._isHidden = clonedNode._isHidden;
		
		//this._extra = node._extra;
		this.setPageNames(clonedNode.getPageNames());
	}

	/**
	 *
	 */
	public void setNodeId(int id) {
		this._id = id;
	}

	/**
	 *
	 */
	public void setNodeName(String name) {
		this._name = name;
	}

	
	protected static Map preloadAllNamesFromDatabase() {
		Map names = new HashMap();
		Collection col = TreeNodeFinder.getAllPageNames();		
		if (col != null) {
			Iterator it = col.iterator();
			while (it.hasNext()) {
				IBPageName nameEntry = (IBPageName)it.next();
				int pageId = nameEntry.getPageId();
				//int localeId = nameEntry.getLocaleId();
								
				//Locale loc = ICLocaleBusiness.getLocale(localeId);
				
				Integer locId = new Integer(pageId);
				Map localizedNames = (Map)names.get(locId);
				if (localizedNames == null) {
					localizedNames = new HashMap();
					names.put(locId,localizedNames);					
				}
				
				putLocalizeName(nameEntry, localizedNames);
			}		
		}
		
		return names;
	}
	
	protected Map loadNamesFromDatabase(int pageId) {
		Map localizedNames = new HashMap();
		Collection col = TreeNodeFinder.getAllPageNames(pageId);		
		if (col != null) {
			Iterator it = col.iterator();
			while (it.hasNext()) {
				IBPageName nameEntry = (IBPageName)it.next();
				putLocalizeName(nameEntry,localizedNames);
			}		
		}
		return localizedNames;
	}
	
	protected static void putLocalizeName(IBPageName nameEntry,Map localizedNames){
		//int pageId = nameEntry.getPageId();
		int localeId = nameEntry.getLocaleId();
						
		Locale loc = ICLocaleBusiness.getLocale(localeId);
		
		/*Integer locId = new Integer(pageId);
		Map localizedNames = (Map)names.get(locId);
		if (localizedNames == null) {
			localizedNames = new Hashtable();
			names.put(locId,localizedNames);					
		}*/
		
		StringBuffer localizedKey = new StringBuffer(loc.getLanguage());
		String country = loc.getCountry();
		if (country != null && !country.equals("")) {
			localizedKey.append("_");
			localizedKey.append(country);
		}
		
		localizedNames.put(localizedKey.toString(),nameEntry.getPageName());
	}

	protected static Map<Integer, PageTreeNode> getTreeFromDatabase() {
		List page = null;
		List template = null;
		List rel = null;
		List rel2 = null;
		Map pageNames=null;
		try {
			page = TreeNodeFinder.listOfAllPages();
			rel = TreeNodeFinder.listOfAllPageRelationships();
			template = TreeNodeFinder.listOfAllTemplates();
			rel2 = TreeNodeFinder.listOfAllTemplateRelationships();
			pageNames = preloadAllNamesFromDatabase();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		Map<Integer, PageTreeNode> tree = new Hashtable<Integer, PageTreeNode>();

		Iterator it = null;
		if (page != null) {
			it = page.iterator();
			while (it.hasNext()) {
				ICPage pages = (ICPage) it.next();
				PageTreeNode node = null;
				int order = pages.getTreeOrder();
				if (order == -1) {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName(), pages.isCategory(), pages.isHidePageInMenu());
				}
				else {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName(), order, pages.isCategory(), pages.isHidePageInMenu());
				}
				//node.setPage(pages);
				node.setPageNames((Map) pageNames.get(pages.getPrimaryKey()));
				tree.put(new Integer(node.getNodeID()), node);
			}
		}

		if (template != null) {
			it = template.iterator();
			while (it.hasNext()) {
				ICPage pages = (ICPage) it.next();
				PageTreeNode node = null;
				int order = pages.getTreeOrder();
				if (order == -1) {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName());
				}
				else {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName(), order);
				}
				//node.setPage(pages);
				node.setPageNames((Map) pageNames.get(pages.getPrimaryKey()));
				tree.put(new Integer(node.getNodeID()), node);
			}
		}

		if (rel != null) {
			it = rel.iterator();
			while (it.hasNext()) {
				Integer parentId = (Integer) it.next();
				Integer childId = (Integer) it.next();
				PageTreeNode parent = tree.get(parentId);
				PageTreeNode child = tree.get(childId);
				if (parent != null) {
					parent.addChild(child,tree);
				}

				if (child != null) {
					child.setParent(parent);
				}
			}
		}

		if (rel2 != null) {
			it = rel2.iterator();
			while (it.hasNext()) {
				Integer parentId = (Integer) it.next();
				Integer childId = (Integer) it.next();
				PageTreeNode parent = tree.get(parentId);
				PageTreeNode child = tree.get(childId);
				if (parent != null) {
					parent.addChild(child,tree);
				}

				if (child != null) {
					child.setParent(parent);
				}
			}
		}

		return tree;
	}

	/**
	 *
	 */
	public Collection getChildren() {
		return getChildren(getTree(getApplicationContext()));
	}
	
	protected Collection getChildren(Map tree){
		//return this._children;
		List pages = new ArrayList();
		Collection childIds = getChildIds();
		for (Iterator iter = childIds.iterator(); iter.hasNext();) {
			Integer childId = (Integer) iter.next();
			PageTreeNode node = (PageTreeNode) tree.get(childId);
			pages.add(node);
		}
		return pages;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getChildIds
	 * </p>
	 * @return
	 */
	protected List getChildIds() {
		if(this.childPageIds==null){
			this.childPageIds=new ArrayList();
		}
		return this.childPageIds;
	}

	/**
	 *
	 */
	public Iterator getChildrenIterator() {
	   return getChildren().iterator();
	}

	/**
	 *
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 *
	 */
	public ICTreeNode getChildAtIndex(int childIndex) {
		/**
		 * @todo fix this
		 */
		return null;
	}

	/**
	 *
	 */
	public int getChildCount() {
		return this.childPageIds.size();
	}

	/**
	 *
	 */
	public int getIndex(ICTreeNode node) {
		return 0;
	}

	/**
	 *
	 */
	public ICTreeNode getParentNode() {
		PageTreeNode parent = getTree(getApplicationContext()).get(getParentId());
		return parent;
	}
	
	/*public ICPage getPage() {
		return this._page;
	}
	
	public void setPage(ICPage page) {
		this._page = page;
	}*/

	/**
	 *
	 */
	public boolean isLeaf() {
		int children = getChildCount();
		if (children > 0) {
		  return false;
		}
		else {
		  return true;
		}
		//return true;
	}

	/**
	 * Returns the node name for this node
	 */
	public String getNodeName() {
		return this._name;
	}

	/**
	 * Returns the Localized node name for this node
	 */
	public String getNodeName(Locale locale){
		return getLocalizedNodeName(locale);
	}
	
	public String getLocalizedNodeName(IWContext iwc) {
		Locale curr = iwc.getCurrentLocale();
		return getLocalizedNodeName(curr);
	}
	
	public String getLocalizedNodeName(Locale locale) {
		Map pageNames = getPageNames();
		if (pageNames == null||pageNames.isEmpty()) {
			return getNodeName();
		}
	
		StringBuffer localeString = new StringBuffer(locale.getLanguage());
		String country = locale.getCountry();
		if (country != null && !country.equals("")) {
			localeString.append("_");
			localeString.append(country);
		}
			
		String localizedName = (String)pageNames.get(localeString.toString());
		if (localizedName != null && !localizedName.equals("")) {
			return localizedName;
		}
		
		return getNodeName();
	}	
	
	public void setLocalizedNodeName(String locale, String name, IWContext iwc) {
		/*Hashtable names = (Hashtable)iwc.getApplicationAttribute(NAME_TREE);
		if (names == null) {
			names = new Hashtable();
			iwc.setApplicationAttribute(NAME_TREE,names);
		}
		
		Integer nodeId = new Integer(getNodeID());
		Hashtable pageNames = (Hashtable)names.get(nodeId);
		if (pageNames == null) {
			pageNames = new Hashtable();
			names.put(nodeId,pageNames);
		}*/
		Map pageNames = getPageNames();
		pageNames.put(locale,name);
	}

	public int getPageId(){
		return this._id;
	}
	
	/**
	 *
	 */
	public int getNodeID() {
		return getPageId();
	}

	/**
	 *
	 */
	public int getSiblingCount() {
		return (0);
	}

	/**
	 *
	 */
	public boolean removeChild(PageTreeNode child) {
		Integer childId = new Integer(child.getId());
		/*int index = this._children.indexOf(child);
		if (index != -1) {
			this._children.remove(index);
			return true;
		}*/
		if(this.childPageIds.contains(childId)){
			this.childPageIds.remove(childId);
			return true;
		}
		return (false);
	}

	public boolean addChild(PageTreeNode child) {
		return addChild(child,null);
	}
	
	/**
	 *
	 */
	public boolean addChild(PageTreeNode child, Map tree) {
		if (child == null) {
			return false;
		}
		
		child.setParent(this);
		Integer childId = new Integer(child.getId());
		List childPageIds = getChildIds();
		if (childPageIds.contains(childId)) {
			int index = childPageIds.indexOf(childId);
			childPageIds.add(index, childId);
		}
		else {
			if (childPageIds.isEmpty()) {
				childPageIds.add(childId);
			}
			else { //Check where in the tree this node should be (from the tree_order field)
				if (child._order < 0) {
					childPageIds.add(childId);
				}
				else {
					
					List childNodeList;
					if(tree==null){
						 childNodeList = (List)getChildren();
					}
					else{
						 childNodeList = (List)getChildren(tree);
					}
					
					ListIterator it = childNodeList.listIterator();//(new LinkedList(getChildren())).listIterator();

					while (it.hasNext()) {
						PageTreeNode node = (PageTreeNode) it.next();
						if (node._order == -1 || node._order > child._order) {
							int i = it.previousIndex();
							childPageIds.add(i, childId);
							return true;
						}
					}

					childPageIds.add(childId);
				}
			}
		}

		return true;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method setParent
	 * </p>
	 * @param node
	 */
	private void setParent(PageTreeNode node) {
		if(node!=null){
			this.parentId=new Integer(node.getId());
		}
	}

	/*
	public void setExtraInfo(Object extra) {
		this._extra = extra;
	}
	public Object getExtraInfo() {
		return this._extra;
	}
	*/

	/**
	 * Gets the tree and preloads it and stores in cache
	 */
	public static Map<Integer, PageTreeNode> getTree(IWApplicationContext iwc) {
		return getTree(iwc,true);
	}
	
	/**
	 * Gets the tree and preloads it if you set the boolean loadIfEmpty to true
	 */
	public static Map<Integer, PageTreeNode> getTree(IWApplicationContext iwc, boolean loadIfEmpty) {
		Map<Integer, PageTreeNode> tree = null;
		try {
			tree = getCacheManager(iwc).getCache(getCacheName(),10000,true,true);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (tree == null) {
			getTreeFromDatabase(tree, iwc);
		}
		else if (tree.isEmpty() && loadIfEmpty) {
			getTreeFromDatabase(tree, iwc);
		}

		return tree;
	}
	
	private static Map<Integer, PageTreeNode> getTreeFromDatabase(Map<Integer, PageTreeNode> tree, IWApplicationContext iwac) {
		synchronized(iwac.getIWMainApplication()) {
			Map<Integer, PageTreeNode> dbTree = getTreeFromDatabase();
			if (dbTree == null) {
				return tree;
			}
			
			if (tree == null) {
				tree = new Hashtable<Integer, PageTreeNode>();
			}
			
			Map.Entry<Integer, PageTreeNode> entry = null;
			for (Iterator<Map.Entry<Integer, PageTreeNode>> it = dbTree.entrySet().iterator(); it.hasNext();) {
				entry = it.next();
				tree.put(entry.getKey(), entry.getValue());
			}
		}
		
		return tree;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCacheName
	 * </p>
	 * @return
	 */
	private static String getCacheName() {
		return CACHE_NAME;
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getCacheManager
	 * </p>
	 * @param iwc
	 * @return
	 */
	private static IWCacheManager2 getCacheManager(IWApplicationContext iwc) {
		return IWCacheManager2.getInstance(iwc.getIWMainApplication());
	}

	/**
	 * Clears the tree from cache
	 * @param iwc
	 */
	public static void clearTree(IWApplicationContext iwc){
		getTree(iwc,false).clear();
		//iwc.removeApplicationAttribute(PageTreeNode.PAGE_TREE);
		//iwc.removeApplicationAttribute(PageTreeNode.NAME_TREE);
	}
	
	/**
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PageTreeNode) {
			PageTreeNode node = (PageTreeNode) obj;
			if (node._id == this._id) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	protected IWApplicationContext getApplicationContext(){
		if(this.applicationContext==null){
			try{
				//Workaround solution if iwac is not set normally
				this.applicationContext = IWContext.getInstance().getApplicationContext();
			}
			catch(Exception e){
				System.err.println("PageTreeNode.getIWApplicationContext() : Tried to get IWApplicationContext from runtime but failed : "+e.getMessage());
				this.applicationContext=IWMainApplication.getDefaultIWApplicationContext();
			}
		}
		return this.applicationContext;
	}
	/**
	 * @return
	 */
	public boolean isCategory() {
		return this._isCategory;
	}
	
	public boolean isHiddenInMenu() {
		return this._isHidden;
	}
	
	public String getId(){
		return Integer.toString(getNodeID());
	}

	/**
	 * @return the pageNames
	 */
	public Map getPageNames() {
		if (this.pageNames==null) {
			this.pageNames=loadNamesFromDatabase(getPageId());
		}
		return this.pageNames;
	}

	
	public void setPageNames(Map pageNames){
		this.pageNames=pageNames;
	}

	
	/**
	 * @param applicationContext the applicationContext to set
	 */
	protected void setApplicationContext(IWApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	
	/**
	 * @return the parentId
	 */
	public Integer getParentId() {
		return this.parentId;
	}

	
	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public void setOrder(int order){
		this._order = order;
	}
	
	public int getOrder (){
		return this._order;
	}
	
	public PageTreeNode fixTreeOrder(PageTreeNode node){
		if (node == null)
			return null;
		if (node.getChildCount() == 0)
			return node;
		List<PageTreeNode> sortedNodes = new ArrayList<PageTreeNode>();
		List<PageTreeNode> unsortedNodes = new ArrayList<PageTreeNode>(node.getChildren());
		List<PageTreeNode> nodesLeft = new ArrayList<PageTreeNode>();

		List sortedNodesIds = new ArrayList();
		
		try {
			for(int i = 0; i < unsortedNodes.size(); i++){
				sortedNodes.add(null);
			}

			for (int i = 0; i < unsortedNodes.size(); i++) {
				PageTreeNode childNode = unsortedNodes.get(i);
				if ((childNode.getOrder() > 0) && (childNode.getOrder() <= sortedNodes.size())){
//			if ((childNode.getOrder() > 0)){
					if (sortedNodes.get(childNode.getOrder() - 1) == null){				
						sortedNodes.set(childNode.getOrder() - 1, childNode);
					}
					else{
						nodesLeft.add(childNode);
						unsortedNodes.set(i, null);		
					}
				}
				else{
					nodesLeft.add(childNode);
					unsortedNodes.set(i, null);		
				}
			}
			int nodesLeftIndex = 0;
			for (int i = 0; i < sortedNodes.size(); i++) {
				PageTreeNode childNode = null;
				if(sortedNodes.get(i) == null){
					childNode = nodesLeft.get(nodesLeftIndex);
					childNode.setOrder(i+1);
					sortedNodes.set(i, childNode);
					nodesLeftIndex++;
					
					BuilderService bservice = null;
					try {
						bservice = BuilderServiceFactory.getBuilderService(this.applicationContext);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ICPage page = bservice.getICPage(childNode.getId());
					if (page != null) {
						page.setTreeOrder(i+1);
						page.store();
					}							
				}
					try {
						childNode = sortedNodes.get(i);
						sortedNodesIds.add(Integer.valueOf(childNode.getId()));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (sortedNodes.get(i).getChildCount() != 0)
						sortedNodes.set(i, fixTreeOrder(sortedNodes.get(i))); //fix children
				
			}
		} catch (IDOStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return node;
		}	
		node.setChildPageIds(sortedNodesIds);
		return node;
	}
	
	public void setChildPageIds(List childPageIds){
		this.childPageIds = childPageIds;
	}

	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getLocalizedNodeName(locale);
	}
	
}
