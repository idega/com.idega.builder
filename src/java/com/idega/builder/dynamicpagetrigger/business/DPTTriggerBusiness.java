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


  public int createPageLink(IWContext iwc, PageTriggerInfo pti, String referencedDataId, String defaultLinkText, String standardParameters, Integer imageFileId, Integer onMouseOverImageFileId, Integer onClickImageFileId) throws SQLException {
    PageLink pl = new PageLink();

    pl.setPageTriggerInfoId(pti.getID());
    pl.setReferencedDataId(referencedDataId);
    pl.setDefaultLinkText(defaultLinkText);

    if(standardParameters != null){
      pl.setStandardParameters(standardParameters);
    }

    int pageId = createPage(iwc,pti.getDefaultTemplateId(), defaultLinkText);

    pl.setPageId(pageId);


    pl.insert();

    return pl.getID();

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


  private int createPage(IWContext iwc, int dptTemplateId, String name, Map createdPages) throws SQLException{
    BuilderLogic instance = BuilderLogic.getInstance();

    IBPage page = new IBPage();
    page.setName(name);
    page.setType(IBPage.PAGE);
    page.insert();

    //instance.unlockRegion(Integer.toString(page.getID()),"-1",null);

    page.setTemplateId(dptTemplateId);
    page.update();

    createdPages.put(Integer.toString(dptTemplateId),Integer.toString(page.getID()));

    instance.setTemplateId(Integer.toString(page.getID()),Integer.toString(dptTemplateId));
    IBXMLPage ibxmlPage =  instance.getIBXMLPage(dptTemplateId);
    ibxmlPage.addUsingTemplate(Integer.toString(page.getID()));

    Page popPage = ibxmlPage.getPopulatedPage();

    List l = popPage.getAllContainedObjectsRecursive();
    if(l != null){
      Iterator iter = l.iterator();
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
      iter = l.iterator();
      while(iter.hasNext()){
        Link item = (Link)iter.next();

        int templateId = item.getDPTTemplateId();
        String createdPage = (String)createdPages.get(Integer.toString(templateId));
        if(createdPage == null){
          int newID = this.createPage(iwc,templateId, name+" subpage",createdPages);
          instance.changeLinkPageId(iwc.getApplication(),item,pageIDString,Integer.toString(newID));
        } else {
          instance.changeLinkPageId(iwc.getApplication(),item,pageIDString,createdPage);
        }
      }
    }

    return page.getID();
  }


  private int createPage(IWContext iwc, int dptTemplateId, String name) throws SQLException{
    return createPage(iwc, dptTemplateId, name, new Hashtable());
  }




}