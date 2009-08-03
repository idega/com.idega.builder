/*
 * $Id: BuilderLogic.java,v 1.380 2009/06/17 12:35:07 valdas Exp $ Copyright
 * (C) 2001 Idega hf. All Rights Reserved. This software is the proprietary
 * information of Idega hf. Use is subject to license terms.
 */
package com.idega.builder.business;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.FinderException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKitFactory;

import org.apache.myfaces.renderkit.html.util.HtmlBufferResponseWriterWrapper;
import org.htmlcleaner.HtmlCleaner;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.beans.factory.annotation.Autowired;

import com.idega.block.web2.business.JQuery;
import com.idega.block.web2.business.Web2Business;
import com.idega.builder.bean.AdminToolbarSession;
import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.facelets.BuilderFaceletConverter;
import com.idega.builder.presentation.AddModuleBlock;
import com.idega.builder.presentation.AdminToolbar;
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
import com.idega.business.chooser.helper.CalendarsChooserHelper;
import com.idega.business.chooser.helper.GroupsChooserHelper;
import com.idega.cal.bean.CalendarPropertiesBean;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.core.accesscontrol.business.StandardRoles;
import com.idega.core.builder.business.BuilderPageException;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.builder.data.CachedDomain;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageBMPBean;
import com.idega.core.builder.data.ICPageHome;
import com.idega.core.builder.presentation.ICPropertyHandler;
import com.idega.core.component.bean.RenderedComponent;
import com.idega.core.component.business.ICObjectBusiness;
import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.core.component.data.ICObjectInstanceHome;
import com.idega.core.data.GenericGroup;
import com.idega.core.data.ICTreeNode;
import com.idega.core.localisation.business.ICLocaleBusiness;
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
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWResourceBundle;
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
import com.idega.presentation.Span;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.HiddenInput;
import com.idega.repository.data.Instantiator;
import com.idega.repository.data.Singleton;
import com.idega.repository.data.SingletonRepository;
import com.idega.servlet.filter.BaseFilter;
import com.idega.slide.business.IWSlideService;
import com.idega.slide.business.IWSlideSession;
import com.idega.user.bean.PropertiesBean;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.FileUtil;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.PresentationUtil;
import com.idega.util.RenderUtils;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;
import com.idega.util.URIUtil;
import com.idega.util.expression.ELUtil;
import com.idega.util.reflect.MethodFinder;
import com.idega.util.reflect.MethodInvoker;
import com.idega.util.reflect.PropertyCache;
import com.idega.util.xml.XmlUtil;
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
 * 
 * Last modified: $Date: 2009/06/17 12:35:07 $ by $Author: valdas $
 * @version 1.0
 */
public class BuilderLogic implements Singleton {

	private static final Logger logger = Logger.getLogger(BuilderLogic.class.getName());
	
	private static final String PAGES_PREFIX = "/pages/";
	public static final String IB_PARENT_PARAMETER = "ib_parent_par";
	public static final String IB_LABEL_PARAMETER = "ib_label";

	public static final String IB_LIBRARY_NAME = "ib_library_name";
	public static final String ACTION_DELETE = "ACTION_DELETE";
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
	
	private Pattern doctypeReplacementPattern;
	private Pattern commentinHtmlReplacementPattern;
	private Pattern xmlEncodingReplacementPattern = null;
	
	private static Instantiator instantiator = new Instantiator() {
		@Override
		public Object getInstance() {
			return new BuilderLogic();
		}
	};

	public static final String PAGE_FORMAT_IBXML="IBXML";
	public static final String PAGE_FORMAT_HTML="HTML";
	public static final String PAGE_FORMAT_JSP_1_2="JSP_1_2";
	public static final String PAGE_FORMAT_FACELET="FACELET";
	public static final String PAGE_FORMAT_IBXML2="IBXML2";
	
	private String[] pageFormats = {PAGE_FORMAT_IBXML,PAGE_FORMAT_IBXML2,PAGE_FORMAT_HTML,PAGE_FORMAT_JSP_1_2,PAGE_FORMAT_FACELET};
	
	//private volatile Web2Business web2 = null;
	private IWSlideService slideService = null;
	private XMLOutputter outputter = null;
	
	@Autowired
	private Web2Business web2;
	@Autowired
	private JQuery jQuery;
	
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

	public IBXMLPage getIBXMLPage(String pageKey) throws Exception {
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

	private String getBuilderSessionMode() {
		AdminToolbarSession session = null;
		try {
			session = ELUtil.getInstance().getBean(AdminToolbarSession.class);
		} catch(Exception e) {
			logger.log(Level.WARNING, "Error getting bean: " + AdminToolbarSession.class, e);
		}
		if (session == null) {
			return null;
		}
		return session.getMode();
	}
	
	/**
	 *  	 *
	 */
	public Page getBuilderTransformed(String pageKey, Page page, IWContext iwc) {
		IWBundle iwb = getBuilderBundle();
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		
		String builderMode = getBuilderSessionMode();
		addResourcesForBuilderEditMode(iwc, iwb, builderMode);
		
		//if we want to use Sortable (javascript from the DnD library) someday
		page.setID("DnDPage");
		
		//Begin with transforming the objects on a normal Page object (constructed from IBXML)
		List<UIComponent> list = page.getChildren();
		if (!ListUtil.isEmpty(list)) {
			PresentationObjectContainer parent = page;
			int index = 0;
			for (UIComponent item: list) {
				getTransformedObject(page,pageKey, item, index, parent, "-1", iwc);
				index++;
			}
		}
		
		if (iwc.getRequestURI().indexOf("/workspace/") == -1 && iwc.getRequestURI().indexOf("/pages") != -1 &&
				(iwc.hasRole(StandardRoles.ROLE_KEY_ADMIN) || iwc.hasRole(StandardRoles.ROLE_KEY_AUTHOR) || iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR))) {
			page.getChildren().add(new AdminToolbar());
			page.setStyleClass("isAdmin");
			
			if (builderMode != null) {
				page.setStyleClass(builderMode);
			}
		}
		else if (iwc.hasRole(StandardRoles.ROLE_KEY_ADMIN) || iwc.hasRole(StandardRoles.ROLE_KEY_AUTHOR) || iwc.hasRole(StandardRoles.ROLE_KEY_EDITOR)) {
			page.setStyleClass("isContentAdmin");
			page.setStyleClass("isEditAdmin");
		}
		
		String addModuleUri = null;
		//"-1" is identified as the top page object (parent)
		if (page.getIsExtendingTemplate()) {
			if (!page.isLocked()) {
				String parentKey = Integer.toString(-1);
				String regionKey = "page";
				addModuleUri = getUriToAddModuleWindow(regionKey);
				Layer marker = getLabelMarker(parentKey, regionKey);
				addButtonsLayer(marker, addModuleUri, regionKey, iwrb, marker.getId());
				marker.addAtBeginning(new CSSSpacer());
				page.add(marker);
			}
			if (page instanceof HtmlPage) {
				HtmlPage hPage = (HtmlPage) page;
				for (String regionKey: hPage.getRegionIds()) {
					addModuleUri = getUriToAddModuleWindow(regionKey);
					Layer marker = getLabelMarker(regionKey, regionKey);
					addButtonsLayer(marker, addModuleUri, regionKey, iwrb, marker.getId());
					hPage.add(marker,regionKey);
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
					for (String regionKey: hPage.getRegionIds()) {
						Text regionText = new Text(regionKey);
						regionText.setFontColor("red");
						hPage.add(regionText,regionKey);
					}
				}
			}
			
			Layer buttonsLayer = null;
			if(mayAddButtonsInPage){
				String parentKey = Integer.toString(-1);
				String regionKey = "page";
				addModuleUri = getUriToAddModuleWindow(regionKey);
				Layer marker = getLabelMarker(parentKey, regionKey);
				buttonsLayer = addButtonsLayer(marker, addModuleUri, regionKey, iwrb, marker.getId());
				page.add(marker);
			}
			
			if (page.getIsTemplate()){
				if (page.isLocked()){
					if (buttonsLayer != null) {
						buttonsLayer.add(getLockedIcon(Integer.toString(-1), iwc, null));
					}
					else {
						page.add(getLockedIcon(Integer.toString(-1), iwc, null));
					}
				}
				else{
					if (buttonsLayer != null) {
						buttonsLayer.add(getUnlockedIcon(Integer.toString(-1), iwc));
					}
					else {
						page.add(getUnlockedIcon(Integer.toString(-1), iwc));
					}
				}
			}
		}
		return (page);
	}

	public void addResourcesForBuilderEditMode(IWContext iwc){
		IWBundle builderBundle = iwc.getIWMainApplication().getBundle(BuilderConstants.IW_BUNDLE_IDENTIFIER);
		addResourcesForBuilderEditMode(iwc, builderBundle, getBuilderSessionMode());
	}
	
	private Web2Business getWeb2Business() {
		if (web2 == null) {
			ELUtil.getInstance().autowire(this);
		}
		return web2;
	}
	
	private JQuery getJQUery() {
		if (jQuery == null) {
			ELUtil.getInstance().autowire(this);
		}
		return jQuery;
	}
	
