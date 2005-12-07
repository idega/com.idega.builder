/*
 * $Id: BuilderInitialSetup.java,v 1.1 2005/12/07 11:35:50 tryggvil Exp $
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
import com.idega.idegaweb.IWMainApplication;


/**
 * <p>
 * Managed bean to back-up the page jsp/initialSetup.jsp.
 * </p>
 *  Last modified: $Date: 2005/12/07 11:35:50 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
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
		
		ICDomain domain = getApplication().getIWApplicationContext().getDomain();
		domain.setDomainName(getDomainName());
		domain.store();
		try {
			getBuilderLogic().initializeBuilderStructure(domain,getFrontPageName());
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
