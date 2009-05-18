package com.idega.builder.business;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWApplicationContext;

@Scope(BeanDefinition.SCOPE_SINGLETON)
@Service(BuilderLogicWrapper.SPRING_BEAN_NAME_BUILDER_LOGIC_WRAPPER)
public class BuilderLogicWrapperBean implements BuilderLogicWrapper {

	public boolean reloadGroupsInCachedDomain(IWApplicationContext iwac, String serverName) {
		return BuilderLogic.getInstance().reloadGroupsInCachedDomain(iwac, serverName);
	}

	public BuilderService getBuilderService(IWApplicationContext iwac) {
		try {
			return BuilderServiceFactory.getBuilderService(iwac);
		} catch(Exception e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error getting " + BuilderService.class, e);
		}
		
		return null;
	}

}
