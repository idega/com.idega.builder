package com.idega.builder.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;

import org.jdom2.Document;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.builder.business.IBXMLConstants;
import com.idega.builder.business.IBXMLReader;
import com.idega.builder.presentation.AddModuleBlock;
import com.idega.builder.presentation.EditModuleBlock;
import com.idega.builder.presentation.SetModulePropertyBlock;
import com.idega.business.IBOSessionBean;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.core.cache.IWCacheManager2;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.repository.RepositorySession;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.CoreConstants;
import com.idega.util.CoreUtil;
import com.idega.util.StringUtil;
import com.idega.xml.XMLElement;

public class BuilderEngineBean extends IBOSessionBean implements BuilderEngine {

	private static final long serialVersionUID = -4806588458269035118L;
	private static final Logger LOGGER = Logger.getLogger(BuilderEngineBean.class.getName());

	private CutModuleBean cutModule = null;

	@Override
	public List<String> getBuilderInitInfo(String uri) {
		List<String> info = new ArrayList<String>();
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return info;
		}

		IWBundle bundle = BuilderLogic.getInstance().getBuilderBundle();
		IWResourceBundle iwrb = bundle.getResourceBundle(iwc);

		info.add(BuilderLogic.getInstance().getUriToObject(AddModuleBlock.class));											// 0
		info.add(iwrb.getLocalizedString("ib_addmodule_window", "Add a new Module"));										// 1
		info.add(iwrb.getLocalizedString("set_module_properties", "Set module properties"));								// 2
		info.add(new StringBuffer(bundle.getResourcesPath()).append("/add.png").toString());								// 3
		info.add(new StringBuffer(bundle.getResourcesPath()).append("/information.png").toString());						// 4
		info.add(iwrb.getLocalizedString("no_ids_inserting_module", "Error occurred while inserting selected module!"));	// 5

		String pageKey = null;
		try {
			pageKey = BuilderLogic.getInstance().getPageKeyByURI(uri, iwc.getDomain());
		} catch(Exception e) {
			LOGGER.log(Level.WARNING, "Error getting page key for uri: " + uri, e);
		}
		info.add(StringUtil.isEmpty(pageKey) ? String.valueOf(-1) : pageKey);												// 6

		info.add(iwrb.getLocalizedString("adding", "Adding..."));															// 7
		info.add(iwrb.getLocalizedString("create_simple_template.Region", "Region"));										// 8
		info.add(BuilderLogic.getInstance().getUriToObject(EditModuleBlock.class));											// 9
		info.add(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);														// 10
		info.add(BuilderConstants.MODULE_NAME);																				// 11
		info.add(iwrb.getLocalizedString("deleting", "Deleting..."));														// 12
		info.add(iwrb.getLocalizedString("are_you_sure", "Are you sure?"));													// 13
		info.add(iwrb.getLocalizedString("saving", "Saving..."));															// 14
		info.add(iwrb.getLocalizedString("loading", "Loading..."));															// 15
		info.add(BuilderConstants.IB_PAGE_PARAMETER_FOR_EDIT_MODULE_BLOCK);													// 16
		info.add(BuilderConstants.HANLDER_VALUE_OBJECTS_STYLE_CLASS);														// 17
		info.add(iwrb.getLocalizedString("reloading", "Reloading..."));														// 18
		info.add(iwrb.getLocalizedString("moving", "Moving..."));															// 19
		info.add(iwrb.getLocalizedString("drop_area", "Drop module into"));													// 20
		info.add(iwrb.getLocalizedString("copying", "Copying..."));															// 21
		info.add(iwrb.getLocalizedString("region", "region"));																// 22
		info.add(bundle.getVirtualPathWithFileNameString("remove.png"));													// 23
		info.add(iwrb.getLocalizedString("remove", "Remove"));																// 24

