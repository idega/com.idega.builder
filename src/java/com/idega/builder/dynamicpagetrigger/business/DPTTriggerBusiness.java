package com.idega.builder.dynamicpagetrigger.business;

import com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo;
import com.idega.builder.dynamicpagetrigger.data.PageLink;
import com.idega.builder.data.IBPage;
import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectInstance;
import com.idega.data.EntityFinder;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLPage;
import com.idega.presentation.Page;
import com.idega.presentation.text.Link;
import com.idega.presentation.IWContext;
import com.idega.core.accesscontrol.data.PermissionGroup;
import com.idega.presentation.PresentationObject;
import com.idega.builder.business.XMLConstants;
import com.idega.builder.business.XMLWriter;
import com.idega.block.IWBlock;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.data.GenericGroup;
import com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroup;
import com.idega.util.idegaTimestamp;

import com.idega.xml.XMLElement;
import com.idega.xml.XMLAttribute;

import com.idega.business.GenericEntityComparator;
import java.util.Collections;


import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;

import java.sql.SQLException;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class DPTTriggerBusiness {

  public DPTTriggerBusiness() {
  }

  public static DPTTriggerBusiness getInstance(){
    return new DPTTriggerBusiness();
  }

  public int createTriggerRule(ICObject source, int defaultTemplateId,int[] objectInstanceIds, IBPage[] templatesAllowed) throws SQLException{
    PageTriggerInfo pti = new PageTriggerInfo();

    pti.setICObject(source);
    pti.setDefaultTemplateId(defaultTemplateId);
    pti.insert();

    if(objectInstanceIds != null){
      for (int i = 0; i < objectInstanceIds.length; i++) {
        pti.addTo(ICObjectInstance.class,objectInstanceIds[i]);
      }
    }

    if(templatesAllowed != null){
      for (int i = 0; i < templatesAllowed.length; i++) {
        pti.addTo(IBPage.class,templatesAllowed[i].getID());
      }
    }

    return pti.getID();

  }

  /*
  public void deleteTriggerRule(PageTriggerInfo pti) throws SQLException{
    pti.removeFrom(IBPage.class);
    pti.removeFrom(ICObjectInstance.class);
    // delete from pageLink where pti_id = thispti_id
    pti.delete();
  }
*/

  public void addTemplateToRule(PageTriggerInfo pti, int ibPageId) throws SQLException{
    pti.addTo(IBPage.class,ibPageId);
  }

  public void addTemplateToRule(IBPage ibp, int ptiId) throws SQLException{
    ibp.addTo(PageTriggerInfo.class,ptiId);
  }

  public void addRuleToInstance(PageTriggerInfo pti, int objectInstanceId) throws SQLException{
    pti.addTo(ICObjectInstance.class,objectInstanceId);
  }

  public void addRuleToInstance(ICObjectInstance icoi, int icoiID) throws SQLException{
    icoi.addTo(PageTriggerInfo.class,icoiID);
  }




  public void removeTemplateFromRule(PageTriggerInfo pti, int ibPageId) throws SQLException{
    pti.removeFrom(IBPage.class,ibPageId);
  }

  public void removeTemplateFromRule(IBPage ibp, int ptiId) throws SQLException{
    ibp.removeFrom(PageTriggerInfo.class,ptiId);
  }

  public void removeRuleFromInstance(PageTriggerInfo pti, int objectInstanceId) throws SQLException{
    pti.removeFrom(ICObjectInstance.class,objectInstanceId);
  }

  public void removeRuleFromInstance(ICObjectInstance icoi, int icoiID) throws SQLException{
    icoi.removeFrom(PageTriggerInfo.class,icoiID);
  }


  public PageLink createPageLink(IWContext iwc, PageTriggerInfo pti, String referencedDataId, String defaultLinkText, String standardParameters, Integer imageFileId, Integer onMouseOverImageFileId, Integer onClickImageFileId) throws SQLException {
    PageLink pl = new PageLink();

    pl.setPageTriggerInfoId(pti.getID());
    pl.setReferencedDataId(referencedDataId);
    pl.setDefaultLinkText(defaultLinkText);

    if(standardParameters != null){
      pl.setStandardParameters(standardParameters);
    }

    int pageId = createPage(iwc,pti.getDefaultTemplateId(), pti.getRootPageId(), defaultLinkText);

    pl.setPageId(pageId);


    pl.insert();

    return pl;

  }

  public List getPageLinkRecords(ICObjectInstance instance) throws SQLException{
    List listOfCopyRules = EntityFinder.findRelated(instance,((PageLink)PageLink.getStaticInstance(PageLink.class)));

    if (listOfCopyRules != null) {
      List toReturn = new Vector();
      Iterator iter = listOfCopyRules.iterator();
      while (iter.hasNext()) {
        PageTriggerInfo item = (PageTriggerInfo)iter.next();
        List linkList = EntityFinder.findAllByColumn(PageLink.getStaticInstance(PageLink.class),PageLink._COLUMNNAME_PAGE_TRIGGER_INFO_ID,item.getID());
        if(linkList != null){
          toReturn.addAll(linkList);
        }
      }
/*
      GenericEntityComparator c = new GenericEntityComparator(PageLink._COLUMNNAME_DEFAULT_LINK_TEXT);
      Collections.sort(toReturn,c);
*/
      return toReturn;

    } else {
      return null;
    }
  }

  /*
  public int triggerPage(){

  }
  */


  private int createPage(IWContext iwc, int dptTemplateId, int parentId, String name, Map createdPages) throws SQLException{
    BuilderLogic instance = BuilderLogic.getInstance();

    IBPage page = new IBPage();
    if (name == null){
      name = "Untitled";
    }
    page.setName(name);
    page.setType(IBPage.PAGE);
    page.setTemplateId(dptTemplateId);

    try {
      page.insert();
      IBPage ibPageParent = new IBPage(parentId);
      ibPageParent.addChild(page);
    }
    catch(SQLException e) {
      return(-1);
    }

    copyPagePermissions(Integer.toString(dptTemplateId), Integer.toString(page.getID()));


    createdPages.put(Integer.toString(dptTemplateId),Integer.toString(page.getID()));

    instance.setTemplateId(Integer.toString(page.getID()),Integer.toString(dptTemplateId));
    IBXMLPage ibxmlPage =  instance.getIBXMLPage(dptTemplateId);
    ibxmlPage.addUsingTemplate(Integer.toString(page.getID()));


    IBXMLPage currentXMLPage = instance.getIBXMLPage(page.getID());
    Page current = currentXMLPage.getPopulatedPage();
    List children = current.getAllContainedObjectsRecursive();

    if (children != null) {
      Iterator it = children.iterator();
      while (it.hasNext()) {
        PresentationObject obj = (PresentationObject)it.next();
        boolean ok = changeInstanceId(obj,currentXMLPage,true);
        if(!ok){
          return(-1);
        }
      }
    }

    if(children != null){
      Iterator iter = children.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        if(!(item instanceof Link)){
          iter.remove();
        }else{
          Link link = (Link)item;
          if(link.getDPTTemplateId() == 0){
            iter.remove();
          }
        }
      }
      String pageIDString = Integer.toString(page.getID());
      iter = children.iterator();
      while(iter.hasNext()){
        Link item = (Link)iter.next();

        int templateId = item.getDPTTemplateId();
        String createdPage = (String)createdPages.get(Integer.toString(templateId));
        if(createdPage == null){
          String subpageName = item.getName();
          if(subpageName == null){
            subpageName = "Untitled";
          }
          int newID = this.createPage(iwc,templateId, page.getID(), subpageName,createdPages);
          instance.changeLinkPageId(item,pageIDString,Integer.toString(newID));
        } else {
          instance.changeLinkPageId(item,pageIDString,createdPage);
        }
      }
    }

    return page.getID();
  }


  private int createPage(IWContext iwc, int dptTemplateId, int parentId, String name) throws SQLException{
    return createPage(iwc, dptTemplateId, parentId, name, new Hashtable());
  }


  public static void copyInstencePermissions( String oldInstanceID, String newInstanceID) throws SQLException{
    AccessControl.copyObjectInstancePermissions(oldInstanceID,newInstanceID);
    //
    //
    //
    /**
     * getTemplateGroups (linked to copyRule)
     * copy group
     * add templateGroup to new group
     * set group to have same permission as templateGroup for new module id
     */
    //
    //
    //
  }

  public static void copyPagePermissions( String oldPageID, String newPageID) throws SQLException{
    AccessControl.copyPagePermissions(oldPageID,newPageID);
    //
    //
    //
    /**
     * getTemplateGroups (linked to copyRule)
     * copy group
     * add templateGroup to new group
     * set group to have same permission as templateGroup for new module id
     */
    //
    //
    //
  }



  /**
   *
   */
  private static boolean changeInstanceId(PresentationObject obj, IBXMLPage xmlpage, boolean copyPermissions) {
    if (obj.getChangeInstanceIDOnInheritance()) {
      int object_id = obj.getICObjectID();
      int ic_instance_id = obj.getICObjectInstanceID();
      ICObjectInstance instance = null;

      try {
        instance = new ICObjectInstance();
        instance.setICObjectID(object_id);
        instance.insert();
        if(copyPermissions){
          copyInstencePermissions(Integer.toString(ic_instance_id),Integer.toString(instance.getID()));
        }
      }
      catch(SQLException e) {
        //System.err.println("DPTTriggerBusiness: "+e.getMessage());
        //e.printStackTrace();
        return(false);
      }

      if(obj instanceof IWBlock){
        boolean ok = ((IWBlock)obj).copyBlock(instance.getID());
        if (!ok){
          return(false);
        }
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


  public static List getDPTPermissionGroups(PageTriggerInfo pti) throws SQLException{
    return EntityFinder.findRelated(pti, GenericGroup.getStaticInstance());
  }

  public static void createDPTPermissionGroup(PageTriggerInfo pti, String name, String description) throws SQLException {
    DPTPermissionGroup newGroup = new DPTPermissionGroup();
    newGroup.setName(name);
    newGroup.setDescription(description);

    newGroup.insert();


    pti.addTo(newGroup);

  }


  public boolean invalidatePageLink(PageLink l, int userId){
    try {
      l.setDeleted(true);
      l.setDeletedBy(userId);
      l.setDeletedWhen(idegaTimestamp.getTimestampRightNow());
      l.update();


      // invalidatePage

      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }


}