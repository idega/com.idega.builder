/*
 * $Id: LibraryTreeNode.java,v 1.8 2006/05/29 18:28:24 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.core.data.ICTreeNode;
import com.idega.idegaweb.IWApplicationContext;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class LibraryTreeNode implements ICTreeNode {
  /**
   *
   */
  public LibraryTreeNode() {
  }

  /**
  *
  */
 public Collection getChildren() {
   /**@todo: Implement this com.idega.core.ICTreeNode method*/
   throw new java.lang.UnsupportedOperationException("Method getChildren() not yet implemented.");
 }
  
  /**
   *
   */
  public Iterator getChildrenIterator() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getChildrenIterator() not yet implemented.");
  }

  /**
   *
   */
  public boolean getAllowsChildren() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getAllowsChildren() not yet implemented.");
  }

  /**
   *
   */
  public ICTreeNode getChildAtIndex(int childIndex) {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getChildAtIndex() not yet implemented.");
  }

  /**
   *
   */
  public int getChildCount() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getChildCount() not yet implemented.");
  }

  /**
   *
   */
  public int getIndex(ICTreeNode node) {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getIndex() not yet implemented.");
  }

  /**
   *
   */
  public ICTreeNode getParentNode() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getParentNode() not yet implemented.");
  }

  /**
   *
   */
  public boolean isLeaf() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method isLeaf() not yet implemented.");
  }

  /**
   *
   */
  public String getNodeName() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getNodeName() not yet implemented.");
  }
  
  public String getNodeName(Locale locale) {
	return getNodeName();
  }
  
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		return getNodeName(locale);
	}

  /**
   *
   */
  public int getNodeID() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getNodeID() not yet implemented.");
  }

  /**
   *
   */
  public int getSiblingCount() {
    /**@todo: Implement this com.idega.core.ICTreeNode method*/
    throw new java.lang.UnsupportedOperationException("Method getSiblingCount() not yet implemented.");
  }
  
	public String getId(){
		 throw new java.lang.UnsupportedOperationException("Method getId() not yet implemented.");
	}
  
}
