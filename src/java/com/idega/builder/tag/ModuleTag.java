/*
 * $Id: ModuleTag.java,v 1.5 2008/09/05 10:11:22 valdas Exp $
 * Created on 14.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import javax.faces.webapp.UIComponentTag;

import com.idega.util.CoreConstants;
import com.idega.util.StringUtil;
import com.idega.webface.WFUtil;


/**
 *
 *  Last modified: $Date: 2008/09/05 10:11:22 $ by $Author: valdas $
 *
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
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
	@Override
	public String getId() {
		return this.id;
	}
	/**
	 * @param id The id to set.
	 */
	@Override
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
	@Override
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
	@Override
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
		return this.ic_object_id;
	}
	public void setIc_object_id(String ic_object_id) {
		this.ic_object_id = ic_object_id;
	}

	public String getComponentClass() {
		return this.componentClass;
	}

	public void setComponentClass(String componentClass) {
		if (StringUtil.isEmpty(componentClass)) {
			throw new NullPointerException("Component class name is null or empty!");
		}

		Object o = null;
		if (componentClass.startsWith(CoreConstants.HASH)) {
			try {
				o = WFUtil.invoke(componentClass);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if (o instanceof String) {
			componentClass = (String) o;
		}

		this.componentClass = componentClass;
	}
}
