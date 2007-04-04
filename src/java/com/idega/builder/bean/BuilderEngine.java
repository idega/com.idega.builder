package com.idega.builder.bean;

import java.util.List;

import org.jdom.Document;

import com.idega.business.IBOService;

public interface BuilderEngine extends IBOService {
	
	public List<String> getBuilderInitInfo();
	
	public Document addSelectedModule(String pageKey, String instanceId, int newObjectId, String containerId, String className);
	
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId);
	
}