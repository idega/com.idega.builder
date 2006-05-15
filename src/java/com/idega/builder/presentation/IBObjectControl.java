/*
 * Created on 8.7.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.builder.presentation;




import javax.faces.component.UIComponent;
import com.idega.builder.business.BuilderLogic;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.CSSSpacer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.HiddenInput;
import com.idega.xml.XMLElement;

/**
 * @author tryggvil
 */
public class IBObjectControl extends PresentationObjectContainer
{
	private static String IW_BUNDLE_IDENTIFIER="com.idega.builder";
	private Layer containerLayer;
	private Layer handleAndMenuLayer;
	private Layer contentLayer;
	private Layer buttonsLayer;
	private Layer nameLayer;
	private Layer dropAreaLayer;
	
	private PresentationObjectContainer _parent;
	private String _parentKey;
	private UIComponent _theObject;
	boolean isPresentationObject = false;
	private int number = 0;

	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
	public IBObjectControl(
		UIComponent obj,
		PresentationObjectContainer objectParent,
		String theParentKey,
		IWContext iwc,
		int index)
	{
		this._parent = objectParent;
		this._theObject = obj;
		this._parentKey = theParentKey;
		this.number = index;
		this.isPresentationObject = this._theObject instanceof PresentationObject;
		
		
		init(iwc);
		add(obj);
	}
	
