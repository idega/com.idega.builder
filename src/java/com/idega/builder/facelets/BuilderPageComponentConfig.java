package com.idega.builder.facelets;

import com.idega.idegaweb.IWMainApplication;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.ComponentConfig;

public class BuilderPageComponentConfig implements ComponentConfig {

    protected final TagConfig parent;

    //protected final String componentType;

    protected String rendererType;
	private String pageId;
	private String templateReference;
	private String templateId;
	
	
    public BuilderPageComponentConfig(TagConfig parent, String pageId,
            String templateReference) {
        this.parent = parent;
        this.pageId=pageId;
        setTemplateReference(templateReference);
    }

    public String getTemplateComponentType() {
        //return this.componentType;
    	return IWMainApplication.BUILDER_PAGE_PREFIX+"_"+getTemplateId();
    }
    
    public String getComponentType(){
    	return IWMainApplication.BUILDER_PAGE_PREFIX+"_"+getPageId();
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

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getPageId() {
		return pageId;
	}

	public void setTemplateReference(String reference) {
		this.templateReference = reference;
		decodeTemplateId(templateReference);
	}

	public void decodeTemplateId(String templateReference2) {
		try{
			Integer.parseInt(templateReference2);
			//We have an int templateId:
			setTemplateId(templateReference2);
		}
		catch(NumberFormatException nfe){
			//We have an URI reference:
			setTemplateId(FaceletsUtil.getPageKey(templateReference2));
		}
	}

	public String getTemplateReference() {
		return templateReference;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getTemplateId() {
		return templateId;
	}
}
