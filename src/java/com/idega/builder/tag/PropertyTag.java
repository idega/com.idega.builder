/*
 * $Id: PropertyTag.java,v 1.5 2006/03/29 13:01:09 laddi Exp $
 * Created on 15.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import com.idega.presentation.PresentationObject;
import com.idega.util.reflect.Property;

/**
 * 
 *  Last modified: $Date: 2006/03/29 13:01:09 $ by $Author: laddi $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.5 $
 */
public class PropertyTag implements BodyTag{

	private Tag parentTag;
	
	private String name;
	private String value;
	private String type;
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	static String MULTIVALUE_SEPARATOR=";";
	
	protected String[] getValues(){
		//if(values==null){
			String value = getValue();
			if(value!=null){
				String values[] = value.split(MULTIVALUE_SEPARATOR);
				setValues(values);
				return values;
			}
			else{
				return null;
			}
		//}
		//else{
		//	return values;
		//}
	}
	
	protected void setValues(String[] values){
	}
	
	/**
	 * 
	 */
	public PropertyTag() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#setPageContext(javax.servlet.jsp.PageContext)
	 */
	public void setPageContext(PageContext pContext) {

	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#setParent(javax.servlet.jsp.tagext.Tag)
	 */
	public void setParent(Tag tag) {
		this.parentTag=tag;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#getParent()
	 */
	public Tag getParent() {
		return parentTag;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		return BodyTag.EVAL_BODY_BUFFERED;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doEndTag()
	 */
	public int doEndTag() throws JspException {
		
		PresentationObject parentPO = getParentPresentationObject();
		//String key = parentPO.getId();
		String name = getName();
		String[] values = getValues();
		if(name!=null && values!=null){
			Class parentComponentClass = parentPO.getClass();
			Property property = new Property(name,parentComponentClass);
			property.setPropertyValues(values);
			property.setPropertyOnInstance(parentPO);
		}
		//PropertyCache pc = PropertyCache.getInstance();
		//pc.addProperty(key,property);
		return Tag.EVAL_PAGE;
	}

	protected UIComponent getParentUIComponent(){
		Tag parentTag = getParent();
		if(parentTag instanceof UIComponentTag){
			UIComponentTag uiTag = (UIComponentTag)parentTag;
			return uiTag.getComponentInstance();
		}
		return null;
	}
	
	protected PresentationObject getParentPresentationObject(){
		return (PresentationObject)getParentUIComponent();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#release()
	 */
	public void release() {
		this.value=null;
		this.name=null;
		this.parentTag=null;
		this.type=null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTag#setBodyContent(javax.servlet.jsp.tagext.BodyContent)
	 */
	public void setBodyContent(BodyContent bodyContent) {
		
		String bodyAsString = bodyContent.getString();
		String[] aPropertyName1 = bodyAsString.split("<name>");
		String propertyName1 = aPropertyName1[1];
		String[] aPropertyName2 = propertyName1.split("</name>");
		String propertyName = aPropertyName2[0];
		setName(propertyName);
		
		//String[] aPropertyValue1 = aPropertyName2[1].split("<value>");
		//String propertyValue1 = aPropertyValue1[1];
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTag#doInitBody()
	 */
	public void doInitBody() throws JspException {
		//boolean b = true;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.IterationTag#doAfterBody()
	 */
	public int doAfterBody() throws JspException {
		return 0;
	}
}
