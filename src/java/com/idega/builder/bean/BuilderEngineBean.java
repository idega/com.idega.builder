package com.idega.builder.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.IBXMLConstants;
import com.idega.builder.business.IBXMLReader;
import com.idega.builder.presentation.AddModuleBlock;
import com.idega.builder.presentation.EditModuleBlock;
import com.idega.builder.presentation.SetModulePropertyBlock;
import com.idega.business.IBOLookup;
import com.idega.business.IBOSessionBean;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.slide.business.IWSlideSession;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.xml.XMLElement;

public class BuilderEngineBean extends IBOSessionBean implements BuilderEngine {
	
	private static final long serialVersionUID = -4806588458269035118L;
	private static final Log log = LogFactory.getLog(BuilderEngineBean.class);
	
	private BuilderLogic builder = BuilderLogic.getInstance();
	
	private CutModuleBean cutModule = null;
	
	public List<String> getBuilderInitInfo() {
		List<String> info = new ArrayList<String>();
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return info;
		}
		
		IWResourceBundle iwrb = builder.getBuilderBundle().getResourceBundle(iwc);
		
		info.add(builder.getUriToObject(AddModuleBlock.class));																// 0
		info.add(iwrb.getLocalizedString("ib_addmodule_window", "Add a new Module"));										// 1
		info.add(iwrb.getLocalizedString("set_module_properties", "Set module properties"));								// 2
		info.add(new StringBuffer(builder.getBuilderBundle().getResourcesPath()).append("/add.png").toString());			// 3
		info.add(new StringBuffer(builder.getBuilderBundle().getResourcesPath()).append("/information.png").toString());	// 4
		info.add(iwrb.getLocalizedString("no_ids_inserting_module", "Error occurred while inserting selected module!"));	// 5
		info.add(String.valueOf(iwc.getCurrentIBPageID()));																	// 6
		info.add(iwrb.getLocalizedString("adding", "Adding..."));															// 7
		info.add(iwrb.getLocalizedString("create_simple_template.Region", "Region"));										// 8
		info.add(builder.getUriToObject(EditModuleBlock.class));															// 9
		info.add(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);															// 10
		info.add(BuilderConstants.MODULE_NAME);																				// 11
		info.add(iwrb.getLocalizedString("deleting", "Deleting..."));														// 12
		info.add(iwrb.getLocalizedString("are_you_sure", "Are you sure?"));													// 13
		info.add(iwrb.getLocalizedString("saving", "Saving..."));															// 14
		info.add(iwrb.getLocalizedString("loading", "Loading..."));															// 15
		info.add(BuilderConstants.IB_PAGE_PARAMETER);																		// 16
		info.add(BuilderConstants.HANLDER_VALUE_OBJECTS_STYLE_CLASS);														// 17
		info.add(iwrb.getLocalizedString("reloading", "Reloading..."));														// 18
		info.add(iwrb.getLocalizedString("moving", "Moving..."));															// 19
		info.add(iwrb.getLocalizedString("drop_area", "Drop module into"));													// 20
		info.add(iwrb.getLocalizedString("copying", "Copying..."));															// 21
		info.add(iwrb.getLocalizedString("region", "region"));																// 22
		
