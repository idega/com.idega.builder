package com.idega.builder.dynamicpagetrigger.data;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJBException;
import com.idega.builder.dynamicpagetrigger.business.DPTTriggerBusiness;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.builder.data.ICDynamicPageTrigger;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.Group;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 21, 2004
 */
public class DynamicPageTrigger implements ICDynamicPageTrigger {
	
	
	private int rootPage = -1;
	
	/**
	 * The variable _currentUserHasRelationToContainingDPTPage is set as null in the clone method 
	 * because it should be set ones each time a DPT page is printed out.  It is used to speed up 
	 * accesscontroling so its only necessary to go ones through the users groups for eash page rendering
	 * to see if one of them matches the group attached to the DPT page tree (or root page).
	 */
	private Boolean currentUserHasRelationToContainingDPTPage = null;
	private Object dptOwnerGroup = null;

	
	public Boolean hasRelationTo(PresentationObject object, List[] permissionGroupLists, IWUserContext iwuc) {
		Boolean hasRelationToPage = hasCurrentUserRelationToContainingDPTPage(object);  //does not need iwuc because the value is set as null in the clone method in PresentationObject
		String templateID = object.getTemplateId();
		if(hasRelationToPage==null && templateID!=null) {							
			//Get ownergroup
			Group relatedDPTPageGroup = (Group) getDPTPageRelationGroup(object, iwuc);
			//check if user is in ownergroup
			if(relatedDPTPageGroup != null) {
				boolean isInOwnerGroup = false;
				Object pk = relatedDPTPageGroup.getPrimaryKey().toString();
				for (int i = 0; i < permissionGroupLists.length; i++) {
					List groupIDs = permissionGroupLists[i];
					for (Iterator iter = groupIDs.iterator(); iter.hasNext();) {
						Object gr = iter.next();
						isInOwnerGroup = pk.equals(gr);
						if(isInOwnerGroup) {
							break;
						}
					}
					if(isInOwnerGroup) {
						break;
					}
				}
				hasRelationToPage = new Boolean(isInOwnerGroup);
				setCurrentUserHasRelationToContainingDPTPage(object, hasRelationToPage);
			}		
		}
		return hasRelationToPage;
	}

	public Object clone() {
		DynamicPageTrigger trigger = new DynamicPageTrigger();
		trigger.currentUserHasRelationToContainingDPTPage = null;
		trigger.dptOwnerGroup = dptOwnerGroup;
		return trigger;
	}
	
	
	public void setRootPage(String id) {
		if(id != null){
			rootPage = Integer.parseInt(id);
		}else {
			rootPage = -1;
		}
	}
	
	public int getRootPage() {
		return rootPage;
	}

	 /**
	  * 
	  * @return returns null if it has not been checked and set yet. It is set in the Accesscontrol class.
	  */
	 private Boolean hasCurrentUserRelationToContainingDPTPage(PresentationObject object) {
	 	Page page = object.getParentPage();
	 	if (page != null) {
	 		return hasCurrentUserRelationToContainingDPTPage(page);
	 	}
	 	return Boolean.FALSE;
	 }


	 /**
	  * @return Returns the DPT page relation group 
	  * @see com.idega.presentation.Page#getDPTPageRelationGroup(IWUserContext)
	  */
	 private Object getDPTPageRelationGroup(PresentationObject object, IWUserContext iwuc) {
	 	Page parentPage = object.getParentPage();
	 	if(parentPage!=null) {
	 		return getDPTPageRelationGroup(parentPage, iwuc);
	 	}
	 	return null;
	 }
	 
	 private  void setCurrentUserHasRelationToContainingDPTPage(PresentationObject object, Boolean value) {
	 	Page page = object.getParentPage();
	 	if (page != null) {
		 	setCurrentUserHasRelationToContainingDPTPage(page, value);
	 	}
	 }

	private Object getDPTPageRelationGroup(Page page, IWUserContext iwuc) {
	 	DynamicPageTrigger pageDynamicPageTrigger = (DynamicPageTrigger) page.getDynamicPageTrigger();
		int rootPageId = pageDynamicPageTrigger.getRootPage();
	 	if(pageDynamicPageTrigger.dptOwnerGroup == null &&  rootPageId != -1) {
	 		try {
				Group gr = ((DPTTriggerBusiness)IBOLookup.getServiceInstance(iwuc.getApplicationContext(),DPTTriggerBusiness.class)).getOwnerGroupFromRootPageID(rootPageId);
				pageDynamicPageTrigger.dptOwnerGroup = gr;
			} catch (IBOLookupException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (EJBException e) {
				e.printStackTrace();
			}
	 	}
	 	return pageDynamicPageTrigger.dptOwnerGroup;
	 }

	 private void setCurrentUserHasRelationToContainingDPTPage(Page page, Boolean value) {
	 	DynamicPageTrigger pageDynamicPageTrigger = (DynamicPageTrigger) page.getDynamicPageTrigger();
	 	pageDynamicPageTrigger.currentUserHasRelationToContainingDPTPage = value;
	 }
	 
	 /**
	  * 
	  * @return returns null if it has not been checked and set yet. It is set in the Accesscontrol class.
	  */
	 private Boolean hasCurrentUserRelationToContainingDPTPage(Page page) {
	 	DynamicPageTrigger pageDynamicPageTrigger = (DynamicPageTrigger) page.getDynamicPageTrigger();
	 	return pageDynamicPageTrigger.currentUserHasRelationToContainingDPTPage;
	 }


}
