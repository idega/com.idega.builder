/*
 * $Id: BuilderLogic.java,v 1.176 2005/07/13 13:39:33 laddi Exp $ Copyright
 * (C) 2001 Idega hf. All Rights Reserved. This software is the proprietary
 * information of Idega hf. Use is subject to license terms.
 */
package com.idega.builder.business;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;
import javax.ejb.FinderException;
import com.idega.builder.dynamicpagetrigger.util.DPTCrawlable;
import com.idega.builder.presentation.IBAddModuleWindow;
import com.idega.builder.presentation.IBAddRegionLabelWindow;
import com.idega.builder.presentation.IBCopyModuleWindow;
import com.idega.builder.presentation.IBCutModuleWindow;
import com.idega.builder.presentation.IBDeleteModuleWindow;
import com.idega.builder.presentation.IBLockRegionWindow;
import com.idega.builder.presentation.IBObjectControl;
import com.idega.builder.presentation.IBPasteModuleWindow;
import com.idega.builder.presentation.IBPermissionWindow;
import com.idega.builder.presentation.IBPropertiesWindow;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.data.GenericGroup;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.event.EventLogic;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.presentation.HtmlPage;
import com.idega.presentation.HtmlPageRegion;
import com.idega.presentation.IFrameContainer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLElement;

/**
 * <p>
 * This class is the main "buisiness logic" class for the Builder.<br>
 * All interface actions are sent to this class and this class manages other helper classes such as PageCacher,IBXMLReader,IBXMLWriter etc.
 * </p>
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * @version 1.0
 */
public class BuilderLogic implements Singleton {

	public static final String IC_OBJECT_INSTANCE_ID_PARAMETER = BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER;
	public static final String IB_PARENT_PARAMETER = "ib_parent_par";
	public static final String IB_LABEL_PARAMETER = "ib_label";

	public static final String IB_LIBRARY_NAME = "ib_library_name";
	public static final String IB_CONTROL_PARAMETER = "ib_control_par";
	public static final String ACTION_DELETE = "ACTION_DELETE";
	public static final String ACTION_EDIT = "ACTION_EDIT";
	public static final String ACTION_ADD = "ACTION_ADD";
	public static final String ACTION_MOVE = "ACTION_MOVE";
	public static final String ACTION_LOCK_REGION = "ACTION_LOCK";
	public static final String ACTION_UNLOCK_REGION = "ACTION_UNLOCK";
	public static final String ACTION_PERMISSION = "ACTION_PERMISSION";
	public static final String ACTION_LABEL = "ACTION_LABEL";
	public static final String ACTION_COPY = "ACTION_COPY";
	public static final String ACTION_PASTE = "ACTION_PASTE";
	public static final String ACTION_PASTE_ABOVE = "ACTION_PASTE_ABOVE";
	public static final String ACTION_LIBRARY = "ACTION_LIBRARY";
	public static final String IW_BUNDLE_IDENTIFIER = "com.idega.builder";
	/**
	 *This is the key that holds the page in the builder session
	 **/
	private static final String SESSION_PAGE_KEY = "ib_page_id";
	
	public static final String SESSION_OBJECT_STATE = BuilderConstants.SESSION_OBJECT_STATE;
	public static final String PRM_HISTORY_ID = BuilderConstants.PRM_HISTORY_ID;
	public static final String IMAGE_ID_SESSION_ADDRESS = "ib_image_id";
	public static final String IMAGE_IC_OBJECT_INSTANCE_SESSION_ADDRESS = "ic_object_id_image";
	
	private static final String IB_APPLICATION_RUNNING_SESSION = "ib_application_running";
	//private static final String DEFAULT_PAGE = "1";
	public static final String CLIPBOARD = "user_clipboard";
	
	private static Instantiator instantiator = new Instantiator() { public Object getInstance() { return new BuilderLogic();}};

	public String PAGE_FORMAT_IBXML="IBXML";
	public String PAGE_FORMAT_HTML="HTML";
	public String PAGE_FORMAT_JSP_1_2="JSP_1_2";
	
	private String[] pageFormats = {PAGE_FORMAT_IBXML,PAGE_FORMAT_HTML,PAGE_FORMAT_JSP_1_2};
	
	private BuilderLogic() {
		// empty
	}

	public static BuilderLogic getInstance() {
		return (BuilderLogic) SingletonRepository.getRepository().getInstance(BuilderLogic.class, instantiator);
	}
	
	public static void unload()	{
		SingletonRepository.getRepository().unloadInstance(BuilderLogic.class);
	}

	public boolean updatePage(int id) {
		String theID = Integer.toString(id);
		CachedBuilderPage xml = getPageCacher().getCachedBuilderPage(theID);
		xml.store();
		getPageCacher().flagPageInvalid(theID);
		return (true);
	}

	public CachedBuilderPage getCachedBuilderPage(String key) {
		return getPageCacher().getCachedBuilderPage(key);
	}

	public IBXMLPage getIBXMLPage(String pageKey) {
		return getPageCacher().getIBXML(pageKey);
	}

	
	public Page getPage(String pageKey, IWContext iwc) {
		return getPageCacher().getComponentBasedPage(pageKey).getPage(iwc);
	}
	
	/*
	public Page getPage(int id, boolean builderview, IWContext iwc) {
		try {
			boolean permissionview = false;
			if (iwc.isParameterSet("ic_pm") && iwc.isSuperAdmin()) {
				permissionview = true;
			}
			Page page = getPageCacher().getPage(Integer.toString(id), iwc);
			if (builderview && iwc.hasEditPermission(page)) {
				return (BuilderLogic.getInstance().getBuilderTransformed(Integer.toString(id), page, iwc));
			}
			else if (permissionview) {
				int groupId = -1906;
				String bla = iwc.getParameter("ic_pm");
				if (bla != null) {
					try {
						groupId = Integer.parseInt(bla);
					}
					catch (NumberFormatException ex) {
					}
				}
				page = getPageCacher().getPage(Integer.toString(id));
				return (BuilderLogic.getInstance().getPermissionTransformed(groupId, Integer.toString(id), page, iwc));
			}
			else {
				return (page);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			Page theReturn = new Page();
			theReturn.add("Page invalid");
			return (theReturn);
		}
	}*/