		return info;
	}

	@Override
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

	@Override
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
			ICObjectInstance oi = BuilderLogic.getInstance().getIBXMLReader().getICObjectInstanceFromComponentId(uuid, className, pageKey);
			if (oi != null) {
				((PresentationObject) component).setICObjectInstanceID(oi.getID());
			}
		}

		Document transformedModule = getTransformedModule(pageKey, iwc, component, index, containerId);
		RepositorySession session = getRepositorySession(iwc);
		if (transformedModule != null && session != null) {
			BuilderLogic.getInstance().clearAllCachedPages();	// Because IBXMLPage is saved using other thread, need to delete cache
		}

		return transformedModule;
	}

	@Override
	public Document getRenderedModule(String pageKey, String uuid, int index, String parentId) {
		if (pageKey == null || uuid == null) {
			return null;
		}

		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}

		UIComponent component = BuilderLogic.getInstance().findComponentInPage(iwc, pageKey, uuid);
		if (component == null) {
			return null;
		}

		return getTransformedModule(pageKey, iwc, component, index, parentId);
	}

	@Override
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId) {
		if (pageKey == null || parentId == null || instanceId == null) {
			return false;
		}
		boolean result = false;
		RepositorySession session = getRepositorySession(CoreUtil.getIWContext());
		result = BuilderLogic.getInstance().deleteModule(pageKey, parentId, instanceId, session);
		if (result && session != null) {
			BuilderLogic.getInstance().clearAllCachedPages();
		}
		return result;
	}

	@Override
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
		iwc.setApplicationAttribute(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, objectInstanceId);

		PresentationObject propertyBox = new SetModulePropertyBlock();
		Document renderedBox = BuilderLogic.getInstance().getRenderedComponent(iwc, propertyBox, false);

		iwc.removeApplicationAttribute(BuilderConstants.IB_PAGE_PARAMETER);
		iwc.removeApplicationAttribute(BuilderConstants.METHOD_ID_PARAMETER);
		iwc.removeApplicationAttribute(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);

		return renderedBox;
	}

	@Override
	public boolean setSimpleModuleProperty(String pageKey, String moduleId, String propertyName, String propertyValue) {
		if (BuilderLogic.getInstance().setModuleProperty(pageKey, moduleId, propertyName, new String[] {propertyValue})) {
			clearCacheIfNeeded(pageKey, moduleId);
			return true;
		}
		return false;
	}

	@Override
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] values) {
		if (BuilderLogic.getInstance().setModuleProperty(pageKey, moduleId, propertyName, values)) {
			clearCacheIfNeeded(pageKey, moduleId);
			return true;
		}
		return false;
	}

	@Override
	public Document reRenderObject(String pageKey, String instanceId) {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return null;
		}

		UIComponent object = BuilderLogic.getInstance().findComponentInPage(iwc, pageKey, instanceId);
		if (object instanceof Table) {
			Page page = BuilderLogic.getInstance().getPage(pageKey, iwc);
			object = BuilderLogic.getInstance().getTransformedTable(page, pageKey, object, iwc, iwc.getSessionAttribute(BuilderLogic.CLIPBOARD) == null);
		}

		boolean isJsfComponent = isModuleJsfType(pageKey, instanceId);
		return BuilderLogic.getInstance().getRenderedComponent(iwc, object, isJsfComponent);
	}

	@Override
	public boolean copyModule(String pageKey, String parentId, String instanceId) {
		if (pageKey == null || instanceId == null) {
			return false;
		}
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}

		cutModule = parentId == null ? null : new CutModuleBean(pageKey, parentId, instanceId);

		return BuilderLogic.getInstance().copyModule(iwc, pageKey, instanceId);
	}

	@Override
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

	@Override
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
			instanceId = BuilderLogic.getInstance().putModuleIntoRegion(iwc, pageKey, parentId, parentId, true);
		}
		else if (cutModule != null) {
			instanceId = BuilderLogic.getInstance().moveModule(pageKey, cutModule.getPageKey(), cutModule.getParentId(), cutModule.getInstanceId(), parentId, iwc);
			cutModule = null;
		}

		if (instanceId == null) {
			return null;
		}

		if (!paste) {
			iwc.removeSessionAttribute(BuilderLogic.CLIPBOARD);
		}

		return getTransformedModule(pageKey, iwc, BuilderLogic.getInstance().findComponentInPage(iwc, pageKey, instanceId), (modulesCount + 1), parentId);
	}

	@Override
	public boolean moveModule(String instanceId, String pageKey, String formerParentId, String newParentId, String neighbourInstanceId, boolean insertAbove) {
		if (instanceId == null || pageKey == null || formerParentId == null || newParentId == null || neighbourInstanceId == null) {
			return false;
		}

		try {
			return BuilderLogic.getInstance().moveModule(instanceId, pageKey, formerParentId, newParentId, neighbourInstanceId, insertAbove);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error moving module: " + instanceId + " for page: " + pageKey, e);
			return false;
		}
	}

	@Override
	public boolean removeProperty(String pageKey, String moduleId, String propertyName) {
		if (BuilderLogic.getInstance().removeModuleProperty(pageKey, moduleId, propertyName)) {
			clearCacheIfNeeded(pageKey, moduleId);
			return true;
		}

		return false;
	}

	@Override
	public boolean needReloadPropertyBox() {
		IWContext iwc = CoreUtil.getIWContext();
		if (iwc == null) {
			return false;
		}

		Object parameter = iwc.getSessionAttribute(BuilderConstants.BUILDER_MODULE_PROPERTY_HAS_BOOLEAN_TYPE_ATTRIBUTE);
		if (parameter instanceof Boolean) {
			return (Boolean) parameter;
		}

		return false;
	}

	private Document getTransformedModule(String pageKey, IWContext iwc, UIComponent component, int index, String parentId) {
		if (component == null) {
			return null;
		}
		Page currentPage = BuilderLogic.getInstance().getPage(pageKey, iwc);
		if (currentPage == null) {
			return null;
		}

		PresentationObject transformed = BuilderLogic.getInstance().getTransformedObject(currentPage, pageKey, component, index, currentPage, parentId, iwc);

		boolean isJsfComponent = IBPropertyHandler.getInstance().isJsfComponent(iwc, component.getClass().getName());
		return BuilderLogic.getInstance().getRenderedComponent(iwc, transformed, isJsfComponent);
	}

	private UIComponent getComponentInstance(String className) {
		Class<?> objectClass = null;
		try {
			objectClass = RefactorClassRegistry.forName(className);
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.WARNING, "Class not found: " + className, e);
			return null;
		}

		Object o = null;
		try {
			o = objectClass.newInstance();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error getting instance for: " + objectClass.getName(), e);
			return null;
		}
		UIComponent component = null;
		if (o instanceof UIComponent) {
			component = (UIComponent) o;
		}
		else {
			LOGGER.warning("Unknown object: " + o);
			return null;
		}

		return component;
	}

	private String addModule(IWContext iwc, String pageKey, String containerId, String parentInstanceId, int objectId, boolean useThread) {
		String uuid = null;
		RepositorySession session = null;
		if (useThread) {
			session = getRepositorySession(iwc);
		}

		if (parentInstanceId.indexOf(CoreConstants.DOT) != -1) {
			containerId = null;
		}

		uuid = BuilderLogic.getInstance().addNewModule(pageKey, parentInstanceId, objectId, containerId, session);
		if (uuid == null) {
			return null;
		}
		return new StringBuffer(IBXMLReader.UUID_PREFIX).append(uuid).toString();
	}

	protected class CutModuleBean {

		private String pageKey = null;
		private String parentId = null;
		private String instanceId = null;

		protected  CutModuleBean(String pageKey, String parentId, String instanceId) {
			this.pageKey = pageKey;
			this.parentId = parentId;
			this.instanceId = instanceId;
		}

		protected String getPageKey() {
			return pageKey;
		}

		protected String getParentId() {
			return parentId;
		}

		protected String getInstanceId() {
			return instanceId;
		}
	}

	private boolean isModuleJsfType(String pageKey, String instanceId) {
		String className = BuilderLogic.getInstance().getModuleClassName(pageKey, instanceId);
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

	@Override
	public String getDecryptedClassName(String encryptedClassName) {
		return IWMainApplication.decryptClassName(encryptedClassName);
	}

}