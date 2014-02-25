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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

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
import com.idega.util.ListUtil;
import com.idega.util.datastructures.map.MapUtil;

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
public class PageTreeNode implements ICTreeNode<PageTreeNode>, Serializable {

	private static final long serialVersionUID = -6879671702204042851L;
	private static final Logger LOGGER = Logger.getLogger(PageTreeNode.class.getName());
	private static final String CACHE_NAME = "BuilderPageTree";

	private int _id = -1;
	private String _name = null;
	private List<Integer> childPageIds;
	private int _order = -1;
	private transient IWApplicationContext applicationContext;
	private boolean _isCategory = false;
	private boolean _isHidden = false;
	private transient Map<String, String> pageNames;
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
		this.childPageIds = new ArrayList<Integer>();
		this._order = order;
		this._isCategory = isCategory;
		this._isHidden = isHidden;
	}

	public PageTreeNode(int id, IWApplicationContext iwac) {
		setApplicationContext(iwac);
		Map<Integer, PageTreeNode> tree = PageTreeNode.getTree(iwac.getIWMainApplication());
		PageTreeNode node = tree.get(new Integer(id));
		if (node != null) {
			copyNode(node);
		} else {
			this._id = id;
			this.childPageIds = new ArrayList<Integer>();
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

	protected static Map<Integer, Map<String, String>> preloadAllNamesFromDatabase() {
		Map<Integer, Map<String, String>> names = new HashMap<Integer, Map<String, String>>();
		Collection<IBPageName> col = TreeNodeFinder.getAllPageNames();
		if (!ListUtil.isEmpty(col)) {
			for (IBPageName nameEntry: col) {
				int pageId = nameEntry.getPageId();
				Integer locId = new Integer(pageId);
				Map<String, String> localizedNames = names.get(locId);
				if (localizedNames == null) {
					localizedNames = new HashMap<String, String>();
					names.put(locId, localizedNames);
				}

				putLocalizeName(nameEntry, localizedNames);
			}
		}

		return names;
	}

	protected Map<String, String> loadNamesFromDatabase(int pageId) {
		Map<String, String> localizedNames = new HashMap<String, String>();
		Collection<IBPageName> col = TreeNodeFinder.getAllPageNames(pageId);
		if (!ListUtil.isEmpty(col)) {
			for (IBPageName nameEntry: col) {
				putLocalizeName(nameEntry,localizedNames);
			}
		}
		return localizedNames;
	}

	protected static void putLocalizeName(IBPageName nameEntry, Map<String, String> localizedNames){
		int localeId = nameEntry.getLocaleId();

		Locale loc = ICLocaleBusiness.getLocale(localeId);

		StringBuffer localizedKey = new StringBuffer(loc.getLanguage());
		String country = loc.getCountry();
		if (country != null && !country.equals("")) {
			localizedKey.append("_");
			localizedKey.append(country);
		}

		localizedNames.put(localizedKey.toString(), nameEntry.getPageName());
	}

	protected static Map<Integer, PageTreeNode> getTreeFromDatabase() {
		List<ICPage> page = null;
		List<ICPage> template = null;
		List<Integer> rel = null;
		List<Integer> rel2 = null;
		Map<Integer, Map<String, String>> pageNames=null;
		try {
			page = TreeNodeFinder.listOfAllPages();
			rel = TreeNodeFinder.listOfAllPageRelationships();
			template = TreeNodeFinder.listOfAllTemplates();
			rel2 = TreeNodeFinder.listOfAllTemplateRelationships();
			pageNames = preloadAllNamesFromDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Map<Integer, PageTreeNode> tree = new HashMap<Integer, PageTreeNode>();

		if (page != null) {
			for (Iterator<ICPage> pagesIter = page.iterator(); pagesIter.hasNext();) {
				ICPage pages = pagesIter.next();
				PageTreeNode node = null;
				int order = pages.getTreeOrder();
				if (order == -1) {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName(), pages.isCategory(), pages.isHidePageInMenu());
				} else {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName(), order, pages.isCategory(), pages.isHidePageInMenu());
				}
				node.setPageNames(pageNames.get(pages.getPrimaryKey()));
				tree.put(new Integer(node.getNodeID()), node);
			}
		}

		if (template != null) {
			for (Iterator<ICPage> templatesIter = template.iterator(); templatesIter.hasNext();) {
				ICPage pages = templatesIter.next();
				PageTreeNode node = null;
				int order = pages.getTreeOrder();
				if (order == -1) {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName());
				} else {
					node = new PageTreeNode((Integer)pages.getPrimaryKey(), pages.getName(), order);
				}
				node.setPageNames(pageNames.get(pages.getPrimaryKey()));
				tree.put(new Integer(node.getNodeID()), node);
			}
		}

		if (rel != null) {
			for (Iterator<Integer> relIter = rel.iterator(); relIter.hasNext();) {
				Integer parentId = relIter.next();
				Integer childId = relIter.next();
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
			for (Iterator<Integer> rel2Iter = rel2.iterator(); rel2Iter.hasNext();) {
				Integer parentId = rel2Iter.next();
				Integer childId = rel2Iter.next();
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

	@Override
	public Collection<PageTreeNode> getChildren() {
		return getChildren(getTree(getApplicationContext() == null ?
				IWMainApplication.getDefaultIWMainApplication() :
				getApplicationContext().getIWMainApplication())
		);
	}

	protected Collection<PageTreeNode> getChildren(Map<Integer, PageTreeNode> tree){
		List<PageTreeNode> pages = new ArrayList<PageTreeNode>();
		Collection<Integer> childIds = getChildIds();
		for (Iterator<Integer> iter = childIds.iterator(); iter.hasNext();) {
			Integer childId = iter.next();
			PageTreeNode node = tree.get(childId);
			pages.add(node);
		}
		return pages;
	}

	/**
	 * <p>
	 * </p>
	 * @return
	 */
	protected List<Integer> getChildIds() {
		if(this.childPageIds==null){
			this.childPageIds=new ArrayList<Integer>();
		}
		return this.childPageIds;
	}

	/**
	 *
	 */
	@Override
	public Iterator<PageTreeNode> getChildrenIterator() {
	   return getChildren().iterator();
	}

	/**
	 *
	 */
	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 *
	 */
	@Override
	public PageTreeNode getChildAtIndex(int childIndex) {
		Collection<PageTreeNode> children = getChildren();
		if (ListUtil.isEmpty(children)) {
			return null;
		}

		List<PageTreeNode> temp = new ArrayList<PageTreeNode>(children);
		return childIndex < temp.size() ? temp.get(childIndex) : null;
	}

	/**
	 *
	 */
	@Override
	public int getChildCount() {
		return this.childPageIds.size();
	}

	/**
	 *
	 */
	@Override
	public int getIndex(PageTreeNode node) {
		return 0;
	}

	/**
	 *
	 */
	@Override
	public PageTreeNode getParentNode() {
		Integer parentId = getParentId();
		if (parentId == null)
			return null;

		Map<Integer, PageTreeNode> tree = getTree(getApplicationContext() == null ?
				IWMainApplication.getDefaultIWMainApplication() :
				getApplicationContext().getIWMainApplication()
		);
		PageTreeNode parent = tree.get(parentId);
		return parent;
	}

	/**
	 *
	 */
	@Override
	public boolean isLeaf() {
		int children = getChildCount();
		if (children > 0) {
		  return false;
		}
		else {
		  return true;
		}
	}

	/**
	 * Returns the node name for this node
	 */
	@Override
	public String getNodeName() {
		return this._name;
	}

	/**
	 * Returns the Localized node name for this node
	 */
	@Override
	public String getNodeName(Locale locale){
		return getLocalizedNodeName(locale);
	}

	public String getLocalizedNodeName(IWContext iwc) {
		Locale curr = iwc.getCurrentLocale();
		return getLocalizedNodeName(curr);
	}

	public String getLocalizedNodeName(Locale locale) {
		Map<String, String> pageNames = getPageNames();
		if (pageNames == null||pageNames.isEmpty()) {
			return getNodeName();
		}

		StringBuffer localeString = new StringBuffer(locale.getLanguage());
		String country = locale.getCountry();
		if (country != null && !country.equals("")) {
			localeString.append("_");
			localeString.append(country);
		}

		String localizedName = pageNames.get(localeString.toString());
		if (localizedName != null && !localizedName.equals("")) {
			return localizedName;
		}

		return getNodeName();
	}

	public void setLocalizedNodeName(String locale, String name, IWContext iwc) {
		Map<String, String> pageNames = getPageNames();
		pageNames.put(locale, name);
	}

	public int getPageId(){
		return this._id;
	}

	/**
	 *
	 */
	@Override
	public int getNodeID() {
		return getPageId();
	}

	/**
	 *
	 */
	@Override
	public int getSiblingCount() {
		return (0);
	}

	/**
	 *
	 */
	public boolean removeChild(PageTreeNode child) {
		Integer childId = new Integer(child.getId());
		if(this.childPageIds.contains(childId)){
			this.childPageIds.remove(childId);
			return true;
		}
		return false;
	}

	public boolean addChild(PageTreeNode child) {
		return addChild(child,null);
	}

	/**
	 *
	 */
	public boolean addChild(PageTreeNode child, Map<Integer, PageTreeNode> tree) {
		if (child == null) {
			return false;
		}

		child.setParent(this);
		Integer childId = new Integer(child.getId());
		List<Integer> childPageIds = getChildIds();
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
				} else {
					Collection<PageTreeNode> childNodeList;
					if (tree == null) {
						 childNodeList = getChildren();
					} else {
						 childNodeList = getChildren(tree);
					}

					if (!ListUtil.isEmpty(childNodeList)) {
						List<PageTreeNode> temp = new ArrayList<PageTreeNode>(childNodeList);
						for (ListIterator<PageTreeNode> it = temp.listIterator(); it.hasNext();) {
							PageTreeNode node = it.next();
							if (node == null)
								continue;

							if (node._order == -1 || node._order > child._order) {
								int i = it.previousIndex();
								childPageIds.add(i, childId);
								return true;
							}
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
	 * </p>
	 * @param node
	 */
	private void setParent(PageTreeNode node) {
		if(node!=null){
			this.parentId=new Integer(node.getId());
		}
	}

	/**
	 * Gets the tree and preloads it and stores in cache
	 */
	public static Map<Integer, PageTreeNode> getTree(IWApplicationContext iwac) {
		return getTree(iwac.getIWMainApplication());

	}
	public static Map<Integer, PageTreeNode> getTree(IWMainApplication iwma) {
		return getTree(iwma, true);
	}

	/**
	 * Gets the tree and preloads it if you set the boolean loadIfEmpty to true
	 */
	public static Map<Integer, PageTreeNode> getTree(IWMainApplication iwma, boolean loadIfEmpty) {
		Map<Integer, PageTreeNode> cache = null;
		try {
			cache = IWCacheManager2.getInstance(iwma).getCache(getCacheName(), 10000, true, true);
		} catch(Exception e) {
			e.printStackTrace();
		}

		Map<Integer, PageTreeNode> copy = cache == null ? new HashMap<Integer, PageTreeNode>() : new HashMap<Integer, PageTreeNode>(cache);
		if (MapUtil.isEmpty(copy) && loadIfEmpty) {
			getTreeFromDatabase(copy, iwma);
			cache.putAll(copy);
		}

		List<Integer> toRemove = new ArrayList<Integer>();
		for (Map.Entry<Integer, PageTreeNode> page: copy.entrySet()) {
			if (page == null)
				continue;

			if (page.getValue() == null && page.getKey() != null) {
				toRemove.add(page.getKey());
			}
		}
		if (toRemove.size() > 0) {
			copy = new HashMap<Integer, PageTreeNode>();
			getTreeFromDatabase(copy, iwma);
			cache.putAll(copy);
		}

		return copy;
	}

	private static Map<Integer, PageTreeNode> getTreeFromDatabase(Map<Integer, PageTreeNode> tree, IWMainApplication iwma) {
		synchronized (iwma) {
			Map<Integer, PageTreeNode> dbTree = getTreeFromDatabase();
			if (dbTree == null) {
				return tree;
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
	 * </p>
	 * @return
	 */
	private static String getCacheName() {
		return CACHE_NAME;
	}

	/**
	 * Clears the tree from cache
	 * @param iwc
	 */
	public static void clearTree(IWApplicationContext iwac) {
		clearTree(iwac.getIWMainApplication());
	}

	public static void clearTree(IWMainApplication iwma) {
		getTree(iwma, false).clear();
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
			} else {
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

	@Override
	public String getId(){
		return Integer.toString(getNodeID());
	}

	/**
	 * @return the pageNames
	 */
	public Map<String, String> getPageNames() {
		if (this.pageNames==null) {
			this.pageNames=loadNamesFromDatabase(getPageId());
		}
		return this.pageNames;
	}


	public void setPageNames(Map<String, String> pageNames){
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

		List<PageTreeNode> unsortedNodes = new ArrayList<PageTreeNode>();
		for (Object o: node.getChildren()) {
			if (o instanceof PageTreeNode) {
				unsortedNodes.add((PageTreeNode) o);
			} else {
				LOGGER.warning("Object " + o + " is not instance of " + PageTreeNode.class);
			}
		}

		List<PageTreeNode> sortedNodes = new ArrayList<PageTreeNode>();
		List<PageTreeNode> nodesLeft = new ArrayList<PageTreeNode>();

		List<Integer> sortedNodesIds = new ArrayList<Integer>();
		try {
			for(int i = 0; i < unsortedNodes.size(); i++){
				sortedNodes.add(null);
			}

			for (int i = 0; i < unsortedNodes.size(); i++) {
				PageTreeNode childNode = unsortedNodes.get(i);
				if (childNode == null) {
					LOGGER.warning("There is null in unsorted pages collection: " + unsortedNodes);
					continue;
				}

				if ((childNode.getOrder() > 0) && (childNode.getOrder() <= sortedNodes.size())){
					if (sortedNodes.get(childNode.getOrder() - 1) == null){
						sortedNodes.set(childNode.getOrder() - 1, childNode);
					} else {
						nodesLeft.add(childNode);
						unsortedNodes.set(i, null);
					}
				} else {
					nodesLeft.add(childNode);
					unsortedNodes.set(i, null);
				}
			}

			int nodesLeftIndex = 0;
			for (int i = 0; i < sortedNodes.size(); i++) {
				PageTreeNode childNode = null;
				if (sortedNodes.get(i) == null) {
					childNode = null;

					if (nodesLeft.size() > 0 && nodesLeftIndex < nodesLeft.size()) {
						childNode = nodesLeft.get(nodesLeftIndex);
					}
					if (childNode == null) {
						continue;
					}

					childNode.setOrder(i+1);
					sortedNodes.set(i, childNode);
					nodesLeftIndex++;

					BuilderService bservice = null;
					try {
						bservice = BuilderServiceFactory.getBuilderService(this.applicationContext);
					} catch (RemoteException e) {
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
					e.printStackTrace();
				}
				if (sortedNodes.get(i).getChildCount() != 0)
					sortedNodes.set(i, fixTreeOrder(sortedNodes.get(i))); //fix children
			}
		} catch (IDOStoreException e) {
			e.printStackTrace();
			return node;
		}
		node.setChildPageIds(sortedNodesIds);
		return node;
	}

	public void setChildPageIds(List<Integer> childPageIds){
		this.childPageIds = childPageIds;
	}

	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getLocalizedNodeName(locale);
	}

	@Override
	public String toString() {
		return getId();
	}
}