	/**
	 *  	 *
	 */
	public Page getBuilderTransformed(String pageKey, Page page, IWContext iwc) {

		//Begin with transforming the objects on a normal Page object (constructed from IBXML)
		List list = page.getChildren();
		if (list != null) {
			ListIterator iter = list.listIterator();
			PresentationObjectContainer parent = page;
			while (iter.hasNext()) {
				int index = iter.nextIndex();
				PresentationObject item = (PresentationObject) iter.next();
				transformObject(page,pageKey, item, index, parent, "-1", iwc);
			}
		}
		

		XMLElement pasted = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		boolean clipboardEmpty = true;
		if (pasted != null) {
			clipboardEmpty = false;
		}
		//"-1" is identified as the top page object (parent)
		if (page.getIsExtendingTemplate()) {
			if (!page.isLocked()) {
				page.add(getAddIcon(Integer.toString(-1), iwc, null));
				if (!clipboardEmpty)
					page.add(getPasteIcon(Integer.toString(-1), iwc));
				//page.add(layer);
			}
			if(page instanceof HtmlPage){
				HtmlPage hPage = (HtmlPage)page;
				Set regions = hPage.getRegionIds();
				for (Iterator iter = regions.iterator(); iter.hasNext();) {
					String regionKey = (String) iter.next();
					hPage.add(getAddIcon(regionKey, iwc, regionKey),regionKey);
				}
			}
		}
		else {
			boolean mayAddButtonsInPage=true;
			CachedBuilderPage iPage = this.getCachedBuilderPage(pageKey);
			if(iPage.getPageFormat().equals("HTML")){
				mayAddButtonsInPage=false;
				if(page instanceof HtmlPage){
					HtmlPage hPage = (HtmlPage)page;
					Set regions = hPage.getRegionIds();
					for (Iterator iter = regions.iterator(); iter.hasNext();) {
						String regionKey = (String) iter.next();
						Text regionText = new Text(regionKey);
						regionText.setFontColor("red");
						hPage.add(regionText,regionKey);
					}
				}
			}
			if(mayAddButtonsInPage){
				page.add(getAddIcon(Integer.toString(-1), iwc, null));
			}
			if ((!clipboardEmpty)&&mayAddButtonsInPage)
				page.add(getPasteIcon(Integer.toString(-1), iwc));
			if (page.getIsTemplate())
				if (page.isLocked())
					page.add(getLockedIcon(Integer.toString(-1), iwc, null));
				else
					page.add(getUnlockedIcon(Integer.toString(-1), iwc));
			//page.add(layer);
		}
		return (page);
	}

