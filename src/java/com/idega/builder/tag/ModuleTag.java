/*
 * $Id: ModuleTag.java,v 1.2 2005/02/01 21:45:51 tryggvil Exp $
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
 *  Last modified: $Date: 2005/02/01 21:45:51 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class ModuleTag extends UIComponentTag {

	private static String componentPrefix="BuilderModule";
	private static String SEPARATOR="_";
	
	private String id;
	private String ic_object_id;
	private String componentClass;
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 
	 */
	public ModuleTag() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		String id = getId();
		String ic_object_id=getIc_object_id();
		//String ic_object_id=null;
		String moduleClass = getComponentClass();
		return componentPrefix+SEPARATOR+id+SEPARATOR+ic_object_id+SEPARATOR+moduleClass;
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		return null;
	}
	/**
	 * 
	public static String getComponentId() {
		return componentId;
	}
	public static void setComponentId(String componentId) {
		ModuleTag.componentId = componentId;
	}
	*/

	/*
	public void setClass(String clazz) {
		setComponentClass(clazz);
	}
	*/
	
	public String getIc_object_id() {
		return ic_object_id;
	}
	public void setIc_object_id(String ic_object_id) {
		this.ic_object_id = ic_object_id;
	}
	
	public String getComponentClass() {
		return componentClass;
	}
	public void setComponentClass(String componentClass) {
		this.componentClass = componentClass;
	}
}
