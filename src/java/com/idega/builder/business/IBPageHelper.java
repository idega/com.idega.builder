/*
 * $Id: IBPageHelper.java,v 1.1 2002/01/11 12:33:12 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

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

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBPageHelper {
  /**
   *
   */
  public static int createNewPage(String pageId, String name, String type, String templateId) {
    IBPage ibPage = new IBPage();
    if (name == null)
      name = "Untitled";
    ibPage.setName(name);
    ICFile file = new ICFile();
    ibPage.setFile(file);

    if (type.equals("1")) {
      ibPage.setType(IBPage.PAGE);
    }
    else if (type.equals("2")) {
      ibPage.setType(IBPage.TEMPLATE);
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
      IBPage ibPageParent = new IBPage(Integer.parseInt(pageId));
      ibPageParent.addChild(ibPage);
    }
    catch(SQLException e) {
      return(-1);
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
            return(-1);
        }
      }
    }

    return(ibPage.getID());
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
}