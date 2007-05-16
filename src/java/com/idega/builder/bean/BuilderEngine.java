package com.idega.builder.bean;

import java.util.List;

import org.jdom.Document;

import com.idega.business.IBOService;

public interface BuilderEngine extends IBOService {
	
	public List<String> getBuilderInitInfo();
	
	public Document addSelectedModule(String pageKey, String instanceId, int objectId, String containerId, String className, int index);
	
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId);
	
	public Document getPropertyBox(String pageKey, String propertyName, String objectInstanceId);
	
	public boolean setSimpleModuleProperty(String pageKey, String moduleId, String propertyName, String propertyValue);
	
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, List<AdvancedProperty> properties);
	
	public Document reRenderObject(String pageKey, String regionId, String instanceId);
	
	public boolean updateHandler(String[] values);
	
	public Document getRenderedPresentationObject(String className, String hiddenInputAttribute, boolean cleanHtml);
	
}