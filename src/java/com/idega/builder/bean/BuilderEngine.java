package com.idega.builder.bean;

import java.util.List;

import com.idega.business.IBOService;

public interface BuilderEngine extends IBOService {
	
	public List<String> getBuilderInitInfo();
	
	public boolean addSelectedModule(String pageKey, String instanceId, int newObjectId, String containerId);
	
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId);
	
}