/*
 * $Id: BuilderPageTag.java,v 1.2.2.1 2007/01/12 19:32:40 idegaweb Exp $
 * Created on 14.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import javax.faces.webapp.UIComponentTag;


/**
 * 
 * Last modified: $Date: 2007/01/12 19:32:40 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2.2.1 $
 */
public class BuilderPageTag extends UIComponentTag {

	private static String pageComponentId="BuilderPage_";
	
	private String type;
	private boolean locked;
	private String template;
	
	/**
	 * @return Returns the locked.
	 */
	public boolean isLocked() {
		return this.locked;
	}
	/**
	 * @param locked The locked to set.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	/**
	 * @return Returns the template.
	 */
	public String getTemplate() {
		return this.template;
	}
	/**
	 * @param template The template to set.
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return this.type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 
	 */
	public BuilderPageTag() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		String templateId = getTemplate();
		return pageComponentId+templateId;
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		return null;
	}
}
