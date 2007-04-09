/*
 * $Id: BuilderInitialSetup.java,v 1.6 2007/04/09 22:17:55 tryggvil Exp $
 * Created on 25.11.2005 in project com.idega.builder
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.bean;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.business.ICDomainLookup;
import com.idega.core.builder.data.CachedDomain;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContextFactory;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWMainApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.servlet.filter.IWWelcomeFilter;


/**
 * <p>
 * Managed bean to back-up the page jsp/initialSetup.jsp.
 * </p>
 *  Last modified: $Date: 2007/04/09 22:17:55 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.6 $
 */
public class BuilderInitialSetup {

	private String domainName;
	private String frontPageName;
	private BuilderLogic builderLogic;
	private IWMainApplication application;
	
	/**
	 * 
	 */
	public BuilderInitialSetup() {
		initialize();
		load();
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initialize
	 * </p>
	 */
	private void initialize() {
		if(this.builderLogic==null){
			this.builderLogic=BuilderLogic.getInstance();
		}
		if(this.application==null){
			this.application=IWApplicationContextFactory.getCurrentIWApplicationContext().getIWMainApplication();
		}
	}

	/**
	 * <p>
	 * TODO tryggvil describe method load
	 * </p>
	 */
	private void load() {
		setDomainName(getApplication().getIWApplicationContext().getDomain().getDomainName());
		setFrontPageName("Home");
	}

	public String store(){
		try{
			
			IWContext iwc = IWContext.getInstance();
			String serverName = iwc.getServerName();
			
			ICDomainLookup lookup = ICDomainLookup.getInstance();
			
			ICDomain cachedDomain = lookup.getPersistentDomainByServerName(serverName);
			cachedDomain.setDomainName(getDomainName());
			
			ICDomainHome domainHome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);
			ICDomain domain = domainHome.findFirstDomain();
			domain.setDomainName(getDomainName());
			domain.store();
			
			getBuilderLogic().initializeBuilderStructure(domain,getFrontPageName());
			
			cachedDomain.setIBPage(domain.getStartPage());
			cachedDomain.setStartTemplate(domain.getStartTemplate());
			if(cachedDomain instanceof CachedDomain){
				CachedDomain ccachedDomain = (CachedDomain)cachedDomain;
				ccachedDomain.setStartTemplateID(domain.getStartTemplateID());
				ccachedDomain.setStartPage(domain.getStartPage());
				ccachedDomain.setStartPageID(domain.getStartPageID());
			}
			
			IWWelcomeFilter.unload();
			
			return "next";
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * @return Returns the domainName.
	 */
	public String getDomainName() {
		return this.domainName;
	}

	
	/**
	 * @param domainName The domainName to set.
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	
	/**
	 * @return Returns the frontPageName.
	 */
	public String getFrontPageName() {
		return this.frontPageName;
	}

	
	/**
	 * @param frontPageName The frontPageName to set.
	 */
	public void setFrontPageName(String frontPageName) {
		this.frontPageName = frontPageName;
	}

	
	/**
	 * @return Returns the application.
	 */
	public IWMainApplication getApplication() {
		return this.application;
	}

	
	/**
	 * @param application The application to set.
	 */
	public void setApplication(IWMainApplication application) {
		this.application = application;
	}

	
	/**
	 * @return Returns the builderLogic.
	 */
	public BuilderLogic getBuilderLogic() {
		return this.builderLogic;
	}

	
	/**
	 * @param builderLogic The builderLogic to set.
	 */
	public void setBuilderLogic(BuilderLogic builderLogic) {
		this.builderLogic = builderLogic;
	}


}
