/*
 * Created on 8.7.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.builder.presentation;

import java.util.Arrays;

import javax.faces.component.UIComponent;

import com.idega.builder.bean.AdvancedProperty;
import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.business.ICBuilderConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Span;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.HiddenInput;
import com.idega.util.CoreConstants;

/**
 * @author tryggvil
 */
public class IBObjectControl extends PresentationObjectContainer {
	
	private Layer contentLayer = null;
	private Layer containerLayer = null;
	
	private UIComponent parent;
	private String parentKey;
	private UIComponent object;
	private boolean isPresentationObject = false;
	private int number = 0;
	private boolean lastModuleInRegion = false;
	private boolean moduleFromCurrentPage = true;

	@Override
	public String getBundleIdentifier(){
		return BuilderConstants.IW_BUNDLE_IDENTIFIER;
	}
	
	public IBObjectControl(UIComponent obj, UIComponent parent, String parentKey, IWContext iwc, int index, boolean lastModuleInRegion,
			boolean moduleFromCurrentPage) {
		this.parent = parent;
		this.object = obj;
		this.parentKey = parentKey;
		this.number = index;
		this.isPresentationObject = this.object instanceof PresentationObject;
		this.lastModuleInRegion = lastModuleInRegion;
		this.moduleFromCurrentPage = moduleFromCurrentPage;

		init(iwc);
		add(obj);
	}
	
	public boolean isPresentationObject() {
		return isPresentationObject;
	}

