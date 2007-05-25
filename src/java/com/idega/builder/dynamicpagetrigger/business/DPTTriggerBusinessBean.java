package com.idega.builder.dynamicpagetrigger.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPageFinder;
import com.idega.builder.business.IBPageHelper;
import com.idega.builder.business.IBXMLConstants;
import com.idega.builder.business.IBXMLPage;
import com.idega.builder.business.PageTreeNode;
import com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroup;
import com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroupHome;
import com.idega.builder.dynamicpagetrigger.data.PageLink;
import com.idega.builder.dynamicpagetrigger.data.PageLinkHome;
import com.idega.builder.dynamicpagetrigger.data.PageTriggerInfo;
import com.idega.builder.dynamicpagetrigger.util.DPTCrawlable;
import com.idega.builder.dynamicpagetrigger.util.DPTCrawlableContainer;
import com.idega.builder.dynamicpagetrigger.util.KeyAndValue;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.data.EntityFinder;
import com.idega.data.GenericEntity;
import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.Group;
import com.idega.util.IWTimestamp;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public class DPTTriggerBusinessBean extends IBOServiceBean implements DPTTriggerBusiness{


  public static DPTTriggerBusiness getInstance(IWApplicationContext iwac) throws IBOLookupException{
    return (DPTTriggerBusiness)IBOLookup.getServiceInstance(iwac,DPTTriggerBusiness.class);
  }

  public int createTriggerRule(ICObject source, int defaultTemplateId,int rootPageId, int[] objectInstanceIds, ICPage[] templatesAllowed) throws SQLException{
    PageTriggerInfo pti = ((com.idega.builder.dynamicpagetrigger.data.PageTriggerInfoHome)com.idega.data.IDOLookup.getHomeLegacy(PageTriggerInfo.class)).createLegacy();

    pti.setICObject(source);
    pti.setDefaultTemplateId(defaultTemplateId);
    pti.setRootPageId(rootPageId);
    pti.insert();

    if(objectInstanceIds != null){
      for (int i = 0; i < objectInstanceIds.length; i++) {
        pti.addTo(ICObjectInstance.class,objectInstanceIds[i]);
      }
    }

    if(templatesAllowed != null){
      for (int i = 0; i < templatesAllowed.length; i++) {
        pti.addTo(ICPage.class,templatesAllowed[i].getID());
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
    pti.addTo(ICPage.class,ibPageId);
  }

  public void addTemplateToRule(ICPage ibp, int ptiId) throws SQLException{
    ibp.addTo(PageTriggerInfo.class,ptiId);
  }

  public void addRuleToInstance(PageTriggerInfo pti, int objectInstanceId) throws SQLException{
    pti.addTo(ICObjectInstance.class,objectInstanceId);
  }

  public void addRuleToInstance(ICObjectInstance icoi, int icoiID) throws SQLException{
    icoi.addTo(PageTriggerInfo.class,icoiID);
  }




  public void removeTemplateFromRule(PageTriggerInfo pti, int ibPageId) throws SQLException{
    pti.removeFrom(ICPage.class,ibPageId);
  }

  public void removeTemplateFromRule(ICPage ibp, int ptiId) throws SQLException{
    ibp.removeFrom(PageTriggerInfo.class,ptiId);
  }

  public void removeRuleFromInstance(PageTriggerInfo pti, int objectInstanceId) throws SQLException{
    pti.removeFrom(ICObjectInstance.class,objectInstanceId);
  }

  public void removeRuleFromInstance(ICObjectInstance icoi, int icoiID) throws SQLException{
    icoi.removeFrom(PageTriggerInfo.class,icoiID);
  }
  
//  public PageLink createPageLink(IWContext iwc, PageTriggerInfo pti, String referencedDataId, String defaultLinkText, String standardParameters, Integer imageFileId, Integer onMouseOverImageFileId, Integer onClickImageFileId) throws SQLException {
//  	
//  }
  
  /**
   * 
   * Creates new page tree using the information in pti, the PageTriggerInfo object,
   * but creating a new DPTPermission group to be some kind of owner for the tree, that is
   * users that are put into that group will get permissions for this new pagetree as set for
   * for the template object.  The user running this method is added automaticly to this group.
   * @param iwc
   * @param pti
   * @param referencedDataId
   * @param defaultLinkText
   * @param ownerGroup
   * @param standardParameters - depricated
   * @param imageFileId - depricated
   * @param onMouseOverImageFileId - depricated
   * @param onClickImageFileId     - depricated
   * @return
   * @throws Exception
   */
  public PageLink createPageLink(IWContext iwc, PageTriggerInfo pti, String referencedDataId, String defaultLinkText, String standardParameters, Integer imageFileId, Integer onMouseOverImageFileId, Integer onClickImageFileId) throws Exception{
    DPTPermissionGroup prGroup = ((DPTPermissionGroupHome)IDOLookup.getHome(DPTPermissionGroup.class)).create();
    prGroup.setName(defaultLinkText);
    prGroup.setDescription("This group is created for permission handling of a generated pagetree");
    prGroup.store();
    
    prGroup.addGroup(iwc.getCurrentUser());
    
    return createPageLink(iwc,pti,referencedDataId,defaultLinkText,prGroup,standardParameters,imageFileId,onMouseOverImageFileId,onClickImageFileId);
  }

 

  
  /**
   * @see #createPageLink(IWContext, PageTriggerInfo, String, String, String, Integer, Integer, Integer)
   * The difference between this and #createPageLink(IWContext, PageTriggerInfo, String, String, String, Integer, Integer, Integer) 
   * is that here you can set an existing Group as the owner, but when using this one has to be sure that the group type of this group is
   * cashed by the Login, otherwise permissions will NOT inherit from the template.
   * 
   * This is mainly created to avoid problems while synchronizing between eGolf and Felix
   */
  public PageLink createPageLink(IWContext iwc, PageTriggerInfo pti, String referencedDataId, String defaultLinkText, Group ownerGroup, String standardParameters, Integer imageFileId, Integer onMouseOverImageFileId, Integer onClickImageFileId) throws Exception {
    if(ownerGroup==null) {
    		return createPageLink(iwc,pti,referencedDataId,defaultLinkText,standardParameters,imageFileId,onMouseOverImageFileId,onClickImageFileId);
    }
  	
  	PageLink pl = ((com.idega.builder.dynamicpagetrigger.data.PageLinkHome)com.idega.data.IDOLookup.getHomeLegacy(PageLink.class)).createLegacy();

    pl.setPageTriggerInfoId(pti.getID());
    pl.setReferencedDataId(referencedDataId);
    pl.setDefaultLinkText(defaultLinkText);

    if(standardParameters != null){
      pl.setStandardParameters(standardParameters);
    }

    int pageId = createPage(iwc,pti.getDefaultTemplateId(), pti.getRootPageId(), defaultLinkText);
    if(pageId == -1){
      return (null);
    }
    pl.setPageId(pageId);
    

    pl.setGroup(ownerGroup);
    

    pl.store();

    return pl;

  }

  public List getPageLinkRecords(ICObjectInstance instance) throws SQLException{
    List listOfCopyRules = EntityFinder.findRelated(instance,(GenericEntity.getStaticInstance(PageLink.class)));

    if (listOfCopyRules != null) {
      List toReturn = new Vector();
      Iterator iter = listOfCopyRules.iterator();
      while (iter.hasNext()) {
        PageTriggerInfo item = (PageTriggerInfo)iter.next();
        List linkList = EntityFinder.findAllByColumn(GenericEntity.getStaticInstance(PageLink.class),com.idega.builder.dynamicpagetrigger.data.PageLinkBMPBean._COLUMNNAME_PAGE_TRIGGER_INFO_ID,item.getID());
        if(linkList != null){
          toReturn.addAll(linkList);
        }
      }
/*
      IDOLegacyEntityComparator c = new IDOLegacyEntityComparator(com.idega.builder.dynamicpagetrigger.data.PageLinkBMPBean._COLUMNNAME_DEFAULT_LINK_TEXT);
      Collections.sort(toReturn,c);
*/
      return toReturn;

    } 
    return null;
  }

  /*
  public int triggerPage(){

  }
  */


  private int createPage(IWContext iwc, int dptTemplateId, int parentId, String name, int rootPageID) throws SQLException, RemoteException{
    BuilderLogic instance = BuilderLogic.getInstance();
    DPTCopySession cSession = (DPTCopySession)IBOLookup.getSessionInstance(iwc,DPTCopySession.class);
    int id = createPageCollectingDPTCrowlable(iwc,dptTemplateId,parentId,name,rootPageID);

      while(cSession.hasNextCollectedDPTCrawlable()){
      	KeyAndValue kv = cSession.nextCollectedDPTCrawlable();
      	String pageIDString = (String)kv.getKey();
      	Object oItem = kv.getValue();
      	if (oItem instanceof DPTCrawlable) {
	        DPTCrawlable item = (DPTCrawlable)oItem;
	
	        int templateId = item.getLinkedDPTTemplateID();
	        String createdPage = (String)cSession.getNewValue(ICPage.class,Integer.toString(templateId));
	        if(createdPage == null){
	          String subpageName = item.getLinkedDPTPageName(iwc);
	          if(subpageName == null){
	            subpageName = "Untitled";
	          }
	          int newID = this.createPageCollectingDPTCrowlable(iwc,templateId, id, subpageName,((rootPageID!=-1)?rootPageID:id));
	          if(newID == -1){
	            return (-1);
	          }
	          instance.changeDPTCrawlableLinkedPageId(((PresentationObject)item).getICObjectInstanceID(),pageIDString,String.valueOf(newID));
	        } else {
	          instance.changeDPTCrawlableLinkedPageId(((PresentationObject)item).getICObjectInstanceID(),pageIDString,createdPage);
	        }
      	} else if (oItem instanceof DPTCrawlableContainer){
      		DPTCrawlableContainer itemC = (DPTCrawlableContainer)oItem;
      		Collection coll = itemC.getDPTCrawlables();
      		int iRootID = itemC.getRootId();
      		String rootIdString = "";
      		if (coll != null && !coll.isEmpty()) {
      			Iterator iter = coll.iterator();
      			while (iter.hasNext()) {
      				DPTCrawlable dpt = (DPTCrawlable) iter.next();
      		        int templateId = dpt.getLinkedDPTTemplateID();
      		        String createdPage = (String)cSession.getNewValue(ICPage.class,Integer.toString(templateId));
      		        if(createdPage == null){
      		          String subpageName = dpt.getLinkedDPTPageName(iwc);
      		          if(subpageName == null){
      		            subpageName = "Untitled";
      		          }
      		          int newID = this.createPageCollectingDPTCrowlable(iwc,templateId, id, subpageName,((rootPageID!=-1)?rootPageID:id));

          		        if (iRootID == templateId) {
          		        	rootIdString = String.valueOf(newID);
          		        }

    		          if(newID == -1){
      		            return (-1);
      		          }
      		        } else {
          		        if (iRootID == templateId) {
          		        	rootIdString = createdPage;
          		        }
      		        }
      			}
      		}
      		
  			instance.changeDPTCrawlableLinkContainerPageIds(((PresentationObject)itemC).getICObjectInstanceID(),pageIDString,rootIdString.toString());
      	}
      } 
      
    return id;
  }
  
  private int createPageCollectingDPTCrowlable(IWContext iwc, int dptTemplateId, int parentId, String name, int rootPageID) throws SQLException, RemoteException{
    BuilderLogic instance = BuilderLogic.getInstance();
    DPTCopySession cSession = (DPTCopySession)IBOLookup.getSessionInstance(iwc,DPTCopySession.class);

    Map tree = PageTreeNode.getTree(iwc);

    int id = IBPageHelper.getInstance().createNewPage(Integer.toString(parentId),name,IBPageHelper.DPT_PAGE,Integer.toString(dptTemplateId),tree,iwc);

    if(id == -1){
      return (-1);
    } else if(rootPageID==-1) {
		cSession.setRootPagePrimaryKey(new Integer(id));
    }

    try {
      ((com.idega.core.builder.data.ICPageHome)com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(id);
    }
    catch(SQLException e) {
      return (-1);
    }

    
    boolean copyPagePermission = false;
	try {
		copyPagePermission = ((DPTCopySession)IBOLookup.getSessionInstance(iwc,DPTCopySession.class)).doCopyPagePermissions();
	} catch (IBOLookupException e2) {
		e2.printStackTrace();
	} catch (RemoteException e2) {
		e2.printStackTrace();
	}
    if(copyPagePermission) {
    		copyPagePermissions(Integer.toString(dptTemplateId), Integer.toString(id));
    }


    cSession.setNewValue(ICPage.class,String.valueOf(dptTemplateId),String.valueOf(id));
    
    IBXMLPage currentXMLPage = instance.getIBXMLPage(Integer.toString(id));
	currentXMLPage.getPageRootElement().setAttribute(IBXMLConstants.DPT_ROOTPAGE_STRING,String.valueOf(((rootPageID!=-1)?rootPageID:id)));
	currentXMLPage.store();
    Page current = currentXMLPage.getPopulatedPage();
    List children = current.getChildrenRecursive();


    if(children != null){
    	  String pageIDString = String.valueOf(id);
      Iterator iter = children.iterator();
      while (iter.hasNext()) {
        Object item = iter.next();
        if((item instanceof DPTCrawlable)&&!(((DPTCrawlable)item).getLinkedDPTTemplateID() == 0)){
          cSession.collectDPTCrawlable(pageIDString,(DPTCrawlable)item);
        } else if (item instanceof DPTCrawlableContainer) {
            cSession.collectDPTCrawlableContainer(pageIDString,(DPTCrawlableContainer)item);
        }
      }
      
    }
    
    return id;
  }

  private int createPage(IWContext iwc, int dptTemplateId, int parentId, String name) throws Exception{
    DPTCopySession cSession = ((DPTCopySession)IBOLookup.getSessionInstance(iwc,DPTCopySession.class));
  	boolean sessionAlreadyStarted = cSession.isRunningSession();
    if(!sessionAlreadyStarted) {
    		cSession.startCopySession();
    }
    int pageID = createPage(iwc, dptTemplateId, parentId, name,-1);
    if(!sessionAlreadyStarted) {
    		cSession.endCopySession();
    }
    	return pageID;
  }


  public void copyInstancePermissions( String oldInstanceID, String newInstanceID) throws SQLException{
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

  
  /**
   * @deprecated not useful anymore.  Only used in eProject
   */
  public void copyPagePermissions( String oldPageID, String newPageID) throws SQLException{
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

  public List getDPTPermissionGroups(PageTriggerInfo pti) throws SQLException{
    return EntityFinder.findRelated(pti, com.idega.core.data.GenericGroupBMPBean.getStaticInstance());
  }

  public static void createDPTPermissionGroup(PageTriggerInfo pti, String name, String description) throws IDOAddRelationshipException {
    DPTPermissionGroup newGroup = ((com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroupHome)com.idega.data.IDOLookup.getHomeLegacy(DPTPermissionGroup.class)).createLegacy();
    newGroup.setName(name);
    newGroup.setDescription(description);

    newGroup.store();


    pti.setRelatedGroup(newGroup);

  }

  protected BuilderLogic getBuilderLogic(){
  	return BuilderLogic.getInstance();
  }

  public boolean invalidatePageLink(IWContext iwc, PageLink l, int userId){
    try {
      l.setDeleted(true);
      l.setDeletedBy(userId);
      l.setDeletedWhen(IWTimestamp.getTimestampRightNow());
      l.store();

      ICDomain domain = getBuilderLogic().getCurrentDomain(iwc);
      com.idega.builder.business.IBPageHelper.getInstance().deletePage(Integer.toString(l.getPageId()),true,PageTreeNode.getTree(iwc),userId, domain);

      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return false;
    }
  }


  public boolean addObjectInstancToSubPages(ICObjectInstance objectTemplate,IWUserContext iwuc){
    System.out.println("addObjectInstancToSubPages begins");
    List pages = IBPageFinder.getAllPagesExtendingTemplate(objectTemplate.getIBPageID());
    if(pages != null){
      System.out.println("addObjectInstancToSubPages - pages != null");
      Iterator iter = pages.iterator();
      int counter = 1;
      while (iter.hasNext()) {
        System.out.println("-----------");
        System.out.println("addObjectInstancToSubPages - addElementToPage : "+counter++);
        ICPage item = (ICPage)iter.next();
        IBPageHelper.getInstance().addElementToPage(item,objectTemplate.getID(),iwuc);
      }
    }else {
      System.out.println("addObjectInstancToSubPages - pages == null");
    }
    System.out.println("addObjectInstancToSubPages ends");
    return(true);
  }

  public boolean addObjectInstancToSubPages(int templateObjectInstanceID,IWUserContext iwuc) throws SQLException{
    ICObjectInstance objinst = (ICObjectInstance)com.idega.data.IDOLookup.findByPrimaryKeyLegacy(ICObjectInstance.class,templateObjectInstanceID);
    return addObjectInstancToSubPages(objinst,iwuc);
  }

  public Group getOwnerGroupFromRootPageID(int rootPageID) {
  	try {
		PageLink pl = ((PageLinkHome)IDOLookup.getHome(PageLink.class)).findByRootPageID(rootPageID);
		return pl.getGroup();
	} catch (IDOLookupException e) {
		e.printStackTrace();
	} catch (FinderException e) {
		e.printStackTrace();
	}
  	return null;
  }


}
