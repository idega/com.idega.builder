/*
 * $Id: PageTreeNode.java,v 1.2 2001/10/30 17:41:40 palli Exp $
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

//  private String _treeType = null;
  private int _id = -1;
  private String _name = null;
  private PageTreeNode _parent = null;
  private List _children = null;
  private Object _extra = null;
//  private Map _tree = null;

  private PageTreeNode(int id, String name) {
    _id = id;
    _name = name;
    _parent = null;
    _children = null;
    _extra = null;
  }

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
  }

  public void setNodeId(int id) {
    _id = id;
  }

  public void setNodeName(String name) {
    _name = name;
  }

  private Map getTreeFromDatabase(String treeType) {
    List page = null;
    List rel = null;
    try {
      if (treeType.equals(PAGE_TREE)) {
        page = TreeNodeFinder.listOfAllPages();
        rel = TreeNodeFinder.listOfAllPageRelationships();
      }
      else if (treeType.equals(this.TEMPLATE_TREE)) {
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
          if (parent._children == null)
            parent._children = new Vector();
          parent._children.add(child);
        }

        if (child != null)
          child._parent = parent;
      }
    }

    return(tree);
  }

  public Iterator getChildren() {
    if (_children != null)
      return(_children.iterator());
    else
      return(null);
  }

  public boolean getAllowsChildren() {
    return(true);
  }

  public ICTreeNode getChildAtIndex(int childIndex) {
  /**
   * @todo fix this
   */
    return(null);
  }

  public int getChildCount() {
    if (_children == null) {
      return(0);
    }
    else {
      return(_children.size());
    }
  }

  public int getIndex(ICTreeNode node) {
    return(0);
  }

  public ICTreeNode getParentNode() {
    return(_parent);
  }

  public boolean isLeaf() {
    int children = getChildCount();
    if (children > 0) {
      return(false);
    }
    else {
      return(true);
    }
  }

  public String getNodeName() {
    return(_name);
  }

  public int getNodeID() {
    return(_id);
  }

  public int getSiblingCount() {
    return(0);
  }

  /**
   *
   */
  public boolean removeChild(PageTreeNode child) {
    if (_children != null) {
      int index = _children.indexOf(child);
      if (index != -1) {
        _children.remove(index);
        return(true);
      }
    }

    return(false);
  }

  /**
   *
   */
  public boolean addChild(PageTreeNode child) {
    if (_children == null)
      _children = new Vector();

    if (_children.contains(child))
      return(false);

    _children.add(child);

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

  private Map getTree(IWContext iwc, String treeType) {
    Map tree = (Map)iwc.getApplicationAttribute(treeType);

    if (tree == null) {
      tree = getTreeFromDatabase(treeType);
      iwc.setApplicationAttribute(treeType,tree);
    }

    return(tree);
  }

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

/*  public List getChildrenList() {
    return(_children);
  }

  public void setChildrenList(List children) {
    _children = children;
  }*/
}