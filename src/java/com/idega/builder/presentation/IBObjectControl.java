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
	private Layer scriptLayer;
	private Layer nameLayer;
	private Layer dropAreaLayer;
	
	private Table tempTable;

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
		_parent = objectParent;
		_theObject = obj;
		_parentKey = theParentKey;
		number = index;
		isPresentationObject = _theObject instanceof PresentationObject;
		
		
		init(iwc);
		add(obj);
	}
	
	private void init(IWContext iwc) {
		
		IWBundle iwb = getBundle(iwc);
		
		//details for divs and layout are changed in the stylesheet
		//initilize stuff
	
		super.add(new Text("<!-- idegaweb-module starts -->"));
		
		containerLayer = new Layer(Layer.DIV);
		containerLayer.setZIndex(number);
		containerLayer.setStyleClass("moduleContainer");

		//must have a parent before getId
		super.add(containerLayer);
		
		String containerId = containerLayer.getID();
		
		handleAndMenuLayer = new Layer(Layer.DIV);
		handleAndMenuLayer.setStyleClass("moduleHandle");
		handleAndMenuLayer.setID("handle_"+containerId);
		
		contentLayer = new Layer(Layer.DIV);
		contentLayer.setStyleClass("moduleContent");
		contentLayer.setID("content_"+containerId);
		
		buttonsLayer = new Layer(Layer.DIV);
		buttonsLayer.setStyleClass("moduleButtons");
		
		dropAreaLayer = new Layer(Layer.DIV);
		dropAreaLayer.setStyleClass("moduleDropArea");
		dropAreaLayer.setID("dropArea_"+containerId);
				
		nameLayer = new Layer(Layer.DIV);
		nameLayer.setStyleClass("moduleName");
		nameLayer.setID("moduleName_"+containerId);
		
		//temporary table solution
		//because I cannot figure out how do the css so the drop area extends under the button layer but not the name layer
		Table tempDragDropContainer = new Table(2,1);
		tempDragDropContainer.setStyleClass("DnDAreaTable");
		tempDragDropContainer.setColumnWidth(1,"60");
		tempDragDropContainer.setColumnWidth(2,"100%");
		tempDragDropContainer.setCellpaddingAndCellspacing(0);
			
		Script drag = new Script();
		drag.addFunction("", getBuilderLogic().getDraggableScript(containerId,nameLayer.getID()));
		Script drop = new Script();
		drop.addFunction("", getBuilderLogic().getModuleToModuleDroppableScript(containerId, dropAreaLayer.getID(),"moduleContainer","moduleDropAreaHover",iwb.getResourcesVirtualPath()+"/services/IWBuilderWS.jws"));
		
		//add scripts
		super.add(drag);
		super.add(drop);		
		
		super.add(new Text("<!-- idegaweb-module ends -->"));
		
		//finally add the object to the contentlayer
		if (_theObject != null) {
			Text text = null; 
			
			if(isPresentationObject){
				text = new Text(((PresentationObject)_theObject).getBuilderName(iwc));
			}
			else{
				//TODO make this localizable and remove getBuilderName from PO
				text = new Text(_theObject.getClass().getName());
			}
			nameLayer.add(text);
			
			//TODO change icobjectinstanceid to String 
			String instanceId = BuilderLogic.getInstance().getInstanceId(_theObject);
				
			HiddenInput instanceIdHidden = new HiddenInput("instanceId_"+containerId,instanceId);
			instanceIdHidden.setID("instanceId_"+containerId);
			
			HiddenInput parentIdHidden = new HiddenInput("parentId_"+containerId,_parentKey);
			parentIdHidden.setID("parentId_"+containerId);
			
			HiddenInput pageIdHidden = new HiddenInput("pageId_"+containerId,BuilderLogic.getInstance().getCurrentIBPage(iwc));
			pageIdHidden.setID("pageId_"+containerId);
			
			containerLayer.add(instanceIdHidden);
			containerLayer.add(parentIdHidden);
			containerLayer.add(pageIdHidden);
			
			XMLElement pasted = (XMLElement) iwc.getSessionAttribute(BuilderLogic.CLIPBOARD);
			if (pasted == null) {
				buttonsLayer.add(getCutIcon(instanceId, _parentKey, iwc));
				buttonsLayer.add(getCopyIcon(instanceId, _parentKey, iwc));
				buttonsLayer.add(getDeleteIcon(instanceId, _parentKey, iwc));
				buttonsLayer.add(getPermissionIcon(instanceId, iwc));
				buttonsLayer.add(getEditIcon(instanceId, iwc));
			}
			else {
				buttonsLayer.add(getCutIcon(instanceId, _parentKey, iwc));
				buttonsLayer.add(getCopyIcon(instanceId, _parentKey, iwc));
				buttonsLayer.add(getPasteAboveIcon(instanceId, _parentKey, iwc));
				buttonsLayer.add(getDeleteIcon(instanceId, _parentKey, iwc));
				buttonsLayer.add(getPermissionIcon(instanceId, iwc));
				buttonsLayer.add(getEditIcon(instanceId, iwc));
			}
			
			
			dropAreaLayer.add(buttonsLayer);
			
			tempDragDropContainer.add(nameLayer,1,1);
			tempDragDropContainer.add(dropAreaLayer,2,1);

			containerLayer.add(tempDragDropContainer);
			containerLayer.add(contentLayer);
			
			//experimental so the box always is around everything
			containerLayer.add(new CSSSpacer());
			
//			handleAndMenuLayer.add(nameLayer);
//			handleAndMenuLayer.add(buttonsLayer);	
			
		}
		else {//object being added is null for some reason!
			//setup layout
			containerLayer.add(handleAndMenuLayer);
			containerLayer.add(contentLayer);
			
			handleAndMenuLayer.add(getDeleteIcon("0", _parentKey, iwc));
			handleAndMenuLayer.add(getEditIcon("0", iwc));
		}
	}
		
	public void add(UIComponent obj) {
		contentLayer.add(obj);
		obj.setParent(_parent);
	}
	
	public void add(PresentationObject obj) {
		
		String objWidth = obj.getWidth();
		String objHeight = obj.getHeight();
		
		if (objWidth!=null) {
			containerLayer.setWidth(objWidth);
			//handleAndContentTable.setWidth(objWidth);
			//handleAndMenuLayer.setWidth(objWidth);
		}
		
		if (objHeight!=null) {
			containerLayer.setHeight(objHeight);
			//handleAndContentTable.setHeight(objHeight);
		}
		
		if (obj.getHorizontalAlignment()!=null) {
			containerLayer.setHorizontalAlignment(obj.getHorizontalAlignment());
		}

		if (obj instanceof Layer) {
			if (obj.isMarkupAttributeSet(Layer.LEFT)){
				containerLayer.setLeftPosition(obj.getMarkupAttribute(Layer.LEFT));
			}
			if (obj.isMarkupAttributeSet(Layer.TOP)){
				containerLayer.setTopPosition(obj.getMarkupAttribute(Layer.TOP));
			}
			if (obj.isMarkupAttributeSet(Layer.ZINDEX)){
				containerLayer.setZIndex(obj.getMarkupAttribute(Layer.ZINDEX));
			}
			obj.removeMarkupAttribute(Layer.LEFT);
			obj.removeMarkupAttribute(Layer.TOP);
			obj.removeMarkupAttribute(Layer.POSITION);
			obj.removeMarkupAttribute(Layer.ZINDEX);
		}
		
		contentLayer.add(obj);
		obj.setParentObject(_parent);
		obj.setLocation(_parent.getLocation());
		
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
