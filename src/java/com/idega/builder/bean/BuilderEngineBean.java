package com.idega.builder.bean;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLReader;
import com.idega.builder.presentation.AddModuleBlock;
import com.idega.builder.presentation.EditModuleBlock;
import com.idega.builder.presentation.SetModulePropertyBlock;
import com.idega.business.IBOLookup;
import com.idega.business.IBOSessionBean;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.slide.business.IWSlideSession;
import com.idega.util.CoreUtil;

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
		info.add(iwrb.getLocalizedString("are_you_sure", "Are You sure?"));													// 13
		info.add(iwrb.getLocalizedString("saving", "Saving..."));															// 14
		info.add(iwrb.getLocalizedString("loading", "Loading..."));															// 15
		info.add(BuilderConstants.IB_PAGE_PARAMETER);																		// 16
		info.add(BuilderConstants.HANLDER_VALUE_OBJECTS_STYLE_CLASS);														// 17
		info.add(iwrb.getLocalizedString("reloading", "Reloading..."));														// 18
		info.add(iwrb.getLocalizedString("moving", "Moving..."));															// 19
		info.add(iwrb.getLocalizedString("drop_area", "You can drop module here"));											// 20
		info.add(iwrb.getLocalizedString("copying", "Copying..."));															// 21
		
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
		
		Document transformedModule = getTransformedModule(pageKey, iwc, component, index, containerId);
		IWSlideSession session = getSession(iwc);
		// Returning result
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
		return builder.setModuleProperty(pageKey, moduleId, propertyName, new String[] {propertyValue});
	}
	
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] values) {
		return builder.setModuleProperty(pageKey, moduleId, propertyName, values);
	}
	
	public Document reRenderObject(String pageKey, String instanceId) {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}
		return builder.getRenderedComponent(iwc, builder.findComponentInPage(iwc, pageKey, instanceId), false);
	}
	
	public boolean copyModule(String pageKey, String parentId, String instanceId) {
		if (pageKey == null || instanceId == null) {
			return false;
		}
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}
		
		cutModule = parentId == null ? null : new CutModuleBean(parentId, instanceId);
		
		return builder.copyModule(iwc, pageKey, instanceId);
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
			instanceId = builder.moveModule(pageKey, cutModule.getParentId(), cutModule.getInstanceId(), parentId, iwc);
			cutModule = null;
		}
		
		if (instanceId == null) {
			return null;
		}

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
		if (currentPage == null) {
			return null;
		}
		
		//	Getting IBObjectControl - 'container'
		PresentationObject transformed = builder.getTransformedObject(currentPage, pageKey, component, index, currentPage, parentId, iwc);
		
		return builder.getRenderedComponent(iwc, transformed, false);
	}
	
	private UIComponent getComponentInstance(String className) {
		Class objectClass = null;
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
		// Adding module
		String uuid = null;
		IWSlideSession session = null;
		if (useThread) {
			session = getSession(iwc);
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
		
		private String parentId = null;
		private String instanceId = null;
		
		private  CutModuleBean(String parentId, String instanceId) {
			this.parentId = parentId;
			this.instanceId = instanceId;
		}
		
		private String getParentId() {
			return parentId;
		}
		
		private String getInstanceId() {
			return instanceId;
		}
	}
}
