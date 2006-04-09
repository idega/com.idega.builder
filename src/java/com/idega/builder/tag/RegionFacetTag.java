/*
 * $Id: RegionFacetTag.java,v 1.2 2006/04/09 11:43:35 laddi Exp $
 * Created on 14.12.2004
 *
 * Copyright (C) 2004-2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import javax.faces.webapp.FacetTag;
import com.idega.presentation.BuilderPageFacetMap;


/**
 * <p>
 * Tag class for a Builder region implemented as a JSF facet and extends therefore the standard FacetTag.
 * </p>
 * Last modified: $Date: 2006/04/09 11:43:35 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.2 $
 */
public class RegionFacetTag extends FacetTag {

	private String id;
	private String label;
	
	/**
	 * 
	 */
	public RegionFacetTag() {
		super();
	}

	
	
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return this.id;
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
		return this.label;
	}
	/**
	 * @param label The label to set.
	 */
	public void setLabel(String label) {
		this.label = label;
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
		if(super.getName()==null){
			if(getLabel()!=null){
				return BuilderPageFacetMap.PREFIX+getLabel();
			}
			else{
				return BuilderPageFacetMap.PREFIX+getId();
			}
		}
		else{
			return super.getName();
		}
	}
}