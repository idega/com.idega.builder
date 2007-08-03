/*
 * Created on 8.7.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.builder.presentation;

import javax.faces.component.UIComponent;

import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Span;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.HiddenInput;

/**
 * @author tryggvil
 */
public class IBObjectControl extends PresentationObjectContainer {
	
	private Layer contentLayer = null;
	private Layer containerLayer = null;
	
	private PresentationObjectContainer parent;
	private String parentKey;
	private UIComponent object;
	private boolean isPresentationObject = false;
	private int number = 0;
	private boolean lastModuleInRegion = false;

	public String getBundleIdentifier(){
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}
	
	public IBObjectControl(UIComponent obj, PresentationObjectContainer parent, String parentKey, IWContext iwc, int index, boolean lastModuleInRegion) {
		this.parent = parent;
		this.object = obj;
		this.parentKey = parentKey;
		this.number = index;
		this.isPresentationObject = this.object instanceof PresentationObject;
		this.lastModuleInRegion = lastModuleInRegion;
		
		init(iwc);
		add(obj);
	}
	
	private void init(IWContext iwc) {
		
		IWBundle iwb = getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		
		//	Details for divs and layout are changed in the stylesheet
		//	Initialize stuff
	
		super.add(new Text("<!-- idegaweb-module starts -->"));
		
		//	Main container
		this.containerLayer = new Layer(Layer.DIV);
		this.containerLayer.setZIndex(this.number);
		this.containerLayer.setStyleClass("moduleContainer");
		super.add(this.containerLayer);
		
		//	Drop area at the begining
		this.containerLayer.add(getDropAreaLayer(true, iwc));
		
		String containerId = this.containerLayer.getId();
		
		//	Content
		this.contentLayer = new Layer(Layer.DIV);
		this.contentLayer.setStyleClass("moduleContent");
		String moduleContentId = new StringBuffer("content_").append(containerId).toString();
		this.contentLayer.setID(moduleContentId);
		
		//	Name
		Layer nameLayer = new Layer(Layer.DIV);
		nameLayer.setStyleClass("moduleName");
		nameLayer.setID("moduleName_"+containerId);
		
		//	Buttons
		Layer buttonsLayer = new Layer(Layer.DIV);
		buttonsLayer.setStyleClass("regionInfoImageContainer");
		
		// Upper part container
		Layer upperPartContainer = new Layer();
		upperPartContainer.add(nameLayer);
		upperPartContainer.add(buttonsLayer);
		upperPartContainer.add(new CSSSpacer());
		this.containerLayer.add(upperPartContainer);
		
		super.add(new Text("<!-- idegaweb-module ends -->"));
		
		if (this.object == null) {
			return;
		}
		
		//	Finally add the object to the contentlayer
		Span text = null; 
		if (this.isPresentationObject) {
			text = new Span(new Text(((PresentationObject)this.object).getBuilderName(iwc)));
		}
		else {
			//TODO make this localizable and remove getBuilderName from PO
			String className = this.object.getClass().getName();
			int indexOfDot = className.lastIndexOf(".");
			String objectName = null;
			if(indexOfDot!=-1){
				objectName = className.substring(indexOfDot+1,className.length());
			}
			else{
				objectName = className;
			}
			
			text = new Span(new Text(objectName));
		}
		StringBuffer tooltip = new StringBuffer(iwrb.getLocalizedString("move", "Move")).append(" :: ");
		tooltip.append(iwrb.getLocalizedString("move_to_other_location", "Move this module to other location"));
		text.setMarkupAttribute("title", tooltip.toString());
		text.setStyleClass("moduleNameTooltip");
		nameLayer.add(text);
		
		String instanceId = BuilderLogic.getInstance().getInstanceId(this.object);
		if (instanceId == null) {
			instanceId = this.object.getId();
		}
		
		HiddenInput instanceIdHidden = new HiddenInput("instanceId_"+containerId,instanceId);
		instanceIdHidden.setID("instanceId_"+containerId);
		
		HiddenInput parentIdHidden = new HiddenInput("parentId_"+containerId,this.parentKey);
		parentIdHidden.setID("parentId_"+containerId);
		
		String pageKey = BuilderLogic.getInstance().getCurrentIBPage(iwc);
		HiddenInput pageIdHidden = new HiddenInput("pageId_"+containerId,pageKey);
		pageIdHidden.setID("pageId_"+containerId);
		
		this.containerLayer.setMarkupAttribute("instanceid", instanceId);
		this.containerLayer.setMarkupAttribute("parentid", parentKey);
		this.containerLayer.setMarkupAttribute("pageid", pageKey);
		this.containerLayer.setMarkupAttribute("islastmodule", lastModuleInRegion);
		
		this.containerLayer.add(instanceIdHidden);
		this.containerLayer.add(parentIdHidden);
		this.containerLayer.add(pageIdHidden);
		
		this.containerLayer.add(this.contentLayer);
		
		//	Delete module
		StringBuffer title = new StringBuffer(iwrb.getLocalizedString("delete", "Delete")).append(" :: ");
		title.append(iwrb.getLocalizedString("delete_module", "Delete module"));
		Image deleteImage = iwb.getImage("delete_32.png", title.toString(), 24, 24);
		String separator = "', '";
		StringBuffer action = new StringBuffer("deleteModule('").append(containerId).append(separator).append(pageKey);
		action.append(separator).append(this.parentKey).append(separator).append(instanceId).append(separator);
		action.append(deleteImage.getId()).append("');");
		deleteImage.setOnClick(action.toString());
		deleteImage.setStyleClass(BuilderConstants.IMAGE_WITH_TOOLTIPS_STYLE_CLASS);
		buttonsLayer.add(deleteImage);
		
		//	Copy module
		title = new StringBuffer(iwrb.getLocalizedString("copy", "Copy")).append(" :: ");
		title.append(iwrb.getLocalizedString("copy_module", "Copy module"));
		Image copyModule = iwb.getImage("copy_24.gif", title.toString(), 24, 24);
		copyModule.setStyleClass(BuilderConstants.IMAGE_WITH_TOOLTIPS_STYLE_CLASS);
		action = new StringBuffer("copyThisModule('").append(containerId).append(separator).append(pageKey).append(separator);
		action.append(instanceId).append("');");
		copyModule.setOnClick(action.toString());
		buttonsLayer.add(copyModule);
		
		//	Cut module
		title = new StringBuffer(iwrb.getLocalizedString("cut", "Cut")).append(" :: ");
		title.append(iwrb.getLocalizedString("cut_module", "Cut module"));
		Image cutModule = iwb.getImage("cut_24.gif", title.toString(), 24, 24);
		cutModule.setStyleClass(BuilderConstants.IMAGE_WITH_TOOLTIPS_STYLE_CLASS);
		action = new StringBuffer("cutThisModule('").append(cutModule.getId()).append(separator).append(containerId).append(separator);
		action.append(pageKey).append(separator).append(parentKey).append(separator).append(instanceId).append("');");
		cutModule.setOnClick(action.toString());
		buttonsLayer.add(cutModule);
		
		//	Module properties
		title = new StringBuffer(iwrb.getLocalizedString("module_properties", "Properties")).append(" :: ");
		title.append(iwrb.getLocalizedString("set_module_properties", "Set module properties"));
		Image propertiesImage = iwb.getImage("info_32.png", title.toString(), 24, 24);
		propertiesImage.setStyleClass(BuilderConstants.IMAGE_WITH_TOOLTIPS_STYLE_CLASS);
		Link link = new Link(propertiesImage);
		link.setMarkupAttribute("rel", "moodalbox");
		link.setStyleClass("modulePropertiesLinkStyleClass");
		buttonsLayer.add(link);
		
		HiddenInput regionIdHidden = new HiddenInput("regionId", this.parentKey);
		buttonsLayer.add(regionIdHidden);
		HiddenInput moduleContentIdHidden = new HiddenInput("moduleContentId", moduleContentId);
		buttonsLayer.add(moduleContentIdHidden);
		
		if (lastModuleInRegion) {
			this.containerLayer.add(getDropAreaLayer(false, iwc));	//	The last module in region
		}
		
		//	The box always is around everything
		this.containerLayer.add(new CSSSpacer());
	}
	
