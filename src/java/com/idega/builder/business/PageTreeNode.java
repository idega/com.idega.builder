/*
 * $Id: PageTreeNode.java,v 1.3 2001/11/01 17:21:07 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.data.IBPage;
import com.idega.core.ICTreeNode;
import com.idega.presentation.IWContext;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.Hashtable;
import java.sql.SQLException;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class PageTreeNode implements ICTreeNode {
  public static final String PAGE_TREE = "ib_page_node_tree";
  public static final String TEMPLATE_TREE = "ib_template_node_tree";

  protected int _id = -1;
  protected String _name = null;
  protected PageTreeNode _parent = null;
  protected List _children = null;
  protected Object _extra = null;

  /**
   *
   */
  protected PageTreeNode(int id, String name) {
    _id = id;
    _name = name;
    _parent = null;
    _children = new Vector();
    _extra = null;
  }

  /**
   *
   */
  public PageTreeNode(int id, IWContext iwc, String treeType) {
    Map tree = getTree(iwc,treeType);
    PageTreeNode node = (PageTreeNode)tree.get(new Integer(id));
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
  protected Map getTreeFromDatabase(String treeType) {
    List page = null;
    List rel = null;
    try {
      if (treeType.equals(PAGE_TREE)) {
        page = TreeNodeFinder.listOfAllPages();
        rel = TreeNodeFinder.listOfAllPageRelationships();
      }
      else if (treeType.equals(TEMPLATE_TREE)) {
        page = TreeNodeFinder.listOfAllTemplates();
        rel = TreeNodeFinder.listOfAllTemplateRelationships();
      }
    }
    catch(SQLException e) {
      e.printStackTrace();
    }

    Map tree = new Hashtable();

    Iterator it = null;
    if (page != null) {
      it = page.iterator();
      while (it.hasNext()) {
        IBPage pages = (IBPage)it.next();
        PageTreeNode node = new PageTreeNode(pages.getID(),pages.getName());
        tree.put(new Integer(node.getNodeID()),node);
      }
    }

    if (rel != null) {
      it = rel.iterator();
      while (it.hasNext()) {
        Integer parentId = (Integer)it.next();
        Integer childId = (Integer)it.next();
        PageTreeNode parent = (PageTreeNode)tree.get(parentId);
        PageTreeNode child = (PageTreeNode)tree.get(childId);
        if (parent != null) {
          parent._children.add(child);
        }

        if (child != null)
          child._parent = parent;
      }
    }

    return(tree);
  }

  /**
   *
   */
  public Iterator getChildren() {
    return(_children.iterator());
  }

  /**
   *
   */
  public boolean getAllowsChildren() {
    return(true);
  }

  /**
   *
   */
  public ICTreeNode getChildAtIndex(int childIndex) {
  /**
   * @todo fix this
   */
    return(null);
  }

  /**
   *
   */
  public int getChildCount() {
System.out.println("Getting child count for node " + _id);
    return(_children.size());
  }

  /**
   *
   */
  public int getIndex(ICTreeNode node) {
    return(0);
  }

  /**
   *
   */
  public ICTreeNode getParentNode() {
    return(_parent);
  }

  /**
   *
   */
  public boolean isLeaf() {
    int children = getChildCount();
    if (children > 0) {
      return(false);
    }
    else {
      return(true);
    }
  }

  /**
   *
   */
  public String getNodeName() {
    return(_name);
  }

  /**
   *
   */
  public int getNodeID() {
    return(_id);
  }

  /**
   *
   */
  public int getSiblingCount() {
    return(0);
  }

  /**
   *
   */
  public boolean removeChild(PageTreeNode child) {
    int index = _children.indexOf(child);
    if (index != -1) {
      _children.remove(index);
      return(true);
    }

    return(false);
  }

  /**
   *
   */
  public boolean addChild(PageTreeNode child) {
    if (_children.contains(child)) {
      int index = _children.indexOf(child);
      _children.add(index,child);
    }
    else
      _children.add(child);

    child._parent = this;

    return(true);
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
    return(_extra);
  }

  /**
   *
   */
  protected Map getTree(IWContext iwc, String treeType) {
    Map tree = (Map)iwc.getApplicationAttribute(treeType);

    if (tree == null) {
      tree = getTreeFromDatabase(treeType);
      iwc.setApplicationAttribute(treeType,tree);
    }

    return(tree);
  }

  /**
   *
   */
  public boolean equals(Object obj) {
    if (obj instanceof PageTreeNode) {
      PageTreeNode node = (PageTreeNode)obj;
      if (node._id == _id)
        return(true);
      else
        return(false);
    }
    else
      return(false);
  }
}