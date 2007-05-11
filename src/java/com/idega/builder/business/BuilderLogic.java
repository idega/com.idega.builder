/*
 * $Id: BuilderLogic.java,v 1.246 2007/05/11 13:47:23 valdas Exp $ Copyright
 * (C) 2001 Idega hf. All Rights Reserved. This software is the proprietary
 * information of Idega hf. Use is subject to license terms.
 */
package com.idega.builder.business;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.myfaces.renderkit.html.util.HtmlBufferResponseWriterWrapper;
import org.htmlcleaner.HtmlCleaner;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.idega.block.web2.business.Web2Business;
import com.idega.builder.presentation.AddModuleBlock;
import com.idega.builder.presentation.IBAddRegionLabelWindow;
import com.idega.builder.presentation.IBCopyModuleWindow;
import com.idega.builder.presentation.IBCutModuleWindow;
import com.idega.builder.presentation.IBDeleteModuleWindow;
import com.idega.builder.presentation.IBLockRegionWindow;
import com.idega.builder.presentation.IBObjectControl;
import com.idega.builder.presentation.IBPasteModuleWindow;
import com.idega.builder.presentation.IBPermissionWindow;
import com.idega.builder.presentation.IBPropertiesWindow;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.builder.business.BuilderPageException;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.data.GenericGroup;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOStoreException;
import com.idega.event.EventLogic;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWApplicationContextFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.idegaweb.block.presentation.Builderaware;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.HtmlPage;
import com.idega.presentation.HtmlPageRegion;
import com.idega.presentation.IFrameContainer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.HiddenInput;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.slide.business.IWSlideSession;
import com.idega.util.FileUtil;
import com.idega.util.StringHandler;
import com.idega.util.reflect.PropertyCache;
import com.idega.xml.XMLAttribute;
import com.idega.xml.XMLDocument;
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

	private static final String PAGES_PREFIX = "/pages/";
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
	
	private String[] pageFormats = {this.PAGE_FORMAT_IBXML,this.PAGE_FORMAT_HTML,this.PAGE_FORMAT_JSP_1_2};
	
	private volatile Web2Business web2 = null;
	
	protected BuilderLogic() {
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
//	.getICPage()
	
	public ICPage getICPage(String key) {
		return getPageCacher().getCachedBuilderPage(key).getICPage();
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

		IWBundle iwb = getBuilderBundle();
		page.addStyleSheetURL(iwb.getVirtualPathWithFileNameString("style/builder.css"));
		
		try {
//			page.addJavascriptURL(getWeb2Business(iwc).getPrototypeScriptFilePath(Web2BusinessBean.PROTOTYPE_LATEST_VERSION));			
//			page.addJavascriptURL(getWeb2Business(iwc).getBundleURIToScriptaculousLib());
//			page.addJavascriptURL(getWeb2Business(iwc).getBundleURIToBehaviourLib());
//			page.addJavascriptURL(getWeb2Business(iwc).getLightboxScriptFilePath());
//			page.addJavascriptURL(getWeb2Business(iwc).getNiftyCubeScriptFilePath());
//			
//			page.addStyleSheetURL(getWeb2Business(iwc).getLightboxStyleFilePath());
			
			page.addJavascriptURL(getWeb2Business(iwc).getBundleURIToMootoolsLib());				//	Mootools
			page.addJavascriptURL(getWeb2Business(iwc).getMoodalboxScriptFilePath(false));			//	MOOdalBox
			
			page.addStyleSheetURL(getWeb2Business(iwc).getMoodalboxStyleFilePath());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		page.addJavascriptURL("/dwr/engine.js");
		page.addJavascriptURL("/dwr/interface/BuilderEngine.js");
		
		page.addJavascriptURL(iwb.getVirtualPathWithFileNameString("javascript/builder_general.js"));
		page.addJavascriptURL(iwb.getVirtualPathWithFileNameString("javascript/BuilderHelper.js"));
		
		page.getAssociatedScript().addScriptLine("registerEvent(window, 'load', getBuilderInitInfo);");
		page.getAssociatedScript().addScriptLine("registerEvent(window, 'load', registerBuilderActions);");
//		page.getAssociatedScript().addScriptLine("registerEvent(document, 'click', removeOldLabels);");
		
		//if we want to use Sortable (javascript from the DnD library) someday
		page.setID("DnDPage");
		
		//Begin with transforming the objects on a normal Page object (constructed from IBXML)
		List list = page.getChildren();
		if (list != null) {
			ListIterator iter = list.listIterator();
			PresentationObjectContainer parent = page;
			while (iter.hasNext()) {
				int index = iter.nextIndex();
				UIComponent item = (UIComponent) iter.next();
				transformObject(page,pageKey, item, index, parent, "-1", iwc);
			}
		}
		

		XMLElement pasted = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		boolean clipboardEmpty = true;
		if (pasted != null) {
			clipboardEmpty = false;
		}
		
		String addModuleUri = getUriToObject(AddModuleBlock.class);
		
		//"-1" is identified as the top page object (parent)
		if (page.getIsExtendingTemplate()) {
			if (!page.isLocked()) {
				String parentKey = Integer.toString(-1);
				Layer marker = getLabelMarker(parentKey, "page", getAddIcon(addModuleUri, null));
				page.add(marker);
							
				if (!clipboardEmpty){
					marker.add(getPasteIcon(parentKey, null, iwc));
				}
				
				/*Script drop = new Script();
				drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),"-1","","moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));	
				page.add(drop);*/
				
				
			}
			if(page instanceof HtmlPage){
				HtmlPage hPage = (HtmlPage)page;
				Set regions = hPage.getRegionIds();
				for (Iterator iter = regions.iterator(); iter.hasNext();) {
					String regionKey = (String) iter.next();
					Layer marker = getLabelMarker(regionKey, regionKey, getAddIcon(addModuleUri, regionKey));
					hPage.add(marker,regionKey);
					
					if (!clipboardEmpty){
						marker.add(getPasteIcon(regionKey,regionKey, iwc));
					}
					
					/*Script drop = new Script();
					drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),regionKey,regionKey,"moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));	
					page.add(drop);*/
					
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
				String parentKey = Integer.toString(-1);
				Layer marker = getLabelMarker(parentKey, "page", getAddIcon(addModuleUri, null));

				if ((!clipboardEmpty)){
					marker.add(getPasteIcon(parentKey, null, iwc));
				}
				page.add(marker);
			}
			
			if (page.getIsTemplate()){
				if (page.isLocked()){
					page.add(getLockedIcon(Integer.toString(-1), iwc, null));
				}
				else{
					page.add(getUnlockedIcon(Integer.toString(-1), iwc));
				}
			}
		}
		return (page);
	}

	public Layer getLabelMarker(String label, String parentKey, PresentationObject addIcon) {
		Layer marker = new Layer(Layer.DIV);
		marker.add(new CSSSpacer());
		marker.setStyleClass("regionLabel");
		
		if (addIcon != null) {
			marker.add(addIcon);
		}
		
		if (label == null) {
			Random generator = new Random();
			marker.setId(new StringBuffer("region_label").append(generator.nextInt(Integer.MAX_VALUE)).toString());
		}
		else {
			Layer labelContainer = new Layer();
			labelContainer.add(label);
			marker.add(labelContainer);
			HiddenInput regionLabel = new HiddenInput("region_label", label);
			marker.add(regionLabel);
			if (label.indexOf(BuilderConstants.DOT) != -1) {
				Random generator = new Random();
				marker.setId(new StringBuffer("region_label").append(generator.nextInt(Integer.MAX_VALUE)).toString());
			}
			else {
				marker.setId(new StringBuffer("region_label").append(label).toString());
			}
		}
		
		marker.add(new HiddenInput("parentKey", parentKey));

		return marker;
	}

	public Page getPermissionTransformed(int groupId, Page page, IWContext iwc) {
		List<String> groupIds = new Vector<String>();
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
			// empty block
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
				List list = obj.getChildren();
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

	private void processImageSet(String pageKey, String instanceId, int imageID, IWMainApplication iwma) {
		setProperty(pageKey, instanceId, "image_id", Integer.toString(imageID), iwma);
	}

	private void transformObject(Page currentPage,String pageKey, UIComponent obj, int index, PresentationObjectContainer parent, String parentKey, IWContext iwc) {
		XMLElement pasted = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		boolean clipboardEmpty = (pasted == null);
		//We can either be working with pure UIComponents or PresentationObjects
		boolean isPresentationObject = obj instanceof  PresentationObject;
		
		//Some very special cases, added the boolean to make it faster
		if (isPresentationObject && obj instanceof Image) {
			obj = transformImage(pageKey, obj, iwc);
		}
		else if ( isPresentationObject && ((PresentationObject)obj).isContainer()) {
			if (obj instanceof Table) {
				transformTable(currentPage, pageKey, obj, iwc, clipboardEmpty);
			}
			else {
				String addModuleUri = getUriToObject(AddModuleBlock.class);
				List list = obj.getChildren();
				if (list != null && !list.isEmpty()) {
					ListIterator iter = list.listIterator();
					while (iter.hasNext()) {
						int index2 = iter.nextIndex();
						UIComponent item = (UIComponent) iter.next();
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
								newParentKey = getInstanceId(obj);
							}
							
							transformObject(currentPage,pageKey, item, index2, (PresentationObjectContainer) obj, newParentKey, iwc);
						}
					}
				}
				if (index != -1) {
					//Page curr = getPageCacher().getPage(getCurrentIBPage(iwc), iwc);
					Page curr = getPageCacher().getComponentBasedPage(getCurrentIBPage(iwc)).getNewPage(iwc);
					PresentationObjectContainer container = ((PresentationObjectContainer) obj);
					String instanceId = getInstanceId(obj);
					
					if (curr.getIsExtendingTemplate()) {
						if (container.getBelongsToParent()) {
							if (!container.isLocked()) {
								Layer marker = getLabelMarker(instanceId, container.getLabel(), getAddIcon(addModuleUri, container.getLabel()));
								container.add(marker);
								
								if (!clipboardEmpty){
									marker.add(getPasteIcon(instanceId,container.getLabel(), iwc));
								}
								
								/*Script drop = new Script();
								drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),instanceId,container.getLabel(),"moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
								container.add(drop);*/
							}
						}
						else {
							Layer marker = getLabelMarker(instanceId, container.getLabel(), getAddIcon(addModuleUri, container.getLabel()));
							container.add(marker);
														
							if (!clipboardEmpty){
								marker.add(getPasteIcon(instanceId,container.getLabel(), iwc));
							}
							
							
							if (curr.getIsTemplate()) {
								marker.add(getLabelIcon(instanceId, iwc, container.getLabel()));
								if (container.isLocked()){
									marker.add(getLockedIcon(instanceId, iwc, container.getLabel()));
								}
								else{
									marker.add(getUnlockedIcon(instanceId, iwc));
									/*Script drop = new Script();
									drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),instanceId,container.getLabel(),"moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
									container.add(drop);*/
								}
							}
							
							
						}
					}
					else {
						Layer marker = getLabelMarker(instanceId, container.getLabel(), getAddIcon(addModuleUri, container.getLabel()));
						container.add(marker);
												
						if (!clipboardEmpty){
							marker.add(getPasteIcon(instanceId,container.getLabel(), iwc));
						}
						
						if (curr.getIsTemplate()) {
							marker.add(getLabelIcon(instanceId, iwc, container.getLabel()));
							if (container.isLocked()){
								marker.add(getLockedIcon(instanceId, iwc, container.getLabel()));
							}
							else{
								marker.add(getUnlockedIcon(instanceId, iwc));
								/*Script drop = new Script();
								drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),instanceId,container.getLabel(),"moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
								container.add(drop);*/
							}
						}
						
					}
				}
			}
		}
		if ( (isPresentationObject && ((PresentationObject) obj).getUseBuilderObjectControl()) || !isPresentationObject ) {
			if (index != -1) {
				//parent.remove(obj);
				//parent.add(new IBObjectControl(obj,parent,parentKey,iwc,index));
				parent.set(index, new IBObjectControl(obj, parent, parentKey, iwc, index));
			}
		}
	
	}

	/**
	 * @param currentPage
	 * @param pageKey
	 * @param obj
	 * @param iwc
	 * @param clipboardEmpty
	 */
	protected void transformTable(Page currentPage, String pageKey, UIComponent obj, IWContext iwc, boolean clipboardEmpty) {
		Table tab = (Table) obj;
		int cols = tab.getColumns();
		int rows = tab.getRows();
		String addModuleUri = getUriToObject(AddModuleBlock.class);
		for (int x = 1; x <= cols; x++) {
			for (int y = 1; y <= rows; y++) {
				PresentationObjectContainer moc = tab.containerAt(x, y);
				String newParentKey = tab.getICObjectInstanceID() + "." + x + "." + y;
				if (moc != null) {
					transformObject(currentPage,pageKey, moc, -1, tab, newParentKey, iwc);
				}
				//Page currentPage = PageCacher.getPage(getCurrentIBPage(iwc), iwc);
				if (currentPage.getIsExtendingTemplate()) {
					if (tab.getBelongsToParent()) {
						if (!tab.isLocked(x, y)) {
							Layer marker = getLabelMarker(newParentKey, tab.getLabel(x, y), getAddIcon(addModuleUri, tab.getLabel(x, y)));
							tab.add(marker, x, y);
							
							if (!clipboardEmpty){
								marker.add(getPasteIcon(newParentKey,tab.getLabel(x, y), iwc));
							}
							
							/*Script drop = new Script();
							drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),newParentKey,tab.getLabel(x,y),"moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
							tab.add(drop,x,y);*/
						}
					}
					else {
						Layer marker = getLabelMarker(newParentKey, tab.getLabel(x, y), getAddIcon(addModuleUri, tab.getLabel(x, y)));
						tab.add(marker, x, y);
						
						if (!clipboardEmpty) {
							marker.add(getPasteIcon(newParentKey,tab.getLabel(x, y), iwc));
						}
						if (currentPage.getIsTemplate()) {
							marker.add(getLabelIcon(newParentKey, iwc, tab.getLabel(x, y)));
							if (tab.isLocked(x, y)){
								marker.add(getLockedIcon(newParentKey, iwc, tab.getLabel(x, y)));
							}
							else{
								marker.add(getUnlockedIcon(newParentKey, iwc));
							}
						}
						//always add the drop area
						/*Script drop = new Script();
						drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),newParentKey,tab.getLabel(x,y),"moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
						tab.add(drop,x,y);*/
					}
				}
				else {
					Layer marker = getLabelMarker(newParentKey, tab.getLabel(x, y), getAddIcon(addModuleUri, tab.getLabel(x, y)));
					tab.add(marker, x, y);
					
					if (!clipboardEmpty) {
						marker.add(getPasteIcon(newParentKey, tab.getLabel(x,y) ,iwc));
					}
					if (currentPage.getIsTemplate()) {
						marker.add(getLabelIcon(newParentKey, iwc, tab.getLabel(x, y)));
						if (tab.isLocked(x, y)){
							marker.add(getLockedIcon(newParentKey, iwc, tab.getLabel(x, y)));
						}
						else{
							marker.add(getUnlockedIcon(newParentKey, iwc));
						}
						/*Script drop = new Script();
						drop.addFunction("",getModuleToRegionDroppableScript(marker.getID(),getCurrentIBPage(iwc),newParentKey,tab.getLabel(x,y),"moduleContainer","regionLabelHover",getBuilderBundle().getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
						tab.add(drop,x,y);*/
					}
					
				}
			}
		}
	}

	/**
	 * @param pageKey
	 * @param obj
	 * @param iwc
	 * @return
	 */
	protected UIComponent transformImage(String pageKey, UIComponent obj, IWContext iwc) {
		Image imageObj = (Image) obj;
		boolean useBuilderObjectControl = imageObj.getUseBuilderObjectControl();
		int ICObjectIntanceID = imageObj.getICObjectInstanceID();
		String sessionID = "ic_" + ICObjectIntanceID;
		String session_image_id = (String) iwc.getSessionAttribute(sessionID);
		if (session_image_id != null) {
			int image_id = Integer.parseInt(session_image_id);
			/**
			 * @todo Change this so that id is done in a more appropriate place,
			 * i.e. set the image_id permanently on the image
			 */
			processImageSet(pageKey, Integer.toString(ICObjectIntanceID), image_id, iwc.getIWMainApplication());
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
		((PresentationObject)obj).setICObjectInstanceID(ICObjectIntanceID);
		((PresentationObject)obj).setUseBuilderObjectControl(useBuilderObjectControl);
		return obj;
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
	
	
	/*public String getPageKeyByURIAndServerName(String requestURI,String serverName){
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
		return theReturn;
	}*/
	
	
	/**
	 * Tries to find the uri in the cache of builder pages
	 * @param requestURI
	 * @return
	 */
	public String getPageKeyByURICached(String pageUri){
		Iterator iter = getPageCacher().getPageCacheMap().values().iterator();
		for (Iterator it = iter; it.hasNext(); ) {
			CachedBuilderPage page = (CachedBuilderPage) it.next();
			if (page == null) {
				return null;
			}
			String cachedPageUri = page.getURIWithContextPath();
			if (cachedPageUri == null) {
				return null;
			}
			if (cachedPageUri.equals(pageUri)) {
				return page.getPageKey();
			}
		}
		return null;
	}
	
	public String getPageKeyByURI(String requestURI,ICDomain domain){
		return getExistingPageKeyByURI(requestURI,domain);
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
				ICDomain domain = iwc.getDomain();
				return getPageKeyByURI(requestURI,domain);
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
		else {
			theReturn = String.valueOf(getInstance().getStartPageIdByServerName(iwc,iwc.getServerName()));
		}
		if (theReturn == null) {
			return Integer.toString(getCurrentDomain(iwc).getStartPageID());
		}
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
		//IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		IWApplicationContext iwac = IWApplicationContextFactory.getCurrentIWApplicationContext();
		if(iwac == null){
			try {
				iwac = IWContext.getInstance();
			} catch (UnavailableIWContext e) {
				e.printStackTrace();
			}
		}
		
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
	public String[] getPropertyValues(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] selectedValues, boolean returnSelectedValueIfNothingFound) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		return IBPropertyHandler.getInstance().getPropertyValues(iwma, xml, instanceId, propertyName, selectedValues, returnSelectedValueIfNothingFound);
		//return getXMLWriter().getPropertyValues(xml,ObjectInstanceId,propertyName);
	}

	public boolean removeProperty(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] values) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (getIBXMLWriter().removeProperty(iwma, xml, instanceId, propertyName, values)) {
			xml.store();
			return true;
		}
		return false;
	}

	/**
	 * Returns the first property if there is an array of properties set
	 */
	public String getProperty(String pageKey, String instanceId, String propertyName) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		return getIBXMLWriter().getProperty(xml, instanceId, propertyName);
	}

	/**
	 * Returns true if properties changed, or error, else false
	 */
	public boolean setProperty(String pageKey, String instanceId, String propertyName, String propertyValue, IWMainApplication iwma) {
		String[] values = {propertyValue};
		return setProperty(pageKey, instanceId, propertyName, values, iwma);
	}

	/**
	 * Returns true if properties changed, or error, else false
	 */
	public boolean setProperty(String pageKey, String instanceId, String propertyName, String[] propertyValues, IWMainApplication iwma) {
		try {
			IBXMLPage xml = getIBXMLPage(pageKey);
			boolean allowMultivalued = isPropertyMultivalued(propertyName, instanceId, iwma);
			if (getIBXMLWriter().setProperty(iwma, xml, instanceId, propertyName, propertyValues, allowMultivalued)) {
				xml.store();
				return (true);
			}
			return (false);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return (false);
		}
	}
	
	/**
	 * Adds method with parameters to IBXMLPage
	 * @param pageKey
	 * @param moduleId
	 */
	public boolean addPropertyToModule(String pageKey, String moduleId, String propName, String propValue) {
		if (pageKey == null || moduleId == null || propName == null || propValue == null) {
			return false;
		}
		FacesContext f = FacesContext.getCurrentInstance();
		IWContext iwc = IWContext.getIWContext(f);
		if (iwc == null) {
			return false;
		}
		StringBuffer values = new StringBuffer();
		String currentValue = getProperty(pageKey, moduleId, propName);
		if (currentValue != null) {
			values.append(currentValue).append(IBXMLConstants.COMMA_STRING);
		}
		values.append(propValue);
		return setProperty(pageKey, moduleId, propName, values.toString(), iwc.getIWMainApplication());
	}

	/**
	 * Returns true if properties changed, or error, else false
	 */
	public boolean isPropertySet(String pageKey, String instanceId, String propertyName, IWMainApplication iwma) {
		try {
			IBXMLPage xml = getIBXMLPage(pageKey);
			return getIBXMLWriter().isPropertySet(iwma, xml, instanceId, propertyName);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			return (false);
		}
	}
	
	
	/**
	 * After deleting module, saves (if successfully deleted) IBXMLPage in other thread
	 * @param pageKey
	 * @param parentObjectInstanceID
	 * @param instanceId
	 * @param session
	 * @return
	 */
	public boolean deleteModule(String pageKey, String parentObjectInstanceID, String instanceId, IWSlideSession session) {
		IBXMLPage page = getIBXMLPage(pageKey);
		deleteBlock(instanceId, pageKey);
		boolean result = getIBXMLWriter().deleteModule(page, parentObjectInstanceID, instanceId);
		
		if (result) {
			return savePage(page, session);
		}
		return result;
	}

	// add by Aron 20.sept 2001 01:49
	public boolean deleteModule(String pageKey, String parentObjectInstanceID, String instanceId) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		
		deleteBlock(instanceId, pageKey);
		
		if (getIBXMLWriter().deleteModule(xml, parentObjectInstanceID, instanceId)) {
			xml.store();
			return (true);
		}
		return (false);
	}
	
	private boolean deleteBlock(String instanceId, String pageKey) {
		try {
			ICObjectInstance instance = getIBXMLReader().getICObjectInstanceFromComponentId(instanceId, null, pageKey);
			Object obj = ICObjectBusiness.getInstance().getNewObjectInstance(instance.getID());
			if (obj != null) {
				if (obj instanceof Builderaware) {
					((Builderaware) obj).deleteBlock(Integer.parseInt(instanceId));
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 *  	 *
	 */
	public boolean copyModule(IWUserContext iwc, String pageKey, String instanceId) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		
		XMLElement element = xml.copyModule(instanceId);
		if (element == null) {
			return false;
		}
		XMLElement el = (XMLElement) element.clone();
		iwc.setSessionAttribute(CLIPBOARD, el);
		return (true);
	}

	/**
	 *  	 *
	 */
	public boolean pasteModuleIntoRegion(IWUserContext iwc, String pageKey, String regionId, String regionLabel) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		XMLElement element = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		if (element == null) {
			return (false);
		}
		XMLElement toPaste = (XMLElement) element.clone();
		if (getIBXMLWriter().pasteElementLastIntoParentOrRegion(xml, pageKey, regionId, regionLabel,toPaste)) {
			xml.store();
			return (true);
		}
		return (false);
	}
	
	public boolean pasteModule(IWUserContext iwc, String pageKey, String parentID) {
		return pasteModuleIntoRegion(iwc,pageKey,parentID,null);
	}

	/**
	 *  	 *
	 */
	public boolean pasteModuleAbove(IWUserContext iwc,String pageKey, String parentID, String objectID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		System.out.println("pageKey = " + pageKey);
		System.out.println("parentID = " + parentID);
		System.out.println("objectID = " + objectID);
		XMLElement element = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		if (element == null) {
			return (false);
		}
		XMLElement toPaste = (XMLElement) element.clone();
		if (getIBXMLWriter().pasteElementAbove(xml, pageKey, parentID, objectID, toPaste)) {
			xml.store();
			return (true);
		}
		return (false);
	}
	
	public boolean pasteModuleBelow(IWUserContext iwc, String pageKey, String parentID, String objectID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		XMLElement element = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		if (element == null) {
			return (false);
		}
		XMLElement toPaste = (XMLElement) element.clone();
		if (getIBXMLWriter().pasteElementBelow(xml, pageKey, parentID, objectID, toPaste)) {
			xml.store();
			return (true);
		}
		return (false);
	}
	
	/**
	 * Copies, cuts and then pastes the module as the last item in a region
	 * @param instanceId
	 * @param formerParentId
	 * @param pageKey
	 * @param regionId
	 * @param regionLabel
	 * @return
	 * @throws Exception
	 */
	public boolean moveModuleIntoRegion(String instanceId, String formerParentId, String pageKey, String regionId, String regionLabel) throws Exception {
		boolean returner = false;
		if("null".equals(regionLabel)){
			regionLabel = null;
		}
		IBXMLPage page = getIBXMLPage(pageKey);
		//find
		XMLElement moduleXML =  getIBXMLWriter().findModule(page,instanceId);
		XMLElement parentXML =  getIBXMLWriter().findModule(page,formerParentId);
		
		XMLElement moduleXMLCopy = (XMLElement)moduleXML.clone();
		
		if(moduleXML!=null && parentXML!=null){
			//remove	
			try {
				returner = getIBXMLWriter().removeElement(parentXML,moduleXML,false);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			
			if(returner){
				returner = getIBXMLWriter().insertElementLastIntoParentOrRegion(page, pageKey, regionId, regionLabel,moduleXMLCopy);
			}		
			
			if(!returner){
				return false;
			}
			
			return page.store();
		}
		
		return returner;
		
	}
	
	
	/**
	 * Copies, cuts and then pastes the module below another module
	 * @param objectId
	 * @param pageKey
	 * @param formerParentId
	 * @param newParentId
	 * @param objectIdToPasteBelow
	 * @return
	 * @throws Exception 
	 */
	public boolean moveModule(String instanceId, String pageKey, String formerParentId, String newParentId, String instanceIdToPasteBelow) throws Exception{

//		System.out.println("pageKey = " + pageKey);
//		System.out.println("parentID = " + formerParentId);
//		System.out.println("instanceId = " + instanceId);
//		System.out.println("newparentID = " + 	newParentId);
//		System.out.println("instanceIdToPasteBelow = " +objectIdToPasteBelow);
		
		boolean returner = false;
		
		//find the XMLElement
		//remove it from the page
		//insert it after the object we dropped on
		IBXMLPage page = getIBXMLPage(pageKey);
		//find
		XMLElement moduleXML =  getIBXMLWriter().findModule(page,instanceId);
		XMLElement parentXML =  getIBXMLWriter().findModule(page,formerParentId);
		
		XMLElement moduleXMLCopy = (XMLElement)moduleXML.clone();
		
		if(moduleXML!=null && parentXML!=null){
			//remove	
			try {
				returner = getIBXMLWriter().removeElement(parentXML,moduleXML,false);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			
			if(!returner) {
				return false;
				//insert
			}

			returner = getIBXMLWriter().insertElementBelow(page,newParentId,moduleXMLCopy,instanceIdToPasteBelow);
			if(!returner){
				return false;
			}
			
			return page.store();
		}
		
		return false;
	}
	
	

	public boolean lockRegion(String pageKey, String parentObjectInstanceID) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (getIBXMLWriter().lockRegion(xml, parentObjectInstanceID)) {
			xml.store();
			if (parentObjectInstanceID.equals("-1")) {
				if (xml.getType().equals(CachedBuilderPage.TYPE_TEMPLATE)) {
					List extend = xml.getUsingTemplate();
					if (extend != null) {
						Iterator i = extend.iterator();
						while (i.hasNext()) {
							lockRegion((String) i.next(), parentObjectInstanceID);
						}
					}
				}
			}
			return true;
		}
		return (false);
	}

	public boolean unlockRegion(String pageKey, String parentObjectInstanceID, String label) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (getIBXMLWriter().unlockRegion(xml, parentObjectInstanceID)) {
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
		//TODO add handling for generic UIComponent adding
		
		String id = getIBXMLWriter().addNewModule(xml, pageKey, parentObjectInstanceID, newICObjectID, label);
		if (id == null) {
			return false;
		}
		xml.store();
		return true;
	}
	
	/**
	 * After insering new module IBXMLPage is saved (if successfully inserted module) in other thread
	 * @param pageKey
	 * @param parentObjectInstanceID
	 * @param newICObjectID
	 * @param label
	 * @param session
	 * @return
	 */
	public String addNewModule(String pageKey, String parentObjectInstanceID, int newICObjectID, String label, IWSlideSession session) {
		IBXMLPage page = getIBXMLPage(pageKey);
		String id = getIBXMLWriter().addNewModule(page, pageKey, parentObjectInstanceID, newICObjectID, label);
		if (id == null) {
			return null;
		}
		
		if (savePage(page, session)) {
			return id;
		}
		return null;
	}
	
	protected boolean setPageSourceAsString(IBXMLPage page) {
		XMLDocument doc = page.getXMLDocument();
		if (doc == null) {
			return false;
		}
		Object o = doc.getDocument();
		if (o instanceof Document) {
			Document d = (Document) o;
			XMLOutputter out = new XMLOutputter();
			out.setFormat(Format.getPrettyFormat());
			try {
				page.setSourceFromString(out.outputString(d));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean savePage(IBXMLPage page, IWSlideSession session) {
		if (page == null) {
			return false;
		}
		if (session == null) {
			return page.store();
		}
		
		boolean existPageSource = true;
		if (page.getSourceAsString() == null) {
			existPageSource = setPageSourceAsString(page);
		}
		if (existPageSource) {
			Thread saver = new Thread(new BuilderLogicWorker(page, session));
			saver.start();
		}
		else {
			return page.store();
		}
		return true;
	}
	
	public boolean addRegion(String pageKey, String label, String parentId, boolean storePage) {
		IBXMLPage page = getIBXMLPage(pageKey);
		if (page == null) {
			return false;
		}
		if (getIBXMLWriter().addRegionToRootElement(page, label, parentId)) {
			if (storePage) {
				page.store();
			}
			return true;
		}
		return false;
	}

	/**
	 *  	 *
	 */
	public boolean addNewModule(String pageKey, String parentObjectInstanceID, ICObject newObjectType, String label) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		String id = getIBXMLWriter().addNewModule(xml, pageKey, parentObjectInstanceID, newObjectType, label);
		if (id == null) {
			return false;
		}
		xml.store();
		return true;	
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

	private boolean isPropertyMultivalued(String propertyName, String instanceId, IWMainApplication iwma) throws Exception {
		try {
			Class c = null;
			IWBundle iwb = null;
			if ("-1".equals(instanceId)) {
				c = com.idega.presentation.Page.class;
				iwb = iwma.getBundle(PresentationObject.CORE_IW_BUNDLE_IDENTIFIER);
			}
			else {
				ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(Integer.parseInt(instanceId));
				c = instance.getObject().getObjectClass();
				iwb = instance.getObject().getBundle(iwma);
			}
			//IWPropertyList complist = iwb.getComponentList();
			IWPropertyList component = iwb.getComponentPropertyList(c.getName());
			IWPropertyList methodlist = component.getPropertyList(IBPropertyHandler.METHODS_KEY);
			if (methodlist == null) {
				return (false);
			}
			IWPropertyList method = methodlist.getPropertyList(propertyName);
			if (method == null) {
				return (false);
			}
			IWProperty prop = method.getIWProperty(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED);
			if (prop != null) {
				boolean value = prop.getBooleanValue();
				return value;
			}
			return false;
		}
		catch (Exception e) {
			//e.printStackTrace(System.err);
			return false;
		}
	}

	/**
	 *  	 *
	 */
	public PresentationObject getPasteIcon(String parentKey,String regionLabel, IWContext iwc) {
		Image pasteImage = getBuilderBundle().getImage("paste.gif", "Paste component");
		Link link = new Link(pasteImage);
		link.setStyleClass("regionButton");
		link.setWindowToOpen(IBPasteModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(IB_CONTROL_PARAMETER, ACTION_PASTE);
		link.addParameter(IB_PARENT_PARAMETER, parentKey);
		if(regionLabel!=null){
			link.addParameter(IB_LABEL_PARAMETER , regionLabel);
		}
		return (link);
	}

	/**
	 *  	 *
	 */
	public boolean labelRegion(String pageKey, String parentObjectInstanceID, String label) {
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (getIBXMLWriter().labelRegion(xml, parentObjectInstanceID, label)) {
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
			return iwc.getIWMainApplication().getBuilderPagePrefixURI()+pageKey+"/";
		}
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
			xml.changeTemplateId(newTemplateId);
		}
	}
	
	/**
	 * Changes the template id for the IBXMLPage.
	 * 
	 * @parama pageKey
	 * 			IBPage id
	 * 
	 * @param newTemplateId
	 *          The new template id for the current page.
	 */
	public void setTemplateId(String pageKey, String newTemplateId) {
		try {
			IBXMLPage xml = getIBXMLPage(pageKey);
			if (xml == null) {
				return;
			}
			xml.setTemplateId(newTemplateId);
		} catch (ClassCastException e) {
			
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
	public PresentationObject getIFrameContent(int instanceId, IWContext iwc) {
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
	public void changeDPTCrawlableLinkedPageId(int moduleId, String currentPageID, String newLinkedPageId) {
		IBXMLPage page = getIBXMLPage(currentPageID);
		XMLElement element = new XMLElement(IBXMLConstants.CHANGE_PAGE_LINK);
		XMLAttribute id = new XMLAttribute(IBXMLConstants.LINK_ID_STRING, Integer.toString(moduleId));
		XMLAttribute newPageLink = new XMLAttribute(IBXMLConstants.LINK_TO, newLinkedPageId);
		element.setAttribute(id);
		element.setAttribute(newPageLink);
		getIBXMLWriter().addNewElement(page, "-1", element);
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
		String sUrl = domain.getURL();
		//cut the last '/' away to avoid double '/'
		if(sUrl!=null){
			if(sUrl.endsWith("/")){
				sUrl = sUrl.substring(0,sUrl.length()-1);
			}
		}
		StringBuffer url = new StringBuffer(sUrl);
		//    url.append(IWMainApplication.BUILDER_SERVLET_URL);
		//    url.append(iwc.getApplication().getBuilderServletURI());
		//    url.append("?");
		//    url.append(IB_PAGE_PARAMETER);
		//    url.append("=");

		url.append(this.getIBPageURL(iwc, Integer.parseInt(ibpage)));
		
		if (url.toString().indexOf("http") == -1) {
			url.insert(0, "http://");
		}

		String html = FileUtil.getStringFromURL(url.toString());
		return (html);
	}

	/**
	 * Invalidates cache for all pages and the cached page tree
	 */
	public void clearAllCachedPages() {
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		System.out.println("Clearing all DomainTree Cache");
		DomainTree.clearCache(iwac);
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
		if(this.pageCacher==null){
			setPageCacher(new PageCacher());
		}		
		return this.pageCacher;
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
		if(this.ibPageHelper==null){
			setIBPageHelper(new IBPageHelper());
		}
		return this.ibPageHelper;
	}
	public void setIBPageHelper(IBPageHelper ibPageHelper){
		this.ibPageHelper=ibPageHelper;
	}
	
	private IBXMLWriter xmlWriter;
	public IBXMLWriter getIBXMLWriter(){
		if(this.xmlWriter==null){
			setIBXMLWriter(new IBXMLWriter());
		}
		return this.xmlWriter;
	}
	public void setIBXMLWriter(IBXMLWriter writer){
		this.xmlWriter=writer;
	}
	
	private IBXMLReader xmlReader;
	public IBXMLReader getIBXMLReader(){
		if(this.xmlReader==null){
			setIBXMLReader(new IBXMLReader());
		}
		return this.xmlReader;
	}
	public void setIBXMLReader(IBXMLReader reader){
		this.xmlReader=reader;
	}
	
	/**
	 * Return an array of the document formats that the Builder supports.
	 * ('IBXML','HTML','JSP_1_2')
	 * @return
	 */
	public String[] getPageFormatsSupported(){
		return this.pageFormats;
	}
	
	/**
	 * <p>
	 * Returns a map with pageFormat as key and descripton as value
	 * </p>
	 * @return
	 */
	public Map getPageFormatsSupportedAndDescription(){
		Map<String, String> map = new HashMap<String, String>();
		map.put(this.PAGE_FORMAT_IBXML,"Builder (IBXML)");
		map.put(this.PAGE_FORMAT_HTML,"HTML");
		map.put(this.PAGE_FORMAT_JSP_1_2,"JSP 1.2");
		return map;
	}
	
	/**
	 * gets the default page format:
	 * @return
	 */
	public String getDefaultPageFormat(){
		return this.PAGE_FORMAT_IBXML;
	}

	/**
	 *
	 */
	public PresentationObject getAddIcon(String uri, String label) {
		Image addImage = getBuilderBundle().getImage("add.png", "Add new component");
		addImage.setOnClick("setPropertiesForAddModule(this.parentNode);");

		//	Link for MOOdalBox
		Link link = new Link(addImage);
		link.setMarkupAttribute("rel", "moodalbox");
		link.setURL(uri);

		if (label != null) {
			link.setToolTip(label);
		}
	
		return link;
		
		/*Image addImage = getBuilderBundle().getImage("add.gif", "Add new component");

		Link link = new Link(addImage);
		link.setStyleClass("regionButton");	
		link.setWindowToOpen(IBAddModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_ADD);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		if(label!=null){
			link.setToolTip(label);
		}
	
		return link;*/
	}
	

	/**
	 *
	 */
	public PresentationObject getLockedIcon(String parentKey, IWContext iwc, String label){
		Image lockImage = getBuilderBundle().getImage("las_close.gif", "Unlock region");
		Link link = new Link(lockImage);
		link.setStyleClass("regionButton");
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_UNLOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		if(label!=null){
			link.setToolTip(label);
		}
		return (link);
	}

	/**
	 *
	 */
	public PresentationObject getUnlockedIcon(String parentKey, IWContext iwc){
		Image lockImage = getBuilderBundle().getImage("las_open.gif", "Lock region");
		Link link = new Link(lockImage);
		link.setStyleClass("regionButton");
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		return (link);
	}
	
	/**
	 *
	 */
	public PresentationObject getLabelIcon(String parentKey, IWContext iwc, String label){
		Image labelImage = getBuilderBundle().getImage("label.gif", "Put label on region");
		Link link = new Link(labelImage);
		link.setStyleClass("regionButton");
		link.setWindowToOpen(IBAddRegionLabelWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LABEL);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		if(label!=null){
			link.setToolTip(label);
		}
		return (link);
	}

	public PresentationObject getCutIcon(String key, String parentKey, IWContext iwc){
		Image cutImage = getBuilderBundle().getImage("cut_16.gif", "Cut component",16,16);
		Link link = new Link(cutImage);
		link.setStyleClass("moduleButton");
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
	public PresentationObject getCopyIcon(String key, String parentKey, IWContext iwc){
		Image copyImage = getBuilderBundle().getImage("copy_16.gif", "Copy component",16,16);
		//copyImage.setAttribute("style","z-index: 0;");
		Link link = new Link(copyImage);
		link.setStyleClass("moduleButton");
		link.setWindowToOpen(IBCopyModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_COPY);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return (link);
	}

	public PresentationObject getDeleteIcon(String key, String parentKey, IWContext iwc){
		Image deleteImage = getBuilderBundle().getImage("del_16.gif", "Delete component",16,16);
		Link link = new Link(deleteImage);
		link.setStyleClass("moduleButton");
		link.setWindowToOpen(IBDeleteModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_DELETE);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	public PresentationObject getPermissionIcon(String key, IWContext iwc){
		Image editImage = getBuilderBundle().getImage("lock_16.gif", "Set permissions",16,16);
		Link link = new Link(editImage);
		link.setStyleClass("moduleButton");
		link.setWindowToOpen(IBPermissionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_PERMISSION);
		link.addParameter(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER, key);
		link.addParameter(
			IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY,
			AccessController.CATEGORY_OBJECT_INSTANCE);
		return link;
	}

	public PresentationObject getEditIcon(String key, IWContext iwc){
		Image editImage = getBuilderBundle().getImage("prefs_16.gif", "Properties",16,16);
		Link link = new Link(editImage);
		link.setStyleClass("moduleButton");
		link.setWindowToOpen(IBPropertiesWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_EDIT);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	/**
	 *
	 */
	public PresentationObject getPasteAboveIcon(String key, String parentKey, IWContext iwc)
	{
		Image pasteImage = getBuilderBundle().getImage("paste_16.gif", "Paste above component",16,16);
		//copyImage.setAttribute("style","z-index: 0;");
		Link link = new Link(pasteImage);
		link.setStyleClass("moduleButton");
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
	
	
	public String getInstanceId(UIComponent object) {
		if(object instanceof PresentationObject){
			/*int icObjectInstanceId = ((PresentationObject)object).getICObjectInstanceID();
			if(icObjectInstanceId!=-1){
				return Integer.toString(icObjectInstanceId);
			}*/
			PresentationObject po = (PresentationObject)object;
			return po.getXmlId();
		}
		//set from the xml
		return object.getId();
	}
	

	/**
	 * Gets a copy of a UIComponent by its instanceId (component.getId()) if it is found in the current pages ibxml
	 * @param component
	 * @return A reset copy of the component from ibxml
	 */
	public UIComponent getCopyOfUIComponentFromIBXML(UIComponent component) {
		String instanceId = getInstanceId(component);
		UIComponent newComponent = null;
		try {
			newComponent = component.getClass().newInstance();
			newComponent.setId(instanceId);
			PropertyCache.getInstance().setAllCachedPropertiesOnInstance(instanceId, newComponent);
			List childrenList = component.getChildren();
			Iterator childrenListIterator = childrenList.iterator();
			while (childrenListIterator.hasNext()) {
				UIComponent childComponent = (UIComponent) childrenListIterator.next();
				UIComponent newChildComponent = getCopyOfUIComponentFromIBXML(childComponent);
				if (newChildComponent != null) {
					newComponent.getChildren().add(newChildComponent);
				}
			}
		}
		catch (InstantiationException ex) {
			ex.printStackTrace();
			return null;
		}
		catch (IllegalAccessException ex) {
			ex.printStackTrace();
			return null;
		}
		return newComponent;
	}
	
/**
	 * @param containerId
	 * @return
	 */
	public String getDraggableScript(String containerId, String handleId) {
	//	return " new Draggable('"+containerId+"',{handle:'"+handleAndMenuLayer.getID()+"',revert:true});";
		return " new Draggable('"+containerId+"',{handle:'"+handleId+"',revert:true, " +
		"endeffect: function(element, top_offset, left_offset) { " +
        //"	new Effect.Opacity(element, {duration:0.2, from:0.7, to:1.0}); " +
        "	new Effect.Pulsate(element,{duration:1.5});" +
        "}});";
	}

	public String getModuleToModuleDroppableScript(String contentLayerId, String droppableId, String acceptableStyleClasses, String hoverStyleClass, String webServiceURI) {
		//TODO make an external script (as much as possible)
		 return "Droppables.add('"+droppableId+"',{accept:['"+acceptableStyleClasses+"'],hoverclass:'"+hoverStyleClass+"'," +
			"onDrop: " +
			"function(element) { \n" +
			"	var elementContainerId = element.id; \n" +
			"	var dropTargetId = '"+contentLayerId+"'; \n" +
		 	"	var dropTarget = $(dropTargetId); \n" +
		 	"   var elementInstanceId = $('instanceId_'+elementContainerId).value; \n" +
		 	"   var dropTargetInstanceId =  $('instanceId_'+dropTargetId).value \n;" +
		 	"   var currentPageKey =  $('pageId_'+dropTargetId).value; \n" +
		 	"   var formerParentId =  $('parentId_'+elementContainerId).value; \n" +
		 	"   var newParentId = $('parentId_'+dropTargetId).value; \n" +
		 	"   var webServiceURI = '"+webServiceURI+"'\n"+
		 	"	var query = 'method=moveModule&objectId='+elementInstanceId+'&pageKey='+currentPageKey+'&formerParentId='+formerParentId+'&newParentId='+newParentId+'&objectIdToPasteBelow='+dropTargetInstanceId;  \n" +	
		 	//"   alert(query);" +
		 	"   	new Ajax.Request(webServiceURI+'?'+query, {  \n" +
		 	"			onComplete: function(request) { \n" +
		 	"				if(request.responseText.indexOf('iwbuilder-ok')>=0){ \n" +
		 	"					$('parentId_'+elementContainerId).value = newParentId; " +
		 	"		 	 		dropTarget.parentNode.insertBefore(element,dropTarget);  \n" +
		 	" 					element.parentNode.insertBefore(dropTarget,element);  \n" +
		 	" 					$('parentId_'+elementContainerId).value = newParentId; \n"	 +
		 	"				}else { alert(request.responseText); } \n" +
		 	"			}, method: 'GET',asynchronous: true})" +

		 	
		 	
		 	//OLD WAY that did not work for droppables within droppables after one drag.

//			"	var elementHandleId = 'handle_'+elementContainerId;" +
//			"   var scriptLayer = $('script_'+elementContainerId);" +
//		 	"   var scriptLayerId = scriptLayer.id;" +
		 	//Copy the moduleContainer layer
			//"   new Insertion['After']($('"+contentLayerId+"'), '<div class=moduleContainer id='+elementContainerId+' >'+element.innerHTML+' </div>');" +
			//get rid of the old
			//"	Element.remove(element); " +
			//"	element = null;" +
			//Copy the script layer also!
			//"   new Insertion['After']($(elementContainerId), '<div class=script id='+scriptLayerId+' >'+scriptLayer.innerHTML+' </div>');" +
			
		 	"}});";
	}
	
	public String getModuleToRegionDroppableScript(String regionMarkerId, String pageKey, String regionId, String regionLabel, String acceptableStyleClasses, String hoverStyleClass, String webServiceURI) {

		return "Droppables.add('"+regionMarkerId+"',{accept:['"+acceptableStyleClasses+"'],hoverclass:'"+hoverStyleClass+"'," +
			"onDrop: " +
			"function(element) { \n" +
			"	var elementContainerId = element.id; \n" +
			"	var dropTargetId = '"+regionMarkerId+"'; \n" +
		 	"	var dropTarget = $(dropTargetId); \n" +
		 	"   var elementInstanceId = $('instanceId_'+elementContainerId).value; \n" +
		 	"   var currentPageKey =  '"+pageKey+"' \n" +
		 	"   var formerParentId =  $('parentId_'+elementContainerId).value; \n" +
		 	"   var regionId = '"+regionId+"'; \n" +
		 	"   var regionLabel = '"+regionLabel+"'\n" +
		 	"   var webServiceURI = '"+webServiceURI+"'\n"+
		 	"	var query = 'method=moveModuleIntoRegion&instanceId='+elementInstanceId+'&formerParentId='+formerParentId+'&pageKey='+currentPageKey+'&regionId='+regionId+'&regionLabel='+regionLabel;  \n" +	
		 	"   	new Ajax.Request(webServiceURI+'?'+query, {  \n" +
		 	"			onComplete: function(request) { \n" +
		 	"				if(request.responseText.indexOf('iwbuilder-ok')>=0){ \n" +
		 	"		 	 		dropTarget.parentNode.insertBefore(element,dropTarget);  \n" +
		 	"					$('parentId_'+elementContainerId).value = regionId; " +
		 	"				}else { alert(request.responseText); } \n" +
		 	"			}, method: 'GET',asynchronous: true}) " +
		 	"}});";
	}
	
	public IWBundle getBuilderBundle(){
		return IWMainApplication.getDefaultIWMainApplication().getBundle(IW_BUNDLE_IDENTIFIER);
	}
	
	
	public boolean isFirstBuilderRun(){
		ICDomain domain =  getCurrentDomain();
		if(domain.getStartPageID()==-1){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initializeBuilderStructure
	 * </p>
	 * @param domain
	 * @param frontPageName
	 * @throws Exception 
	 */
	public void initializeBuilderStructure(ICDomain domain, String frontPageName) throws Exception {
	    
		ICPageHome pageHome = getICPageHome();

	    ICPage page = pageHome.create();
	    String rootPageName = frontPageName;
	    page.setName(rootPageName);
	    page.setDefaultPageURI("/");
	    page.setType(ICPageBMPBean.PAGE);
	    page.store();
	    unlockRegion(page.getPrimaryKey().toString(),"-1",null);

	    ICPage page2 = pageHome.create();
	    page2.setName(frontPageName+" - Template");
	    page2.setType(ICPageBMPBean.TEMPLATE);
	    page2.store();

	    unlockRegion(page2.getPageKey(),"-1",null);

	    page.setTemplateKey(page2.getPageKey());
	    page.store();

	    domain.setIBPage(page);
	    domain.setStartTemplate(page2);
	    domain.store();

	    IBXMLPage xml = getIBXMLPage(page2.getPrimaryKey().toString());
	    if (xml != null) {
	    	xml.setTemplateId(page2.getPrimaryKey().toString());
	    	xml.addPageUsingThisTemplate(page.getPrimaryKey().toString());
	    }
	    
	    clearAllCachedPages();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method getICPageHome
	 * </p>
	 * @return
	 */
	private ICPageHome getICPageHome() {
		try {
			return (ICPageHome)IDOLookup.getHome(ICPage.class);
		}
		catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
	}
	
//	protected Web2Business getWeb2Business(){
//		
//		try {
//			return (Web2Business) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), Web2Business.class);
//		}
//		catch (IBOLookupException e) {
//			e.printStackTrace();
//		}	
//		
//		return null;
//	}
	
	/**
	 * Saving page structure after moving (drag & drop) tree nodes
	 * @param IDs Tree nodes' IDs
	 */
	public boolean movePage(int newParentId, int nodeId, ICDomain domain) {
		IBPageHelper.getInstance().movePage(nodeId, newParentId, domain);
		return true;
	}
	
	public boolean changePageName(int id, String newName) {
//		PageNameHandler.onChangePageName(ID, newName);
		IBPageUpdater.updatePageName(id, newName);
//		IBPageUpdater.updatePageName(ID, newName);
		
		Map tree = PageTreeNode.getTree(IWContext.getInstance());
		PageTreeNode node = (PageTreeNode) tree.get(id);
		node.setNodeName(newName);
		
		return true;
	}
		
//	public Collection getTopLevelPages(IWContext iwc){
//		return DomainTree.getDomainTree(iwc).getPagesNode().getChildren();
//	}

	public Collection getTopLevelTemplates(IWContext iwc){
		return DomainTree.getDomainTree(iwc).getTemplatesNode().getChildren();		
	}
//	public Collection getSortedTopLevelPages(IWContext iwc){
	public Collection getTopLevelPages(IWContext iwc){
		Collection<PageTreeNode> coll = DomainTree.getDomainTree(iwc).getPagesNode().getChildren();
		
		List <PageTreeNode>unsortedNodes = new ArrayList <PageTreeNode> (coll);
		List <PageTreeNode>sortedNodes = new ArrayList<PageTreeNode>();
		List <PageTreeNode>nodesLeft = new ArrayList<PageTreeNode>();
		
		try {
			for(int i = 0; i < coll.size(); i++){
				sortedNodes.add(null);
			}
			
			for (int i = 0; i < unsortedNodes.size(); i++) {
				PageTreeNode node = unsortedNodes.get(i);
//			if (node.getOrder() > 0){
				if ((node.getOrder() > 0) && (node.getOrder() <= sortedNodes.size())){
					if (sortedNodes.get(node.getOrder() - 1) == null){
						sortedNodes.set(node.getOrder() - 1, node);
					}
					else{
						nodesLeft.add(node);
						unsortedNodes.set(i, null);		
					}				
				}
				else{
					nodesLeft.add(node);
					unsortedNodes.set(i, null);		
				}
			}
			int nodesLeftIndex = 0;
			if (!nodesLeft.isEmpty()){
				for (int i = 0; i < sortedNodes.size(); i++) {
					if(sortedNodes.get(i) == null){
						PageTreeNode node = nodesLeft.get(nodesLeftIndex);
						node.setOrder(i+1);
						sortedNodes.set(i, node);
						nodesLeftIndex++;
						if(Integer.parseInt(node.getId()) > -1){
							ICPage page = getICPage(node.getId());
							if (page != null) {
								page.setTreeOrder(i+1);
								page.store();
							}				
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return coll;
		} catch (IDOStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return coll;
		}

		return sortedNodes;
	}
	
	
//	public boolean wasMoved(String child, String parent){
//		String oldParent = child.substring(0, child.length()-1);
//		if (oldParent.equals(parent))
//			return false;
//		else {
//			saveChanges(child, parent);
//			return true;
//		}
//	}
//	public void saveChanges(String child, String parent){
//		
//	}
	
	public String getTopLevelTemplateId(Collection templates) {
		String id = "-1";
		if (templates == null) {
			return id;
		}
		Iterator it = templates.iterator();
		PageTreeNode node = null;
		Object o = null;
		while (it.hasNext()) {
			o = it.next();
			if (o instanceof PageTreeNode) {
				node = (PageTreeNode) o;
				if (node.getParentId() == null) {
					return node.getId();
				}
			}
		}
		return id;
	}
	
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup){
		return createNewPage(parentId, name, type, templateId, pageUri, tree, creatorContext, subType, domainId, format, sourceMarkup, null);
	}
	
	public int createNewPage(String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup, String treeOrder) {
		//return getIBPageHelper().createNewPage(parentId, name, type, templateId, pageUri, tree, creatorContext, subType, domainId, treeOrder);
		return getIBPageHelper().createNewPage(parentId, name, type, templateId, pageUri, tree, creatorContext, subType, domainId, format, sourceMarkup, treeOrder);
//		String parentId, String name, String type, String templateId, String pageUri, Map tree, IWUserContext creatorContext, String subType, int domainId, String format, String sourceMarkup, String orderTree
	}
	
	public boolean deletePage(String pageId, boolean deleteChildren, Map tree, int userId, ICDomain domain) {
		return getIBPageHelper().deletePage(pageId, deleteChildren, tree, userId, domain);
	}
	
	public boolean checkDeletePage(String pageId, ICDomain domain) {
		return getIBPageHelper().checkDeletePage(pageId, domain);
	}
	
	public boolean changePageUriByTitle(String parentId, ICPage page, String pageTitle, int domainId) {
		if (page == null || pageTitle == null) {
			return false;
		}
		ICPage parentPage = null;
		String pageUri = null;
		if(parentId != null){
			try {
				parentPage = getIBPageHelper().getICPageHome().findByPrimaryKey(parentId);
			} catch (FinderException e) {
				e.printStackTrace();
				return false;
			}
			PageUrl pUrl = new PageUrl(parentPage, pageTitle, domainId);
			pageUri = pUrl.getGeneratedUrlFromName();
		}
		else{
			PageUrl pUrl = new PageUrl(pageTitle);
			pageUri = pUrl.getGeneratedUrlFromName();
		}
		if (pageUri != null) {
			page.setDefaultPageURI(pageUri);
			page.store();
		}
		return true;
	}
	
	public boolean movePageToTopLevel(int pageID, IWContext iwc) {
		return getIBPageHelper().movePageToTopLevel(pageID, iwc);
	}
	
	/**
	 * Finds the modules' ids
	 * @param pageKey
	 * @param moduleClass
	 * @return
	 */
	public List<String> getModuleId(String pageKey, String moduleClass) {
		if (pageKey == null || moduleClass == null) {
			return null;
		}
		IBXMLPage page = getIBXMLPage(pageKey);
		if (page == null) {
			return null;
		}
		XMLDocument doc = page.getXMLDocument();
		if (doc == null) {
			return null;
		}
		Document d = (Document) doc.getDocument();
		Iterator elements = d.getDescendants();
		if (elements == null) {
			return null;
		}
		Element element = null;
		Attribute elementClass = null;
		Attribute elementId = null;
		Object o = null;
		List<String> ids = new ArrayList<String>();
		for (Iterator it = elements; it.hasNext();) {
			o = it.next();
			if (o instanceof Element) {
				element = (Element) o;
			}
			elementClass = element.getAttribute(IBXMLConstants.CLASS_STRING);
			if (elementClass != null) {
				if (moduleClass.equals(elementClass.getValue())) {
					elementId = element.getAttribute(IBXMLConstants.ID_STRING);
					if (elementId != null) {
						if (!ids.contains(elementId.getValue())) {
							ids.add(elementId.getValue());
						}
					}
				}
			}
		}
		return ids;
	}
	
	/**
	 * Checks if exact value is set to properties parameter
	 * @param pageKey
	 * @param moduleId
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	public boolean isPropertyValueSet(String pageKey, String moduleId, String propertyName, String propertyValue) {
		if (pageKey == null || moduleId == null || propertyName == null || propertyValue == null) {
			return false;
		}
		
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (xml == null) {
			return false;
		}
		XMLElement parent = getIBXMLWriter().findModule(xml, moduleId);
		if (parent == null) {
			return false;
		}
		XMLElement property = getIBXMLWriter().findProperty(parent, propertyName);
		if (property == null) {
			return false;
		}
		XMLAttribute value = property.getAttribute(IBXMLConstants.VALUE_STRING);
		if (value == null) {
			return false;
		}
		if (value.getValue() == null) {
			return false;
		}
		if (value.getValue().indexOf(propertyValue) == -1) {
			return false;
		}
		return true;
	}
	
	/**
	 * Finds exact value and removes from property value (attribute) string
	 * @param pageKey
	 * @param moduleId
	 * @param propertyName
	 * @param valueToRemove
	 * @return
	 */
	public boolean removeValueFromModuleProperty(String pageKey, String moduleId, String propertyName, String valueToRemove) {
		if (pageKey == null || moduleId == null || propertyName == null || valueToRemove == null) {
			return false;
		}
		IBXMLPage xml = getIBXMLPage(pageKey);
		if (xml == null) {
			return false;
		}
		XMLElement parent = getIBXMLWriter().findModule(xml, moduleId);
		if (parent == null) {
			return false;
		}
		XMLElement property = getIBXMLWriter().findProperty(parent, propertyName);
		if (property == null) {
			return false;
		}
		XMLAttribute value = property.getAttribute(IBXMLConstants.VALUE_STRING);
		if (value == null) {
			return false;
		}
		if (value.getValue() == null) {
			return false;
		}
		if (value.getValue().indexOf(valueToRemove) == -1) {
			return false;
		}
		String[] propertyValues = value.getValue().split(IBXMLConstants.COMMA_STRING);
		if (propertyValues == null) {
			property.detach();
			xml.store();
			return true;
		}
		if (propertyValues.length == 0) {
			property.detach();
		}
		
		StringBuffer newValue = new StringBuffer();
		boolean foundValueToRemove = false;
		boolean canAppendValue = true;
		for (int i = 0; i < propertyValues.length; i++) {
			canAppendValue = true;
			if (!foundValueToRemove) {
				if (propertyValues[i].equals(valueToRemove)) {
					foundValueToRemove = true;
					canAppendValue = false;
				}
			}
			if (canAppendValue) {
				if (i > 0 && propertyValues.length > 2) {
					newValue.append(IBXMLConstants.COMMA_STRING);
				}
				newValue.append(propertyValues[i]);
			}
		}
		if (newValue.toString().equals(IBXMLConstants.EMPTY_STRING)) {
			property.detach();
		}
		else {
			value.setValue(newValue.toString());
		}
		
		xml.store();
		return true;
	}

	public void setTreeOrder(int id, int order){
		IBPageHelper.getInstance().setTreeOrder(id, order);
	}

	public int getTreeOrder(int id){
		return IBPageHelper.getInstance().getTreeOrder(id);
	}
	
	public void increaseTreeOrder(int id){
		IBPageHelper.getInstance().increaseTreeOrder(id);
	}

	public void decreaseTreeOrder(int id){
		IBPageHelper.getInstance().decreaseTreeOrder(id);
	}
	
	public int setAsLastInLevel(boolean isTopLevel, String parentId){
		return IBPageHelper.getInstance().setAsLastInLevel(isTopLevel, parentId);
	}
	
	public String getExistingPageKeyByURI(String requestURI,ICDomain domain) {
		int indexOfPage = requestURI.indexOf(PAGES_PREFIX);
		boolean requestingRoot = false;
		if (indexOfPage == -1) {
			BuilderPageException pe = new BuilderPageException("Page Cannot be Found for URI: '"+requestURI+"'");
			pe.setCode(BuilderPageException.CODE_NOT_FOUND);
			pe.setPageUri(requestURI);
			throw pe;
		}
		else{
			if(requestURI.equals(PAGES_PREFIX)){
				requestingRoot=true;
			}
		}
		if(requestingRoot){
			int pageId = domain.getStartPageID();
			return Integer.toString(pageId);
		}
		String iPageId = null;
		String uriWithoutPages = requestURI;
		//this string will be tried to convert to a number
		String uriForNumberParse = requestURI;
		if(indexOfPage!=-1) {
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
		catch (NumberFormatException nfe) {
			//the string is not a number:
			//try to find the page in the cache first:
			String pageKey = getPageKeyByURICached(requestURI);			
			if (pageKey != null) { // Should be null if not found, but if found - checking if page is valid
				if (getPageCacher().isPageValid(pageKey)) {
					return pageKey;
				}
			}
			//if it isn't found in the cache try the database:
			ICPage page = getICPageByURIFromDatabase(uriWithoutPages);
			if (page == null) {
				return null;
			}
			else {
				return page.getPageKey();
			}
		}
	}
	
	private ICPage getICPageByURIFromDatabase(String uri) {
		if (uri == null) {
			return null;
		}
		try {
			ICPageHome pageHome = (ICPageHome)IDOLookup.getHome(ICPage.class);
			return pageHome.findExistingByUri(uri, getCurrentDomain().getID());
		}
		catch (IDOLookupException e) {
		}
		catch(FinderException fe) {
		}
		return null;
	}
	
	private Web2Business getWeb2Business(IWContext iwc) {
		if (web2 == null) {
			synchronized (BuilderLogic.class) {
				try {
					if (iwc == null) {
						iwc = IWContext.getInstance();
					}
					web2 = (Web2Business) IBOLookup.getServiceInstance(iwc, Web2Business.class);
				} catch (IBOLookupException e) {
					e.printStackTrace();
				}
			}
		}
		return web2;
	}
	
	public String getUriToObject(Class objectClass) {
		if (objectClass == null) {
			return null;
		}
		String className = objectClass.getName();
		StringBuffer uri = new StringBuffer("/servlet/ObjectInstanciator?").append(IWMainApplication.classToInstanciateParameter);
		uri.append("=").append(className);
		return uri.toString();
	}
	
	@SuppressWarnings("deprecation")
	public boolean removeBlockObjectFromCache(IWContext iwc, String cacheKey) {
		if (iwc == null || cacheKey == null) {
			return false;
		}
		IWCacheManager cache = iwc.getIWMainApplication().getIWCacheManager();
		if (cache == null) {
			return false;
		}
		if (cache.isCacheValid(cacheKey)) {
			cache.invalidateCache(cacheKey);
		}
		else {
			List <String> modifiedKeys = new ArrayList<String>();
			Map cached = cache.getCacheMap();
			if (cached != null) {
				for (Iterator it = cached.keySet().iterator(); it.hasNext(); ) {
					modifiedKeys.add(it.next().toString());
				}
			}
			if (modifiedKeys.size() == 0) {
				return false;
			}
			for (int i = 0; i < modifiedKeys.size(); i++) {
				if (modifiedKeys.get(i).startsWith(cacheKey)) {
					cache.invalidateCache(modifiedKeys.get(i));
				}
			}
		}
		
		return true;
	}
	
	public void renameRegion(String pageKey, String region_id, String region_label, String new_region_id, String new_region_label) {
		
		if (pageKey == null || region_id == null || new_region_id == null) {
			throw new NullPointerException(
					"Either is not provided: "+
					"\npageKey: "+pageKey+
					"\nregion_id: "+region_id+
					"\nnew_region_id: "+new_region_id
			);
		}
		IBXMLPage xml = getIBXMLPage(pageKey);
		
		if (xml == null)
			throw new NullPointerException("Page not found by key provided: "+pageKey);
		
		XMLElement region = getIBXMLWriter().findRegion(xml, region_label == null ? region_id : region_label, region_id);
		
		if(region == null)
			throw new NullPointerException(
					"Region not found by values provided: "+
					"\nregion_id: "+region_id+
					"\nregion_label: "+region_label
			);
		
		region.setAttribute(IBXMLConstants.ID_STRING, new_region_id);
		region.setAttribute(IBXMLConstants.LABEL_STRING, new_region_label == null ? new_region_id : new_region_label);
		xml.store();
	}
	
	/**
	 * Renders single PresentationObject
	 * @param iwc
	 * @param object - object to render
	 * @param cleanHtml
	 * @return String of rendered object or null
	 */
	public String getRenderedPresentationObjectAsString(IWContext iwc, PresentationObject object, boolean cleanHtml) {
		//	Writing (rendering) object to ResponseWriter
		HtmlBufferResponseWriterWrapper writer = HtmlBufferResponseWriterWrapper.getInstance(iwc.getResponseWriter());
		iwc.setResponseWriter(writer);		
		try {
			object.renderComponent(iwc);
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
		
		String renderedObject = writer.toString();
//		System.out.println("Rendered object: " + renderedObject);
		
		if (cleanHtml) {
			// Cleaning - need valid XML structure
			HtmlCleaner cleaner = new HtmlCleaner(renderedObject);
			cleaner.setOmitDoctypeDeclaration(true);
			try {
				cleaner.clean();
				renderedObject = cleaner.getPrettyXmlAsString();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
//			System.out.println("Cleaned object: " + renderedObject);
		}
		
		return renderedObject;
	}
	
	/**
	 * Renders single PresentationObject and created JDOM Document of rendered object
	 * @param iwc
	 * @param object - object to render
	 * @param cleanHtml
	 * @return JDOM Document or null
	 */
	public Document getRenderedPresentationObject(IWContext iwc, PresentationObject object, boolean cleanHtml) {
		String rendered = getRenderedPresentationObjectAsString(iwc, object, cleanHtml);
		if (rendered == null) {
			return null;
		}
		
		// Building JDOM Document
		InputStream stream = new ByteArrayInputStream(rendered.getBytes());
		SAXBuilder sax = new SAXBuilder(false);
		Document renderedObject = null;
		try {
			renderedObject = sax.build(stream);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeStream(stream);
		}
		
		if (cleanHtml) {
			// After clean up, the whole <html> document is created, but our real component is in html > body > real component
			Document realComponentContent = getRealComponentContent(renderedObject);
			if (realComponentContent != null) {
				return realComponentContent;
			}
		}
		
		return renderedObject;
	}
	
	private Document getRealComponentContent(Document renderedObject) {
		if (renderedObject == null) {
			return null;
		}
		Element root = renderedObject.getRootElement();
		if (root == null) {
			return null;
		}
		Element body = root.getChild("body");
		if (body == null) {
			return null;
		}
		List oldContent = body.getContent();
		if (oldContent == null) {
			return null;
		}
		List<Content> needless = new ArrayList<Content>();
		Content c = null;
		for (int i = 0; i < oldContent.size(); i++) {
			c = (Content) oldContent.get(i);
			if (!(c instanceof Element)) {
				needless.add(c);
			}
		}
		for (int i = 0; i < needless.size(); i++) {
			needless.get(i).detach();
		}
		
		try {
			return new Document(body.cloneContent());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void closeStream(InputStream stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}