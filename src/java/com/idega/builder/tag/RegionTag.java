/*
 * $Id: RegionTag.java,v 1.2 2004/12/20 08:55:07 tryggvil Exp $
 * Created on 14.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import javax.faces.webapp.FacetTag;


/**
 * 
 *  Last modified: $Date: 2004/12/20 08:55:07 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class RegionTag extends FacetTag {

	//private static String componentId="BuilderRegion";
	
	private String id;
	private String label;
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
	 * @return Returns the label.
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * 
	 */
	public RegionTag() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
	/*
	public String getComponentType() {
		//String regionId = getId();
		//return componentId+regionId;
		return componentId;
	}

	public String getRendererType() {
		return null;
	}
	*/
	
	/** 
	 * Overrided from the super FacetTag to return the Label or Id attribute of the tag.
	 * (this method is called from UIComponentTag).
	 */
	public String getName() {
		String PREFIX="builder";
		if(super.getName()==null){
			if(getLabel()!=null){
				return PREFIX+getLabel();
			}
			else{
				return PREFIX+getId();
			}
		}
		else{
			return super.getName();
		}
	}
}
