/*
 * $Id: PropertyTagHandler.java,v 1.2 2009/01/14 15:35:25 tryggvil Exp $
 * Created on 15.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.facelets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.idega.builder.business.IBXMLConstants;
import com.idega.util.reflect.ComponentProperty;
import com.idega.util.reflect.Property;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * <p>
 * Implementation of the "property" tag in the IBXML page format
 * as a Facelets Tag handler
 * </p>
 * 
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson </a>
 * 
 * Last modified: $Date: 2009/01/14 15:35:25 $ by $Author: tryggvil $
 * @version $Id: PropertyTagHandler.java,v 1.2 2009/01/14 15:35:25 tryggvil Exp $
 */
public class PropertyTagHandler extends TagHandler{

	private String name;
	private String value;
	private String[] values;
	private String type;
	private UIComponent parent;

	private PropertyNameTagHandler nameChild;
	private List valueChildrenHandlers;
	
	static String MULTIVALUE_SEPARATOR=";";
	
    private final Logger log = Logger.getLogger("builder.tag.property");

	
	/**
	 * 
	 */
	public PropertyTagHandler(TagConfig config) {
		super(config);
        Iterator itr = this.findNextByType(PropertyNameTagHandler.class);
        while (itr.hasNext()) {
        	nameChild = (PropertyNameTagHandler) itr.next();
            if (log.isLoggable(Level.FINE)) {
                log.fine(tag + " found PropertyNameTagHandler[" + nameChild + "]");
            }
        }
        Iterator itr2 = this.findNextByType(PropertyValueTagHandler.class);
        PropertyValueTagHandler v = null;
        while (itr2.hasNext()) {
        	v = (PropertyValueTagHandler) itr2.next();
        	if(this.valueChildrenHandlers==null){
        		this.valueChildrenHandlers = new ArrayList();
        	}
            this.valueChildrenHandlers.add(v);
            if (log.isLoggable(Level.FINE)) {
                log.fine(tag + " found PropertyValueTagHandler[" + v + "]");
            }
        }
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
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
		return this.type;
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
		return this.value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	protected String[] getValues(){
		if(values==null){
			String value = getValue();
			if(value!=null){
				String values[] = value.split(MULTIVALUE_SEPARATOR);
				setValues(values);
				return values;
			}
			else{
				return null;
			}
		}
		else{
			return values;
		}
	}
	
	protected void setValues(String[] values){
	}
	


	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#getParent()
	 */
	public UIComponent getParent() {
		return this.parent;
	}


	public void setProperties() {
		
		UIComponent parentPO = getParent();
		//String key = parentPO.getId();
		String name = getName();
		String[] values = getValues();
		if(name!=null && values!=null){
			Class<? extends UIComponent> parentComponentClass = parentPO.getClass();
			System.out.println("PropertyTagHandler: property: name='"+name+"' value='"+value+"' for component:"+parentComponentClass.getName()+","+parentPO.getId());
			try{
				Property property=null;
				if(name.startsWith(IBXMLConstants.METHOD_STRING)){
					//Is old style methodIdentifier - using Property Class
					property = new Property(name,parentComponentClass);
				}
				else{
					//Else use standard JSF/Beans property handling
					property = new ComponentProperty(name,parentComponentClass);
				}
				property.setPropertyValues(values);
				property.setPropertyOnInstance(parentPO);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		//PropertyCache pc = PropertyCache.getInstance();
		//pc.addProperty(key,property);
	}
	
	//protected PresentationObject getParentPresentationObject(){
	//	return (PresentationObject)getParentUIComponent();
	//}
	
	public void release() {
		this.value=null;
		this.name=null;
		this.type=null;
		this.parent=null;
	}

	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {
		this.parent=parent;
		TagAttribute nameAttr = this.getAttribute("name");
		if(nameAttr!=null){
			this.name=nameAttr.getValue();
		}
		TagAttribute valueAttr = this.getAttribute("value");
		if(valueAttr!=null){
			this.value=valueAttr.getValue();
		}
		if(this.nameChild!=null){
			this.name=nameChild.getName();
		}
		if(this.valueChildrenHandlers!=null){
			if(!this.valueChildrenHandlers.isEmpty()){
				ArrayList valuesList = new ArrayList();
				for (Iterator iterator = valueChildrenHandlers.iterator(); iterator
						.hasNext();) {
					PropertyValueTagHandler handler = (PropertyValueTagHandler) iterator.next();
					valuesList.add(handler.getValue());
				}
				this.values=(String[]) valuesList.toArray(new String[0]);
			}
		}
		setProperties();
	}
}