	private Layer getDropAreaLayer(boolean topArea, IWContext iwc) {
		Layer dropArea = new Layer();
		dropArea.setStyleClass("moduleDropArea");
		dropArea.setMarkupAttribute("insertbefore", topArea);
		dropArea.add(new Text(getBundle(iwc).getResourceBundle(iwc).getLocalizedString("drop_area", "You can drop module here")));
		return dropArea;
	}
		
	public void add(UIComponent obj) {
		this.contentLayer.add(obj);
		obj.setParent(this.parent);
	}
	
	public void add(PresentationObject obj) {
		
		String objWidth = obj.getWidth();
		String objHeight = obj.getHeight();
		
		if (objWidth!=null) {
			this.containerLayer.setWidth(objWidth);
		}
		
		if (objHeight!=null) {
			this.containerLayer.setHeight(objHeight);
		}
		
		if (obj.getHorizontalAlignment()!=null) {
			this.containerLayer.setHorizontalAlignment(obj.getHorizontalAlignment());
		}

		if (obj instanceof Layer) {
			if (obj.isMarkupAttributeSet(Layer.LEFT)){
				this.containerLayer.setLeftPosition(obj.getMarkupAttribute(Layer.LEFT));
			}
			if (obj.isMarkupAttributeSet(Layer.TOP)){
				this.containerLayer.setTopPosition(obj.getMarkupAttribute(Layer.TOP));
			}
			if (obj.isMarkupAttributeSet(Layer.ZINDEX)){
				this.containerLayer.setZIndex(obj.getMarkupAttribute(Layer.ZINDEX));
			}
			obj.removeMarkupAttribute(Layer.LEFT);
			obj.removeMarkupAttribute(Layer.TOP);
			obj.removeMarkupAttribute(Layer.POSITION);
			obj.removeMarkupAttribute(Layer.ZINDEX);
		}
		
		this.contentLayer.add(obj);
		obj.setParentObject(this.parent);
		obj.setLocation(this.parent.getLocation());
		
	}

