/*
 * $Id: BuilderInitialSetup.java,v 1.2 2006/03/20 12:11:19 tryggvil Exp $
 * Created on 25.11.2005 in project com.idega.builder
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.bean;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * Managed bean to back-up the page jsp/initialSetup.jsp.
 * </p>
 *  Last modified: $Date: 2006/03/20 12:11:19 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.2 $
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
		if(builderLogic==null){
			builderLogic=BuilderLogic.getInstance();
		}
		if(application==null){
			application=IWMainApplication.getDefaultIWMainApplication();
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
			ICDomain cachedDomain = getApplication().getIWApplicationContext().getDomain();
			cachedDomain.setDomainName(getDomainName());
			
			ICDomainHome domainHome = (ICDomainHome)IDOLookup.getHome(ICDomain.class);
			ICDomain domain = domainHome.findFirstDomain();
			domain.setDomainName(getDomainName());
			domain.store();
			
			getBuilderLogic().initializeBuilderStructure(cachedDomain,getFrontPageName());
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
		return domainName;
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
		return frontPageName;
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
		return application;
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
		return builderLogic;
	}

	
	/**
	 * @param builderLogic The builderLogic to set.
	 */
	public void setBuilderLogic(BuilderLogic builderLogic) {
		this.builderLogic = builderLogic;
	}


}
