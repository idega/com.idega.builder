/*
 * $Id: BuilderPageFacetMap.java,v 1.1 2004/12/20 08:55:07 tryggvil Exp $
 * Created on 16.12.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.tag;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.faces.component.UIComponent;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectComponentFacetMap;
import com.idega.presentation.PresentationObjectContainer;

/**
 * 
 *  Last modified: $Date: 2004/12/20 08:55:07 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.1 $
 */
public class BuilderPageFacetMap extends PresentationObjectComponentFacetMap {

	/**
	 * @param component
	 */
	public BuilderPageFacetMap(UIComponent component) {
		super(component);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub
		super.clear();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		// TODO Auto-generated method stub
		return super.clone();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return super.containsKey(key);
	}
	/* (non-Javadoc)
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return super.containsValue(value);
	}
	/* (non-Javadoc)
	 * @see java.util.Map#entrySet()
	 */
	public Set entrySet() {
		// TODO Auto-generated method stub
		return super.entrySet();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(Object key) {
		// TODO Auto-generated method stub
		return super.get(key);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectComponentFacetMap#getComponent()
	 */
	protected UIComponent getComponent() {
		// TODO Auto-generated method stub
		return super.getComponent();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return super.isEmpty();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#keySet()
	 */
	public Set keySet() {
		// TODO Auto-generated method stub
		return super.keySet();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		//return super.put(key, value);
		String facetKey = (String)key;
		String PREFIX="builder";
		if(facetKey.startsWith(PREFIX)){
			String regionKey = facetKey.substring(PREFIX.length(),facetKey.length());
			UIComponent region = findRegionComponent(regionKey);
			region.getChildren().add(value);
			return null;
		}
		else{
			return super.put(key,value);
		}
	}
	/**
	 * @param key
	 * @return
	 */
	protected UIComponent findRegionComponent(String key) {
		Page page = (Page) this.getComponent();

		UIComponent component = page;
			
		component = findRegionComponentRecursive(component,key);
		
		
		return component;
	}
	
	
	protected UIComponent findRegionComponentRecursive(UIComponent component, String key) {
		for (Iterator iter = component.getFacetsAndChildren(); iter.hasNext();) {
			UIComponent child = (UIComponent) iter.next();
			if(child instanceof PresentationObjectContainer){
				PresentationObjectContainer poc = (PresentationObjectContainer)child;
				String label = poc.getLabel();
				int icoinstanceId = poc.getICObjectInstanceID();
				//if(icoinstanceId==104){
				//	return component.getFacet("1.2");
//					boolean ok = true;
				//}
				if(key.equals(label)){
					return poc;
				}
			}
			UIComponent obj = findRegionComponentRecursive(child,key);
			if(obj!=null){
				return obj;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map t) {
		// TODO Auto-generated method stub
		super.putAll(t);
	}
	/* (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		// TODO Auto-generated method stub
		return super.remove(key);
	}
	/* (non-Javadoc)
	 * @see com.idega.presentation.PresentationObjectComponentFacetMap#setComponent(javax.faces.component.UIComponent)
	 */
	protected void setComponent(UIComponent _component) {
		// TODO Auto-generated method stub
		super.setComponent(_component);
	}
	/* (non-Javadoc)
	 * @see java.util.Map#size()
	 */
	public int size() {
		// TODO Auto-generated method stub
		return super.size();
	}
	/* (non-Javadoc)
	 * @see java.util.Map#values()
	 */
	public Collection values() {
		// TODO Auto-generated method stub
		return super.values();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
}
