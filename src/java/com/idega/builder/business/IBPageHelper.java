/*
 * $Id: IBPageHelper.java,v 1.5 2002/02/12 13:36:59 gummi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.business.PageTreeNode;
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICObjectInstance;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLAttribute;
import java.sql.SQLException;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageHelper {
  public static final String PAGE = IBPage.PAGE;
  public static final String TEMPLATE = IBPage.TEMPLATE;
  public static final String DRAFT = IBPage.DRAFT;
  public static final String DPT_PAGE = IBPage.DPT_PAGE;
  public static final String DPT_TEMPLATE = IBPage.DPT_TEMPLATE;

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
   * @return The new IBPage
   */
  public static IBPage createNewPage(String parentId, String name, String type, String templateId, Map tree) {
    IBPage ibPage = new IBPage();
    if (name == null)
      name = "Untitled";
    ibPage.setName(name);
    ICFile file = new ICFile();
    ibPage.setFile(file);

    if (type.equals(PAGE)) {
      ibPage.setType(IBPage.PAGE);
    }
    else if (type.equals(TEMPLATE)) {
      ibPage.setType(IBPage.TEMPLATE);
    }
    else if (type.equals(DRAFT)) {
      ibPage.setType(IBPage.DRAFT);
    }
    else if (type.equals(DPT_PAGE)) {
      ibPage.setType(IBPage.DPT_PAGE);
    }
    else if (type.equals(DPT_TEMPLATE)) {
      ibPage.setType(IBPage.DPT_TEMPLATE);
    }
    else {
      ibPage.setType(IBPage.PAGE);
    }

    int tid = -1;
    try {
      tid = Integer.parseInt(templateId);
      ibPage.setTemplateId(tid);
    }
    catch(java.lang.NumberFormatException e) {
    }

    try {
      ibPage.insert();
      IBPage ibPageParent = new IBPage(Integer.parseInt(parentId));
      ibPageParent.addChild(ibPage);
    }
    catch(SQLException e) {
      //return(-1);
      return (null);
    }

    if (tid != -1) {
      IBXMLPage currentXMLPage = BuilderLogic.getInstance().getIBXMLPage(ibPage.getID());
      Page current = currentXMLPage.getPopulatedPage();
      List children = current.getAllContainedObjectsRecursive();
      if (children != null) {
        Iterator it = children.iterator();
        while (it.hasNext()) {
          PresentationObject obj = (PresentationObject)it.next();
          boolean ok = changeInstanceId(obj,currentXMLPage);
          if (!ok)
            //return(-1);
            return (null);
        }
      }
    }

    int id = ibPage.getID();
    if (tree != null) {
      PageTreeNode node = new PageTreeNode(id,name);
      PageTreeNode parent = (PageTreeNode)tree.get(Integer.valueOf(parentId));

      if (parent != null)
        parent.addChild(node);

      tree.put(new Integer(node.getNodeID()),node);
    }

    if ((templateId != null) && (!templateId.equals(""))) {
      IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(templateId);
      xml.addUsingTemplate(Integer.toString(id));
      Page templateParent = xml.getPopulatedPage();
      if (!templateParent.isLocked()) {
        BuilderLogic.getInstance().unlockRegion(Integer.toString(id),"-1",null);
      }
    }

    //return(id);
    return (ibPage);
  }

  /**
   *
   */
  private static boolean changeInstanceId(PresentationObject obj, IBXMLPage xmlpage) {
    if (obj.getChangeInstanceIDOnInheritance()) {
      int object_id = obj.getICObjectID();
      int ic_instance_id = obj.getICObjectInstanceID();
      ICObjectInstance instance = null;

      try {
        instance = new ICObjectInstance();
        instance.setICObjectID(object_id);
        instance.insert();
      }
      catch(SQLException e) {
        return(false);
      }

      XMLElement element = new XMLElement(XMLConstants.CHANGE_IC_INSTANCE_ID);
      XMLAttribute from = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_FROM,Integer.toString(ic_instance_id));
      XMLAttribute to = new XMLAttribute(XMLConstants.IC_INSTANCE_ID_TO,Integer.toString(instance.getID()));
      element.setAttribute(from);
      element.setAttribute(to);

      XMLWriter.addNewElement(xmlpage,-1,element);
    }

    return(true);
  }

  /**
   *
   */
  public static boolean checkDeletePage(String pageId) {
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

    return(okToDelete);
  }

  /**
   *
   */
  public static boolean checkDeleteChildrenOfPage(String pageId) {
    try {
      IBPage page = new IBPage(Integer.parseInt(pageId));
      boolean okToDelete = true;

      if (page.getType().equals(IBPage.PAGE))
        return(true);
      else if (page.getType().equals(IBPage.DRAFT))
        return(true);
      else if (page.getType().equals(IBPage.DPT_PAGE))
        return(true);
      else {
        Iterator it = page.getChildren();

        if (it != null) {
          while (it.hasNext()) {
            IBPage child = (IBPage)it.next();

            IBXMLPage xml = BuilderLogic.getInstance().getIBXMLPage(child.getID());
            List map = xml.getUsingTemplate();

            if ((map != null) || (!map.isEmpty())) {
              return(false);
            }

            boolean check = true;
            if (child.getChildCount() != 0)
              check = checkDeleteChildrenOfPage(Integer.toString(child.getID()));

            if (!check)
              return(false);
          }
        }
        return(true);
      }
    }
    catch(SQLException e) {
      return(false);
    }
  }

  /**
   *
   */
  public static boolean deletePage(String pageId, boolean deleteChildren, Map tree, int userId) {
    javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
    try {
      t.begin();
      IBPage ibpage = new IBPage(Integer.parseInt(pageId));
      IBPage parent = (IBPage)ibpage.getParentNode();

      parent.removeChild(ibpage);
      ibpage.delete(userId);
      int templateId = ibpage.getTemplateId();
      if (templateId > 0) {
        BuilderLogic.getInstance().getIBXMLPage(templateId).removeUsingTemplate(pageId);
      }

      if (deleteChildren) {
        deleteAllChildren(ibpage,tree,userId);
        if (tree != null) {
          PageTreeNode parentNode = (PageTreeNode)tree.get(parent.getIDInteger());
          PageTreeNode childNode = (PageTreeNode)tree.get(ibpage.getIDInteger());
          parentNode.removeChild(childNode);
          tree.remove(ibpage.getIDInteger());
        }
      }
      else {
        parent.moveChildrenFrom(ibpage);
        if (tree != null) {
          PageTreeNode parentNode = (PageTreeNode)tree.get(parent.getIDInteger());
          PageTreeNode childNode = (PageTreeNode)tree.get(ibpage.getIDInteger());
          Iterator it = childNode.getChildren();
          if (it != null) {
            while (it.hasNext()) {
              parentNode.addChild((PageTreeNode)it.next());
            }
          }
          parentNode.removeChild(childNode);
          tree.remove(ibpage.getIDInteger());
        }
      }
    }
    catch(Exception e) {
      try {
        t.rollback();
      }
      catch(javax.transaction.SystemException ex) {
      }
      return(false);
    }
    finally {
      try {
        t.commit();
      }
      catch(Exception e) {
        try {
          t.rollback();
        }
        catch(javax.transaction.SystemException ex) {
        }

        return(false);
      }
      return(true);
    }
  }

  /**
   *
   */
  private static void deleteAllChildren(IBPage page, Map tree, int userId) throws java.sql.SQLException {
    Iterator it = page.getChildren();

    if (it != null) {
      while (it.hasNext()) {
        IBPage child = (IBPage)it.next();

        if (child.getChildCount() != 0)
          deleteAllChildren(child,tree,userId);

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
}