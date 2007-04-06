package com.idega.builder.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.renderkit.html.util.HtmlBufferResponseWriterWrapper;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBXMLReader;
import com.idega.builder.presentation.AddModuleWindow;
import com.idega.builder.presentation.EditModuleWindow;
import com.idega.builder.presentation.IBObjectControl;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.slide.business.IWSlideSession;

public class BuilderEngineBean extends IBOServiceBean implements BuilderEngine {
	
	private static final long serialVersionUID = -4806588458269035118L;
	private static final Log log = LogFactory.getLog(BuilderEngineBean.class);
	
	private BuilderLogic builder = BuilderLogic.getInstance();
	
	private IWContext getIWContext() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc == null) {
			return IWContext.getInstance();
		}
		else {
			return IWContext.getIWContext(fc);
		}
	}
	
	private String getWindowLink(Class className) {
		Link l = new Link();
		l.setWindowToOpen(className);
		return l.getURL();
	}
	
	public List<String> getBuilderInitInfo() {
		List<String> info = new ArrayList<String>();
		IWContext iwc = getIWContext();
		if (iwc == null) {
			return info;
		}
		
		IWResourceBundle iwrb = builder.getBuilderBundle().getResourceBundle(iwc);
		
		info.add(getWindowLink(AddModuleWindow.class));																		// 0
		info.add(iwrb.getLocalizedString("ib_addmodule_window", "Add a new Module"));										// 1
		info.add(iwrb.getLocalizedString("set_module_properties", "Set module properties"));								// 2
		info.add(new StringBuffer(builder.getBuilderBundle().getResourcesPath()).append("/add.png").toString());			// 3
		info.add(new StringBuffer(builder.getBuilderBundle().getResourcesPath()).append("/information.png").toString());	// 4
		info.add(iwrb.getLocalizedString("no_ids_inserting_module", "Error occurred while inserting selected module!"));	// 5
		info.add(String.valueOf(iwc.getCurrentIBPageID()));																	// 6
		info.add(iwrb.getLocalizedString("adding", "Adding..."));															// 7
		info.add(iwrb.getLocalizedString("create_simple_template.Region", "Region"));										// 8
		info.add(getWindowLink(EditModuleWindow.class));																	// 9
		info.add(BuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER);															// 10
		info.add(BuilderConstants.MODULE_NAME);																				// 11
		info.add(iwrb.getLocalizedString("deleting", "Deleting..."));														// 12
		info.add(iwrb.getLocalizedString("are_you_sure", "Are You sure?"));													// 13
		
		return info;
	}
	
	public Document addSelectedModule(String pageKey, String instanceId, int objectId, String containerId, String className, int index) {
		if (pageKey == null || instanceId == null || objectId < 0 || containerId == null || className == null) {
			return null;
		}
		
		IWContext iwc = getIWContext();
		if (iwc == null) {
			return null;
		}
		IWSlideSession session = getSession(iwc);
		
		String uuid = null;
		synchronized (BuilderEngineBean.class) {
			uuid = builder.addNewModule(pageKey, instanceId, objectId, containerId, session);
		}
		if (uuid == null) {
			return null;
		}
		uuid = new StringBuffer(IBXMLReader.UUID_PREFIX).append(uuid).toString();

		Class objectClass = null;
		try {
			objectClass = RefactorClassRegistry.forName(className);
		} catch (ClassNotFoundException e) {
			log.error(e);
			return null;
		}
		PresentationObject obj = null;
		try {
			obj = (PresentationObject) objectClass.newInstance();
			obj.setId(uuid);
		} catch (Exception e){
			log.error(e);
			return null;
		}

		Page currentPage = builder.getPage(pageKey, iwc);
		if (currentPage == null) {
			return null;
		}
		IBObjectControl objectComponent =  new IBObjectControl(obj, currentPage, containerId, iwc, index);
		if (objectComponent == null) {
			return null;
		}

		HtmlBufferResponseWriterWrapper writer = HtmlBufferResponseWriterWrapper.getInstance(iwc.getResponseWriter());
		iwc.setResponseWriter(writer);		
		try {
			objectComponent.renderComponent(iwc);
		} catch (Exception e){
			log.error(e);
			return null;
		}
		
		String result = writer.toString();
		InputStream stream = new ByteArrayInputStream(result.getBytes());
		SAXBuilder sax = new SAXBuilder(false);
		Document newComponent = null;
		try {
			newComponent = sax.build(stream);
		} catch (JDOMException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} finally {
			closeStream(stream);
		}
		
		if (newComponent != null) {
			builder.clearAllCachedPages();	// Because IBXMLPage is saved using other thread, need to delete cache (also need to improve)
		}
		return newComponent;
	}
	
	private void closeStream(InputStream stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		} catch (IOException e) {
			log.error(e);
		}
	}
	
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId) {
		if (pageKey == null || parentId == null || instanceId == null) {
			return false;
		}
		boolean result = false;
		synchronized (BuilderEngineBean.class) {
			result = builder.deleteModule(pageKey, parentId, instanceId, getSession(getIWContext()));
		}
		if (result) {
			builder.clearAllCachedPages();
		}
		return result;
	}
	
	private IWSlideSession getSession(IWContext iwc) {
		if (iwc == null) {
			iwc = getIWContext();
			if (iwc == null) {
				return null;
			}
		}
		IWSlideSession session = null;
		try {
			session = (IWSlideSession) IBOLookup.getSessionInstance(iwc, IWSlideSession.class);
		} catch (IBOLookupException e) {
			log.error(e);
			return null;
		}
		return session;
	}

}