		return info;
	}
	
	public String addModule(String pageKey, String containerId, String instanceId, int objectId, boolean useThread) {
		if (pageKey == null || instanceId == null || objectId < 0) {
			return null;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		
		return addModule(iwc, pageKey, containerId, instanceId, objectId, useThread);
	}
	
	public Document addSelectedModule(String pageKey, String instanceId, int objectId, String containerId, String className, int index, boolean useThread) {
		if (pageKey == null || instanceId == null || objectId < 0 || className == null) {
			return null;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}

		UIComponent component = getComponentInstance(className);
		if (component == null) {
			return null;
		}
		
		String uuid = addModule(iwc, pageKey, containerId, instanceId, objectId, useThread);
		if (uuid == null) {
			return null;
		}
		if (useThread) {
			component.setId(uuid);
		}
		if (component instanceof PresentationObject) {
			ICObjectInstance oi = builder.getIBXMLReader().getICObjectInstanceFromComponentId(uuid, className, pageKey);
			if (oi != null) {
				((PresentationObject) component).setICObjectInstanceID(oi.getID());
			}
		}
		
		Document transformedModule = getTransformedModule(pageKey, iwc, component, index, containerId);
		IWSlideSession session = getSession(iwc);
		if (transformedModule != null && session != null) {
			builder.clearAllCachedPages();	// Because IBXMLPage is saved using other thread, need to delete cache
		}
		
		return transformedModule;
	}
	
	public Document getRenderedModule(String pageKey, String uuid, int index, String parentId) {
		if (pageKey == null || uuid == null) {
			return null;
		}
		
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		
		UIComponent component = builder.findComponentInPage(iwc, pageKey, uuid);
		if (component == null) {
			return null;
		}
		
		return getTransformedModule(pageKey, iwc, component, index, parentId);
	}
	
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId) {
		if (pageKey == null || parentId == null || instanceId == null) {
			return false;
		}
		boolean result = false;
		IWSlideSession session = getSession(CoreUtil.getIWContext());
		synchronized (BuilderEngineBean.class) {
			result = builder.deleteModule(pageKey, parentId, instanceId, session);
		}
		if (result && session != null) {
			builder.clearAllCachedPages();
		}
		return result;
	}
	
	public Document getPropertyBox(String pageKey, String propertyName, String objectInstanceId) {
		if (propertyName == null || objectInstanceId == null) {
			return null;
		}
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		if (pageKey == null) {
			pageKey = String.valueOf(iwc.getCurrentIBPageID());
		}
		
		iwc.setApplicationAttribute(BuilderConstants.IB_PAGE_PARAMETER, pageKey);
		iwc.setApplicationAttribute(BuilderConstants.METHOD_ID_PARAMETER, propertyName);
		iwc.setApplicationAttribute(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, objectInstanceId);
		
		PresentationObject propertyBox = new SetModulePropertyBlock();
		Document renderedBox = builder.getRenderedComponent(iwc, propertyBox, false);
		
		iwc.removeApplicationAttribute(BuilderConstants.IB_PAGE_PARAMETER);
		iwc.removeApplicationAttribute(BuilderConstants.METHOD_ID_PARAMETER);
		iwc.removeApplicationAttribute(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);
		
		return renderedBox;
	}
	
	public boolean setSimpleModuleProperty(String pageKey, String moduleId, String propertyName, String propertyValue) {
		if (builder.setModuleProperty(pageKey, moduleId, propertyName, new String[] {propertyValue})) {
			clearCacheIfNeeded(pageKey, moduleId);
			return true;
		}
		return false;
	}
	
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] values) {
		if (builder.setModuleProperty(pageKey, moduleId, propertyName, values)) {
			clearCacheIfNeeded(pageKey, moduleId);
			return true;
		}
		return false;
	}
	
	public Document reRenderObject(String pageKey, String instanceId) {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		
		UIComponent object = builder.findComponentInPage(iwc, pageKey, instanceId);
		if (object instanceof Table) {
			Page page = builder.getPage(pageKey, iwc);
			object = builder.getTransformedTable(page, pageKey, object, iwc, iwc.getSessionAttribute(BuilderLogic.CLIPBOARD) == null);
		}
		
		boolean isJsfComponent = isModuleJsfType(pageKey, instanceId);
		return builder.getRenderedComponent(iwc, object, isJsfComponent);
	}
	
	public boolean copyModule(String pageKey, String parentId, String instanceId) {
		if (pageKey == null || instanceId == null) {
			return false;
		}
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}
		
		cutModule = parentId == null ? null : new CutModuleBean(pageKey, parentId, instanceId);
		
		return builder.copyModule(iwc, pageKey, instanceId);
	}
	
	public String[] isModuleInClipboard() {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		String[] ids = new String[2];
		Object o = iwc.getSessionAttribute(BuilderLogic.CLIPBOARD);
		if (o instanceof XMLElement) {
			ids[0] = ((XMLElement) o).getAttributeValue(IBXMLConstants.ID_STRING);
		}
		if (cutModule != null) {
			ids[1] = cutModule.getInstanceId();
		}
		return ids;
	}
	
	public Document pasteModule(String pageKey, String parentId, int modulesCount, boolean paste) {
		if (pageKey == null || parentId == null) {
			return null;
		}
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		
		String instanceId = null;
		if (paste) {
			instanceId = builder.pasteModule(pageKey, parentId, iwc);
		}
		else if (cutModule != null) {
			instanceId = builder.moveModule(pageKey, cutModule.getPageKey(), cutModule.getParentId(), cutModule.getInstanceId(), parentId, iwc);
			cutModule = null;
		}
		
		if (instanceId == null) {
			return null;
		}
		
		iwc.removeSessionAttribute(BuilderLogic.CLIPBOARD);

		return getTransformedModule(pageKey, iwc, builder.findComponentInPage(iwc, pageKey, instanceId), (modulesCount + 1), parentId);
	}
	
	public boolean moveModule(String instanceId, String pageKey, String formerParentId, String newParentId, String neighbourInstanceId, boolean insertAbove) {
		if (instanceId == null || pageKey == null || formerParentId == null || newParentId == null || neighbourInstanceId == null) {
			return false;
		}
		
		try {
			return builder.moveModule(instanceId, pageKey, formerParentId, newParentId, neighbourInstanceId, insertAbove);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Document getTransformedModule(String pageKey, IWContext iwc, UIComponent component, int index, String parentId) {
		Page currentPage = builder.getPage(pageKey, iwc);
		if (currentPage == null || component == null) {
			return null;
		}
		
		PresentationObject transformed = builder.getTransformedObject(currentPage, pageKey, component, index, currentPage, parentId, iwc);
		
		boolean isJsfComponent = IBPropertyHandler.getInstance().isJsfComponent(iwc, component.getClass().getName());
		return builder.getRenderedComponent(iwc, transformed, isJsfComponent);
	}
	
	private UIComponent getComponentInstance(String className) {
		Class<?> objectClass = null;
		try {
			objectClass = RefactorClassRegistry.forName(className);
		} catch (ClassNotFoundException e) {
			log.error(e);
			return null;
		}

		Object o = null;
		try {
			o = objectClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		UIComponent component = null;
		if (o instanceof UIComponent) {
			component = (UIComponent) o;
		}
		else {
			log.error("Unknown object: " + o);
			return null;
		}
		
		return component;
	}
	
	private String addModule(IWContext iwc, String pageKey, String containerId, String parentInstanceId, int objectId, boolean useThread) {
		String uuid = null;
		IWSlideSession session = null;
		if (useThread) {
			session = getSession(iwc);
		}
		
		if (parentInstanceId.indexOf(CoreConstants.DOT) != -1) {
			containerId = null;
		}
		
		synchronized (BuilderEngineBean.class) {
			uuid = builder.addNewModule(pageKey, parentInstanceId, objectId, containerId, session);
		}
		if (uuid == null) {
			return null;
		}
		return new StringBuffer(IBXMLReader.UUID_PREFIX).append(uuid).toString();
	}
	
	private IWSlideSession getSession(IWContext iwc) {
		if (iwc == null) {
			iwc = CoreUtil.getIWContext();
			if (iwc == null) {
				return null;
			}
		}
		IWSlideSession session = null;
		try {
			session = (IWSlideSession) IBOLookup.getSessionInstance(iwc, IWSlideSession.class);
		} catch (Exception e) {
			log.error(e);
			return null;
		}
		return session;
	}

	private class CutModuleBean {
		
		private String pageKey = null;
		private String parentId = null;
		private String instanceId = null;
		
		private  CutModuleBean(String pageKey, String parentId, String instanceId) {
			this.pageKey = pageKey;
			this.parentId = parentId;
			this.instanceId = instanceId;
		}
		
		private String getPageKey() {
			return pageKey;
		}
		
		private String getParentId() {
			return parentId;
		}
		
		private String getInstanceId() {
			return instanceId;
		}
	}
	
	private boolean isModuleJsfType(String pageKey, String instanceId) {
		String className = builder.getModuleClassName(pageKey, instanceId);
		IWContext iwc = CoreUtil.getIWContext();
		return IBPropertyHandler.getInstance().isJsfComponent(iwc, className);
	}
	
	private void clearCacheIfNeeded(String pageKey, String instanceId) {
		if (isModuleJsfType(pageKey, instanceId)) {
			IWContext iwc = CoreUtil.getIWContext();
			if (iwc != null) {
				IWCacheManager2 cache = IWCacheManager2.getInstance(iwc.getIWMainApplication());
				cache.reset();
			}
		}
	}
}
