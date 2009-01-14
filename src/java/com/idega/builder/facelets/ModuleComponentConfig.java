package com.idega.builder.facelets;

import com.idega.idegaweb.IWMainApplication;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class ModuleComponentConfig implements ComponentConfig {

    protected final TagConfig parent;

    //protected final String componentType;

    protected String rendererType;
	private String componentId;
	private String componentClass;

    public ModuleComponentConfig(TagConfig parent, String componentId,
            String componentClass) {
        this.parent = parent;
        this.componentId=componentId;
        this.componentClass=componentClass;
    }

    public String getComponentType() {
        //return this.componentType;
    	return IWMainApplication.BUILDER_MODULE_PREFIX+"_"+getComponentId()+"_"+getComponentClass();
    }

    public String getRendererType() {
        return this.rendererType;
    }

    public FaceletHandler getNextHandler() {
        return this.parent.getNextHandler();
    }

    public Tag getTag() {
        return this.parent.getTag();
    }

    public String getTagId() {
        return this.parent.getTagId();
    }

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentClass(String componentClass) {
		this.componentClass = componentClass;
	}

	public String getComponentClass() {
		return componentClass;
	}
}