	public void setPresentationObject(boolean isPresentationObject) {
		this.isPresentationObject = isPresentationObject;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isLastModuleInRegion() {
		return lastModuleInRegion;
	}

	public void setLastModuleInRegion(boolean lastModuleInRegion) {
		this.lastModuleInRegion = lastModuleInRegion;
	}

	private void init(IWContext iwc) {
		IWBundle iwb = getBundle(iwc);
		IWResourceBundle iwrb = iwb.getResourceBundle(iwc);
		
		//	Details for divs and layout are changed in the stylesheet
		//	Initialize stuff
		super.add(new Text("<!-- idegaweb-module starts -->"));
		
		//	Main container
		this.containerLayer = new Layer(Layer.DIV);
		this.containerLayer.setStyleAttribute("z-index", this.number + "px");
		this.containerLayer.setStyleClass("moduleContainer");
		super.add(this.containerLayer);
		
		String containerId = this.containerLayer.getId();
		
		//	Content
		this.contentLayer = new Layer(Layer.DIV);
		this.contentLayer.setStyleClass("moduleContent");
		String moduleContentId = new StringBuffer("content_").append(containerId).toString();
		this.contentLayer.setID(moduleContentId);
		
		super.add(new Text("<!-- idegaweb-module ends -->"));
		
		if (moduleFromCurrentPage) {
			//	Drop area at the begining
			this.containerLayer.add(getDropAreaLayer(true, iwc));
		}
		
		//	Name
		Layer nameLayer = new Layer(Layer.DIV);
		String nameLayerClassName = "moduleName";
		if (!moduleFromCurrentPage) {
			nameLayerClassName = "moduleFromOtherPageName";
		}
		nameLayer.setStyleClass(nameLayerClassName);
		nameLayer.setID("moduleName_"+containerId);
		
		//	Buttons
		Layer buttonsLayer = new Layer(Layer.DIV);
		buttonsLayer.setStyleClass("regionInfoImageContainer");
		
		containerLayer.add(nameLayer);
		nameLayer.add(buttonsLayer);
		
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
			text = new Span(new Text(this.object.getClass().getSimpleName()));
		}
		if (moduleFromCurrentPage) {
			StringBuffer tooltip = new StringBuffer(iwrb.getLocalizedString("move", "Move")).append(" :: ");
			tooltip.append(iwrb.getLocalizedString("move_to_other_location", "Move this module to other location"));
			text.setMarkupAttribute("title", tooltip.toString());
			text.setStyleClass("moduleNameTooltip");
		}
		nameLayer.add(text);
		/*if (!moduleFromCurrentPage) {
			ICPage foreignPage = BuilderLogic.getInstance().findPageForModule(iwc, BuilderLogic.getInstance().getInstanceId(this.object));
			if (foreignPage != null) {
				String linkText = iwrb.getLocalizedString("link_to_edit_page", "Click here to edit this module");
				Link goToForeignPage = new Link(linkText, new StringBuilder("/pages").append(foreignPage.getDefaultPageURI()).append("?view=builder").toString());
				goToForeignPage.setStyleClass("foreignPageContainingCurrentModuleLinkStyle");
				goToForeignPage.setToolTip(linkText);
				nameLayer.add(goToForeignPage);
			}
		}*/
		
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
		
		if (moduleFromCurrentPage) {
			String separator = "', '";

			Lists list = new Lists();
			list.setListOrdered(true);
			buttonsLayer.add(list);
			
			//	Delete module
			ListItem item = new ListItem();
			item.setStyleClass("deleteModule");
			item.add(new Span(new Text(iwrb.getLocalizedString("delete", "Delete"))));
			list.add(item);
			StringBuffer action = new StringBuffer("deleteModule('").append(containerId).append(separator).append(instanceId);
			action.append(separator).append(item.getId()).append("');");
			item.setOnClick(action.toString());
			
			//	Copy module
			item = new ListItem();
			item.setStyleClass("copyModule");
			item.add(new Span(new Text(iwrb.getLocalizedString("copy", "Copy"))));
			list.add(item);
			action = new StringBuffer("copyThisModule('").append(containerId).append(separator).append(instanceId).append("');");
			item.setOnClick(action.toString());
			
			//	Cut module
			item = new ListItem();
			item.setStyleClass("cutModule");
			item.add(new Span(new Text(iwrb.getLocalizedString("cut", "Cut"))));
			list.add(item);
			action = new StringBuffer("cutThisModule('").append(item.getId()).append(separator).append(containerId).append(separator);
			action.append(instanceId).append("');");
			item.setOnClick(action.toString());
			
			//	Module properties
			item = new ListItem();
			item.setStyleClass("propertiesModule");
			list.add(item);
			Link link = new Link(new Span(new Text(iwrb.getLocalizedString("module_properties", "Properties"))));
			link.setMarkupAttribute("rel", "moodalbox");
			link.setStyleClass("modulePropertiesLinkStyleClass");
			link.setStyleClass("modulePropertiesButtonInitialized");
			link.setURL(BuilderLogic.getInstance().getUriToObject(EditModuleBlock.class, Arrays.asList(
					new AdvancedProperty(ICBuilderConstants.IC_OBJECT_INSTANCE_ID_PARAMETER, instanceId),
					new AdvancedProperty(BuilderConstants.IB_PAGE_PARAMETER_FOR_EDIT_MODULE_BLOCK, pageKey)
			)));
			item.add(link);
			
			HiddenInput regionIdHidden = new HiddenInput("regionId", this.parentKey);
			buttonsLayer.add(regionIdHidden);
			HiddenInput moduleContentIdHidden = new HiddenInput("moduleContentId", moduleContentId);
			buttonsLayer.add(moduleContentIdHidden);
		}
		
		if (lastModuleInRegion) {
			this.containerLayer.add(getDropAreaLayer(false, iwc));	//	The last module in region
		}
	}
	
	private Layer getDropAreaLayer(boolean topArea, IWContext iwc) {
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		Layer dropArea = new Layer();
		dropArea.setStyleClass("moduleDropArea");
		dropArea.setMarkupAttribute("insertbefore", topArea);
		StringBuilder label = new StringBuilder(iwrb.getLocalizedString("drop_area", "Drop module into")).append(CoreConstants.SPACE).append(this.parentKey);
		label.append(CoreConstants.SPACE).append(iwrb.getLocalizedString("region", "region"));
		dropArea.add(new Text(label.toString()));
		return dropArea;
	}
		
	@Override
	public void add(UIComponent obj) {
		this.contentLayer.add(obj);
		obj.setParent(this.parent);
	}
	
	@Override
	public void add(PresentationObject obj) {
		/*String objWidth = obj.getWidth();
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
		}*/
		
		obj.setStyleClass("addedBuilderModule");
		this.contentLayer.add(obj);
		if(this.parent instanceof PresentationObjectContainer){
			PresentationObjectContainer parentContainer = (PresentationObjectContainer)parent;
			obj.setParentObject(parentContainer);
			obj.setLocation(parentContainer.getLocation());
		}
		else{
			obj.setParent(this.parent);
		}
		
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