	private void addResourcesForBuilderEditMode(IWContext iwc, IWBundle builderBundle, String mode) {
		Web2Business web2 = getWeb2Business();
		
		//	JavaScript
		try {
			PresentationUtil.addJavaScriptSourcesLinesToHeader(iwc, Arrays.asList(
					CoreConstants.DWR_ENGINE_SCRIPT,
					CoreConstants.DWR_UTIL_SCRIPT,
					"/dwr/interface/BuilderEngine.js",
					builderBundle.getVirtualPathWithFileNameString("javascript/BuilderHelper.js"),
					builderBundle.getVirtualPathWithFileNameString("javascript/BuilderDragDropHelper.js"),
					web2.getBundleURIToMootoolsLib(),
					web2.getMoodalboxScriptFilePath(false),
					web2.getReflectionForMootoolsScriptFilePath(),
					web2.getBundleUriToMootabsScript()
			));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//	JavaScript actions
		mode = mode == null ? CoreConstants.EMPTY : mode;
		PresentationUtil.addJavaScriptActionsToBody(iwc, Arrays.asList(
				new StringBuilder("window.addEvent('domready', function() { BuilderHelper.initializeBuilder('").append(mode).append("'); });").toString(),
				"window.addEvent('resize', intializeMoodalboxInBuilder);",
				"window.addEvent('beforeunload', showMessageForUnloadingPage);"
		));
		
		//	CSS
		try {
			PresentationUtil.addStyleSheetsToHeader(iwc, Arrays.asList(
					builderBundle.getVirtualPathWithFileNameString("style/builder.css"),
					web2.getMoodalboxStyleFilePath(),
					web2.getBundleUriToMootabsStyle()
			));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Layer getLabelMarker(String instanceId, String parentKey) {
		Layer marker = new Layer(Layer.DIV);
		marker.setStyleClass("regionLabel");
		
		if (instanceId == null) {
			Random generator = new Random();
			marker.setId(new StringBuffer("region_label").append(generator.nextInt(Integer.MAX_VALUE)).toString());
		}
		else {
			marker.setMarkupAttribute("instanceid", instanceId);
			
			Layer labelContainer = new Layer();
			marker.add(labelContainer);
			HiddenInput regionLabel = new HiddenInput("region_label", instanceId);
			marker.add(regionLabel);
			if (instanceId.indexOf(BuilderConstants.DOT) != -1) {
				Random generator = new Random();
				marker.setId(new StringBuffer("region_label").append(generator.nextInt(Integer.MAX_VALUE)).toString());
			}
			else {
				marker.setId(new StringBuffer("region_label").append(instanceId).toString());
			}
		}
		
		if (parentKey != null) {
			marker.add(new HiddenInput("parentKey", parentKey));
		}

		return marker;
	}

	public Page getPermissionTransformed(int groupId, Page page, IWContext iwc) {
		List<Integer> groupIds = new ArrayList<Integer>();
		groupIds.add(groupId);
		try {
			List groups = AccessControl.getPermissionGroups(((com.idega.core.data.GenericGroupHome) com.idega.data.IDOLookup.getHomeLegacy(GenericGroup.class)).findByPrimaryKeyLegacy(groupId));
			if (groups != null) {
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
					com.idega.core.data.GenericGroup item = (GenericGroup) iter.next();
					groupIds.add(item.getID());
				}
			}
		}
		catch (Exception ex) {
			// empty block
		}

		int index = 0;
		for (UIComponent item: page.getChildren()) {
			if (item instanceof PresentationObject) {
				filterForPermission(groupIds, (PresentationObject) item, page, index, iwc);
			}
			index++;
		}

		return page;
	}

	private void filterForPermission(List<Integer> groupIds, PresentationObject obj, PresentationObjectContainer parentObject, int index, IWContext iwc) {
		if (!iwc.hasViewPermission(groupIds, obj)) {
			logger.severe(obj + ": removed");
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

	public PresentationObject getTransformedObject(Page currentPage, String pageKey, UIComponent obj, int index, PresentationObjectContainer parent, String parentKey, IWContext iwc) {
		IWResourceBundle iwrb = getBuilderBundle().getResourceBundle(iwc);
		
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
				getTransformedTable(currentPage, pageKey, obj, iwc, clipboardEmpty);
			}
			else {
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
							getTransformedObject(currentPage,pageKey, item, index2, (PresentationObjectContainer) obj, parentKey, iwc);
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
							
							getTransformedObject(currentPage,pageKey, item, index2, (PresentationObjectContainer) obj, newParentKey, iwc);
						}
					}
				}
				if (index != -1) {
					Page curr = getPageCacher().getComponentBasedPage(getCurrentIBPage(iwc)).getNewPage(iwc);
					PresentationObjectContainer container = ((PresentationObjectContainer) obj);
					String instanceId = getInstanceId(obj);
					if (instanceId == null) {
						instanceId = obj.getId();
					}
					
					String regionLabel = container.getLabel();
					String addModuleUri = getUriToAddModuleWindow(regionLabel);
					if (curr.getIsExtendingTemplate()) {
						if (container.getBelongsToParent()) {
							if (!container.isLocked()) {
								Layer marker = getLabelMarker(instanceId, regionLabel);
								addButtonsLayer(marker, addModuleUri, regionLabel, iwrb, marker.getId());
								container.add(marker);
							}
						}
						else {
							Layer marker = getLabelMarker(instanceId, regionLabel);
							Layer buttons = addButtonsLayer(marker, addModuleUri, regionLabel, iwrb, marker.getId());
							container.add(marker);
							
							if (curr.getIsTemplate()) {
								buttons.add(getLabelIcon(instanceId, iwc, regionLabel));
								if (container.isLocked()){
									buttons.add(getLockedIcon(instanceId, iwc, regionLabel));
								}
								else{
									buttons.add(getUnlockedIcon(instanceId, iwc));
								}
							}
							
							
						}
					}
					else {
						Layer marker = getLabelMarker(instanceId, regionLabel);
						Layer buttons = addButtonsLayer(marker, addModuleUri, regionLabel, iwrb, marker.getId());
						container.add(marker);
						
						if (curr.getIsTemplate()) {
							marker.add(getLabelIcon(instanceId, iwc, regionLabel));
							if (container.isLocked()){
								buttons.add(getLockedIcon(instanceId, iwc, regionLabel));
							}
							else{
								buttons.add(getUnlockedIcon(instanceId, iwc));
							}
						}
						
					}
				}
			}
		}
		PresentationObject transformed = null;
		if ((isPresentationObject && ((PresentationObject) obj).getUseBuilderObjectControl()) || !isPresentationObject) {
			if (index != -1) {
				boolean lastModuleInRegion = false;
				if (index >= parent.getChildCount()) {
					lastModuleInRegion = true;
				} else if (index == (parent.getChildCount() - 1)) {
					lastModuleInRegion = true;
				}
				
				boolean objectFromCurrentPage = true;
				try {
					IBXMLPage page = getIBXMLPage(pageKey);
					objectFromCurrentPage = getIBXMLWriter().findModule(page, getInstanceId(obj)) != null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				transformed = new IBObjectControl(obj, parent, parentKey, iwc, index, lastModuleInRegion, objectFromCurrentPage);
				
				if (index < parent.getChildCount()) {	
					parent.set(index, transformed);
				}
				else {
					parent.add(transformed);
					index++;
				}
			}
			return transformed;
		}
		if (isPresentationObject) {
			return (PresentationObject) obj;
		}
		return null;
	}

	/**
	 * @param currentPage
	 * @param pageKey
	 * @param obj
	 * @param iwc
	 * @param clipboardEmpty
	 */
	public PresentationObject getTransformedTable(Page currentPage, String pageKey, UIComponent obj, IWContext iwc, boolean clipboardEmpty) {
		IWResourceBundle iwrb = getBuilderBundle().getResourceBundle(iwc);
		
		Table tab = (Table) obj;
		int cols = tab.getColumns();
		int rows = tab.getRows();
		for (int x = 1; x <= cols; x++) {
			for (int y = 1; y <= rows; y++) {
				PresentationObjectContainer moc = tab.containerAt(x, y);
				String newParentKey = tab.getICObjectInstanceID() + CoreConstants.DOT + x + CoreConstants.DOT + y;
				if (moc != null) {
					String id = newParentKey;
					getTransformedObject(currentPage, pageKey, moc, -1, tab, newParentKey, iwc);
					id = id.replace(CoreConstants.DOT, CoreConstants.UNDER);
					moc.setId(id);
				}
				
				String regionLabel = tab.getLabel(x, y);
				String addModuleUri = getUriToAddModuleWindow(regionLabel);
				if (currentPage.getIsExtendingTemplate()) {
					if (tab.getBelongsToParent()) {
						if (!tab.isLocked(x, y)) {
							Layer marker = getLabelMarker(newParentKey, regionLabel);
							addButtonsLayer(marker, addModuleUri, regionLabel, iwrb, marker.getId());
							tab.add(marker, x, y);
						}
					}
					else {
						Layer marker = getLabelMarker(newParentKey, regionLabel);
						Layer buttons = addButtonsLayer(marker, addModuleUri, regionLabel, iwrb, marker.getId());
						tab.add(marker, x, y);
						if (currentPage.getIsTemplate()) {
							marker.add(getLabelIcon(newParentKey, iwc, regionLabel));
							if (tab.isLocked(x, y)){
								buttons.add(getLockedIcon(newParentKey, iwc, regionLabel));
							}
							else{
								buttons.add(getUnlockedIcon(newParentKey, iwc));
							}
						}
					}
				}
				else {
					Layer marker = getLabelMarker(newParentKey, regionLabel);
					Layer buttons = addButtonsLayer(marker, addModuleUri, regionLabel, iwrb, marker.getId());
					tab.add(marker, x, y);
					if (currentPage.getIsTemplate()) {
						marker.add(getLabelIcon(newParentKey, iwc, regionLabel));
						if (tab.isLocked(x, y)){
							buttons.add(getLockedIcon(newParentKey, iwc, regionLabel));
						}
						else{
							buttons.add(getUnlockedIcon(newParentKey, iwc));
						}
					}
					
				}
			}
		}
		return tab;
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
		if (theReturn == null) {
			return -1;
		}
		try {
			return Integer.parseInt(theReturn);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		return -1;
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
			if(pageFormat!=null && pageFormat.equals(PAGE_FORMAT_FACELET)||pageFormat.equals(PAGE_FORMAT_IBXML2)){
				CachedBuilderPage page = getCachedBuilderPage(pageKey);
				BuilderFaceletConverter converter = new BuilderFaceletConverter(page,pageFormat,stringSourceMarkup);
				converter.convert();
				stringSourceMarkup = converter.getConvertedMarkupString();
			}
			
			
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return IBPropertyHandler.getInstance().getPropertyValues(iwma, xml, instanceId, propertyName, selectedValues, returnSelectedValueIfNothingFound);
	}

	public boolean removeProperty(IWMainApplication iwma, String pageKey, String instanceId, String propertyName, String[] values) {
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (getIBXMLWriter().removeProperty(iwma, xml, instanceId, propertyName, values)) {
			xml.store();
			return true;
		}
		return false;
	}
	
	public boolean removeModuleProperty(String pageKey, String moduleId, String propertyName) {
		if (pageKey == null || moduleId == null || propertyName == null) {
			return false;
		}
		
		IWMainApplication iwma = null;
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			iwma = IWMainApplication.getDefaultIWMainApplication();
		}
		else {
			iwma = iwc.getIWMainApplication();
		}
		if (iwma == null) {
			return false;
		}
		
		String[] values = getPropertyValues(iwma, pageKey, moduleId, propertyName, null, true);
		if (values == null) {
			return false;
		}
		boolean result = removeProperty(iwma, pageKey, moduleId, propertyName, values);
		
		if (result) {
			removeBlockObjectFromCache(iwc, BuilderConstants.SET_MODULE_PROPERTY_CACHE_KEY);
			removeBlockObjectFromCache(iwc, BuilderConstants.EDIT_MODULE_WINDOW_CACHE_KEY);
		}
		
		return result;
	}