	/**
	 *
	 */
	public PresentationObject getLabelIcon(String parentKey, IWContext iwc, String label) {
		return getBuilderLogic().getLabelIcon(parentKey,iwc,label);
	}

	public PresentationObject getCutIcon(String key, String parentKey, IWContext iwc) {
		return getBuilderLogic().getCutIcon(key,parentKey,iwc);
	}

	public PresentationObject getCopyIcon(String key, String parentKey, IWContext iwc) {
		return getBuilderLogic().getCopyIcon(key,parentKey,iwc);
	}

	public PresentationObject getDeleteIcon(String key, String parentKey, IWContext iwc) {
		return getBuilderLogic().getDeleteIcon(key,parentKey,iwc);
	}

	public PresentationObject getPermissionIcon(String key, IWContext iwc) {
		return getBuilderLogic().getPermissionIcon(key,iwc);
	}

	public PresentationObject getEditIcon(String key, IWContext iwc) {
		return getBuilderLogic().getEditIcon(key,iwc);
	}

	public PresentationObject getPasteAboveIcon(String key, String parentKey, IWContext iwc) {
		return getBuilderLogic().getPasteAboveIcon(key,parentKey,iwc);
	}

	protected BuilderLogic getBuilderLogic() {
		return BuilderLogic.getInstance();
	}
}
