/*
 * $Id: ModuleTag.java,v 1.1 2004/12/15 22:00:38 tryggvil Exp $
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
 *  Last modified: $Date: 2004/12/15 22:00:38 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
public class ModuleTag extends UIComponentTag {

	private static String componentId="BuilderModule_";
	
	private String id;
	private String ic_object_id;
	private String clazz;
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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		String regionId = getId();
		return componentId+regionId;
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @return Returns the componentId.
	 */
	public static String getComponentId() {
		return componentId;
	}
	/**
	 * @param componentId The componentId to set.
	 */
	public static void setComponentId(String componentId) {
		ModuleTag.componentId = componentId;
	}
	/**
	 * @return Returns the clazz.
	 */
	public String getModuleClass() {
		return clazz;
	}
	/**
	 * @param clazz The clazz to set.
	 */
	public void setClass(String clazz) {
		this.clazz = clazz;
	}
	/**
	 * @return Returns the ic_object_id.
	 */
	public String getIc_object_id() {
		return ic_object_id;
	}
	/**
	 * @param ic_object_id The ic_object_id to set.
	 */
	public void setIc_object_id(String ic_object_id) {
		this.ic_object_id = ic_object_id;
	}
}
