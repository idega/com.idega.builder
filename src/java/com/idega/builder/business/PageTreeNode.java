/*
 * $Id: PageTreeNode.java,v 1.1 2001/10/30 14:46:50 palli Exp $
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

  private String _treeType = null;
  private int _id = -1;
  private String _name = null;
  private int _parentId = -1;
  private List _children = null;
  private Object _extra = null;
  private Map _tree = null;

  private PageTreeNode(int id, String name, String treeType) {
    _id = id;
    _name = name;
    _parentId = -1;
    _children = null;
    _extra = null;
    _treeType = treeType;
  }

  public PageTreeNode(int id, IWContext iwc, String treeType) {
    _treeType = treeType;
    getTree(iwc);
    PageTreeNode node = (PageTreeNode)_tree.get(new Integer(id));
    if (node != null) {
      _id = node._id;
      _name = node._name;
      _parentId = node._parentId;
      _children = node._children;
      _extra = node._extra;
    }
  }

  private void getTreeFromDatabase() {
    List page = null;
    List rel = null;
    try {
      if (_treeType.equals(PAGE_TREE)) {
        page = TreeNodeFinder.listOfAllPages();
        rel = TreeNodeFinder.listOfAllPageRelationships();
      }
      else if (_treeType.equals(this.TEMPLATE_TREE)) {
        page = TreeNodeFinder.listOfAllTemplates();
        rel = TreeNodeFinder.listOfAllTemplateRelationships();
      }
    }
    catch(SQLException e) {
      e.printStackTrace();
    }

    _tree = new Hashtable();

    Iterator it = null;
    if (page != null) {
      it = page.iterator();
      while (it.hasNext()) {
        IBPage pages = (IBPage)it.next();
        PageTreeNode node = new PageTreeNode(pages.getID(),pages.getName(),_treeType);
        _tree.put(new Integer(node.getNodeID()),node);
      }
    }

    if (rel != null) {
      it = rel.iterator();
      while (it.hasNext()) {
        Integer parentId = (Integer)it.next();
        Integer childId = (Integer)it.next();
        PageTreeNode node = (PageTreeNode)_tree.get(parentId);
        if (node != null) {
          if (node._children == null)
            node._children = new Vector();
          node._children.add(childId);
        }

        node = (PageTreeNode)_tree.get(childId);
        if (node != null)
          node._parentId = parentId.intValue();
      }
    }
  }

  public Iterator getChildren() {
    List ret = null;
    if (_children != null) {
      Iterator it = _children.iterator();
      while (it.hasNext()) {
        Integer childId = (Integer)it.next();
        PageTreeNode node = (PageTreeNode)_tree.get(childId);
        if (node != null) {
          if (ret == null)
            ret = new Vector();

          ret.add(node);
        }
      }
    }

    if (ret != null)
      return(ret.iterator());
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
    if (_children == null)
      return(0);
    else
      return(_children.size());
  }

  public int getIndex(ICTreeNode node) {
    return(0);
  }

  public ICTreeNode getParentNode() {
    Integer parentId = new Integer(_parentId);
    PageTreeNode node = (PageTreeNode)_tree.get(parentId);

    return(node);
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
  public boolean removeChild(int childId) {
    if (_children != null) {
      Integer id = new Integer(childId);
      int index = _children.indexOf(id);
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
  public boolean addChild(int childId) {
    if (_children == null)
      _children = new Vector();

    Integer id = new Integer(childId);
    if (_children.contains(id))
      return(false);

    _children.add(id);

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

  private void getTree(IWContext iwc) {
    _tree = (Map)iwc.getApplicationAttribute(_treeType);

    if (_tree == null) {
      getTreeFromDatabase();
      iwc.setApplicationAttribute(_treeType,_tree);
      Iterator it = _tree.keySet().iterator();
      while (it.hasNext()) {
        PageTreeNode node = (PageTreeNode)_tree.get((Integer)it.next());
        node._tree = _tree;
      }
    }
  }
}