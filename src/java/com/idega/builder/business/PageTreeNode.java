/*
 * $Id: PageTreeNode.java,v 1.9 2002/08/15 10:53:35 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import com.idega.builder.data.IBPage;
import com.idega.core.ICTreeNode;
import com.idega.idegaweb.IWApplicationContext;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class PageTreeNode implements ICTreeNode {
	private static final String PAGE_TREE = "ib_page_node_tree";

	protected int _id = -1;
	protected String _name = null;
	protected PageTreeNode _parent = null;
	protected List _children = null;
	protected Object _extra = null;
	protected int _order = -1;

	protected PageTreeNode(int id, String name) {
		this(id,name,-1);
	}
	
	protected PageTreeNode(int id, String name, int order) {
		_id = id;
		_name = name;
		_parent = null;
		_children = new Vector();
		_extra = null;
		_order = order;
	}

	/**
	 *
	 */
	public PageTreeNode(int id, IWApplicationContext iwc) {
		Map tree = PageTreeNode.getTree(iwc);
		PageTreeNode node = (PageTreeNode) tree.get(new Integer(id));
		if (node != null) {
			_id = node._id;
			_name = node._name;
			_parent = node._parent;
			_children = node._children;
			_extra = node._extra;
		}
		else {
			_id = id;
			_children = new Vector();
		}
	}

	/**
	 *
	 */
	public void setNodeId(int id) {
		_id = id;
	}

	/**
	 *
	 */
	public void setNodeName(String name) {
		_name = name;
	}

	/**
	 *
	 */
	protected static Map getTreeFromDatabase() {
		List page = null;
		List template = null;
		List rel = null;
		List rel2 = null;
		try {
			page = TreeNodeFinder.listOfAllPages();
			rel = TreeNodeFinder.listOfAllPageRelationships();
			template = TreeNodeFinder.listOfAllTemplates();
			rel2 = TreeNodeFinder.listOfAllTemplateRelationships();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		Map tree = new Hashtable();

		Iterator it = null;
		if (page != null) {
			it = page.iterator();
			while (it.hasNext()) {
				IBPage pages = (IBPage) it.next();
				PageTreeNode node = null;
				int order = pages.getTreeOrder();
				if (order == -1)
					node = new PageTreeNode(pages.getID(), pages.getName());
				else
					node = new PageTreeNode(pages.getID(), pages.getName(), order);
				tree.put(new Integer(node.getNodeID()), node);
			}
		}

		if (template != null) {
			it = template.iterator();
			while (it.hasNext()) {
				IBPage pages = (IBPage) it.next();
				PageTreeNode node = null;
				int order = pages.getTreeOrder();
				if (order == -1)
					node = new PageTreeNode(pages.getID(), pages.getName());
				else
					node = new PageTreeNode(pages.getID(), pages.getName(), order);
				tree.put(new Integer(node.getNodeID()), node);
			}
		}

		if (rel != null) {
			it = rel.iterator();
			while (it.hasNext()) {
				Integer parentId = (Integer) it.next();
				Integer childId = (Integer) it.next();
				PageTreeNode parent = (PageTreeNode) tree.get(parentId);
				PageTreeNode child = (PageTreeNode) tree.get(childId);
				if (parent != null) {
//					parent._children.add(child);
					parent.addChild(child);
				}

				if (child != null)
					child._parent = parent;
			}
		}

		if (rel2 != null) {
			it = rel2.iterator();
			while (it.hasNext()) {
				Integer parentId = (Integer) it.next();
				Integer childId = (Integer) it.next();
				PageTreeNode parent = (PageTreeNode) tree.get(parentId);
				PageTreeNode child = (PageTreeNode) tree.get(childId);
				if (parent != null) {
//					parent._children.add(child);
					parent.addChild(child);
				}

				if (child != null)
					child._parent = parent;
			}
		}

		return tree;
	}

	/**
	 *
	 */
	public Iterator getChildren() {
		return _children.iterator();
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
		return _children.size();
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
		return _parent;
	}

	/**
	 *
	 */
	public boolean isLeaf() {
		/*int children = getChildCount();
		if (children > 0) {
		  return(false);
		}
		else {
		  returntrue;
		}*/
		return true;
	}

	/**
	 *
	 */
	public String getNodeName() {
		return _name;
	}

	/**
	 *
	 */
	public int getNodeID() {
		return _id;
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
		int index = _children.indexOf(child);
		if (index != -1) {
			_children.remove(index);
			return true;
		}

		return (false);
	}

	/**
	 *
	 */
	public boolean addChild(PageTreeNode child) {
		child._parent = this;

		if (_children.contains(child)) {
			int index = _children.indexOf(child);
			_children.add(index, child);
		}
		else {
			if (_children.isEmpty()) {
				_children.add(child);
			}
			else { //Check where in the tree this node should be (from the tree_order field)
				if (child._order < 0) {
					_children.add(child);
				}
				else {
					ListIterator it = (new LinkedList(_children)).listIterator();
					
					while (it.hasNext()) {
						PageTreeNode node = (PageTreeNode)it.next();
						if (node._order == -1 || node._order > child._order) {
							int i = it.previousIndex();
							_children.add(i,child);
							return true;							
						}
					}
					
					_children.add(child);	
				}				
			}
		}

		return true;
	}

	/**
	 *
	 */
	public void setExtraInfo(Object extra) {
		_extra = extra;
	}

	/**
	 *
	 */
	public Object getExtraInfo() {
		return (_extra);
	}

	/**
	 *
	 */
	public static Map getTree(IWApplicationContext iwc) {
		Map tree = (Map) iwc.getApplicationAttribute(PageTreeNode.PAGE_TREE);

		if (tree == null) {
			tree = getTreeFromDatabase();
			iwc.setApplicationAttribute(PageTreeNode.PAGE_TREE, tree);
		}

		return (tree);
	}

	/**
	 *
	 */
	public boolean equals(Object obj) {
		if (obj instanceof PageTreeNode) {
			PageTreeNode node = (PageTreeNode) obj;
			if (node._id == _id)
				return true;
			else
				return (false);
		}
		else
			return (false);
	}
}