	/**
	 * Returns the first property if there is an array of properties set
	 */
	public String getProperty(String pageKey, String instanceId, String propertyName) {
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
	public boolean setProperty(IWContext iwc, String pageKey, String instanceId, String propertyName, List<AdvancedProperty> properties) {
		if (iwc == null || pageKey == null || instanceId == null || propertyName == null || properties == null) {
			return false;
		}
		
		String[] propertyValues = null;
		boolean allowMultivalued = false;
		try {
			allowMultivalued = isPropertyMultivalued(propertyName, instanceId, iwc.getIWMainApplication(), pageKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (allowMultivalued) {
			propertyValues = new String[properties.size()];
			for (int i = 0; i < properties.size(); i++) {
				propertyValues[i] = properties.get(i).getValue();
			}
		}
		else {
			propertyValues = modifyPropertyValuesRegardingParameterType(pageKey, instanceId, propertyName, properties);
		}
		
		if (propertyValues == null) {
			//	Can not set value(s), removing old value(s) if exist such
			String values[] = getPropertyValues(iwc.getIWMainApplication(),pageKey, instanceId, propertyName, null, true);
			if (removeProperty(iwc.getIWMainApplication(), pageKey, instanceId, propertyName, values)) {
				return removeAllBlockObjectsFromCache(iwc);
			}
		}
		
		if (setProperty(pageKey, instanceId, propertyName, propertyValues, iwc.getIWMainApplication())) {
			removeBlockObjectFromCache(iwc, BuilderConstants.SET_MODULE_PROPERTY_CACHE_KEY);
			removeBlockObjectFromCache(iwc, BuilderConstants.EDIT_MODULE_WINDOW_CACHE_KEY);
			return true;
		}
		return false;
	}

	/**
	 * Returns true if properties changed, or error, else false
	 */
	public boolean setProperty(String pageKey, String instanceId, String propertyName, String[] propertyValues, IWMainApplication iwma) {
		try {
			boolean allowMultivalued = isPropertyMultivalued(propertyName, instanceId, iwma, pageKey);
			
			IBXMLPage xml = getIBXMLPage(pageKey);
			
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
	
	private String[] modifyPropertyValuesRegardingParameterType(String pageKey, String instanceId, String propertyName, List<AdvancedProperty> values) {
		if (pageKey == null || instanceId == null || propertyName == null || values == null) {
			return null;
		}
		if (values.size() == 0) {
			return null;
		}
		
		String[] propertyValues = null;
		
		try {
			Class<?> clazz = Class.forName(getModuleClassName(pageKey, instanceId));
			Method method = getMethodFinder().getMethod(propertyName, clazz);
			
			Class<?>[] types = method.getParameterTypes();
			
			if (types != null && types.length > 0) {
				//	TODO: add ability to register specific types handlers (e.g. handler for List etc)
				
				//	List
				if(types[0].equals(List.class)) {
					StringBuilder value = new StringBuilder();

					for (int i = 0; i < values.size(); i++) {
						value.append(values.get(i).getValue())
						.append(ICBuilderConstants.BUILDER_MODULE_PROPERTY_VALUES_SEPARATOR);
					}
					
					return new String[] {value.toString()};
				}
				
				//	PropertiesBean
				if (types[0].equals(PropertiesBean.class)) {
					GroupsChooserHelper helper = new GroupsChooserHelper();
					return helper.getPropertyValue(values, true);
				}
				
				//	CalendarPropertiesBean
				if (types[0].equals(CalendarPropertiesBean.class)) {
					CalendarsChooserHelper helper = new CalendarsChooserHelper();
					return helper.getPropertyValue(values, false);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		propertyValues = new String[values.size()];
		for (int i = 0; i < values.size(); i++) {
			propertyValues[i] = values.get(i).getValue();
		}
		return propertyValues;
	}
	
	private MethodFinder getMethodFinder() {
		return MethodFinder.getInstance();
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
	
	public boolean addPropertyToModules(String pageKey, List<String> moduleIds, String propName, String propValue) {
		if (moduleIds == null) {
			return false;
		}
		for (int i = 0; i < moduleIds.size(); i++) {
			addPropertyToModule(pageKey, moduleIds.get(i), propName, propValue);
		}
		return true;
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
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		deleteBlock(instanceId, pageKey);
		boolean result = getIBXMLWriter().deleteModule(page, parentObjectInstanceID, instanceId);
		
		if (result) {
			return savePage(page, session);
		}
		return result;
	}

	// add by Aron 20.sept 2001 01:49
	public boolean deleteModule(String pageKey, String parentObjectInstanceID, String instanceId) {
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		XMLElement element = xml.copyModule(instanceId);
		if (element == null) {
			return false;
		}
		XMLElement el = (XMLElement) element.clone();
		iwc.setSessionAttribute(CLIPBOARD, el);
		return (true);
	}

	/**
	 * 
	 */
	public boolean pasteModuleIntoRegion(IWContext iwc, String pageKey, String regionId, String regionLabel) {
		String instanceId = putModuleIntoRegion(iwc, pageKey, regionId, regionLabel, true);
		return instanceId != null;
	}
	
	public String putModuleIntoRegion(IWContext iwc, String pageKey, String regionId, String regionLabel, boolean changeInstanceId) {
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		XMLElement element = (XMLElement) iwc.getSessionAttribute(CLIPBOARD);
		if (element == null) {
			return null;
		}
		XMLElement toPaste = (XMLElement) element.clone();
		String instanceId = getIBXMLWriter().insertElementLast(page, pageKey, regionId, regionLabel == null ? regionId : regionLabel, toPaste, changeInstanceId);
		if (instanceId == null) {
			return null;
		}
		page.store();
		return instanceId;
	}
	
	public String moveModule(String pageKey, String formerPageKey, String formerParentId, String instanceId, String parentId, IWContext iwc) {
		//	Page to put module into
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (page == null) {
			return null;
		}
		
		//	Page to take module from
		IBXMLPage formerPageForModule = page;
		if (!pageKey.equals(formerPageKey)) {
			try {
				formerPageForModule = getIBXMLPage(formerPageKey);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		if (formerPageForModule == null) {
			return null;
		}
		
		//	Module's XML
		XMLElement moduleXML = getIBXMLWriter().findModule(formerPageForModule, instanceId);
		if (moduleXML == null) {
			return null;
		}
		
		//	Module container's XML
		XMLElement parentXML = getIBXMLWriter().findModule(formerPageForModule, formerParentId);
		if (parentXML == null) {
			return null;
		}
		
		//	Removing module from original page
		boolean success = false;
		try {
			success = getIBXMLWriter().removeElement(parentXML, moduleXML, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!success) {
			return null;
		}
		
		//	Storing page module was taken from (current page will be stored later)
		if (!pageKey.equals(formerPageKey)) {
			formerPageForModule.store();
		}
		
		//	Putting module in new region (and page (if needed))
		return putModuleIntoRegion(iwc, pageKey, parentId, null, false);
	}

	/**
	 *  	 *
	 */
	public boolean pasteModuleAbove(IWUserContext iwc, String pageKey, String parentID, String objectID) {
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		return moveModule(instanceId, pageKey, formerParentId, newParentId, instanceIdToPasteBelow, false);
	}
	
	/**
	 * Copies, cuts and then pastes the module above or below another module
	 * @param instanceId
	 * @param pageKey
	 * @param formerParentId
	 * @param newParentId
	 * @param neighbourInstanceId
	 * @param insertAbove
	 * @return
	 * @throws Exception
	 */
	public boolean moveModule(String instanceId, String pageKey, String formerParentId, String newParentId, String neighbourInstanceId, boolean insertAbove) throws Exception {
		boolean result = false;
		
		//	Page
		IBXMLPage page = getIBXMLPage(pageKey);
		if (page == null) {
			return false;
		}
		
		//	Current XMLElement
		XMLElement moduleXML =  getIBXMLWriter().findModule(page, instanceId);
		if (moduleXML == null) {
			return false;
		}
		
		//	Parent container
		XMLElement parentXML =  getIBXMLWriter().findModule(page, formerParentId);
		if (parentXML == null) {
			return false;
		}
		
		//	Copy of current element
		XMLElement moduleXMLCopy = (XMLElement) moduleXML.clone();
		
		//	Removes element from current region	
		result = removeElement(parentXML, moduleXML, false);
		if (!result) {
			//	Module is in a region of some (parent) module
			result = removeElement(getIBXMLWriter().findRegion(page, null,
					new StringBuilder(IBXMLConstants.REGION_OF_MODULE_STRING).append(formerParentId).toString()), moduleXML, false);
		}
		
		//	Finds real region
		if (result) {
			XMLElement newRegion = getIBXMLWriter().findRegion(page, newParentId, newParentId);
			if (newRegion == null) {
				newParentId = new StringBuilder(IBXMLConstants.REGION_OF_MODULE_STRING).append(newParentId).toString();
			}
			else if (!IBXMLConstants.REGION_STRING.equals(newRegion.getName())) {
				newParentId = new StringBuilder(IBXMLConstants.REGION_OF_MODULE_STRING).append(newParentId).toString();
			}
		}
		else {
			return false;
		}

		//	Inserts to the needed position
		if (StringUtil.isEmpty(neighbourInstanceId)) {
			result = getIBXMLWriter().insertElementLastIntoParentOrRegion(page, pageKey, newParentId, newParentId, moduleXMLCopy);
		}
		else if (insertAbove) {
			result = getIBXMLWriter().insertElementAbove(page, newParentId, moduleXMLCopy, neighbourInstanceId);
		}
		else {
			result = getIBXMLWriter().insertElementBelow(page, newParentId, moduleXMLCopy, neighbourInstanceId);
		}
		
		if (!result) {
			return false;
		}
		
		return page.store();
	}
	
	private boolean removeElement(XMLElement container, XMLElement elementToRemove, boolean removeInstanceId) {
		if (container == null || elementToRemove == null) {
			return false;
		}
		try {
			return getIBXMLWriter().removeElement(container, elementToRemove, removeInstanceId);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean lockRegion(String pageKey, String parentObjectInstanceID) {
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
	
	public String addNewModule(String pageKey, String parentObjectInstanceID, String regionId, int newICObjectID, String label, IWSlideSession session) {
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String id = getIBXMLWriter().addNewModule(page, pageKey, parentObjectInstanceID, regionId, newICObjectID, label);
		if (id == null) {
			return null;
		}
		
		if (savePage(page, session)) {
			return id;
		}
		return null;
	}

	/**
	 *  	 *
	 */
	public boolean addNewModule(String pageKey, String parentObjectInstanceID, int newICObjectID, String label) {
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		//TODO add handling for generic UIComponent adding
		
		String id = getIBXMLWriter().addNewModule(xml, pageKey, parentObjectInstanceID, newICObjectID, label);
		if (id == null) {
			return false;
		}
		xml.store();
		return true;
	}
	
	/**
	 * After inserting new module IBXMLPage is saved (if successfully inserted module) in other thread
	 * @param pageKey
	 * @param parentObjectInstanceID
	 * @param newICObjectID
	 * @param label
	 * @param slideSession
	 * @return
	 */
	public String addNewModule(String pageKey, String parentObjectInstanceID, int newICObjectID, String label, IWSlideSession slideSession) {
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if (slideSession != null) {
			//	Will check if can use thread
			XMLElement region = null;
			try {
				region = getIBXMLWriter().findRegion(page, label, null);
			} catch(Exception e) {
				e.printStackTrace();
			}
			boolean useThread = true;
			if (region == null) {
				useThread = false;
			}
			else {
				List children = region.getChildren();
				if (children == null || children.size() == 0) {
					useThread = false;
				}
			}
			if (!useThread) {
				slideSession = null;
			}
		}
		
		String id = getIBXMLWriter().addNewModule(page, pageKey, parentObjectInstanceID, newICObjectID, label);
		if (id == null) {
			return null;
		}
		
		if (savePage(page, slideSession)) {
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
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String id = getIBXMLWriter().addNewModule(xml, pageKey, parentObjectInstanceID, newObjectType, label);
		if (id == null) {
			return false;
		}
		xml.store();
		return true;	
	}

	public Class<?> getObjectClass(int icObjectInstanceID) {
		try {
			ICObjectInstance instance = ((ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(icObjectInstanceID);
			return instance.getObject().getObjectClass();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isPropertyMultivalued(String propertyName, String instanceId, IWMainApplication iwma, String pageKey) throws Exception {
		int objectId = -1;
		try {
			objectId = Integer.valueOf(instanceId).intValue();
		} catch (NumberFormatException e) {
			objectId = getIBXMLReader().getICObjectInstanceIdFromComponentId(instanceId, null, pageKey);
		}
		if (objectId == -1) {
			return false;
		}
		try {
			Class c = null;
			IWBundle iwb = null;
			if ("-1".equals(instanceId)) {
				c = com.idega.presentation.Page.class;
				iwb = iwma.getBundle(PresentationObject.CORE_IW_BUNDLE_IDENTIFIER);
			}
			else {
				ICObjectInstance instance = ((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).findByPrimaryKeyLegacy(objectId);
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
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, ACTION_PASTE);
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		return getIBPageURL(iwc, pageKey, false);
	}
	
	public String getIBPageURL(IWApplicationContext iwc, String pageKey, boolean checkIfDeleted) {
		if (IWMainApplication.useNewURLScheme) {
			CachedBuilderPage page = getPageCacher().getCachedBuilderPage(pageKey);
			if (page == null) {
				return null;
			}
			
			if (checkIfDeleted && page.getICPage().getDeleted()) {
				return null;
			}
			
			String pageUri = page.getPageUri();
			if (pageUri != null) {
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
		String src = iwc.getIWMainApplication().getIFrameContentURI() + "?" + ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER + "=" + ICObjectInstanceId;
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
				Map<Integer, PageTreeNode> tree = PageTreeNode.getTree(iwc);
				if (tree != null) {
					String currentId = getCurrentIBPage(iwc);
					if (currentId != null) {
						PageTreeNode node = tree.get(Integer.valueOf(currentId));
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			return;
		}
		if (xml == null) {
			return;
		}
		xml.setTemplateId(newTemplateId);
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

	public void changeDPTCrawlableLinkContainerPageIds(int moduleId, String currentPageID, String newLinkedPageIds) {
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(currentPageID);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		XMLElement element = new XMLElement(IBXMLConstants.CHANGE_ROOT_PAGE);
		XMLAttribute id = new XMLAttribute(IBXMLConstants.LINK_ID_STRING, Integer.toString(moduleId));
		XMLAttribute newPageLink = new XMLAttribute(IBXMLConstants.LINK_TO, newLinkedPageIds);
		element.setAttribute(id);
		element.setAttribute(newPageLink);
		getIBXMLWriter().addNewElement(page, "-1", element);
		page.store();
		getPageCacher().flagPageInvalid(currentPageID);
	}
	
	public void changeDPTCrawlableLinkedPageId(int moduleId, String currentPageID, String newLinkedPageId) {
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(currentPageID);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
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
		clearCaches(IWMainApplication.getDefaultIWApplicationContext());
	}
	
	/**
	 * Clears all caches
	 */
	public void clearAllCaches() {
		IWApplicationContext iwac = IWMainApplication.getDefaultIWApplicationContext();
		clearCaches(iwac);
		PageTreeNode.clearTree(iwac);
	}
	
	private void clearCaches(IWApplicationContext iwac) {
		logger.info("Clearing all DomainTree Cache");
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
	public Map<String, String> getPageFormatsSupportedAndDescription(){
		Map<String, String> map = new HashMap<String, String>();
		map.put(PAGE_FORMAT_IBXML,"Builder (IBXML)");
		map.put(PAGE_FORMAT_IBXML2,"Builder Facelet (IBXML2)");
		map.put(PAGE_FORMAT_HTML,"HTML");
		map.put(PAGE_FORMAT_JSP_1_2,"JSP 1.2");
		map.put(PAGE_FORMAT_FACELET,"Facelet");
		return map;
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
	private Layer addButtonsLayer(Layer parent, String uri, String label, IWResourceBundle iwrb, String labelMarkerContainerId) {
		Layer buttons = new Layer();
		buttons.setStyleClass("builderButtons");
		
		parent.addAtBeginning(buttons);
		
		//	Add module button
		Layer addModuleContainer = new Layer();
		addModuleContainer.setStyleClass("builderButton");
		StringBuffer title = new StringBuffer(iwrb.getLocalizedString("create_simple_template.Region", "Region"));
		if (label != null) {
			title.append(": ").append(label);
		}
		title.append(" :: ").append(iwrb.getLocalizedString(BuilderConstants.ADD_MODULE_TO_REGION_LOCALIZATION_KEY,
				BuilderConstants.ADD_MODULE_TO_REGION_LOCALIZATION_VALUE));
		
		// Link for MOOdalBox
		Link link = new Link(new Span(new Text(iwrb.getLocalizedString("add", "Add"))), "javascript:void(0);");
		link.setToolTip(title.toString());
		link.setOnClick(new StringBuilder("openSelectAndAddModuleWindow('").append(labelMarkerContainerId).append("', '").append(uri).append("');").toString());
		link.setStyleClass("addModuleLinkStyleClass");
		addModuleContainer.add(link);
		buttons.add(addModuleContainer);
		
		//	Add article button
		Layer addArticleContainer = new Layer();
		addArticleContainer.setStyleClass("builderButton");
		title = new StringBuffer(iwrb.getLocalizedString("article_module", "Article")).append(" :: ");
		title.append(iwrb.getLocalizedString("add_article_module", "Add article module"));
		title.append(getLabelToRegion(iwrb, label));
		Span addArticle = new Span(new Text(iwrb.getLocalizedString("text", "Text")));
		addArticleContainer.setTitle(title.toString());
		addArticleContainer.setStyleClass("add_article_module_to_region_image");
		
		ICObject article = null;
		try {
			article = getICObjectHome().findByClassName(CoreConstants.getArticleItemViewerClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (article != null) {
			addArticle.setMarkupAttribute("icobjectid", article.getID());
			addArticle.setMarkupAttribute("icobjectclass", article.getClassName());
			addArticle.setMarkupAttribute("regioncontainerid", labelMarkerContainerId);
		}
		
		addArticleContainer.add(addArticle);
		buttons.add(addArticleContainer);
		
		//	Paste module button
		Layer pasteButtonContainer = new Layer();
		pasteButtonContainer.setStyleClass("builderButton");
		title = new StringBuffer(iwrb.getLocalizedString("paste", "Paste")).append(" :: ");
		title.append(iwrb.getLocalizedString("paste_module", "Paste module"));
		title.append(getLabelToRegion(iwrb, label));
		/*Image pasteImage = getBuilderBundle().getImage("paste_16.png", title.toString(), 16, 16);
		pasteImage.setStyleClass(BuilderConstants.IMAGE_WITH_TOOLTIPS_STYLE_CLASS);*/
		StringBuffer pasteAction = new StringBuffer("pasteCopiedModule('").append(pasteButtonContainer.getId()).append("');");
		//pasteImage.setOnClick(pasteAction.toString());
		Span paste = new Span(new Text(iwrb.getLocalizedString("paste", "Paste")));
		pasteButtonContainer.setTitle(title.toString());
		pasteButtonContainer.setOnClick(pasteAction.toString());
		//pasteButtonContainer.add(pasteImage);
		pasteButtonContainer.add(paste);
		pasteButtonContainer.setStyleClass("pasteModuleIconContainer");
		buttons.add(pasteButtonContainer);
		
		return buttons;
	}
	
	private String getLabelToRegion(IWResourceBundle iwrb, String regionLabel) {
		if (regionLabel == null) {
			return CoreConstants.EMPTY;
		}
			return new StringBuilder(CoreConstants.SPACE).append(iwrb.getLocalizedString("to", "to")).append(CoreConstants.SPACE).append(regionLabel).toString();
	}

	/**
	 *
	 */
	public PresentationObject getLockedIcon(String parentKey, IWContext iwc, String label){
		IWBundle iwb = getBuilderBundle();
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);

		Layer layer = new Layer();
		layer.setStyleClass("builderButton");
		layer.setStyleClass("lockedRegion");
		
		Link link = new Link(new Span(new Text(iwrb.getLocalizedString("unlock_region", "Unlock region"))));
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_UNLOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		if(label!=null){
			link.setToolTip(label);
		}
		layer.add(link);
		
		return layer;
	}

	/**
	 *
	 */
	public PresentationObject getUnlockedIcon(String parentKey, IWContext iwc){
		IWBundle iwb = getBuilderBundle();
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);

		Layer layer = new Layer();
		layer.setStyleClass("builderButton");
		layer.setStyleClass("unlockedRegion");
		
		Link link = new Link(new Span(new Text(iwrb.getLocalizedString("lock_region", "Lock region"))));
		link.setWindowToOpen(IBLockRegionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LOCK_REGION);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		layer.add(link);
		
		return layer;
	}
	
	/**
	 *
	 */
	public PresentationObject getLabelIcon(String parentKey, IWContext iwc, String label){
		IWBundle iwb = getBuilderBundle();
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);

		Layer layer = new Layer();
		layer.setStyleClass("builderButton");
		layer.setStyleClass("labelButton");
		
		Link link = new Link(new Span(new Text(iwrb.getLocalizedString("set_label", "Put label on region"))));
		link.setWindowToOpen(IBAddRegionLabelWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LABEL);
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
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_COPY);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
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
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_COPY);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return (link);
	}

	public PresentationObject getDeleteIcon(String key, String parentKey, IWContext iwc){
		Image deleteImage = getBuilderBundle().getImage("del_16.gif", "Delete component",16,16);
		Link link = new Link(deleteImage);
		link.setStyleClass("moduleButton");
		link.setWindowToOpen(IBDeleteModuleWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_DELETE);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	public PresentationObject getPermissionIcon(String key, IWContext iwc){
		Image editImage = getBuilderBundle().getImage("lock_16.gif", "Set permissions",16,16);
		Link link = new Link(editImage);
		link.setStyleClass("moduleButton");
		link.setWindowToOpen(IBPermissionWindow.class);
		link.addParameter(BuilderConstants.IB_PAGE_PARAMETER, getCurrentIBPage(iwc));
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_PERMISSION);
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
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, ICBuilderConstants.ACTION_EDIT);
		link.addParameter(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
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
		link.addParameter(ICBuilderConstants.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_PASTE_ABOVE);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return (link);
	}	
	
	
	public ViewNode getBuilderPageRootViewNode(){
		String BUILDER_PAGE_VIEW_ID="pages";
		return ViewManager.getInstance(IWMainApplication.getDefaultIWMainApplication()).getApplicationRoot().getChild(BUILDER_PAGE_VIEW_ID);
	}
	
	
	public String getInstanceId(UIComponent object) {
		String instanceId = null;
		if (object instanceof PresentationObject) {
			PresentationObject po = (PresentationObject) object;
			instanceId = po.getXmlId();
		}
		
		if (instanceId == null) {
			//	set from the xml
			instanceId = object.getId();
		}
		
		return instanceId;
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
			List<UIComponent> childrenList = component.getChildren();
			for (UIComponent childComponent: childrenList) {
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
		return IWMainApplication.getDefaultIWMainApplication().getBundle(BuilderConstants.IW_BUNDLE_IDENTIFIER);
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
	
	private ICObjectHome getICObjectHome() {
		try {
			return (ICObjectHome) IDOLookup.getHome(ICObject.class);
		} catch (IDOLookupException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getICObjectId(String objectClass) {
		try {
			return getICObjectHome().findByClassName(objectClass).getID();
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Saving page structure after moving (drag & drop) tree nodes
	 * @param IDs Tree nodes' IDs
	 */
	public boolean movePage(int newParentId, int nodeId, ICDomain domain) {
		IBPageHelper.getInstance().movePage(nodeId, newParentId, domain);
		return true;
	}
	
	public boolean changePageName(int id, String newName, IWContext iwc) {	
		Map<Integer, PageTreeNode> tree = PageTreeNode.getTree(IWContext.getInstance());
		PageTreeNode node = tree.get(id);
		node.setNodeName(newName);
		node.setLocalizedNodeName(iwc.getCurrentLocale().getLanguage(), newName, iwc);
		
		return IBPageUpdater.addLocalizedPageName(id, ICLocaleBusiness.getLocaleId(iwc.getCurrentLocale()), newName);
	}
	
	public Collection getTopLevelTemplates(IWContext iwc){
		return DomainTree.getDomainTree(iwc).getTemplatesNode().getChildren();		
	}

	public Collection<PageTreeNode> getTopLevelPages(IWContext iwc){
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
	
	public boolean setPageUri(ICPage page, String pageUri, int domainId) {
		if (page == null || pageUri == null) {
			return false;
		}
		
		pageUri = StringHandler.removeMultipleSlashes(pageUri);
		
		List<String> uriParts = null;
		if (pageUri.indexOf(CoreConstants.SLASH) != -1) {
			uriParts = Arrays.asList(pageUri.split(CoreConstants.SLASH));
			pageUri = "";
			for (int i = 0; i < uriParts.size(); i++) {
				pageUri = new StringBuilder(pageUri).append(StringHandler.convertToUrlFriendly(uriParts.get(i))).append(CoreConstants.SLASH).toString();
			}
		}
		else {
			pageUri = StringHandler.convertToUrlFriendly(pageUri);
		}
		
		if (!pageUri.startsWith(CoreConstants.SLASH)) {
			pageUri = new StringBuilder(CoreConstants.SLASH).append(pageUri).toString();
		}
		if (!pageUri.endsWith(CoreConstants.SLASH)) {
			pageUri = new StringBuilder(pageUri).append(CoreConstants.SLASH).toString();
		}
		
		String pageKey = page.getId();
		ICPage pageWithSameUri = page;
		String deleted = "deleted";
		String uri = null;
		int index = 1;
		while (pageWithSameUri != null) {
			if (!pageWithSameUri.getId().equals(pageKey)) {
				if (pageWithSameUri.getDeleted()) {
					uri = new StringBuilder(pageWithSameUri.getDefaultPageURI()).append(deleted).append(index).append(CoreConstants.SLASH).toString();
					pageWithSameUri.setDefaultPageURI(uri);
					pageWithSameUri.store();
				}
				else {
					pageUri = new StringBuilder(pageUri.substring(0, pageUri.length() - 1)).append(index).append(CoreConstants.SLASH).toString();
				}
				index++;
			}
			
			try {
				pageWithSameUri = getICPageHome().findByUri(pageUri, domainId);
			} catch (FinderException e) {
				pageWithSameUri = null;
			}
		}

		page.setDefaultPageURI(pageUri);
		page.store();
		
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
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
	
	public String getModuleClassName(String pageKey, String instanceId) {
		if (pageKey == null || instanceId == null) {
			return null;
		}
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (xml == null) {
			return null;
		}
		XMLElement module = getIBXMLWriter().findModule(xml, instanceId);
		if (module == null) {
			return null;
		}
		XMLAttribute className = module.getAttribute(IBXMLConstants.CLASS_STRING);
		if (className == null) {
			return null;
		}
		return className.getValue();
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
		
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		
		String realValue = value.getValue();
		if (realValue == null) {
			return false;
		}
		if (realValue.indexOf(IBXMLConstants.COMMA_STRING) == -1) {	//	Not multivalue?
			return propertyValue.equals(realValue);
		}
		else {
			String[] values = realValue.split(IBXMLConstants.COMMA_STRING);
			for (int i = 0; i < values.length; i++) {
				if (propertyValue.equals(values[i])) {
					return true;
				}
			}
		}
		
		return false;
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
		return removeValueFromModuleProperty(pageKey, moduleId, propertyName, valueToRemove, true);
	}
	
	private boolean removeValueFromModuleProperty(String pageKey, String moduleId, String propertyName, String valueToRemove, boolean storePage) {
		if (pageKey == null || moduleId == null || propertyName == null || valueToRemove == null) {
			return false;
		}
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
		
		if (storePage) {
			xml.store();
		}
		return true;
	}
	
	public boolean removeValueFromModuleProperty(String pageKey, List<String> moduleIds, String propertyName, String valueToRemove) {
		if (moduleIds == null) {
			return false;
		}
		for (int i = 0; i < moduleIds.size(); i++) {
			removeValueFromModuleProperty(pageKey, moduleIds.get(i), propertyName, valueToRemove, !(i + 1 < moduleIds.size()));
		}
		return true;
	}

	public void setTreeOrder(int id, int order){
		IBPageHelper.getInstance().setTreeOrder(id, order);
	}

	public int getTreeOrder(int id){
		return IBPageHelper.getInstance().getTreeOrder(id);
	}
	
	public void changeTreeOrder(int pageId, int change) {
		IBPageHelper.getInstance().changeTreeOrder(pageId, change);
	}
	
	public int setAsLastInLevel(boolean isTopLevel, String parentId){
		return IBPageHelper.getInstance().setAsLastInLevel(isTopLevel, parentId);
	}
	
	public String getExistingPageKeyByURI(String requestURI,ICDomain domain) {
		// if the request contains an anchor like http://hello/pages/mypage#foo the requestURI is without an slash at the end
		// that is http://hello/pages/mypage
		// The missing slash is needed for finding the page.
		if (! requestURI.endsWith(StringHandler.SLASH)) {
			requestURI = StringHandler.concat(requestURI, StringHandler.SLASH);
		}
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
	
	private String getUriToAddModuleWindow(String regionName) {
		Class<AddModuleBlock> addModuleClass = AddModuleBlock.class;
		
		if (regionName == null || CoreConstants.EMPTY.equals(regionName)) {
			return getUriToObject(addModuleClass);
		}
		
		List<AdvancedProperty> parameters = new ArrayList<AdvancedProperty>();
		parameters.add(new AdvancedProperty(BuilderConstants.REGION_NAME, regionName));
		return getUriToObject(addModuleClass, parameters);
	}
	
	public String getUriToObject(Class<? extends UIComponent> objectClass) {
		if (objectClass == null) {
			return null;
		}
		
		URIUtil uri = new URIUtil(IWMainApplication.getDefaultIWMainApplication().getPublicObjectInstanciatorURI(objectClass));
		uri.setParameter("uiObject", Boolean.TRUE.toString());
		
		return uri.getUri();
	}
	
	public String getUriToObject(Class<? extends UIComponent> objectClass, List<AdvancedProperty> parameters) {
		String baseUri = getUriToObject(objectClass);
		if (StringUtil.isEmpty(baseUri)) {
			return null;
		}
		
		if (ListUtil.isEmpty(parameters)) {
			return baseUri;
		}
		
		URIUtil uri = new URIUtil(baseUri);
		
		for (AdvancedProperty parameter: parameters) {
			if (!StringUtil.isEmpty(parameter.getId()) && !StringUtil.isEmpty(parameter.getValue())) {
				uri.setParameter(parameter.getId(), parameter.getValue());
			}
		}
		
		return uri.getUri();
	}
	
	public boolean removeBlockObjectFromCache(String cacheKey) {
		return removeBlockObjectFromCache(CoreUtil.getIWContext(), cacheKey);
	}
	
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
	
	public boolean removeAllBlockObjectsFromCache(IWContext iwc) {
		if (iwc == null) {
			return false;
		}
		
		IWCacheManager cache = iwc.getIWMainApplication().getIWCacheManager();
		if (cache == null) {
			return false;
		}
		
		List <String> cacheKeys = new ArrayList<String>();
		Map cached = cache.getCacheMap();
		if (cached != null) {
			for (Iterator it = cached.keySet().iterator(); it.hasNext(); ) {
				cacheKeys.add(it.next().toString());
			}
		}
		if (cacheKeys.size() == 0) {
			return false;
		}
		for (int i = 0; i < cacheKeys.size(); i++) {
			cache.invalidateCache(cacheKeys.get(i));
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
		IBXMLPage xml = null;
		try {
			xml = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
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
	 * Renders single UIComponent
	 * @param iwc
	 * @param component - object to render
	 * @param cleanCode
	 * @return String of rendered object or null
	 */
	public String getRenderedComponent(UIComponent component, IWContext iwc, boolean cleanCode) {
		return getRenderedComponent(component, iwc, cleanCode, true, true);
	}
	
	@SuppressWarnings("unchecked")
	public synchronized String getRenderedComponent(UIComponent component, IWContext iwc, boolean cleanCode, boolean omitDocTypeDeclaration,
			boolean omitHtmlEnvelope) {
		if (iwc == null || component == null) {
			return null;
		}
		
		iwc.setSessionAttribute(CoreConstants.SINGLE_UICOMPONENT_RENDERING_PROCESS, Boolean.TRUE);

		HtmlBufferResponseWriterWrapper writer = null;
		try {
			writer = HtmlBufferResponseWriterWrapper.getInstance(iwc.getResponseWriter());
			iwc.setResponseWriter(writer);
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (writer == null) {
			return null;
		}
		
		if (iwc.getViewRoot() == null) {
			UIViewRoot root = new UIViewRoot();
			root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
			iwc.setViewRoot(root);
		}
		
		List<String> jsSources = null;
		List<String> jsActions = null;
		List<String> cssSources = null;
		try {
			RenderUtils.renderChild(iwc, component);
			
			Object o = iwc.getSessionAttribute(PresentationUtil.ATTRIBUTE_JAVA_SCRIPT_SOURCE_FOR_HEADER);
			if (o instanceof List) {
				jsSources = (List) o;
			}
			
			o = iwc.getSessionAttribute(PresentationUtil.ATTRIBUTE_JAVA_SCRIPT_ACTION_FOR_BODY);
			if (o instanceof List) {
				jsActions = (List) o;
			}
			
			o = iwc.getSessionAttribute(PresentationUtil.ATTRIBUTE_CSS_SOURCE_LINE_FOR_HEADER);
			if (o instanceof List) {
				cssSources = (List) o;
			}
		} catch (Exception e){
			e.printStackTrace();
			return null;
		} finally {
			iwc.removeSessionAttribute(CoreConstants.SINGLE_UICOMPONENT_RENDERING_PROCESS);
			
			iwc.removeSessionAttribute(PresentationUtil.ATTRIBUTE_JAVA_SCRIPT_SOURCE_FOR_HEADER);
			iwc.removeSessionAttribute(PresentationUtil.ATTRIBUTE_JAVA_SCRIPT_ACTION_FOR_BODY);
			iwc.removeSessionAttribute(PresentationUtil.ATTRIBUTE_CSS_SOURCE_LINE_FOR_HEADER);
		}
		
		String rendered = writer.toString();
		if (rendered == null) {
			return null;
		}

		if (cleanCode) {
			rendered = getCleanedHtmlContent(rendered, omitDocTypeDeclaration, omitHtmlEnvelope, false);
		}
		
		if (jsSources != null || jsActions != null || cssSources != null) {
			Object addCSSDirectly = iwc.getSessionAttribute(PresentationUtil.ATTRIBUTE_ADD_CSS_DIRECTLY);
			if (addCSSDirectly != null) {
				iwc.removeSessionAttribute(PresentationUtil.ATTRIBUTE_ADD_CSS_DIRECTLY);
			}
			rendered = getRenderedComponentWithDynamicResources(getXMLDocumentFromComponentHTML(rendered, false, omitDocTypeDeclaration, omitHtmlEnvelope,
					false), cssSources, jsSources, jsActions, addCSSDirectly instanceof Boolean ? (Boolean) addCSSDirectly : false);
		}
		
		return rendered;
	}
	
	private String getRenderedComponentWithDynamicResources(Document component, List<String> cssSources, List<String> jsSources, List<String> jsActions,
			boolean addCSSDirectly) {
		if (component == null) {
			return null;
		}
		
		Element root = component.getRootElement();
		if (root == null) {
			return null;
		}
		
		List<String> resourcesToLoad = new ArrayList<String>();
		
		//	CSS
		if (!ListUtil.isEmpty(cssSources)) {
			if (addCSSDirectly) {
				Collection<Element> cssElements = new ArrayList<Element>(cssSources.size());
				for (String cssUri: cssSources) {
					Element css = new Element("link");
					css.setAttribute(new Attribute("type", "text/css"));
					css.setAttribute(new Attribute("rel", "stylesheet"));
					css.setAttribute(new Attribute("media", "screen"));
					css.setAttribute(new Attribute("href", cssUri));
					cssElements.add(css);
				}
				root.addContent(cssElements);
			}
			else {
				for (String cssUri: cssSources) {
					if (!resourcesToLoad.contains(cssUri)) {
						resourcesToLoad.add(cssUri);
					}
				}
			}
		}
		
		//	JavaScript sources
		if (!ListUtil.isEmpty(jsSources) || !ListUtil.isEmpty(resourcesToLoad)) {
			StringBuffer actionsInSingleFunction = null;
			if (jsActions != null) {
				actionsInSingleFunction = new StringBuffer("function() {");
				for (String jsAction: jsActions) {
					actionsInSingleFunction.append(jsAction);
				}
				actionsInSingleFunction.append("}");
				jsActions.clear();
			}
			
			if (!ListUtil.isEmpty(jsSources)) {
				for (String jsResource: jsSources) {
					if (!resourcesToLoad.contains(jsResource)) {
						resourcesToLoad.add(jsResource);
					}
				}
			}
			
			String action = PresentationUtil.getJavaScriptLinesLoadedLazily(resourcesToLoad, actionsInSingleFunction == null ? null :
																															actionsInSingleFunction.toString());
			Collection<Element> includeScriptsAndExecuteActions = new ArrayList<Element>(1);
			Element mainAction = new Element("script");
			mainAction.setAttribute(new Attribute("type", "text/javascript"));
			mainAction.setText(action);
			includeScriptsAndExecuteActions.add(mainAction);
			root.addContent(includeScriptsAndExecuteActions);
		}
		
		//	JavaScript actions
		if (jsActions != null && !jsActions.isEmpty()) {
			Collection<Element> actions = new ArrayList<Element>(jsActions.size());
			for (String jsAction: jsActions) {
				Element action = new Element("script");
				action.setAttribute(new Attribute("type", "text/javascript"));
				action.setText(jsAction);
				actions.add(action);
			}
			
			root.addContent(actions);
		}
		
		return getJDOMOutputter().outputString(component);
	}
	
	private XMLOutputter getJDOMOutputter() {
		if (outputter == null) {
			outputter = new XMLOutputter(Format.getPrettyFormat());
		}
		return outputter;
	}
	
	/**
	 * Renders single UIComponent and creates JDOM Document of rendered object
	 * @param iwc
	 * @param component - object to render
	 * @param cleanCode
	 * @return JDOM Document or null
	 */
	public Document getRenderedComponent(IWContext iwc, UIComponent component, boolean cleanCode) {
		return getRenderedComponent(iwc, component, cleanCode, true, true);
	}
	
	public Document getRenderedComponent(IWContext iwc, UIComponent component, boolean cleanCode, boolean omitDocTypeDeclaration, boolean omitHtmlEnvelope) {
		return getXMLDocumentFromComponentHTML(getRenderedComponent(component, iwc, cleanCode, omitDocTypeDeclaration, omitHtmlEnvelope), false,
				omitDocTypeDeclaration, omitHtmlEnvelope, true);
	}
	
	private Document getXMLDocumentFromComponentHTML(String componentHTML, boolean cleanCode, boolean omitDocTypeDeclaration, boolean omitHtmlEnvelope,
			boolean omitComments) {
		if (StringUtil.isEmpty(componentHTML)) {
			return null;
		}
		
		if (cleanCode) {
			componentHTML = getCleanedHtmlContent(componentHTML, omitDocTypeDeclaration, omitHtmlEnvelope, omitComments);
		}
		
		//	Removing <!--... ms-->
		Matcher commentsMatcher = getCommentRemplacementPattern().matcher(componentHTML);
		componentHTML = commentsMatcher.replaceAll(CoreConstants.EMPTY);

		//	Removing <!DOCTYPE .. >
		Matcher docTypeMatcher = getDoctypeReplacementPattern().matcher(componentHTML);
		componentHTML = docTypeMatcher.replaceAll(CoreConstants.EMPTY);
			
		//	Do not add any more replaceAll - HtmlCleaner should fix ALL problems
		//	Replace symbols which can cause exceptions with SAXParser
		componentHTML = componentHTML.replaceAll("&ouml;", "&#246;");
		componentHTML = componentHTML.replaceAll("&Ouml;", "&#246;");
		componentHTML = componentHTML.replaceAll("&nbsp;", "&#160;");
		componentHTML = componentHTML.replaceAll("&iacute;", "&#237;");
//		componentHTML = componentHTML.replaceAll("&lt;", "&#60;");
//		componentHTML = componentHTML.replaceAll("&gt;", "&#62;");
		
		if (StringUtil.isEmpty(componentHTML)) {
			logger.warning("HTML code is empty!");
			return null;
		}

		Document componentXML = null;
		try {
			componentXML = XmlUtil.getJDOMXMLDocument(componentHTML, false);
		} catch(Exception e) {
			logger.log(Level.SEVERE, "Error getting XML document from HTML code:\n" + componentHTML, e);
		}
		if (componentXML == null && !cleanCode) {
			logger.log(Level.INFO, "Trying with cleaned HTML code because uncleaned HTML code just failed to create document from:\n" + componentHTML);
			return getXMLDocumentFromComponentHTML(componentHTML, true, omitDocTypeDeclaration, omitHtmlEnvelope, omitComments);
		}
		
		return componentXML;
	}
	
	public String getCleanedHtmlContent(InputStream htmlStream, boolean omitDocTypeDeclaration, boolean omitHtmlEnvelope, boolean omitComments) {
		return getCleanedHtmlContent(htmlStream, null, omitDocTypeDeclaration, omitHtmlEnvelope, omitComments);
	}
	
	public String getCleanedHtmlContent(String htmlContent, boolean omitDocTypeDeclaration, boolean omitHtmlEnvelope, boolean omitComments) {
		return getCleanedHtmlContent(null, htmlContent, omitDocTypeDeclaration, omitHtmlEnvelope, omitComments);
	}
	
	private String getCleanedHtmlContent(InputStream htmlStream, String htmlContent, boolean omitDocTypeDeclaration, boolean omitHtmlEnvelope,
			boolean omitComments) {
		if (htmlStream == null && htmlContent == null) {
			return null;
		}
		
		HtmlCleaner cleaner = htmlStream == null ? new HtmlCleaner(htmlContent) : new HtmlCleaner(htmlStream);
		cleaner.setOmitDoctypeDeclaration(omitDocTypeDeclaration);
		cleaner.setOmitHtmlEnvelope(omitHtmlEnvelope);
		cleaner.setOmitComments(omitComments);
		cleaner.setOmitXmlDeclaration(true);
		cleaner.setUseCdataForScriptAndStyle(false);
		
		try {
			cleaner.clean();
			htmlContent = XmlUtil.getPrettyJDOMDocument(XmlUtil.getJDOMXMLDocument(cleaner.getPrettyXmlAsString(), false));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error cleaning content", e);
			return null;
		}
		
		// Removing <?xml version... />
		Matcher commentsMatcher = getXmlEncodingReplacementPattern().matcher(htmlContent);
		htmlContent = commentsMatcher.replaceAll(CoreConstants.EMPTY);
		
		return htmlContent;
	}
	
	private Pattern getXmlEncodingReplacementPattern() {
		if (xmlEncodingReplacementPattern == null) {
			xmlEncodingReplacementPattern = Pattern.compile("&lt.+?xml.+&gt;");
		}
		return xmlEncodingReplacementPattern;
	}
	
	private Pattern getDoctypeReplacementPattern() {
		if (doctypeReplacementPattern == null) {
			doctypeReplacementPattern = Pattern.compile("<!DOCTYPE[^>]*>");
		}
		
		return doctypeReplacementPattern;
	}
	
	private Pattern getCommentRemplacementPattern() {
		if (commentinHtmlReplacementPattern == null) {
			commentinHtmlReplacementPattern = Pattern.compile("<!--\\d+ ms-->");
		}
		return commentinHtmlReplacementPattern;
	}
	
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] properties) {
		if (pageKey == null || moduleId == null || propertyName == null || properties == null) {
			return false;
		}
		
		IWMainApplication application = null;
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			application = IWMainApplication.getDefaultIWMainApplication();
		}
		else {
			application = iwc.getIWMainApplication();
		}
		if (application == null) {
			return false;
		}
		
		boolean result = setProperty(pageKey, moduleId, propertyName, properties, application);
		
		if (result) {
			Object handler = iwc.getSessionAttribute(CoreConstants.HANDLER_PARAMETER);
			if (handler instanceof ICPropertyHandler) {
				iwc.removeSessionAttribute(CoreConstants.HANDLER_PARAMETER);
				
				((ICPropertyHandler) handler).onUpdate(properties, iwc);
			}
			
			removeBlockObjectFromCache(iwc, BuilderConstants.SET_MODULE_PROPERTY_CACHE_KEY);
			removeBlockObjectFromCache(iwc, BuilderConstants.EDIT_MODULE_WINDOW_CACHE_KEY);
		}
		
		return result;
	}
	
	public String generateResourcePath(String base, String scope, String fileName) {
		IWSlideService service = getSlideService();
		StringBuffer path = new StringBuffer(getYearMonthPath(base)).append(BuilderConstants.SLASH);
		path.append(service.createUniqueFileName(scope)).append(BuilderConstants.DOT);
		path.append(fileName);
		return path.toString();
	}
	
	/**
	 * Creates path (uri) based on base path and current time
	 * @return
	 */
	private String getYearMonthPath(String basePath) {
		StringBuffer path = new StringBuffer(basePath).append(BuilderConstants.SLASH);
		path.append(getYearMonthPath());
		return path.toString();
	}
	
	/**
	 * Creates path (uri) based on current time
	 * @return
	 */
	public String getYearMonthPath() {
		IWTimestamp now = new IWTimestamp();
		StringBuffer path = new StringBuffer();
		path.append(now.getYear()).append(BuilderConstants.SLASH).append(now.getDateString("MM"));
		return path.toString();
	}
	
	private synchronized IWSlideService getSlideService() {
		if (slideService == null) {
			try {
				slideService = (IWSlideService) IBOLookup.getServiceInstance(CoreUtil.getIWContext(), IWSlideService.class);
			} catch (IBOLookupException e) {
				e.printStackTrace();
				return null;
			}
		}
		return slideService;
	}
	
	public UIComponent findComponentInPage(IWContext iwc, String pageKey, String instanceId) {
		if (pageKey == null || instanceId == null || iwc == null) {
			return null;
		}
		
		Page page = getPage(pageKey, iwc);
		if (page == null) {
			return null;
		}
		
		return findComponentInList(page.getChildren(), instanceId);
	}
	
	@SuppressWarnings("unchecked")
	private UIComponent findComponentInList(List<UIComponent> children, String instanceId) {
		if (children == null || instanceId == null) {
			return null;
		}
		
		UIComponent component = null;
		UIComponent componentFromCycle = null;
		Map facets = null;
		List<UIComponent> cFromMap = null;
		boolean foundComponent = false;
		for (int i = 0; (i < children.size() && !foundComponent); i++) {
			component = children.get(i);

			if (instanceId.equals(getInstanceId(component))) {
				foundComponent = true;
			} else {
				componentFromCycle = findComponentInList(component.getChildren(), instanceId);
				
				if (componentFromCycle == null) {
					//	Didn't find in children's list, will check facets map
					facets = component.getFacets();
					if (facets != null) {
						cFromMap = new ArrayList<UIComponent>();
						for (Iterator it = facets.values().iterator(); it.hasNext();) {
							cFromMap.add((UIComponent) it.next());
						}
						componentFromCycle = findComponentInList(cFromMap, instanceId);
					}
				}
				
				if (componentFromCycle != null) {
					foundComponent = true;
					component = componentFromCycle;
				}
			}
		}
		if (!foundComponent) {
			return null;
		}
		return component;
	}
	
	public Document getRenderedModule(String pageKey, String componentId, boolean cleanCode) {
		if (pageKey == null || componentId == null) {
			return null;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		
		UIComponent component = findComponentInPage(iwc, pageKey, componentId);
		if (component == null) {
			return null;
		}
		
		return getRenderedComponent(iwc, component, cleanCode);
	}
	
	public boolean existsRegion(String pageKey, String label, String regionId) {
		if (pageKey == null) {
			return false;
		}
		
		XMLElement region = null;
		try {
			region = getIBXMLWriter().findRegion(getIBXMLPage(pageKey), label, regionId);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return region == null ? false : true;
	}
	
	public boolean copyAllModulesFromRegionIntoRegion(String pageKey, String sourceRegionLabel, String destinationRegionId, String destinationRegionLabel,
			IWSlideSession session) {
		if (pageKey == null || sourceRegionLabel == null || destinationRegionId == null || destinationRegionLabel == null) {
			return false;
		}
		
		IBXMLPage page = null;
		try {
			page = getIBXMLPage(pageKey);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (page == null) {
			return false;
		}
		XMLElement sourceRegion = getIBXMLWriter().findRegion(page, sourceRegionLabel, null);
		if (sourceRegion == null) {
			return false;
		}
		
		//	TODO:	Find all modules, not just first level ones
		List<XMLElement> modules = sourceRegion.getChildren(IBXMLConstants.MODULE_STRING);
		if (modules == null) {
			return false;
		}
		
		String id = null;
		Object o = null;
		for (int i = 0; i < modules.size(); i++) {
			o = modules.get(i).clone();
			if (o instanceof XMLElement) {
				id = getIBXMLWriter().insertElementLast(page, pageKey, destinationRegionId, destinationRegionLabel, (XMLElement) o, true);
				if (id == null) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		if (savePage(page, session)) {
			return true;
		}
		
		return false;
	}
	
	public ICPage findPageForModule(IWContext iwc, String instanceId) {
		if (instanceId == null) {
			return null;
		}
		
		Map<Integer, PageTreeNode> pages = PageTreeNode.getTree(iwc);
		if (pages == null || pages.isEmpty()) {
			return null;
		}
		
		Iterator<PageTreeNode> allPages = pages.values().iterator();
		ICPage page = null;
		String pageId = null;
		for (Iterator<PageTreeNode> it = allPages; it.hasNext();) {
			pageId = it.next().getId();
			
			try {
				page = getICPageHome().findByPrimaryKey(pageId);
			} catch(Exception e) {
				page = null;
			}
			if (page != null && page.getIsFormattedInIBXML()) {
				try {
					if (getIBXMLWriter().findModule(getIBXMLPage(pageId), instanceId) != null) {
						return page;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	public boolean reloadGroupsInCachedDomain(IWApplicationContext iwac, String serverName) {
		try {
			ICDomain mostlyCachedDomain = iwac.getDomain();
			if (serverName == null) {
				serverName = mostlyCachedDomain.getServerName();
			}
			ICDomain domain = ((ICDomainHome) IDOLookup.getHome(ICDomain.class)).findDomainByServernameOrDefault(serverName);
			Collection groups = domain.getTopLevelGroupsUnderDomain();
			if (mostlyCachedDomain instanceof CachedDomain) {
				((CachedDomain) mostlyCachedDomain).setTopLevelGroupsUnderDomain(groups);
				BaseFilter.reInitializeCachedDomainOnNextRequest();
			}
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public String getFullPageUrlByPageType(IWContext iwc, String pageType, boolean checkFirstlyNearestPages) {
		User usr = iwc == null ? null : iwc.isLoggedOn() ? iwc.getCurrentUser() : null;
		return getFullPageUrlByPageType(usr, iwc, pageType, checkFirstlyNearestPages);
	}
	
	public String getFullPageUrlByPageType(User user, IWContext iwc, String pageType, boolean checkFirstlyNearestPages) {
		
		String serverURL = iwc.getServerURL();
		String pageUri;
		
		if(user == null)
			pageUri = getPageUri(iwc, pageType, checkFirstlyNearestPages);
		else
			pageUri = getPageUri(user, iwc, pageType, checkFirstlyNearestPages);
		
		serverURL = serverURL.endsWith(CoreConstants.SLASH) ? serverURL.substring(0, serverURL.length()-1) : serverURL;
		
		String fullURL = new StringBuilder(serverURL)
		.append(pageUri.startsWith(CoreConstants.SLASH) ? CoreConstants.EMPTY : CoreConstants.SLASH)
		.append(pageUri)
		.toString();
		
		return fullURL;
	}
	
	public String getFullPageUrlByPageType(User user, String pageType, boolean checkFirstlyNearestPages) {
		String serverURL = getIWMainApplication().getSettings().getProperty(IWConstants.SERVER_URL_PROPERTY_NAME);
		String pageUri;
		
		pageUri = getPageUri(user, pageType, checkFirstlyNearestPages);
		
		serverURL = serverURL.endsWith(CoreConstants.SLASH) ? serverURL.substring(0, serverURL.length()-1) : serverURL;
		
		String fullURL = new StringBuilder(serverURL)
		.append(pageUri.startsWith(CoreConstants.SLASH) ? CoreConstants.EMPTY : CoreConstants.SLASH)
		.append(pageUri)
		.toString();
		
		return fullURL;
	}
	
	public ICPage getNearestPageForUserHomePageOrCurrentPageByPageType(IWContext iwc, String pageType) {
		
		User usr = iwc.isLoggedOn() ? iwc.getCurrentUser() : null;
		return getNearestPageForUserHomePageOrCurrentPageByPageType(usr, iwc, pageType);
	}
	
	@SuppressWarnings("unchecked")
	public ICPage getNearestPageForUserHomePageOrCurrentPageByPageType(User user, IWContext iwc, String pageType) {
		ICPage startPage = null;
		ICPage nearestPage = null;
		
		if (user != null) {
			nearestPage = getNearestPageForUserHomePage(user,pageType);
			if(nearestPage!=null){
				return nearestPage;
			}
		}
		
		//	Trying to get nearest page to current page
		try {
			startPage = getICPage(String.valueOf(iwc.getCurrentIBPageID()));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		if (startPage == null) {
			logger.warning("Didn't find start page! Searching for :" + pageType + ", by user: " + user);
			return null;
		}
		
		ICTreeNode parentNode = startPage.getParentNode();
		Collection<ICTreeNode> children = null;
		if (parentNode == null) {
			children = new ArrayList<ICTreeNode>(1);	//	Checking "start" page and its children
			children.add(startPage);
		}
		else {
			children = parentNode.getChildren();		//	Checking "start" page's siblings and children
		}
		
		nearestPage = getPageByPageType(children, pageType);
		return nearestPage;
	}
	/**
	 * Only searches the users main home page and below that for the pagetype
	 * @param currentUser
	 * @param pageType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ICPage getNearestPageForUserHomePage(User user, String pageType) {
		ICPage startPage = null;
		ICPage nearestPage = null;
		
		if (user != null) {
			//	Trying to get nearest page to user's home page
			startPage = getUsersHomePage(user);
			
			if(startPage!=null){
				Collection<ICTreeNode> searchTops = new ArrayList<ICTreeNode>();
				searchTops.add(startPage);
				Collection children = startPage.getChildren();
				if(!ListUtil.isEmpty(children)){
					searchTops.addAll(children);
				}
				nearestPage = getPageByPageType(searchTops, pageType);
			}
		}
		
		if (startPage == null) {
			logger.warning("Didn't find start page for search: " + pageType + ", user: " + user);
			return null;
		}
				
		return nearestPage;
	}

	public ICPage getUsersHomePage(User user) {
		ICPage startPage = null;
		try {
			UserBusiness userBiz = IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), UserBusiness.class);
			startPage = userBiz.getHomePageForUser(user);
		
		}
		catch (IBOLookupException e1) {
			e1.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		
		return startPage;
	}
	
	@SuppressWarnings("unchecked")
	private ICPage getPageByPageType(Collection<ICTreeNode> pages, String pageType) {
		if (ListUtil.isEmpty(pages)) {
			return null;
		}
		
		ICPage page = null;
		Collection<ICTreeNode> children = null;
		for (ICTreeNode node: pages) {
			page = getICPage(node.getId());
			
			if (page != null) {
				if (pageType.equals(page.getSubType())) {
					return page;
				}

				children = page.getChildren();
				if (!ListUtil.isEmpty(children)) {
					return getPageByPageType(children, pageType);
				}
			}
			
		}
		
		return null;
	}
	
	protected String getPageUri(IWContext iwc, String pageType, boolean checkFirstlyNearestPages) {
		
		User user = iwc.isLoggedOn() ? iwc.getCurrentUser() : null;
		return getPageUri(user, iwc, pageType, checkFirstlyNearestPages);
	}
	
	protected String getPageUri(User user, IWContext iwc, String pageType, boolean checkFirstlyNearestPages) {
		ICPage icPage = null;
		String messageForException = "No page found by page type: " + pageType;
		
		if (checkFirstlyNearestPages) {

			if(user == null) {
				
				icPage = getNearestPageForUserHomePageOrCurrentPageByPageType(iwc, pageType);
				
			} else {
				icPage = getNearestPageForUserHomePageOrCurrentPageByPageType(user, iwc, pageType);
			}
			
		}
		
		//last resort find the first page of that type
		if (icPage == null) {
			Collection<ICPage> icpages = getPages(pageType);
			
			if (icpages == null || icpages.isEmpty()) {
				throw new RuntimeException(messageForException);
			}
			
			icPage = icpages.iterator().next();
		}
		
		if (icPage == null) {
			throw new RuntimeException(messageForException);
		}
		
		String uri = icPage.getDefaultPageURI();
		
		if (!uri.startsWith(CoreConstants.PAGES_URI_PREFIX)) {
			uri = CoreConstants.PAGES_URI_PREFIX + uri;
		}
		
		return iwc.getIWMainApplication().getTranslatedURIWithContext(uri);
	}
	
	protected String getPageUri(User user, String pageType, boolean checkFirstlyNearestPages) {
		ICPage icPage = null;
		String messageForException = "No page found by page type: " + pageType;
		
		if (checkFirstlyNearestPages) {
			icPage = getNearestPageForUserHomePage(user, pageType);
		}
		
		if (icPage == null) {
			Collection<ICPage> icpages = getPages(pageType);
			
			if (icpages == null || icpages.isEmpty()) {
				throw new RuntimeException(messageForException);
			}
			
			icPage = icpages.iterator().next();
		}
		
		if (icPage == null) {
			throw new RuntimeException(messageForException);
		}
		
		String uri = icPage.getDefaultPageURI();
		
		if (!uri.startsWith(CoreConstants.PAGES_URI_PREFIX)) {
			uri = CoreConstants.PAGES_URI_PREFIX + uri;
		}
		
		return getIWMainApplication().getTranslatedURIWithContext(uri);
	}
	
	protected Collection<ICPage> getPages(String pageSubType) {
		
		try {
			ICPageHome home = (ICPageHome) IDOLookup.getHome(ICPage.class);
			@SuppressWarnings("unchecked")
			Collection<ICPage> icpages = home.findBySubType(pageSubType, false);
			
			return icpages;
			
		} catch (Exception e) {
			throw new RuntimeException("Exception while resolving icpages by subType: "+pageSubType, e);
		}
	}
	
	private RenderedComponent getRenderedInstanciatedComponent(IWContext iwc, UIComponent component) {
		RenderedComponent rendered = new RenderedComponent();
		rendered.setErrorMessage("Ooops... Some error occurred rendering component...");
		
		Web2Business web2 = getWeb2Business();
		JQuery jQuery = getJQUery();
		if (web2 != null && jQuery != null) {
			List<String> resources = new ArrayList<String>();
			resources.add(web2.getBundleUriToHumanizedMessagesStyleSheet());
			resources.add(jQuery.getBundleURIToJQueryLib());
			resources.add(web2.getBundleUriToHumanizedMessagesScript());
			rendered.setResources(resources);
		}
		
		if (iwc == null) {
			iwc = CoreUtil.getIWContext();
		}
		
		String html = getRenderedComponent(component, iwc, true, true, true);
		if (StringUtil.isEmpty(html)) {
			if (iwc != null) {
				rendered.setErrorMessage(iwc.getIWMainApplication().getBundle(BuilderConstants.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc)
						.getLocalizedString("error_rendering_component", rendered.getErrorMessage()));
			}
			return rendered;
		}
		
		rendered.setErrorMessage(null);
		rendered.setHtml(html);
		
		return rendered;
	}
	
	public RenderedComponent getRenderedComponentById(String uuid, String uri, List<AdvancedProperty> properties) {
		if (StringUtil.isEmpty(uuid)) {
			logger.log(Level.WARNING, "Unknown UUID!");
			return getRenderedInstanciatedComponent(null, null);
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			logger.log(Level.WARNING, "IWContext is unavailable");
			return getRenderedInstanciatedComponent(null, null);
		}
		
		String pageKey = null;
		try {
			pageKey = getPageKeyByURI(uri, iwc.getDomain());
		} catch(Exception e) {
			logger.log(Level.WARNING, "Error getting page key by uri: " + uri);
		}
		if (StringUtil.isEmpty(pageKey)) {
			try {
				pageKey = getPageKeyByURI(iwc.getRequestURI(), iwc.getDomain());
			} catch(Exception e) {
				logger.log(Level.WARNING, "Error getting page key by uri: " + iwc.getRequestURI());
			}
		}
		if (StringUtil.isEmpty(pageKey)) {
			pageKey = String.valueOf(iwc.getCurrentIBPageID());
		}
		if (StringUtil.isEmpty(pageKey)) {
			logger.log(Level.WARNING, "Unable to get page key!");
			return getRenderedInstanciatedComponent(null, null);
		}
		
		UIComponent component = findComponentInPage(iwc, pageKey, uuid);
		if (component == null) {
			logger.log(Level.SEVERE, "Didn't find component by uuid ('" + uuid + "') in page: " + pageKey);
		}
		
		return getRenderedComponent(component, properties);
	}
	
	public RenderedComponent getRenderedComponentByClassName(String className, List<AdvancedProperty> properties) {
		UIComponent component = null;
		if (StringUtil.isEmpty(className)) {
			return getRenderedInstanciatedComponent(null, null);
		}
		
		Object o = null;
		try {
			o = Class.forName(className).newInstance();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error creating instance of: " + className, e);
		}
		if (o instanceof UIComponent) {
			component = (UIComponent) o;
		}
		else {
			logger.log(Level.WARNING, "Instance of '" + className + "' is not UIComponent!");
			return getRenderedInstanciatedComponent(null, null);
		}
		
		return getRenderedComponent(component, properties);
	}
	
	public RenderedComponent getRenderedComponent(UIComponent component, List<AdvancedProperty> properties) {
		if (component == null) {
			return getRenderedInstanciatedComponent(null, null);
		}
		
		setPropertiesForObjectInstance(component, properties);
		
		return getRenderedInstanciatedComponent(CoreUtil.getIWContext(), component);
	}
	
	private void setPropertiesForObjectInstance(Object o, List<AdvancedProperty> properties) {
		if (o == null || ListUtil.isEmpty(properties)) {
			return;
		}
		
		for (AdvancedProperty property: properties) {
			try {
				MethodInvoker.getInstance().invokeMethodWithParameter(o, property.getId(), property.getValue());
			} catch (Exception e) {
				logger.log(Level.WARNING, "Error invoking method '" + property.getId() + "' with value: " + property.getValue(), e);
			}
		}
	}
	
	public List<com.idega.core.component.business.ComponentProperty> getComponentProperties(IWContext iwc, String instanceId) {
		if (StringUtil.isEmpty(instanceId) || iwc == null) {
			return null;
		}
		
		return IBPropertyHandler.getInstance().getComponentProperties(instanceId, iwc.getIWMainApplication(), iwc.getCurrentLocale());
	}
	
	private IWMainApplication getIWMainApplication() {
		return IWMainApplication.getDefaultIWMainApplication();
	}
}