package com.idega.builder.business;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWApplicationContext;

@Scope("singleton")
@Service("builderLogicWrapperBeanId")
public class BuilderLogicWrapperBean implements BuilderLogicWrapper {

	public boolean reloadGroupsInCachedDomain(IWApplicationContext iwac, String serverName) {
		return BuilderLogic.getInstance().reloadGroupsInCachedDomain(iwac, serverName);
	}

	public BuilderService getBuilderService(IWApplicationContext iwac) {
		try {
			return BuilderServiceFactory.getBuilderService(iwac);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
