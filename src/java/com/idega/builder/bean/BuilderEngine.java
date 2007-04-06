package com.idega.builder.bean;

import java.util.List;

import org.jdom.Document;

import com.idega.business.IBOService;

public interface BuilderEngine extends IBOService {
	
	public List<String> getBuilderInitInfo();
	
	public Document addSelectedModule(String pageKey, String instanceId, int objectId, String containerId, String className, int index);
	
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId);
	
}