	public Page getPermissionTransformed(int groupId, String pageKey, Page page, IWContext iwc) {
		List groupIds = new Vector();
		groupIds.add(Integer.toString(groupId));
		try {
			List groups = AccessControl.getPermissionGroups(((com.idega.core.data.GenericGroupHome) com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
			if (groups != null) {
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
					com.idega.core.data.GenericGroup item = (GenericGroup) iter.next();
					groupIds.add(Integer.toString(item.getID()));
				}
			}
		}
		catch (Exception ex) {
		}

		List list = page.getChildren();
		if (list != null) {
			ListIterator iter = list.listIterator();
			while (iter.hasNext()) {
				int index = iter.nextIndex();
				Object item = iter.next();
				if (item instanceof PresentationObject) {
					filterForPermission(groupIds, (PresentationObject) item, page, index, iwc);
				}
			}
		}
		return page;
	}

	private void filterForPermission(List groupIds, PresentationObject obj, PresentationObjectContainer parentObject, int index, IWContext iwc) {
		if (!iwc.hasViewPermission(groupIds, obj)) {
			System.err.println(obj + ": removed");
			parentObject.getChildren().remove(index);
			parentObject.getChildren().add(index, PresentationObject.NULL_CLONE_OBJECT);
		}
		else if (obj.isContainer()) {
			if (obj instanceof Table) {
				Table tab = (Table) obj;
				int cols = tab.getColumns();
				int rows = tab.getRows();
				for (int x = 1; x <= cols; x++) {
					for (int y = 1; y <= rows; y++) {
						PresentationObjectContainer moc = tab.containerAt(x, y);
						if (moc != null) {
							List l = moc.getChildren();
							if (l != null) {
								ListIterator iterT = l.listIterator();
								while (iterT.hasNext()) {
									int index2 = iterT.nextIndex();
									Object itemT = iterT.next();
									if (itemT instanceof PresentationObject) {
										filterForPermission(groupIds, (PresentationObject) itemT, moc, index2, iwc);
									}
								}
							}
						}
					}
				}
			}
			else {
				List list = ((PresentationObjectContainer) obj).getChildren();
				if (list != null) {
					ListIterator iter = list.listIterator();
					while (iter.hasNext()) {
						int index2 = iter.nextIndex();
						PresentationObject item = (PresentationObject) iter.next();
						filterForPermission(groupIds, item, (PresentationObjectContainer) obj, index2, iwc);
					}
				}
			}
		}
	}

	private void processImageSet(String pageKey, int ICObjectInstanceID, int imageID, IWMainApplication iwma) {
		setProperty(pageKey, ICObjectInstanceID, "image_id", Integer.toString(imageID), iwma);
	}

	private void transformObject(Page currentPage,String pageKey, PresentationObject obj, int index, PresentationObjectContainer parent, String parentKey, IWContext iwc) {
		XMLElement pasted = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		boolean clipboardEmpty = true;
		if (pasted != null)
			clipboardEmpty = false;
		if (obj instanceof Image) {
			Image imageObj = (Image) obj;
			boolean useBuilderObjectControl = obj.getUseBuilderObjectControl();
			int ICObjectIntanceID = imageObj.getICObjectInstanceID();
			String sessionID = "ic_" + ICObjectIntanceID;
			String session_image_id = (String) iwc.getSessionAttribute(sessionID);
			if (session_image_id != null) {
				int image_id = Integer.parseInt(session_image_id);
				/**
				 * @todo Change this so that id is done in a more appropriate place,
				 * i.e. set the image_id permanently on the image
				 */
				processImageSet(pageKey, ICObjectIntanceID, image_id, iwc.getIWMainApplication());
				iwc.removeSessionAttribute(sessionID);
				imageObj.setImageID(image_id);
			}
			IBImageInserter inserter = null;
			inserter = (new IBClassesFactory()).createImageInserterImpl();
			inserter.setHasUseBox(false);
			String width = imageObj.getWidth();
			String height = imageObj.getHeight();
			inserter.limitImageWidth(false);

			if (width != null) {
				inserter.setWidth(width);
			}
			if (height != null) {
				inserter.setHeight(height);
			}

			int image_id = imageObj.getImageID(iwc);
			if (image_id != -1) {
				inserter.setImageId(image_id);
			}
			inserter.setImSessionImageName(sessionID);
			inserter.setWindowToReload(true);
			//inserter.maintainSessionParameter();
			obj = (PresentationObject) inserter;
			obj.setICObjectInstanceID(ICObjectIntanceID);
			obj.setUseBuilderObjectControl(useBuilderObjectControl);
		}
		//else if (obj instanceof Block) {
		//}
		//else if (obj instanceof PresentationObjectContainer) {
		else if (obj.isContainer()) {
			if (obj instanceof Table) {
				Table tab = (Table) obj;
				//tab.setBorder(1);
				tab.setStyleAttribute("border","1px dotted grey");
				int cols = tab.getColumns();
				int rows = tab.getRows();
				for (int x = 1; x <= cols; x++) {
					for (int y = 1; y <= rows; y++) {
						PresentationObjectContainer moc = tab.containerAt(x, y);
						String newParentKey = obj.getICObjectInstanceID() + "." + x + "." + y;
						if (moc != null) {
							transformObject(currentPage,pageKey, moc, -1, tab, newParentKey, iwc);
						}
						//Page currentPage = PageCacher.getPage(getCurrentIBPage(iwc), iwc);
						if (currentPage.getIsExtendingTemplate()) {
							if (tab.getBelongsToParent()) {
								if (!tab.isLocked(x, y)) {
									tab.add(getAddIcon(newParentKey, iwc, tab.getLabel(x, y)), x, y);
									if (!clipboardEmpty)
										tab.add(getPasteIcon(newParentKey, iwc), x, y);
								}
							}
							else {
								tab.add(getAddIcon(newParentKey, iwc, tab.getLabel(x, y)), x, y);
								if (!clipboardEmpty)
									tab.add(getPasteIcon(newParentKey, iwc), x, y);
								if (currentPage.getIsTemplate()) {
									tab.add(getLabelIcon(newParentKey, iwc, tab.getLabel(x, y)), x, y);
									if (tab.isLocked(x, y))
										tab.add(getLockedIcon(newParentKey, iwc, tab.getLabel(x, y)), x, y);
									else
										tab.add(getUnlockedIcon(newParentKey, iwc), x, y);
								}
							}
						}
						else {
							tab.add(getAddIcon(newParentKey, iwc, tab.getLabel(x, y)), x, y);
							if (!clipboardEmpty)
								tab.add(getPasteIcon(newParentKey, iwc), x, y);
							if (currentPage.getIsTemplate()) {
								tab.add(getLabelIcon(newParentKey, iwc, tab.getLabel(x, y)), x, y);
								if (tab.isLocked(x, y))
									tab.add(getLockedIcon(newParentKey, iwc, tab.getLabel(x, y)), x, y);
								else
									tab.add(getUnlockedIcon(newParentKey, iwc), x, y);
							}
						}
					}
				}
			}
			else {
				List list = ((PresentationObjectContainer) obj).getChildren();
				if (list != null) {
					ListIterator iter = list.listIterator();
					while (iter.hasNext()) {
						int index2 = iter.nextIndex();
						PresentationObject item = (PresentationObject) iter.next();
						/**
						 * If parent is Table
						 */
						if (index == -1) {
							transformObject(currentPage,pageKey, item, index2, (PresentationObjectContainer) obj, parentKey, iwc);
						}
						else {
							String newParentKey = null;
							//Ugly Hack of handling the regions inside HTML template based pages. This needs to change.
							//TODO: Remove this instanceof case, to make that possible then the getICObjectInstanceID 
							//		method needs to be changed to return String
							if(obj instanceof HtmlPageRegion){
								HtmlPageRegion region = (HtmlPageRegion)obj;
								//newParentKey is normally an ICObjectInstanceId or -1 to mark the top page 
								//but here we make a workaround.
								newParentKey = region.getRegionId();
							}
							else{
								newParentKey = Integer.toString(obj.getICObjectInstanceID());
							}
							transformObject(currentPage,pageKey, item, index2, (PresentationObjectContainer) obj, newParentKey, iwc);
						}
					}
				}
				if (index != -1) {
					//Page curr = getPageCacher().getPage(getCurrentIBPage(iwc), iwc);
					Page curr = getPageCacher().getComponentBasedPage(getCurrentIBPage(iwc)).getNewPage(iwc);
					if (curr.getIsExtendingTemplate()) {
						if (obj.getBelongsToParent()) {
							if (!((PresentationObjectContainer) obj).isLocked()) {
								((PresentationObjectContainer) obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()), iwc, ((PresentationObjectContainer) obj).getLabel()));
								if (!clipboardEmpty)
									((PresentationObjectContainer) obj).add(getPasteIcon(Integer.toString(obj.getICObjectInstanceID()), iwc));
							}
						}
						else {
							((PresentationObjectContainer) obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()), iwc, ((PresentationObjectContainer) obj).getLabel()));
							if (!clipboardEmpty)
								((PresentationObjectContainer) obj).add(getPasteIcon(Integer.toString(obj.getICObjectInstanceID()), iwc));
							if (curr.getIsTemplate()) {
								((PresentationObjectContainer) obj).add(getLabelIcon(Integer.toString(obj.getICObjectInstanceID()), iwc, ((PresentationObjectContainer) obj).getLabel()));
								if (((PresentationObjectContainer) obj).isLocked())
									((PresentationObjectContainer) obj).add(getLockedIcon(Integer.toString(obj.getICObjectInstanceID()), iwc, ((PresentationObjectContainer) obj).getLabel()));
								else
									((PresentationObjectContainer) obj).add(getUnlockedIcon(Integer.toString(obj.getICObjectInstanceID()), iwc));
							}
						}
					}
					else {
						((PresentationObjectContainer) obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()), iwc, ((PresentationObjectContainer) obj).getLabel()));
						if (!clipboardEmpty)
							((PresentationObjectContainer) obj).add(getPasteIcon(Integer.toString(obj.getICObjectInstanceID()), iwc));
						if (curr.getIsTemplate()) {
							((PresentationObjectContainer) obj).add(getLabelIcon(Integer.toString(obj.getICObjectInstanceID()), iwc, ((PresentationObjectContainer) obj).getLabel()));
							if (((PresentationObjectContainer) obj).isLocked())
								((PresentationObjectContainer) obj).add(getLockedIcon(Integer.toString(obj.getICObjectInstanceID()), iwc, ((PresentationObjectContainer) obj).getLabel()));
							else
								((PresentationObjectContainer) obj).add(getUnlockedIcon(Integer.toString(obj.getICObjectInstanceID()), iwc));
						}
					}
				}
			}
		}
		if (obj.getUseBuilderObjectControl()) {
			if (index != -1) {
				//parent.remove(obj);
				//parent.add(new IBObjectControl(obj,parent,parentKey,iwc,index));
				parent.set(index, new IBObjectControl(obj, parent, parentKey, iwc, index));
			}
		}
	}

	public ICPage getCurrentIBPageEntity(IWContext iwc) throws Exception {
		String sID = getCurrentIBPage(iwc);
		//if(sID!=null){
		return ((com.idega.core.builder.data.ICPageHome) com.idega.data.IDOLookup.getHomeLegacy(ICPage.class)).findByPrimaryKeyLegacy(Integer.parseInt(sID));
		//}
	}

	/**
	 * Returns the current IBPageID that the user has requested
	 */
	public int getCurrentIBPageID(IWContext iwc) {
		String theReturn = getCurrentIBPage(iwc);
		return Integer.parseInt(theReturn);
	}
	
	
	public String getPageKeyByURIAndServerName(String requestURI,String serverName){
		try{
			return getPageKeyByURI(requestURI);
		}
		catch(NumberFormatException nfe){
			//nothing printed out here
		}
		catch(Exception e){
			e.printStackTrace();
		}
		String theReturn = String.valueOf(getStartPageIdByServerName(serverName));
		if (theReturn == null) {
			return Integer.toString(getCurrentDomain().getStartPageID());
		}
		else{
			return theReturn;
		}
		
	}
	
	
	/**
	 * Tries to find the uri in the cache of builder pages
	 * @param requestURI
	 * @return
	 */
	public String getPageKeyByURICached(String pageUri){
		Iterator iter = getPageCacher().getPageCacheMap().values().iterator();
		while(iter.hasNext()){
			CachedBuilderPage page = (CachedBuilderPage)iter.next();
			String cachedPageUri = page.getURI();
			if(cachedPageUri!=null){
				if(cachedPageUri.equals(pageUri)){
					return page.getPageKey();
				}
			}
		}
		return null;
	}
	
	public String getPageKeyByURI(String requestURI){
		//String requestURI = iwc.getRequestURI();
		//if (requestURI.startsWith(iwc.getIWMainApplication().getBuilderPagePrefixURI())) {
			int indexOfPage = requestURI.indexOf("/pages/");
			//if (indexOfPage != -1) {
				//boolean pageISNumber = true;
				String iPageId = null;
				//try {
					String uriWithoutPages = requestURI;
					//this string will be tried to convert to a number
					String uriForNumberParse = requestURI;
					if(indexOfPage!=-1){
						uriWithoutPages = requestURI.substring(indexOfPage + 6);
						uriForNumberParse = requestURI.substring(indexOfPage + 7);
					}
					
					int lastSlash = uriForNumberParse.lastIndexOf(StringHandler.SLASH);
					if (lastSlash == -1) {
						iPageId = uriForNumberParse;
					}
					else {
						iPageId = uriForNumberParse.substring(0, lastSlash);
					}
					try{
						Integer.parseInt(iPageId);
						return iPageId;
					}
					catch(NumberFormatException nfe){
						//the string is not a number:
						
						//try to find the page in the cache first:
						String pageKey = getPageKeyByURICached(uriWithoutPages);
						if(pageKey!=null){
							return pageKey;
						}
						
						//if it isn't found in the cache try the database:
						try {
							ICPageHome pageHome = (ICPageHome)IDOLookup.getHome(ICPage.class);
							//TODO: change - here domainId is hardcoded to -1
							int domainId=-1;
							ICPage page = pageHome.findByUri(uriWithoutPages,domainId);
							return page.getPageKey();
						}
						catch (IDOLookupException e) {
						}
						catch(FinderException fe){
						}	
					}
				//return pageID;
				//}
				//catch (NumberFormatException e) {
				//	pageISNumber = false;
				//}
				//if (pageISNumber) {
				//	return pageID;
				//}
			//}
		//}
		throw new RuntimeException("Page Key Can not be found from URI '"+requestURI+"'");
	}
	
	
	/**
	 * Returns the key for the ICPage that the user has requested
	 */
	public String getCurrentIBPage(IWContext iwc) {
		String theReturn = null;
		String requestURI = iwc.getRequestURI();
		
		if(IWMainApplication.useNewURLScheme){
		//if (requestURI.startsWith(iwc.getIWMainApplication().getBuilderPagePrefixURI())) {
			/*int indexOfPage = requestURI.indexOf("/pages/");
			if (indexOfPage != -1) {
				boolean pageISNumber = true;
				String pageID = null;
				try {
					String subString = requestURI.substring(indexOfPage + 7);
					int lastSlash = subString.indexOf("/");
					if (lastSlash == -1) {
						pageID = subString;
					}
					else {
						pageID = subString.substring(0, lastSlash);
					}
					Integer.parseInt(pageID);
				}
				catch (NumberFormatException e) {
					pageISNumber = false;
				}
				if (pageISNumber) {
					return pageID;
				}
			}*/
			try{
				return getPageKeyByURI(requestURI);
			}
			catch(NumberFormatException nfe){
				//nothing printed out
			}
			catch(RuntimeException re){
				//nothing printed out
				//this can come from getPageKeyByURI() for old style systems with /servlet/IBMainSerlet uri
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

		// normal page check
		if (iwc.isParameterSet(BuilderConstants.IB_PAGE_PARAMETER)) {
			theReturn = iwc.getParameter(BuilderConstants.IB_PAGE_PARAMETER);
		}
		// session page check
		else if (iwc.getSessionAttribute(SESSION_PAGE_KEY) != null) {
			theReturn = (String) iwc.getSessionAttribute(SESSION_PAGE_KEY);
		}
		// otherwise use startpage
		else
			theReturn = String.valueOf(getInstance().getStartPageIdByServerName(iwc,iwc.getServerName()));
		if (theReturn == null) {
			return Integer.toString(getCurrentDomain(iwc).getStartPageID());
		}
		else
			return theReturn;
	}
	
	/**
	 * Sets the IBpage Id to use in the builder session.
	 * @param iwc
	 * @param pageKey
	 */
	public void setCurrentIBPage(IWContext iwc,String pageKey){
	    //This method only sets the session variant
	    iwc.setSessionAttribute(SESSION_PAGE_KEY,pageKey);
	}

	
	/**
	 * Sets the source and pageFormat for current page and stores to the datastore
	 * @param iwc IWContext to get the current page
	 * @param pageFormat
	 * @param stringSourceMarkup
	 */
	public boolean setPageSource(IWContext iwc,String pageFormat,String stringSourceMarkup){
		String pageKey = getCurrentIBPage(iwc);
		return setPageSource(pageKey,pageFormat,stringSourceMarkup);
	}
	
	/**
	 * Sets the source and pageFormat for the page with key pageKey and stores to the datastore
	 * @param pageKey
	 * @param pageFormat
	 * @param stringSourceMarkup
	 */
	public boolean setPageSource(String pageKey,String pageFormat,String stringSourceMarkup){
		try{
			getPageCacher().storePage(pageKey,pageFormat,stringSourceMarkup);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Gets the source code (IBXML,HTML) as a String for the current page
	 * @param iwc IWContext to get the current page
	 * @param pageFormat
	 * @param stringSourceMarkup
	 */
	public String getPageSource(IWContext iwc){
		String pageKey = getCurrentIBPage(iwc);
		return getPageSource(pageKey);
	}
	
	/**
	 * Gets the source code (IBXML,HTML) as a String for the page with key pageKey
	 * @param iwc IWContext to get the current page
	 * @param pageFormat
	 * @param stringSourceMarkup
	 */
	public String getPageSource(String pageKey){
		return getCachedBuilderPage(pageKey).toString();
	}	
	
	CachedBuilderPage getCurrentCachedBuilderPage(IWContext iwc) {
		String key = getCurrentIBPage(iwc);
		if (key != null) {
			return (getCachedBuilderPage(key));
		}
		return null;
	}
	
	IBXMLPage getCurrentIBXMLPage(IWContext iwc) {
		return (IBXMLPage)getCurrentCachedBuilderPage(iwc);
	}

	public ICDomain getCurrentDomain(){
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		return getCurrentDomain(iwac);
	}
	
	public ICDomain getCurrentDomain(IWApplicationContext iwac) {
		try {
			return iwac.getDomain();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public ICDomain getCurrentDomainByServerName(String serverName){
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		return getCurrentDomainByServerName(iwac,serverName);
	}
	
	public ICDomain getCurrentDomainByServerName(IWApplicationContext iwac,String serverName) {
		try {
			return iwac.getDomainByServerName(serverName);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Returns the real properties set for the property if the property is set
	 * with the specified keys Returns the selectedValues[] if nothing found
	 */
	public String[] getPropertyValues(IWMainApplication iwma, String pageKey, int ObjectInstanceId, String propertyName, String[] selectedValues, boolean returnSelectedValueIfNothingFound) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		return IBPropertyHandler.getInstance().getPropertyValues(iwma, xml, ObjectInstanceId, propertyName, selectedValues, returnSelectedValueIfNothingFound);
		//return XMLWriter.getPropertyValues(xml,ObjectInstanceId,propertyName);
	}

	public boolean removeProperty(IWMainApplication iwma, String pageKey, int ObjectInstanceId, String propertyName, String[] values) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (XMLWriter.removeProperty(iwma, xml, ObjectInstanceId, propertyName, values)) {
			xml.store();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns the first property if there is an array of properties set
	 */
	public String getProperty(String pageKey, int ObjectInstanceId, String propertyName) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		return XMLWriter.getProperty(xml, ObjectInstanceId, propertyName);
	}

	/**
	 * Returns true if properties changed, or error, else false
	 */
	public boolean setProperty(String pageKey, int ObjectInstanceId, String propertyName, String propertyValue, IWMainApplication iwma) {
		String[] values = {propertyValue};
		return setProperty(pageKey, ObjectInstanceId, propertyName, values, iwma);
	}

	/**
	 * Returns true if properties changed, or error, else false
	 */
	public boolean setProperty(String pageKey, int ObjectInstanceId, String propertyName, String[] propertyValues, IWMainApplication iwma) {
		try {
			IBXMLPage xml = getIBXMLPage(pageKey);
			boolean allowMultivalued = isPropertyMultivalued(propertyName, ObjectInstanceId, iwma);
			if (XMLWriter.setProperty(iwma, xml, ObjectInstanceId, propertyName, propertyValues, allowMultivalued)) {
				xml.store();
				return (true);
			}
			else {
				return (false);
			}
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return (false);
		}
	}

	/**
	 * Returns true if properties changed, or error, else false
	 */
	public boolean isPropertySet(String pageKey, int ObjectInstanceId, String propertyName, IWMainApplication iwma) {
		try {
			IBXMLPage xml = getIBXMLPage(pageKey);
			return XMLWriter.isPropertySet(iwma, xml, ObjectInstanceId, propertyName);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return (false);
		}
	}

	// add by Aron 20.sept 2001 01:49
	public boolean deleteModule(String pageKey, String parentObjectInstanceID, int ICObjectInstanceID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		try {
			PresentationObject Block = ICObjectBusiness.getInstance().getNewObjectInstance(ICObjectInstanceID);
			if (Block != null) {
				if (Block instanceof Builderaware) {
					((Builderaware) Block).deleteBlock(ICObjectInstanceID);
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		if (XMLWriter.deleteModule(xml, parentObjectInstanceID, ICObjectInstanceID)) {
			xml.store();
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *  	 *
	 */
	public boolean copyModule(IWContext iwc, String pageKey, int ICObjectInstanceID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		XMLElement element = xml.copyModule(pageKey, ICObjectInstanceID);
		if (element == null)
			return (false);
		else {
			XMLElement el = (XMLElement) element.clone();
			iwc.setSessionAttribute(CLIPBOARD, el);
		}
		return (true);
	}

	/**
	 *  	 *
	 */
	public boolean pasteModule(IWContext iwc, String pageKey, String parentID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		XMLElement element = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		if (element == null)
			return (false);
		XMLElement toPaste = (XMLElement) element.clone();
		if (XMLWriter.pasteElement(xml, pageKey, parentID, toPaste)) {
			xml.store();
			return (true);
		}
		return (false);
	}

	/**
	 *  	 *
	 */
	public boolean pasteModule(IWContext iwc, String pageKey, String parentID, String objectID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		System.out.println("pageKey = " + pageKey);
		System.out.println("parentID = " + parentID);
		System.out.println("objectID = " + objectID);
		XMLElement element = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		if (element == null)
			return (false);
		XMLElement toPaste = (XMLElement) element.clone();
		if (XMLWriter.pasteElementAbove(xml, pageKey, parentID, objectID, toPaste)) {
			xml.store();
			return (true);
		}
		return (false);
	}

	public boolean lockRegion(String pageKey, String parentObjectInstanceID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (XMLWriter.lockRegion(xml, parentObjectInstanceID)) {
			xml.store();
			if (parentObjectInstanceID.equals("-1")) {
				if (xml.getType().equals(CachedBuilderPage.TYPE_TEMPLATE)) {
					List extend = xml.getUsingTemplate();
					if (extend != null) {
						Iterator i = extend.iterator();
						while (i.hasNext())
							lockRegion((String) i.next(), parentObjectInstanceID);
					}
				}
			}
			return true;
		}
		return (false);
	}

	public boolean unlockRegion(String pageKey, String parentObjectInstanceID, String label) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (XMLWriter.unlockRegion(xml, parentObjectInstanceID)) {
			xml.store();
			if (parentObjectInstanceID.equals("-1")) {
				if (xml.getType().equals(CachedBuilderPage.TYPE_TEMPLATE)) {
					List extend = xml.getUsingTemplate();
					if (extend != null) {
						Iterator i = extend.iterator();
						while (i.hasNext()) {
							String child = (String) i.next();
							unlockRegion(child, parentObjectInstanceID, null);
						}
					}
				}
			}
			labelRegion(pageKey, parentObjectInstanceID, label);
			return true;
		}
		return (false);
	}

	/**
	 *  	 *
	 */
	public boolean addNewModule(String pageKey, String parentObjectInstanceID, int newICObjectID, String label) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (XMLWriter.addNewModule(xml, pageKey, parentObjectInstanceID, newICObjectID, label)) {
			xml.store();
			return (true);
		}
		else {
			return (false);
		}
	}

	/**
	 *  	 *
	 */
	public boolean addNewModule(String pageKey, String parentObjectInstanceID, ICObject newObjectType, String label) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (XMLWriter.addNewModule(xml, pageKey, parentObjectInstanceID, newObjectType, label)) {
			xml.store();
			return true;
		}
		else {
			return false;
		}
	}

	public Class getObjectClass(int icObjectInstanceID) {
		try {
			ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjectInstanceID);
			return instance.getObject().getObjectClass();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isPropertyMultivalued(String propertyName, int icObjecctInstanceID, IWMainApplication iwma) throws Exception {
		try {
			Class c = null;
			IWBundle iwb = null;
			if (icObjecctInstanceID == -1) {
				c = com.idega.presentation.Page.class;
				iwb = iwma.getBundle(PresentationObject.IW_BUNDLE_IDENTIFIER);
			}
			else {
				ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjecctInstanceID);
				c = instance.getObject().getObjectClass();
				iwb = instance.getObject().getBundle(iwma);
			}
			//IWPropertyList complist = iwb.getComponentList();
			IWPropertyList component = iwb.getComponentPropertyList(c.getName());
			IWPropertyList methodlist = component.getPropertyList(IBPropertyHandler.METHODS_KEY);
			if (methodlist == null)
				return (false);
			IWPropertyList method = methodlist.getPropertyList(propertyName);
			if (method == null)
				return (false);
			IWProperty prop = method.getIWProperty(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED);
			if (prop != null) {
				boolean value = prop.getBooleanValue();
				return value;
			}
			else
				return false;
		}
		catch (Exception e) {
			//e.printStackTrace(System.err);
			return false;
		}
	}

	public boolean setTemplateId(String pageKey, String id) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (XMLWriter.setAttribute(xml, "-1", XMLConstants.TEMPLATE_STRING, id)) {
			xml.store();
			return true;
		}
		return (false);
	}

	/**
	 *  	 *
	 */
	public PresentationObject getPasteIcon(String parentKey, IWContext iwc) {
		IWBundle bundle = iwc.getIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
		Image pasteImage = bundle.getImage("paste.gif", "Paste component");
		Link link = new Link(pasteImage);
		link.setWindowToOpen(IBPasteModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(IB_CONTROL_PARAMETER, ACTION_PASTE);
		link.addParameter(IB_PARENT_PARAMETER, parentKey);
		return (link);
	}

	/**
	 *  	 *
	 */
	public boolean labelRegion(String pageKey, String parentObjectInstanceID, String label) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (XMLWriter.labelRegion(xml, parentObjectInstanceID, label)) {
			xml.store();
			return true;
		}
		return (false);
	}

	
	public String getCurrentIBPageURL(IWContext iwc) {
		String sPageId = getCurrentIBPage(iwc);
		int pageId = Integer.parseInt(sPageId);
		return getIBPageURL(iwc,pageId);
	}	
	
	/**
	 *  	 *
	 */
	public String getIBPageURL(IWApplicationContext iwc, int ib_page_id) {
		String pageKey = Integer.toString(ib_page_id);
		return getIBPageURL(iwc,pageKey);
	}
		
		
		
	public String getIBPageURL(IWApplicationContext iwc, String pageKey) {
		if(IWMainApplication.useNewURLScheme){
			//String pageKey = Integer.toString(ib_page_id);
			String pageUri = getPageCacher().getCachedBuilderPage(pageKey).getPageUri();
			if(pageUri!=null){
				String returnUrl = iwc.getIWMainApplication().getBuilderPagePrefixURI()+pageUri;
				//clean out the potential double slash:
				return StringHandler.removeMultipleSlashes(returnUrl);
			}
			else{
				return iwc.getIWMainApplication().getBuilderPagePrefixURI()+pageKey+"/";
			}
		}
		else{
			StringBuffer url = new StringBuffer();
			url.append(iwc.getIWMainApplication().getBuilderPagePrefixURI());
			int ib_page_id = Integer.parseInt(pageKey);
			if (ib_page_id > 0) {
				url.append("?");
				url.append(BuilderConstants.IB_PAGE_PARAMETER);
				url.append("=");
				url.append(ib_page_id);
			}
			return url.toString();
		}
	}

	/**
	 *  	 *
	 */
	public String getIFrameContentURL(IWContext iwc, int ICObjectInstanceId) {
		String src = iwc.getIWMainApplication().getIFrameContentURI() + "?" + IC_OBJECT_INSTANCE_ID_PARAMETER + "=" + ICObjectInstanceId;
		String query = iwc.getQueryString();
		if (query != null && !query.equals("")) {
			src += ("&" + query);
		}
		return src;
	}

	/**
	 * Changes the name of the current page.
	 * 
	 * @param name
	 *          The new name for the page
	 * @param iwc
	 *          The IdegeWeb Context object
	 */
	public void changeName(String name, IWContext iwc) {
		CachedBuilderPage xml = getCurrentCachedBuilderPage(iwc);
		if (xml != null) {
			if (!xml.getName().equals(name)) {
				xml.setName(name);
				java.util.Map tree = PageTreeNode.getTree(iwc);
				if (tree != null) {
					String currentId = getCurrentIBPage(iwc);
					if (currentId != null) {
						Integer id = new Integer(currentId);
						PageTreeNode node = (PageTreeNode) tree.get(id);
						if (node != null) {
							node.setNodeName(name);
						}
					}
				}
			}
		}
	}

	/**
	 * Changes the template id for the current page.
	 * 
	 * @param newTemplateId
	 *          The new template id for the current page.
	 * @param iwc
	 *          The IdegeWeb Context object @todo make this work for templates!
	 */
	public void changeTemplateId(String newTemplateId, IWContext iwc) {
		IBXMLPage xml = getCurrentIBXMLPage(iwc);
		if (xml != null) {
			if (xml.getType().equals(CachedBuilderPage.TYPE_PAGE)) {
				//int newId = Integer.parseInt(templateId);
				String oldId = xml.getTemplateKey();
				if (!newTemplateId.equals(oldId)) {
					xml.setTemplateKey(newTemplateId);
					String currentPageId = getCurrentIBPage(iwc);
					setTemplateId(currentPageId, newTemplateId);
					//if (newId > 0)
						getIBXMLPage(newTemplateId).addPageUsingThisTemplate(currentPageId);
					//if (oldId > 0)
						getIBXMLPage(oldId).removePageAsUsingThisTemplate(currentPageId);
				}
			}
		}
	}

	/**
	 *  	 *
	 */
	public void startBuilderSession(IWUserContext iwuc) {
		iwuc.setSessionAttribute(IB_APPLICATION_RUNNING_SESSION, Boolean.TRUE);
	}

	/**
	 *  	 *
	 */
	public void endBuilderSession(IWUserContext iwuc) {
		iwuc.removeSessionAttribute(IB_APPLICATION_RUNNING_SESSION);
	}

	/**
	 *  	 *
	 */
	public boolean isBuilderApplicationRunning(IWUserContext iwuc) {
		return !(iwuc.getSessionAttribute(IB_APPLICATION_RUNNING_SESSION) == null);
	}

	/**
	 *  	 *
	 */
	public PresentationObject getIFrameContent(int ibPageId, int instanceId, IWContext iwc) {
		PresentationObject obj = EventLogic.getPopulatedObjectInstance(instanceId, iwc);
		PresentationObject iframeContent = null;
		if (obj instanceof IFrameContainer && obj != null) {
			iframeContent = ((IFrameContainer) obj).getIFrameContent();
		}
		return iframeContent;
	}

	/**
	 *  	 *
	 */
	public void changeDPTCrawlableLinkedPageId(DPTCrawlable item, int moduleId, String currentPageID, String newLinkedPageId) {
		IBXMLPage page = getIBXMLPage(currentPageID);
		XMLElement element = new XMLElement(XMLConstants.CHANGE_PAGE_LINK);
		XMLAttribute id = new XMLAttribute(XMLConstants.LINK_ID_STRING, Integer.toString(moduleId));
		XMLAttribute newPageLink = new XMLAttribute(XMLConstants.LINK_TO, newLinkedPageId);
		element.setAttribute(id);
		element.setAttribute(newPageLink);
		XMLWriter.addNewElement(page, -1, element);
		page.store();
		getPageCacher().flagPageInvalid(currentPageID);
	}

	/**
	 *  	 *
	 */
	public int getStartPageId(IWApplicationContext iwac) {
		ICDomain domain = getCurrentDomain(iwac);
		return domain.getStartPageID();
	}
	
	/**
	 *  	 *
	 */
	public String getStartPageKey(IWApplicationContext iwac) {
		int id = getStartPageId(iwac);
		return Integer.toString(id);
	}
	
	
	/**
	 *  	 *
	 */
	public int getStartPageIdByServerName(String serverName) {
		ICDomain domain = getCurrentDomainByServerName(serverName);
		return domain.getStartPageID();
	}
	
	/**
	 *  	 *
	 */
	public int getStartPageIdByServerName(IWApplicationContext iwac,String serverName) {
		ICDomain domain = getCurrentDomainByServerName(iwac,serverName);
		return domain.getStartPageID();
	}

	/**
	 *  	 *
	 */
	public String getCurrentPageHtml(IWContext iwc) {
		String ibpage = getCurrentIBPage(iwc);
		ICDomain domain = getCurrentDomain(iwc);
		StringBuffer url = new StringBuffer(domain.getURL());
		//    url.append(IWMainApplication.BUILDER_SERVLET_URL);
		//    url.append(iwc.getApplication().getBuilderServletURI());
		//    url.append("?");
		//    url.append(IB_PAGE_PARAMETER);
		//    url.append("=");
		url.append(this.getIBPageURL(iwc, Integer.parseInt(ibpage)));

		if (url.toString().indexOf("http") == -1)
			url.insert(0, "http://");

		String html = FileUtil.getStringFromURL(url.toString());
		return (html);
	}

	/**
	 * Invalidates cache for all pages and the cached page tree
	 */
	public void clearAllCachedPages() {
		System.out.println("Clearing all BuilderPageTree Cache");
		PageTreeNode.clearTree(IWMainApplication.getDefaultIWApplicationContext());
		System.out.println("Clearing all BuilderPage Cache");
		getPageCacher().flagAllPagesInvalid();
	}

	/**
	 * Invalidates all caches for a page with key pageKey
	 * @param pageKey
	 */
	public void invalidatePage(String pageKey){
		getPageCacher().flagPageInvalid(pageKey);
	}
	
	private PageCacher pageCacher;
	/**
	 * Return the singleton instance of PageCacher
	 * @return
	 */
	public PageCacher getPageCacher(){
		if(pageCacher==null){
			setPageCacher(new PageCacher());
		}		
		return pageCacher;
	}
	
	public void setPageCacher(PageCacher pageCacherInstance){
		this.pageCacher=pageCacherInstance;
	}
	
	private IBPageHelper ibPageHelper;
	/**
	 * Return the singleton instance of IBPageHelper
	 * @return
	 */
	public synchronized IBPageHelper getIBPageHelper(){
		if(ibPageHelper==null){
			setIBPageHelper(new IBPageHelper());
		}
		return ibPageHelper;
	}
	public void setIBPageHelper(IBPageHelper ibPageHelper){
		this.ibPageHelper=ibPageHelper;
	}
	
	/**
	 * Return an array of the document formats that the Builder supports.
	 * ('IBXML','HTML','JSP_1_2')
	 * @return
	 */
	public String[] getPageFormatsSupported(){
		return pageFormats;
	}
	
	/**
	 * gets the default page format:
	 * @return
	 */
	public String getDefaultPageFormat(){
		return PAGE_FORMAT_IBXML;
	}

	/**
	 *
	 */
	public PresentationObject getAddIcon(String parentKey, IWContext iwc, String label)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image addImage = bundle.getImage("add.gif", "Add new component");
		//addImage.setAttribute("style","z-index: 0;");
		Link link = new Link(addImage);
		link.setWindowToOpen(IBAddModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_ADD);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		return (link);
	}
	

	/**
	 *
	 */
	public PresentationObject getLockedIcon(String parentKey, IWContext iwc, String label)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image lockImage = bundle.getImage("las_close.gif", "Unlock region");
		Link link = new Link(lockImage);
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_UNLOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		return (link);
	}

	/**
	 *
	 */
	public PresentationObject getUnlockedIcon(String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image lockImage = bundle.getImage("las_open.gif", "Lock region");
		Link link = new Link(lockImage);
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		return (link);
	}
	
	/**
	 *
	 */
	public PresentationObject getLabelIcon(String parentKey, IWContext iwc, String label)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image labelImage = bundle.getImage("label.gif", "Put label on region");
		Link link = new Link(labelImage);
		link.setWindowToOpen(IBAddRegionLabelWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LABEL);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		return (link);
	}

	public PresentationObject getCutIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image cutImage = bundle.getImage("shared/menu/cut.gif", "Cut component");
		Link link = new Link(cutImage);
		link.setWindowToOpen(IBCutModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_COPY);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	/**
	 *
	 */
	public PresentationObject getCopyIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image copyImage = bundle.getImage("shared/menu/copy.gif", "Copy component");
		//copyImage.setAttribute("style","z-index: 0;");
		Link link = new Link(copyImage);
		link.setWindowToOpen(IBCopyModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_COPY);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return (link);
	}

	public PresentationObject getDeleteIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image deleteImage = bundle.getImage("shared/menu/delete.gif", "Delete component");
		Link link = new Link(deleteImage);
		link.setWindowToOpen(IBDeleteModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_DELETE);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	public PresentationObject getPermissionIcon(int key, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image editImage = bundle.getImage("shared/menu/permission.gif", "Set permissions");
		Link link = new Link(editImage);
		link.setWindowToOpen(IBPermissionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_PERMISSION);
		link.addParameter(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER, key);
		link.addParameter(
			IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY,
			AccessController.CATEGORY_OBJECT_INSTANCE);
		return link;
	}

	public PresentationObject getEditIcon(int key, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image editImage = bundle.getImage("shared/menu/edit.gif", "Properties");
		Link link = new Link(editImage);
		link.setWindowToOpen(IBPropertiesWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_EDIT);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	/**
	 *
	 */
	public PresentationObject getPasteAboveIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image pasteImage = bundle.getImage("shared/menu/paste.gif", "Paste above component");
		//copyImage.setAttribute("style","z-index: 0;");
		Link link = new Link(pasteImage);
		link.setWindowToOpen(IBPasteModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_PASTE_ABOVE);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return (link);
	}	
	
	
	public ViewNode getBuilderPageRootViewNode(){
		String BUILDER_PAGE_VIEW_ID="pages";
		return ViewManager.getInstance(IWMainApplication.getDefaultIWMainApplication()).getApplicationRoot().getChild(BUILDER_PAGE_VIEW_ID);
	}
	
}
