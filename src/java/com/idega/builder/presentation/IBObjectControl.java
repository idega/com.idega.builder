/*
 * Created on 8.7.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.builder.presentation;




import com.idega.builder.business.BuilderLogic;
import com.idega.core.accesscontrol.business.AccessController;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.text.TextStyler;
import com.idega.xml.XMLElement;

/**
 * @author tryggvil
 */
public class IBObjectControl extends PresentationObjectContainer
{
	private static String IW_BUNDLE_IDENTIFIER="com.idega.builder";
	private com.idega.presentation.Layer _layer;
	private Table _table;
	private Table table;
	private Layer _tableLayer;
	private PresentationObjectContainer _parent;
	private String _parentKey;
	private PresentationObject _theObject;
	private int number = 0;
	String showLayers;
	String hideLayers;
	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}
	public IBObjectControl(
		PresentationObject obj,
		PresentationObjectContainer objectParent,
		String theParentKey,
		IWContext iwc,
		int index)
	{
		_parent = objectParent;
		_theObject = obj;
		_parentKey = theParentKey;
		number = index;
		init(iwc);
		add(obj);
	}
	public void main(IWContext iwc)
	{
		try
		{
			Page page = getParentPage();
			Script script = page.getAssociatedScript();
			script.addFunction(
				"findObj(n, d)",
				"function findObj(n, d) { \n\t var p,i,x;  if(!d) d=document; \n\t if((p=n.indexOf(\"?\"))>0&&parent.frames.length) { \n\t     d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p); \n\t } \n\t  if(!(x=d[n])&&d.all) x=d.all[n]; \n\t for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n]; \n\t  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=findObj(n,d.layers[i].document); \n\t if(!x && document.getElementById) x=document.getElementById(n); return x; \n }");
			script.addFunction(
				"showHideLayers()",
				"function showHideLayers() { \n\t var i,p,v,obj,args=showHideLayers.arguments; \n\t for (i=0; i<(args.length-2); i+=3) \n\t if ((obj=findObj(args[i]))!=null) { \n\t v=args[i+2]; \n\t   if (obj.style) { obj=obj.style; v=(v=='show')?'visible':(v='hide')?'hidden':v; \n\t }    obj.visibility=v; \n\t }\n}");
			getParentPage().setAssociatedScript(script);
		}
		catch (NullPointerException e)
		{
			System.out.println(
				"getParentPage() returns null in BuilderObjectControl for Object "
					+ _theObject.getClass().getName()
					+ " and ICObjectInstanceID="
					+ _theObject.getICObjectInstanceID());
		}
	}
	private void init(IWContext iwc)
	{
		String frameColor = "#000000";
		String topColor = "#CCCCCC";
		//Different color of the top and the frame if the object is set to change instance id on dpt pages that inherit current page
		if(_theObject.getChangeInstanceIDOnInheritance()) {
			frameColor = "#7D0000";
			topColor = "#D2C0C0";
		}
		_layer = new Layer(Layer.DIV);
		_tableLayer = new Layer(Layer.DIV);
		_tableLayer.setZIndex(0);
		//_tableLayer.setPositionType(Layer.ABSOLUTE);
		//_layer.setPositionType(Layer.RELATIVE);
		/** To work around layer stacking in Opera browser version 5, revise for newer versions */
		boolean hideLayer = iwc.isOpera();
		/** @todo Make a plug-in presentation/interface object which all plug-ins inherit */
		if (_theObject instanceof com.idega.block.messenger.presentation.Messenger)
			hideLayer = true;
		if (_theObject instanceof com.idega.presentation.Applet)
			hideLayer = true;
		if (_theObject instanceof com.idega.presentation.GenericPlugin)
			hideLayer = true;
		if (_theObject instanceof DropdownMenu)
			hideLayer = true;
		/*Layer controlLayer = new Layer(Layer.DIV);
		controlLayer.setPositionType(Layer.RELATIVE);
		controlLayer.setWidth(1);
		controlLayer.setHeight(1);*/
		Layer layer2 = new Layer(Layer.DIV);
		layer2.setZIndex(37999);
		layer2.setPositionType(Layer.ABSOLUTE);
		layer2.setWidth(0);
		layer2.setHeight(0);
		Layer layer = new Layer(Layer.DIV);
		layer2.add(layer);
		layer.setID(_layer.getID() + "a");
		layer.setPositionType(Layer.ABSOLUTE);
		layer.setTopPosition(-1);
		layer.setLeftPosition(-1);
		layer.setBackgroundColor("#CCCCCC");
		layer.setVisibility("hidden");
		layer.setZIndex(37999);
		layer.setWidth(0);
		layer.setHeight(0);
		hideLayers = "showHideLayers('" + layer.getID() + "','','hide');";
		if (hideLayer)
			hideLayers += " showHideLayers('" + _tableLayer.getID() + "','','show');";
		showLayers = "showHideLayers('" + layer.getID() + "','','show');";
		if (hideLayer)
			showLayers += " showHideLayers('" + _tableLayer.getID() + "','','hide');";
		layer.setMarkupAttribute("onmouseout", hideLayers);
		//controlLayer.add(layer);
		_table = new Table(3, 5);
		//_table.add(controlLayer);
		super.add(layer2);
		_table.add(_tableLayer, 2, 4);
		_layer.add(_table);
		_layer.setZIndex(number);
		super.add(_layer);
		_table.setBorder(0);
		//_table.setWidth(1,"3");
		//_table.setHeight(2,"1");
		_table.setWidth(1, "1");
		_table.setWidth(3, "1");
		_table.setHeight(1, "1");
		_table.setHeight(3, "1");
		_table.setHeight(5, "1");
		_table.setCellpadding(0);
		_table.setCellspacing(0);
		//_table.setCellspacing(1);
		//_table.setColor("#000000");
		//_table.setColor(1,2,"white");
		_table.setColor(2, 2, topColor);
		_table.setRowColor(1, frameColor);
		_table.setRowColor(3, frameColor);
		_table.setRowColor(5, frameColor);
		_table.setColumnColor(1, frameColor);
		_table.setColumnColor(3, frameColor);
		_table.setVerticalAlignment(2, 4, "top");
		/*_table.setLineFrame(true);
		_table.setLineAfterRow(1);
		_table.setLineWidth("1");
		_table.setLineHeight("1");
		_table.setLineColor("#000000");*/
		_table.setHeight(2, "11");
		Image image = getBundle(iwc).getImage("menuicon.gif", "Component menu");
		image.setHorizontalSpacing(1);
		image.setAlignment("absmiddle");
		image.setOnClick(showLayers);
		Text text = new Text("");
		text.setFontStyle(
			"font-size:5pt;font-family:Verdana,Arial,Helvetica,sans-serif;font-weight:bold;text-transform:uppercase;");
		if (_theObject != null)
		{
			//table.add(theObject.getClassName());
			StringBuffer buffer = new StringBuffer();
			buffer.append(Text.NON_BREAKING_SPACE);
			buffer.append(_theObject.getBuilderName(iwc));
			buffer.append(Text.NON_BREAKING_SPACE);
			text.setText(buffer.toString());
			_table.add(image, 2, 2);
			_table.add(text, 2, 2);
			Table rTable = new Table(3, 3);
			rTable.setWidth(80);
			rTable.setHeight(50);
			rTable.setHeight(1, "1");
			rTable.setHeight(3, "1");
			rTable.setWidth(1, "1");
			rTable.setWidth(3, "1");
			rTable.setColumnColor(1, frameColor);
			rTable.setColumnColor(3, frameColor);
			rTable.setRowColor(1, frameColor);
			rTable.setRowColor(3, frameColor);
			rTable.setCellpaddingAndCellspacing(0);
			table = new Table();
			table.setCellpadding(3);
			table.setCellspacing(0);
			table.setWidth("100%");
			table.setHeight("100%");
			table.setColor("#CCCCCC");
			table.setMarkupAttribute("onMouseOver", showLayers);
			table.setMarkupAttribute("onClick", hideLayers);
			rTable.add(table, 2, 2);
			Image separator = getBundle(iwc).getImage("shared/menu/menu_separator.gif");
			separator.setWidth("100%");
			separator.setHeight(2);
			XMLElement pasted = (XMLElement) iwc.getSessionAttribute(BuilderLogic.CLIPBOARD);
			if (pasted == null)
			{
				addToTable(getCutIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc), 1, 1);
				addToTable(
					getCutIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc),
					"Cut",
					IBCutModuleWindow.class,
					2,
					1);
				addToTable(getCopyIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc), 1, 2);
				addToTable(
					getCopyIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc),
					"Copy",
					IBCopyModuleWindow.class,
					2,
					2);
				addToTable(getDeleteIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc), 1, 3);
				addToTable(
					getDeleteIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc),
					"Delete",
					IBDeleteModuleWindow.class,
					2,
					3);
				table.add(separator, 2, 4);
				addToTable(getPermissionIcon(_theObject.getICObjectInstanceID(), iwc), 1, 5);
				addToTable(
					getPermissionIcon(_theObject.getICObjectInstanceID(), iwc),
					"Permission",
					IBPermissionWindow.class,
					2,
					5);
				addToTable(getEditIcon(_theObject.getICObjectInstanceID(), iwc), 1, 6);
				addToTable(
					getEditIcon(_theObject.getICObjectInstanceID(), iwc),
					"Properties",
					IBPropertiesWindow.class,
					2,
					6);
			}
			else
			{
				addToTable(getCutIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc), 1, 1);
				addToTable(
					getCutIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc),
					"Cut",
					IBCutModuleWindow.class,
					2,
					1);
				addToTable(getCopyIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc), 1, 2);
				addToTable(
					getCopyIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc),
					"Copy",
					IBCopyModuleWindow.class,
					2,
					2);
				addToTable(getPasteAboveIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc), 1, 3);
				addToTable(
					getPasteAboveIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc),
					"Paste",
					IBPasteModuleWindow.class,
					2,
					3);
				addToTable(getDeleteIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc), 1, 4);
				addToTable(
					getDeleteIcon(_theObject.getICObjectInstanceID(), _parentKey, iwc),
					"Delete",
					IBDeleteModuleWindow.class,
					2,
					4);
				table.add(separator, 2, 5);
				addToTable(getPermissionIcon(_theObject.getICObjectInstanceID(), iwc), 1, 6);
				addToTable(
					getPermissionIcon(_theObject.getICObjectInstanceID(), iwc),
					"Permission",
					IBPermissionWindow.class,
					2,
					6);
				addToTable(getEditIcon(_theObject.getICObjectInstanceID(), iwc), 1, 7);
				addToTable(
					getEditIcon(_theObject.getICObjectInstanceID(), iwc),
					"Properties",
					IBPropertiesWindow.class,
					2,
					7);
			}
			table.setColumnColor(1, "#D8D8D1");
			table.setColumnColor(2, "#F9F8F7");
			table.setColumnAlignment(1, "center");
			layer.add(rTable);
		}
		else
		{
			_table.add(getDeleteIcon(0, _parentKey, iwc), 2, 2);
			_table.add(getEditIcon(0, iwc), 2, 2);
		}
	}
	private void addToTable(PresentationObject obj, int col, int row)
	{
		obj.setMarkupAttribute("onMouseOver", showLayers);
		table.add(obj, col, row);
	}
	private void addToTable(PresentationObject obj, String textString, Class className, int col, int row)
	{
		Text text = new Text(textString);
		text.setFontStyle(
			"font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-style: normal; font-size: 8pt; text-decoration: none; color: #000000");
		Link link = (Link) obj;
		link.setObject(text);
		if (className != null)
			link.setWindowToOpen(className);
		addToTable(link, col, row);
	}
	public void add(PresentationObject obj)
	{
		if (obj.isWidthSet())
		{
			_layer.setWidth(obj.getWidth());
			_table.setWidth(obj.getWidth());
		}
		if (obj.isHeightSet())
		{
			_layer.setHeight(obj.getHeight());
			_table.setHeight(obj.getHeight());
		}
		if (obj.isMarkupAttributeSet(PresentationObject.HORIZONTAL_ALIGNMENT))
		{
			_layer.setHorizontalAlignment(obj.getHorizontalAlignment());
		}
		if (obj instanceof Layer)
		{
			Layer tempLayer = (Layer) obj;
			TextStyler styler = new TextStyler(tempLayer.getStyleAttribute());
			
			if (styler.isStyleSet(Layer.LEFT)) {
				_layer.setLeftPosition(styler.getStyleValue(Layer.LEFT));
			}
			if (styler.isStyleSet(Layer.TOP)) {
				_layer.setLeftPosition(styler.getStyleValue(Layer.TOP));
			}
			if (styler.isStyleSet(Layer.ZINDEX)) {
				_layer.setLeftPosition(styler.getStyleValue(Layer.ZINDEX));
			}
			styler.removeStyleValue(Layer.LEFT);
			styler.removeStyleValue(Layer.TOP);
			styler.removeStyleValue(Layer.POSITION);
			styler.removeStyleValue(Layer.ZINDEX);
			obj.setStyleAttribute(styler.getStyleString());
		}
		_tableLayer.add(obj);
		obj.setParentObject(_parent);
		obj.setLocation(_parent.getLocation());
	}

	/**
	 *
	 */
	public static PresentationObject getLabelIcon(String parentKey, IWContext iwc, String label)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image labelImage = bundle.getImage("label.gif", "Put label on region");
		Link link = new Link(labelImage);
		link.setWindowToOpen(IBAddRegionLabelWindow.class);
		link.addParameter(BuilderLogic.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_LABEL);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IB_LABEL_PARAMETER, label);
		return (link);
	}

	public static PresentationObject getCutIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image cutImage = bundle.getImage("shared/menu/cut.gif", "Cut component");
		Link link = new Link(cutImage);
		link.setWindowToOpen(IBCutModuleWindow.class);
		link.addParameter(BuilderLogic.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_COPY);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	/**
	 *
	 */
	public static PresentationObject getCopyIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image copyImage = bundle.getImage("shared/menu/copy.gif", "Copy component");
		//copyImage.setAttribute("style","z-index: 0;");
		Link link = new Link(copyImage);
		link.setWindowToOpen(IBCopyModuleWindow.class);
		link.addParameter(BuilderLogic.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_COPY);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return (link);
	}

	public static PresentationObject getDeleteIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image deleteImage = bundle.getImage("shared/menu/delete.gif", "Delete component");
		Link link = new Link(deleteImage);
		link.setWindowToOpen(IBDeleteModuleWindow.class);
		link.addParameter(BuilderLogic.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_DELETE);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	public static PresentationObject getPermissionIcon(int key, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image editImage = bundle.getImage("shared/menu/permission.gif", "Set permissions");
		Link link = new Link(editImage);
		link.setWindowToOpen(IBPermissionWindow.class);
		link.addParameter(BuilderLogic.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_PERMISSION);
		link.addParameter(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER, key);
		link.addParameter(
			IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY,
			AccessController.CATEGORY_OBJECT_INSTANCE);
		return link;
	}

	public static PresentationObject getEditIcon(int key, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image editImage = bundle.getImage("shared/menu/edit.gif", "Properties");
		Link link = new Link(editImage);
		link.setWindowToOpen(IBPropertiesWindow.class);
		link.addParameter(BuilderLogic.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_EDIT);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return link;
	}

	/**
	 *
	 */
	public static PresentationObject getPasteAboveIcon(int key, String parentKey, IWContext iwc)
	{
		IWBundle bundle = iwc.getIWMainApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
		Image pasteImage = bundle.getImage("shared/menu/paste.gif", "Paste above component");
		//copyImage.setAttribute("style","z-index: 0;");
		Link link = new Link(pasteImage);
		link.setWindowToOpen(IBPasteModuleWindow.class);
		link.addParameter(BuilderLogic.IB_PAGE_PARAMETER, BuilderLogic.getCurrentIBPage(iwc));
		link.addParameter(BuilderLogic.IB_CONTROL_PARAMETER, BuilderLogic.ACTION_PASTE_ABOVE);
		link.addParameter(BuilderLogic.IB_PARENT_PARAMETER, parentKey);
		link.addParameter(BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER, key);
		return (link);
	}

}