	private void init(IWContext iwc) {
		
		IWBundle iwb = getBundle(iwc);
		
		//details for divs and layout are changed in the stylesheet
		//initilize stuff
	
		super.add(new Text("<!-- idegaweb-module starts -->"));
		
		this.containerLayer = new Layer(Layer.DIV);
		this.containerLayer.setZIndex(this.number);
		this.containerLayer.setStyleClass("moduleContainer");

		//must have a parent before getId
		super.add(this.containerLayer);
		
		String containerId = this.containerLayer.getID();
		
		this.handleAndMenuLayer = new Layer(Layer.DIV);
		this.handleAndMenuLayer.setStyleClass("moduleHandle");
		this.handleAndMenuLayer.setID("handle_"+containerId);
		
		this.contentLayer = new Layer(Layer.DIV);
		this.contentLayer.setStyleClass("moduleContent");
		this.contentLayer.setID("content_"+containerId);
		
		this.buttonsLayer = new Layer(Layer.DIV);
		this.buttonsLayer.setStyleClass("moduleButtons");
		
		this.dropAreaLayer = new Layer(Layer.DIV);
		this.dropAreaLayer.setStyleClass("moduleDropArea");
		this.dropAreaLayer.setID("dropArea_"+containerId);
				
		this.nameLayer = new Layer(Layer.DIV);
		this.nameLayer.setStyleClass("moduleName");
		this.nameLayer.setID("moduleName_"+containerId);
		
		//temporary table solution
		//because I cannot figure out how do the css so the drop area extends under the button layer but not the name layer
		Table tempDragDropContainer = new Table(2,1);
		tempDragDropContainer.setStyleClass("DnDAreaTable");
		tempDragDropContainer.setColumnWidth(1,"60");
		tempDragDropContainer.setColumnWidth(2,"100%");
		tempDragDropContainer.setCellpaddingAndCellspacing(0);
			
		Script drag = new Script();
		drag.addFunction("", getBuilderLogic().getDraggableScript(containerId,this.nameLayer.getID()));
		Script drop = new Script();
		drop.addFunction("", getBuilderLogic().getModuleToModuleDroppableScript(containerId, this.dropAreaLayer.getID(),"moduleContainer","moduleDropAreaHover",iwb.getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
		
		//add scripts
		super.add(drag);
		super.add(drop);		
		
		super.add(new Text("<!-- idegaweb-module ends -->"));
		
		//finally add the object to the contentlayer
		if (this._theObject != null) {
			Text text = null; 
			
			if(this.isPresentationObject){
				text = new Text(((PresentationObject)this._theObject).getBuilderName(iwc));
			}
			else{
				//TODO make this localizable and remove getBuilderName from PO
				String className = this._theObject.getClass().getName();
				int indexOfDot = className.lastIndexOf(".");
				String objectName = null;
				if(indexOfDot!=-1){
					objectName = className.substring(indexOfDot+1,className.length());
				}
				else{
					objectName = className;
				}
				
				text = new Text(objectName);
			}
			this.nameLayer.add(text);
			
			//TODO change icobjectinstanceid to String 
			String instanceId = BuilderLogic.getInstance().getInstanceId(this._theObject);
			
			HiddenInput instanceIdHidden = new HiddenInput("instanceId_"+containerId,instanceId);
			instanceIdHidden.setID("instanceId_"+containerId);
			
			HiddenInput parentIdHidden = new HiddenInput("parentId_"+containerId,this._parentKey);
			parentIdHidden.setID("parentId_"+containerId);
			
			HiddenInput pageIdHidden = new HiddenInput("pageId_"+containerId,BuilderLogic.getInstance().getCurrentIBPage(iwc));
			pageIdHidden.setID("pageId_"+containerId);
			
			this.containerLayer.add(instanceIdHidden);
			this.containerLayer.add(parentIdHidden);
			this.containerLayer.add(pageIdHidden);
			
			XMLElement pasted = (XMLElement) iwc.getSessionAttribute(BuilderLogic.CLIPBOARD);
			if (pasted == null) {
				this.buttonsLayer.add(getCutIcon(instanceId, this._parentKey, iwc));
				this.buttonsLayer.add(getCopyIcon(instanceId, this._parentKey, iwc));
				this.buttonsLayer.add(getDeleteIcon(instanceId, this._parentKey, iwc));
				this.buttonsLayer.add(getPermissionIcon(instanceId, iwc));
				this.buttonsLayer.add(getEditIcon(instanceId, iwc));
			}
			else {
				this.buttonsLayer.add(getCutIcon(instanceId, this._parentKey, iwc));
				this.buttonsLayer.add(getCopyIcon(instanceId, this._parentKey, iwc));
				this.buttonsLayer.add(getPasteAboveIcon(instanceId, this._parentKey, iwc));
				this.buttonsLayer.add(getDeleteIcon(instanceId, this._parentKey, iwc));
				this.buttonsLayer.add(getPermissionIcon(instanceId, iwc));
				this.buttonsLayer.add(getEditIcon(instanceId, iwc));
			}
			
			
			this.dropAreaLayer.add(this.buttonsLayer);
			
			tempDragDropContainer.add(this.nameLayer,1,1);
			tempDragDropContainer.add(this.dropAreaLayer,2,1);

			this.containerLayer.add(tempDragDropContainer);
			this.containerLayer.add(this.contentLayer);
			
			//experimental so the box always is around everything
			this.containerLayer.add(new CSSSpacer());
			
//			handleAndMenuLayer.add(nameLayer);
//			handleAndMenuLayer.add(buttonsLayer);	
			
		}
		else {//object being added is null for some reason!
			//setup layout
			this.containerLayer.add(this.handleAndMenuLayer);
			this.containerLayer.add(this.contentLayer);
			
			this.handleAndMenuLayer.add(getDeleteIcon("0", this._parentKey, iwc));
			this.handleAndMenuLayer.add(getEditIcon("0", iwc));
		}
	}
		
	public void add(UIComponent obj) {
		this.contentLayer.add(obj);
		obj.setParent(this._parent);
	}
	
	public void add(PresentationObject obj) {
		
		String objWidth = obj.getWidth();
		String objHeight = obj.getHeight();
		
		if (objWidth!=null) {
			this.containerLayer.setWidth(objWidth);
			//handleAndContentTable.setWidth(objWidth);
			//handleAndMenuLayer.setWidth(objWidth);
		}
		
		if (objHeight!=null) {
			this.containerLayer.setHeight(objHeight);
			//handleAndContentTable.setHeight(objHeight);
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
		obj.setParentObject(this._parent);
		obj.setLocation(this._parent.getLocation());
		
}

	/**
	 *
	 */
	public PresentationObject getLabelIcon(String parentKey, IWContext iwc, String label)
	{
		return getBuilderLogic().getLabelIcon(parentKey,iwc,label);
	}

	public PresentationObject getCutIcon(String key, String parentKey, IWContext iwc)
	{
		return getBuilderLogic().getCutIcon(key,parentKey,iwc);
	}

	public PresentationObject getCopyIcon(String key, String parentKey, IWContext iwc)
	{
		return getBuilderLogic().getCopyIcon(key,parentKey,iwc);
	}

	public PresentationObject getDeleteIcon(String key, String parentKey, IWContext iwc)
	{
		return getBuilderLogic().getDeleteIcon(key,parentKey,iwc);
	}

	public PresentationObject getPermissionIcon(String key, IWContext iwc)
	{
		return getBuilderLogic().getPermissionIcon(key,iwc);
	}

	public PresentationObject getEditIcon(String key, IWContext iwc)
	{
		return getBuilderLogic().getEditIcon(key,iwc);
	}

	public PresentationObject getPasteAboveIcon(String key, String parentKey, IWContext iwc)
	{
		return getBuilderLogic().getPasteAboveIcon(key,parentKey,iwc);
	}

	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
}
