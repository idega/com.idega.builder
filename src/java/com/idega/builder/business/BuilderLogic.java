/*
 * $Id: BuilderLogic.java,v 1.104 2002/02/26 23:02:39 aron Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBDomain;
import com.idega.builder.presentation.IBAdminWindow;
import com.idega.builder.presentation.IBAddModuleWindow;
import com.idega.builder.presentation.IBDeleteModuleWindow;
import com.idega.builder.presentation.IBPropertiesWindow;
import com.idega.builder.presentation.IBPermissionWindow;
import com.idega.builder.presentation.IBLockRegionWindow;
import com.idega.builder.presentation.IBAddRegionLabelWindow;
import com.idega.builder.presentation.IBCopyModuleWindow;
import com.idega.builder.presentation.IBPasteModuleWindow;
import com.idega.core.data.ICObject;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.data.ICObjectInstance;
import com.idega.core.business.ICObjectBusiness;
import com.idega.block.IWBlock;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.Table;
import com.idega.presentation.RaisedTable;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Page;
import com.idega.presentation.Image;
import com.idega.presentation.Script;
import com.idega.presentation.Layer;
import com.idega.presentation.text.Link;
import com.idega.presentation.Block;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Window;
import com.idega.presentation.IFrameContainer;
import com.idega.xml.XMLElement;
import com.idega.xml.XMLAttribute;

import java.util.ListIterator;
import java.util.List;
import java.util.Hashtable;
import java.util.Enumeration;
import com.idega.core.data.GenericGroup;
import java.util.Vector;
import java.util.Iterator;
import java.sql.SQLException;
import java.util.StringTokenizer;
import com.idega.core.localisation.business.ICLocaleBusiness;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class BuilderLogic {
  public static final String IC_OBJECT_INSTANCE_ID_PARAMETER = "ic_object_instance_id_par";

  public static final String IB_PARENT_PARAMETER = "ib_parent_par";
  public static final String IB_PAGE_PARAMETER ="ib_page";
  public static final String IB_LABEL_PARAMETER = "ib_label";
  public static final String IB_OBJECT_INSTANCE_COORDINATE = "ib_ob_inst";
  public static final String IB_OBJECT_INSTANCE_EVENT_SOURCE = "ib_ob_inst_ev_s";
  public static final String IB_LIBRARY_NAME = "ib_library_name";

  public static final String IB_CONTROL_PARAMETER = "ib_control_par";
  public static final String ACTION_DELETE ="ACTION_DELETE";
  public static final String ACTION_EDIT ="ACTION_EDIT";
  public static final String ACTION_ADD ="ACTION_ADD";
  public static final String ACTION_MOVE ="ACTION_MOVE";
  public static final String ACTION_LOCK_REGION ="ACTION_LOCK";
  public static final String ACTION_UNLOCK_REGION ="ACTION_UNLOCK";
  public static final String ACTION_PERMISSION ="ACTION_PERMISSION";
  public static final String ACTION_LABEL = "ACTION_LABEL";
  public static final String ACTION_COPY = "ACTION_COPY";
  public static final String ACTION_PASTE = "ACTION_PASTE";
  public static final String ACTION_PASTE_ABOVE = "ACTION_PASTE_ABOVE";
  public static final String ACTION_LIBRARY = "ACTION_LIBRARY";

  public static final String IW_BUNDLE_IDENTIFIER="com.idega.builder";

  public static final String SESSION_PAGE_KEY = "ib_page_id";
  public static final String SESSION_OBJECT_STATE = "obj_inst_state";
  public static final String PRM_HISTORY_ID = "ib_history";

  public static final String IMAGE_ID_SESSION_ADDRESS = "ib_image_id";
  public static final String IMAGE_IC_OBJECT_INSTANCE_SESSION_ADDRESS = "ic_object_id_image";

  private static final String IB_APPLICATION_RUNNING_SESSION = "ib_application_running";

  //private static final String DEFAULT_PAGE = "1";

  public static final String CLIPBOARD = "user_clipboard";

  private static BuilderLogic _instance;

  private BuilderLogic(){

  }

  public static BuilderLogic getInstance(){
    if (_instance == null) {
      _instance = new BuilderLogic();
    }
    return(_instance);
  }

  public boolean updatePage(int id) {
    String theID = Integer.toString(id);
    IBXMLPage xml = PageCacher.getXML(theID);
    xml.update();
    PageCacher.flagPageInvalid(theID);
    return(true);
  }

  public IBXMLPage getIBXMLPage(String key) {
    return PageCacher.getXML(key);
  }

  public IBXMLPage getIBXMLPage(int id){
    return PageCacher.getXML(Integer.toString(id));
  }

  /**
   *
   */
  public Page getPage(int id, boolean builderview, IWContext iwc) {
    try {
      boolean permissionview = false;
      if (iwc.isParameterSet("ic_pm") && iwc.isSuperAdmin()) {
	permissionview = true;
      }

      Page page = PageCacher.getPage(Integer.toString(id),iwc);
      if (builderview && iwc.hasEditPermission(page)) {
	return(BuilderLogic.getInstance().getBuilderTransformed(Integer.toString(id),page,iwc));
      }
      else if(permissionview) {
	int groupId = -1906;
	String bla = iwc.getParameter("ic_pm");
	if (bla != null) {
	  try {
	    groupId = Integer.parseInt(bla);
	  }
	  catch (NumberFormatException ex) {

	  }
	}
	page = PageCacher.getPage(Integer.toString(id));
	return(BuilderLogic.getInstance().getPermissionTransformed(groupId, Integer.toString(id),page,iwc));
      }
      else {
	return(page);
      }
    }
    catch(Exception e) {
      e.printStackTrace();
      Page theReturn = new Page();
      theReturn.add("Page invalid");
      return(theReturn);
    }
  }

  /**
   *
   */
  public Page getBuilderTransformed(String pageKey, Page page, IWContext iwc) {
    List list = page.getAllContainingObjects();
    if (list != null) {
      ListIterator iter = list.listIterator();
      PresentationObjectContainer parent = page;
      while (iter.hasNext()) {
	int index = iter.nextIndex();
	PresentationObject item = (PresentationObject)iter.next();
	transformObject(pageKey,item,index,parent,"-1",iwc);
      }
    }

    XMLElement pasted = (XMLElement)iwc.getSessionAttribute(CLIPBOARD);
    boolean clipboardEmpty = true;
    if (pasted != null)
      clipboardEmpty = false;

    //"-1" is identified as the top page object (parent)
    if (page.getIsExtendingTemplate()) {
      if (!page.isLocked()) {
	page.add(getAddIcon(Integer.toString(-1),iwc,null));
	if (!clipboardEmpty)
	  page.add(getPasteIcon(Integer.toString(-1),iwc));
	//page.add(layer);
      }
    }
    else {
      page.add(getAddIcon(Integer.toString(-1),iwc,null));
      if (!clipboardEmpty)
	page.add(getPasteIcon(Integer.toString(-1),iwc));
      if (page.getIsTemplate())
	if (page.isLocked())
	  page.add(getLockedIcon(Integer.toString(-1),iwc,null));
	else
	  page.add(getUnlockedIcon(Integer.toString(-1),iwc));
      //page.add(layer);
    }

    return(page);
  }

    public Page getPermissionTransformed(int groupId, String pageKey,Page page,IWContext iwc){
      List groupIds = new Vector();
      groupIds.add(Integer.toString(groupId));
      try {
	List groups = AccessControl.getPermissionGroups(new GenericGroup(groupId));
	if(groups != null){
	  Iterator iter = groups.iterator();
	  while (iter.hasNext()) {
	    com.idega.core.data.GenericGroup item = (GenericGroup)iter.next();
	    groupIds.add(Integer.toString(item.getID()));
	  }
	}
      }
      catch (Exception ex) {
      }

      List list = page.getAllContainingObjects();
      if(list != null){
	ListIterator iter = list.listIterator();
	while (iter.hasNext()) {
	  int index = iter.nextIndex();
	  Object item = iter.next();
	  if(item instanceof PresentationObject){
	    filterForPermission(groupIds,(PresentationObject)item,page,index,iwc);
	  }
	}
      }

      return page;
  }

  private void filterForPermission(List groupIds, PresentationObject obj, PresentationObjectContainer parentObject, int index, IWContext iwc){
    if(!iwc.hasViewPermission(groupIds,obj)){
      System.err.println(obj+": removed");
      parentObject.getAllContainingObjects().remove(index);
      parentObject.getAllContainingObjects().add(index,PresentationObject.NULL_CLONE_OBJECT);
    }else if(obj instanceof PresentationObjectContainer){
      if(obj instanceof Table){
	Table tab = (Table)obj;
	int cols = tab.getColumns();
	int rows = tab.getRows();
	for (int x=1;x<=cols ;x++ ) {
	  for (int y=1;y<=rows ;y++ ) {
	    PresentationObjectContainer moc = tab.containerAt(x,y);
	    if(moc!=null){
	      List l = moc.getAllContainingObjects();
	      if(l != null){
		ListIterator iterT = l.listIterator();
		while (iterT.hasNext()) {
		  int index2 = iterT.nextIndex();
		  Object itemT = iterT.next();
		  if(itemT instanceof PresentationObject){
		    filterForPermission(groupIds,(PresentationObject)itemT,moc,index2,iwc);
		  }
		}
	      }
	    }
	  }
	}
      } else{
	List list = ((PresentationObjectContainer)obj).getAllContainingObjects();
	if(list!=null){
	  ListIterator iter = list.listIterator();
	  while (iter.hasNext()) {
	    int index2 = iter.nextIndex();
	    PresentationObject item = (PresentationObject)iter.next();
	    filterForPermission(groupIds,item,(PresentationObjectContainer)obj,index2,iwc);
	  }
	}
      }
    }
  }

  private void processImageSet(String pageKey,int ICObjectInstanceID,int imageID,IWMainApplication iwma){
    setProperty(pageKey,ICObjectInstanceID,"image_id",Integer.toString(imageID),iwma);
  }

  private void transformObject(String pageKey,PresentationObject obj,int index, PresentationObjectContainer parent,String parentKey,IWContext iwc){
    XMLElement pasted = (XMLElement)iwc.getSessionAttribute(CLIPBOARD);
    boolean clipboardEmpty = true;
    if (pasted != null)
      clipboardEmpty = false;

    if(obj instanceof Image){
      Image imageObj = (Image)obj;
      boolean useBuilderObjectControl = obj.getUseBuilderObjectControl();
      com.idega.block.media.presentation.ImageInserter inserter = null;
      int ICObjectIntanceID = imageObj.getICObjectInstanceID();
      String sessionID="ic_"+ICObjectIntanceID;
      String session_image_id = (String)iwc.getSessionAttribute(sessionID);
      if(session_image_id!=null){
	int image_id = Integer.parseInt(session_image_id);
	/**
	 * @todo
	 * Change this so that id is done in a more appropriate place, i.e. set the image_id permanently on the image
	 */
	processImageSet(pageKey,ICObjectIntanceID,image_id,iwc.getApplication());
	iwc.removeSessionAttribute(sessionID);
	imageObj.setImageID(image_id);
      }
      inserter = new com.idega.block.media.presentation.ImageInserter();
      inserter.setHasUseBox(false);
      inserter.limitImageWidth(false);
      int image_id=imageObj.getImageID(iwc);
      if(image_id!=-1){
	inserter.setImageId(image_id);
      }

      inserter.setImSessionImageName(sessionID);
      inserter.setWindowToReload(true);
      //inserter.maintainSessionParameter();
      //inserter.setWindowClassToOpen(com.idega.jmodule.image.presentation.SimpleChooserWindow.class);
      //inserter.setWindowClassToOpen(ImageEditorWindow.class);

      obj = inserter;
      obj.setICObjectInstanceID(ICObjectIntanceID);
      obj.setUseBuilderObjectControl(useBuilderObjectControl);
    }
    else if(obj instanceof Block) {

    }
    else if(obj instanceof PresentationObjectContainer){
      if(obj instanceof Table){
	Table tab = (Table)obj;
	tab.setBorder(1);
	int cols = tab.getColumns();
	int rows = tab.getRows();
	for (int x=1;x<=cols ;x++ ) {
	  for (int y=1;y<=rows ;y++ ) {
	    PresentationObjectContainer moc = tab.containerAt(x,y);
	    String newParentKey = obj.getICObjectInstanceID()+"."+x+"."+y;
	    if(moc!=null){
	      transformObject(pageKey,moc,-1,tab,newParentKey,iwc);
	    }

	    Page curr = PageCacher.getPage(getCurrentIBPage(iwc),iwc);
	    if (curr.getIsExtendingTemplate()) {
	      if (tab.getBelongsToParent()) {
		if (!tab.isLocked(x,y)) {
		  tab.add(getAddIcon(newParentKey,iwc,tab.getLabel(x,y)),x,y);
		  if (!clipboardEmpty)
		    tab.add(getPasteIcon(newParentKey,iwc),x,y);
		}
	      }
	      else {
		tab.add(getAddIcon(newParentKey,iwc,tab.getLabel(x,y)),x,y);
		if (!clipboardEmpty)
		  tab.add(getPasteIcon(newParentKey,iwc),x,y);
		if (curr.getIsTemplate()) {
		  tab.add(getLabelIcon(newParentKey,iwc,tab.getLabel(x,y)),x,y);
		  if (tab.isLocked(x,y))
		    tab.add(getLockedIcon(newParentKey,iwc,tab.getLabel(x,y)),x,y);
		  else
		    tab.add(getUnlockedIcon(newParentKey,iwc),x,y);
		}
	      }
	    }
	    else {
	      tab.add(getAddIcon(newParentKey,iwc,tab.getLabel(x,y)),x,y);
	      if (!clipboardEmpty)
		tab.add(getPasteIcon(newParentKey,iwc),x,y);
	      if (curr.getIsTemplate()) {
		tab.add(getLabelIcon(newParentKey,iwc,tab.getLabel(x,y)),x,y);
		if (tab.isLocked(x,y))
		  tab.add(getLockedIcon(newParentKey,iwc,tab.getLabel(x,y)),x,y);
		else
		  tab.add(getUnlockedIcon(newParentKey,iwc),x,y);
	      }
	    }
	  }
	}
      }
      else{
	List list = ((PresentationObjectContainer)obj).getAllContainingObjects();
	if(list!=null){
	  ListIterator iter = list.listIterator();
	  while (iter.hasNext()) {
	    int index2 = iter.nextIndex();
	    PresentationObject item = (PresentationObject)iter.next();
	    /**
	     * If parent is Table
	     */
	    if(index==-1){
		transformObject(pageKey,item,index2,(PresentationObjectContainer)obj,parentKey,iwc);
	    }
	    else{
	      String newParentKey = Integer.toString(obj.getICObjectInstanceID());
	      transformObject(pageKey,item,index2,(PresentationObjectContainer)obj,newParentKey,iwc);
	    }
	  }
	}

	if (index != -1) {
	  Page curr = PageCacher.getPage(getCurrentIBPage(iwc),iwc);
	  if (curr.getIsExtendingTemplate()) {
	    if (obj.getBelongsToParent()) {
	      if (!((PresentationObjectContainer)obj).isLocked()) {
		((PresentationObjectContainer)obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()),iwc,((PresentationObjectContainer)obj).getLabel()));
		if (!clipboardEmpty)
		  ((PresentationObjectContainer)obj).add(getPasteIcon(Integer.toString(obj.getICObjectInstanceID()),iwc));
	      }
	    }
	    else {
	      ((PresentationObjectContainer)obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()),iwc,((PresentationObjectContainer)obj).getLabel()));
	      if (!clipboardEmpty)
		((PresentationObjectContainer)obj).add(getPasteIcon(Integer.toString(obj.getICObjectInstanceID()),iwc));
	      if (curr.getIsTemplate()) {
		((PresentationObjectContainer)obj).add(getLabelIcon(Integer.toString(obj.getICObjectInstanceID()),iwc,((PresentationObjectContainer)obj).getLabel()));
		if (!((PresentationObjectContainer)obj).isLocked())
		  ((PresentationObjectContainer)obj).add(getLockedIcon(Integer.toString(obj.getICObjectInstanceID()),iwc,((PresentationObjectContainer)obj).getLabel()));
		else
		  ((PresentationObjectContainer)obj).add(getUnlockedIcon(Integer.toString(obj.getICObjectInstanceID()),iwc));
	      }
	    }
	  }
	  else {
	    ((PresentationObjectContainer)obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()),iwc,((PresentationObjectContainer)obj).getLabel()));
	    if (!clipboardEmpty)
	      ((PresentationObjectContainer)obj).add(getPasteIcon(Integer.toString(obj.getICObjectInstanceID()),iwc));
	    if (curr.getIsTemplate()) {
	      ((PresentationObjectContainer)obj).add(getLabelIcon(Integer.toString(obj.getICObjectInstanceID()),iwc,((PresentationObjectContainer)obj).getLabel()));
	      if (!((PresentationObjectContainer)obj).isLocked())
		((PresentationObjectContainer)obj).add(getLockedIcon(Integer.toString(obj.getICObjectInstanceID()),iwc,((PresentationObjectContainer)obj).getLabel()));
	      else
		((PresentationObjectContainer)obj).add(getUnlockedIcon(Integer.toString(obj.getICObjectInstanceID()),iwc));
	    }
	  }
	}
      }
    }

    if (obj.getUseBuilderObjectControl()) {
      if(index != -1){
	//parent.remove(obj);
	//parent.add(new BuilderObjectControl(obj,parent));
	parent.set(index,new BuilderObjectControl(obj,parent,parentKey,iwc,index));
      }
    }
  }


  public IBPage getCurrentIBPageEntity(IWContext iwc)throws Exception{
      String sID = getCurrentIBPage(iwc);
      //if(sID!=null){
      return new IBPage(Integer.parseInt(sID));
      //}
  }

  /**
   * Returns the current IBPageID that the user has requested
   */
  public int getCurrentIBPageID(IWContext iwc) {
    String theReturn = getCurrentIBPage(iwc);
    return Integer.parseInt(theReturn);
  }

  public String getCurrentIBPage(IWContext iwc) {
    String theReturn = (String)iwc.getSessionAttribute(SESSION_PAGE_KEY);
    if (theReturn == null) {
      return Integer.toString(getCurrentDomain(iwc).getStartPageID());
    }
    else
      return theReturn;
  }


  public IBXMLPage getCurrentIBXMLPage(IWContext iwc){
    String key = getCurrentIBPage(iwc);
    if(key!=null){
      return(getIBXMLPage(key));
    }
    return null;
  }

  public IBDomain getCurrentDomain(IWContext iwc){
      /**
       * @todo: Change from hardcoded DomainID:
       */
       try{
	 int domainID=1;
	 return IBDomain.getDomain(domainID);
       }
       catch(Exception e){
	e.printStackTrace();
	throw new RuntimeException(e.getMessage());
       }
  }

  /**
   *
   */
  public PresentationObject getAddIcon(String parentKey, IWContext iwc, String label) {
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image addImage = bundle.getImage("add.gif","Add new component");
    //addImage.setAttribute("style","z-index: 0;");
    Link link = new Link(addImage);
    link.setWindowToOpen(IBAddModuleWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_ADD);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IB_LABEL_PARAMETER,label);

    return(link);
  }

  /**
   *
   */
  public PresentationObject getLockedIcon(String parentKey, IWContext iwc, String label) {
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image lockImage = bundle.getImage("las_close.gif","Unlock region");
    Link link = new Link(lockImage);
    link.setWindowToOpen(IBLockRegionWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_UNLOCK_REGION);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IB_LABEL_PARAMETER,label);

    return(link);
  }

  /**
   *
   */
  public PresentationObject getUnlockedIcon(String parentKey, IWContext iwc) {
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image lockImage = bundle.getImage("las_open.gif","Lock region");
    Link link = new Link(lockImage);
    link.setWindowToOpen(IBLockRegionWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_LOCK_REGION);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);

    return(link);
  }

  public PresentationObject getDeleteIcon(int key,String parentKey,IWContext iwc){
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image deleteImage = bundle.getImage("shared/menu/delete.gif","Delete component");
    Link link = new Link(deleteImage);
    link.setWindowToOpen(IBDeleteModuleWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_DELETE);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,key);
    return link;
  }


  public  PresentationObject getMoveIcon(int key,String parentKey,IWContext iwc){
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image moveImage = bundle.getImage("move.gif");
    Link link = new Link(moveImage);
    link.setWindowToOpen(IBAdminWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_MOVE);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,key);
    return link;
  }

  public  PresentationObject getPermissionIcon(int key,IWContext iwc){
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image editImage = bundle.getImage("shared/menu/permission.gif","Set permissions");
    Link link = new Link(editImage);
    link.setWindowToOpen(IBPermissionWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_PERMISSION);
    link.addParameter(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER,key);
    link.addParameter(IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY,AccessControl._CATEGORY_OBJECT_INSTANCE);

    return link;
  }

  public  PresentationObject getEditIcon(int key,IWContext iwc){
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image editImage = bundle.getImage("shared/menu/edit.gif","Properties");
    Link link = new Link(editImage);
    link.setWindowToOpen(IBPropertiesWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_EDIT);
    link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,key);
    return link;
  }

  private class BuilderObjectControl extends PresentationObjectContainer {
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

    public BuilderObjectControl(PresentationObject obj, PresentationObjectContainer objectParent, String theParentKey, IWContext iwc,int index) {
      _parent = objectParent;
      _theObject = obj;
      _parentKey = theParentKey;
      number = index;
      init(iwc);
      add(obj);
    }

    public void main(IWContext iwc){
      try{
	Script script = getParentPage().getAssociatedScript();
	script.addFunction("findObj(n, d)","function findObj(n, d) { \n\t var p,i,x;  if(!d) d=document; \n\t if((p=n.indexOf(\"?\"))>0&&parent.frames.length) { \n\t     d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p); \n\t } \n\t  if(!(x=d[n])&&d.all) x=d.all[n]; \n\t for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n]; \n\t  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=findObj(n,d.layers[i].document); \n\t if(!x && document.getElementById) x=document.getElementById(n); return x; \n }");
	script.addFunction("showHideLayers()","function showHideLayers() { \n\t var i,p,v,obj,args=showHideLayers.arguments; \n\t for (i=0; i<(args.length-2); i+=3) \n\t if ((obj=findObj(args[i]))!=null) { \n\t v=args[i+2]; \n\t   if (obj.style) { obj=obj.style; v=(v=='show')?'visible':(v='hide')?'hidden':v; \n\t }    obj.visibility=v; \n\t }\n}");
	getParentPage().setAssociatedScript(script);
      }
      catch(NullPointerException e){
	System.out.println("getParentPage() returns null in BuilderObjectControl for Object "+_theObject.getClass().getName()+" and ICObjectInstanceID="+_theObject.getICObjectInstanceID());
      }
    }

    public String getBundleIdentifier(){
      return "com.idega.builder";
    }

    private void init(IWContext iwc){
      _layer = new Layer();
      _tableLayer = new Layer();
      _tableLayer.setZIndex(-1);

      /** To work around layer stacking in Opera browser version 5, revise for newer versions */
      boolean hideLayer = iwc.isOpera();

      /** @todo Make a plug-in presentation/interface object which all plug-ins inherit */
      if ( _theObject instanceof com.idega.block.messenger.presentation.Messenger )
	hideLayer = true;
      if ( _theObject instanceof com.idega.presentation.Applet )
	hideLayer = true;
      if ( _theObject instanceof com.idega.presentation.GenericPlugin )
	hideLayer = true;

      Layer controlLayer = new Layer(Layer.DIV);
	controlLayer.setPositionType(Layer.RELATIVE);
	controlLayer.setWidth(1);
	controlLayer.setHeight(1);

      Layer layer = new Layer(Layer.DIV);
	layer.setID(_layer.getID()+"a");
	layer.setPositionType(Layer.ABSOLUTE);
	layer.setTopPosition(-1);
	layer.setLeftPosition(-1);
	layer.setBackgroundColor("#CCCCCC");
	layer.setVisibility("hidden");
	layer.setZIndex(37999);
	layer.setWidth(0);
	layer.setHeight(0);

      hideLayers = "showHideLayers('"+layer.getID()+"','','hide');";
      if ( hideLayer ) hideLayers += " showHideLayers('"+_tableLayer.getID()+"','','show');";

      showLayers = "showHideLayers('"+layer.getID()+"','','show');";
      if ( hideLayer ) showLayers += " showHideLayers('"+_tableLayer.getID()+"','','hide');";

      layer.setOnMouseOut(hideLayers);

      controlLayer.add(layer);

      _table = new Table(1,2);
      _table.add(controlLayer);
      _table.add(_tableLayer,1,2);
      _layer.add(_table);
      _layer.setZIndex(number);
      super.add(_layer);
      _table.setBorder(0);
      //_table.setWidth(1,"3");
      _table.setHeight(2,"1");
      _table.setCellpadding(0);
      _table.setCellspacing(0);
      //_table.setCellspacing(1);
      //_table.setColor("#000000");
      //_table.setColor(1,2,"white");
      _table.setColor(1,1,"#CCCCCC");
      _table.setLineFrame(true);
      _table.setLineAfterRow(1);
      _table.setLineWidth("1");
      _table.setLineHeight("1");
      _table.setLineColor("#000000");

      _table.setHeight(1,1,"11");

      Image image = getBundle(iwc).getImage("menuicon.gif","Component menu");
      image.setHorizontalSpacing(1);
      image.setAlignment("absmiddle");
      image.setOnClick(showLayers);

      Text text = new Text("");
	text.setFontStyle("font-size:5pt;font-family:Verdana,Arial,Helvetica,sans-serif;font-weight:bold;text-transform:uppercase;");


      if(_theObject!=null){
	//table.add(theObject.getClassName());
	StringBuffer buffer = new StringBuffer();
	  buffer.append(Text.NON_BREAKING_SPACE);
	  buffer.append(_theObject.getBuilderName(iwc));
	  buffer.append(Text.NON_BREAKING_SPACE);

	text.setText(buffer.toString());
	_table.add(image);
	_table.add(text);

	RaisedTable rTable = new RaisedTable();
	  rTable.setWidth(80);
	  rTable.setHeight(50);
	  rTable.setLightShadowColor("#000000");
	  rTable.setDarkShadowColor("#000000");

	table = new Table();
	  table.setCellpadding(3);
	  table.setCellspacing(0);
	  table.setWidth("100%");
	  table.setHeight("100%");
	  table.setColor("#CCCCCC");
	  table.setAttribute("onMouseOver",showLayers);
	  table.setAttribute("onClick",hideLayers);
	rTable.add(table);

	Image separator = getBundle(iwc).getImage("shared/menu/menu_separator.gif");
	  separator.setWidth("100%");
	  separator.setHeight(2);

	XMLElement pasted = (XMLElement)iwc.getSessionAttribute(BuilderLogic.CLIPBOARD);
	if (pasted == null) {
	  addToTable(getCopyIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),1,1);
	  addToTable(getCopyIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),"Copy",IBCopyModuleWindow.class,2,1);
	  addToTable(getDeleteIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),1,2);
	  addToTable(getDeleteIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),"Delete",IBDeleteModuleWindow.class,2,2);
	  table.add(separator,2,3);
	  addToTable(getPermissionIcon(_theObject.getICObjectInstanceID(),iwc),1,4);
	  addToTable(getPermissionIcon(_theObject.getICObjectInstanceID(),iwc),"Permission",IBPermissionWindow.class,2,4);
	  addToTable(getEditIcon(_theObject.getICObjectInstanceID(),iwc),1,5);
	  addToTable(getEditIcon(_theObject.getICObjectInstanceID(),iwc),"Properties",IBPropertiesWindow.class,2,5);
	}
	else {
	  addToTable(getCopyIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),1,1);
	  addToTable(getCopyIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),"Copy",IBCopyModuleWindow.class,2,1);
	  addToTable(getPasteAboveIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),1,2);
	  addToTable(getPasteAboveIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),"Paste above",IBPasteModuleWindow.class,2,2);
	  addToTable(getDeleteIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),1,3);
	  addToTable(getDeleteIcon(_theObject.getICObjectInstanceID(),_parentKey,iwc),"Delete",IBDeleteModuleWindow.class,2,3);
	  table.add(separator,2,4);
	  addToTable(getPermissionIcon(_theObject.getICObjectInstanceID(),iwc),1,5);
	  addToTable(getPermissionIcon(_theObject.getICObjectInstanceID(),iwc),"Permission",IBPermissionWindow.class,2,5);
	  addToTable(getEditIcon(_theObject.getICObjectInstanceID(),iwc),1,6);
	  addToTable(getEditIcon(_theObject.getICObjectInstanceID(),iwc),"Properties",IBPropertiesWindow.class,2,6);
	}

	table.setColumnColor(1,"#D8D8D1");
	table.setColumnColor(2,"#F9F8F7");
	table.setColumnAlignment(1,"center");
	layer.add(rTable);
      }
      else {
	_table.add(getDeleteIcon(0,_parentKey,iwc));
	_table.add(getEditIcon(0,iwc));
      }
    }

    private void addToTable(PresentationObject obj,int col,int row) {
      obj.setAttribute("onMouseOver",showLayers);
      table.add(obj,col,row);
    }

    private void addToTable(PresentationObject obj,String textString,Class className,int col,int row) {
      Text text = new Text(textString);
	text.setFontStyle("font-family: Arial, Helvetica, sans-serif; font-weight: bold; font-size: 8pt; text-decoration: none;");
      Link link = (Link) obj;
      link.setObject(text);
      if ( className != null ) link.setWindowToOpen(className);
      addToTable(link,col,row);
    }

    public void add(PresentationObject obj){
      if(obj instanceof Table){
	String width=((Table)obj).getWidth();
	if(width!=null){
	  _table.setWidth(width);
	  ((Table)obj).setWidth("100%");
	}

	String height=((Table)obj).getHeight();
	if(height!=null){
	  _table.setHeight(height);
	  ((Table)obj).setHeight("100%");

	}

      }
      _tableLayer.add(obj);

      obj.setParentObject(_parent);
    }
  }

  /**
   * Returns the real properties set for the property if the property is set with the specified keys
   * Returns the selectedValues[] if nothing found
   */
  public String[] getPropertyValues(IWMainApplication iwma,String pageKey,int ObjectInstanceId,String propertyName,String[] selectedValues,boolean returnSelectedValueIfNothingFound){
      IBXMLPage xml = getIBXMLPage(pageKey);
      return IBPropertyHandler.getInstance().getPropertyValues(iwma,xml,ObjectInstanceId,propertyName,selectedValues,returnSelectedValueIfNothingFound);
      //return XMLWriter.getPropertyValues(xml,ObjectInstanceId,propertyName);
  }

  public boolean removeProperty(IWMainApplication iwma,String pageKey,int ObjectInstanceId,String propertyName,String[] values){
      IBXMLPage xml = getIBXMLPage(pageKey);
      if(XMLWriter.removeProperty(iwma,xml,ObjectInstanceId,propertyName,values)){
	xml.update();
	return true;
      }
      else{
	return false;
      }
  }

  /**
   * Returns the first property if there is an array of properties set
   */
  public String getProperty(String pageKey,int ObjectInstanceId,String propertyName){
    IBXMLPage xml = getIBXMLPage(pageKey);
    return XMLWriter.getProperty(xml,ObjectInstanceId,propertyName);
  }

  /**
   * Returns true if properties changed, or error, else false
   */
  public boolean setProperty(String pageKey,int ObjectInstanceId,String propertyName,String propertyValue,IWMainApplication iwma){
      String[] values = {propertyValue};
      return setProperty(pageKey,ObjectInstanceId,propertyName,values,iwma);
  }

  /**
   * Returns true if properties changed, or error, else false
   */
  public boolean setProperty(String pageKey,int ObjectInstanceId,String propertyName,String[] propertyValues,IWMainApplication iwma){
    try {
      IBXMLPage xml = getIBXMLPage(pageKey);
      boolean allowMultivalued=isPropertyMultivalued(propertyName,ObjectInstanceId,iwma);
      if (XMLWriter.setProperty(iwma,xml,ObjectInstanceId,propertyName,propertyValues,allowMultivalued)) {
	xml.update();
	return(true);
      }
      else {
	return(false);
      }
    }
    catch(Exception e) {
      e.printStackTrace(System.err);
      return(false);
    }
  }

   // add by Aron 20.sept 2001 01:49
  public boolean deleteModule(String pageKey, String parentObjectInstanceID, int ICObjectInstanceID) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    boolean blockDeleted = false;
    try {
      PresentationObject Block = ICObjectBusiness.getInstance().getNewObjectInstance(ICObjectInstanceID);
      if (Block != null) {
	if (Block instanceof IWBlock) {
	  blockDeleted = ((IWBlock)Block).deleteBlock(ICObjectInstanceID);
	}
      }
      else {
	blockDeleted = true;
      }
    }
    catch(Exception ex) {
      blockDeleted = false;
      ex.printStackTrace();
    }

    if (XMLWriter.deleteModule(xml,parentObjectInstanceID,ICObjectInstanceID)) {
      xml.update();
      return(true);
    }
    else{
      return(false);
    }
  }

  /**
   *
   */
  public boolean copyModule(IWContext iwc, String pageKey, int ICObjectInstanceID) {
    IBXMLPage xml = getIBXMLPage(pageKey);

    XMLElement element = xml.copyModule(pageKey,ICObjectInstanceID);

    if (element == null)
      return(false);
    else {
      XMLElement el = (XMLElement)element.clone();
      iwc.setSessionAttribute(CLIPBOARD,el);
    }

    return(true);
  }

  /**
   *
   */
  public boolean pasteModule(IWContext iwc, String pageKey, String parentID) {
    IBXMLPage xml = getIBXMLPage(pageKey);

    XMLElement element = (XMLElement)iwc.getSessionAttribute(CLIPBOARD);
    if (element == null)
      return(false);

    XMLElement toPaste = (XMLElement)element.clone();

    if (XMLWriter.pasteElement(xml,parentID,toPaste)) {
      xml.update();
      return(true);
    }

    return(false);
  }

  /**
   *
   */
  public boolean pasteModule(IWContext iwc, String pageKey, String parentID, String objectID) {
    IBXMLPage xml = getIBXMLPage(pageKey);

    System.out.println("pageKey = " + pageKey);
    System.out.println("parentID = " + parentID);
    System.out.println("objectID = " + objectID);

    XMLElement element = (XMLElement)iwc.getSessionAttribute(CLIPBOARD);
    if (element == null)
      return(false);

    XMLElement toPaste = (XMLElement)element.clone();

    if (XMLWriter.pasteElementAbove(xml,parentID,objectID,toPaste)) {
      xml.update();
      return(true);
    }


    return(false);
  }

  public boolean lockRegion(String pageKey, String parentObjectInstanceID) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.lockRegion(xml,parentObjectInstanceID)) {
      xml.update();

      if (parentObjectInstanceID.equals("-1")) {
	if (xml.getType().equals(xml.TYPE_TEMPLATE)) {
	  List extend = xml.getUsingTemplate();
	  if (extend != null) {
	    Iterator i = extend.iterator();
	    while (i.hasNext())
	      lockRegion((String)i.next(),parentObjectInstanceID);
	  }
	}
      }
      return true;
    }

    return(false);
  }

  public boolean unlockRegion(String pageKey, String parentObjectInstanceID, String label) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.unlockRegion(xml,parentObjectInstanceID)) {
      xml.update();

      if (parentObjectInstanceID.equals("-1")) {
	if (xml.getType().equals(xml.TYPE_TEMPLATE)) {
	  List extend = xml.getUsingTemplate();
	  if (extend != null) {
	    Iterator i = extend.iterator();
	    while (i.hasNext()) {
	      String child = (String)i.next();
	      unlockRegion(child,parentObjectInstanceID,null);
	    }
	  }
	}
      }

      labelRegion(pageKey,parentObjectInstanceID,label);

      return true;
    }

    return(false);
  }

  /**
   *
   */
  public boolean addNewModule(String pageKey, String parentObjectInstanceID, int newICObjectID, String label) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.addNewModule(xml,parentObjectInstanceID,newICObjectID,label)) {
      xml.update();
      return(true);
    }
    else {
      return(false);
    }
  }

  /**
   *
   */
  public boolean addNewModule(String pageKey, String parentObjectInstanceID, ICObject newObjectType, String label) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if(XMLWriter.addNewModule(xml,parentObjectInstanceID,newObjectType,label)){
      xml.update();
      return true;
    }
    else{
      return false;
    }
  }

    public Class getObjectClass(int icObjectInstanceID){
      try{
	ICObjectInstance instance = new ICObjectInstance(icObjectInstanceID);
	return instance.getObject().getObjectClass();
      }
      catch(Exception e){
	e.printStackTrace();
      }
      return null;
    }

    private boolean isPropertyMultivalued(String propertyName,int icObjecctInstanceID,IWMainApplication iwma)throws Exception{
      try{
	Class c = null;
	IWBundle iwb = null;
	if(icObjecctInstanceID==-1){
	  c = com.idega.presentation.Page.class;
	  iwb = iwma.getBundle(PresentationObject.IW_BUNDLE_IDENTIFIER);
	}
	else{
	  ICObjectInstance instance = new ICObjectInstance(icObjecctInstanceID);
	  c = instance.getObject().getObjectClass();
	  iwb = instance.getObject().getBundle(iwma);
	}
	IWPropertyList complist = iwb.getComponentList();
	IWPropertyList component = complist.getPropertyList(c.getName());
	IWPropertyList methodlist = component.getPropertyList(IBPropertyHandler.METHODS_KEY);
	if (methodlist == null)
	  return(false);
	IWPropertyList method = methodlist.getPropertyList(propertyName);
	if (method == null)
	  return(false);
	IWProperty prop = method.getIWProperty(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED);
	if(prop!=null){
	  boolean value = prop.getBooleanValue();
	  return value;
	}
	else return false;
      }
      catch(Exception e){
	//e.printStackTrace(System.err);
	return false;
      }
    }

  public boolean setTemplateId(String pageKey, String id) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.setAttribute(xml,"-1",XMLConstants.TEMPLATE_STRING,id)) {
      xml.update();
      return true;
    }
    return(false);
  }

  /**
   *
   */
  public PresentationObject getLabelIcon(String parentKey, IWContext iwc, String label) {
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image labelImage = bundle.getImage("label.gif","Put label on region");
    Link link = new Link(labelImage);
    link.setWindowToOpen(IBAddRegionLabelWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_LABEL);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IB_LABEL_PARAMETER,label);

    return(link);
  }

  /**
   *
   */
  public PresentationObject getCopyIcon(int key, String parentKey, IWContext iwc) {
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image copyImage = bundle.getImage("shared/menu/copy.gif","Copy component");
    //copyImage.setAttribute("style","z-index: 0;");
    Link link = new Link(copyImage);
    link.setWindowToOpen(IBCopyModuleWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_COPY);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,key);

    return(link);
  }

  /**
   *
   */
  public PresentationObject getPasteAboveIcon(int key, String parentKey, IWContext iwc) {
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image pasteImage = bundle.getImage("shared/menu/copy.gif","Paste above component");
    //copyImage.setAttribute("style","z-index: 0;");
    Link link = new Link(pasteImage);
    link.setWindowToOpen(IBPasteModuleWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_PASTE_ABOVE);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IC_OBJECT_INSTANCE_ID_PARAMETER,key);

    return(link);
  }

  /**
   *
   */
  public PresentationObject getPasteIcon(String parentKey, IWContext iwc) {
    IWBundle bundle = iwc.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image pasteImage = bundle.getImage("paste.gif","Paste component");
    Link link = new Link(pasteImage);
    link.setWindowToOpen(IBPasteModuleWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(iwc));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_PASTE);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);

    return(link);
  }


  /**
   *
   */
  public boolean labelRegion(String pageKey, String parentObjectInstanceID, String label) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.labelRegion(xml,parentObjectInstanceID,label)) {
      xml.update();

      return true;
    }

    return(false);
  }

  /**
   *
   */
  public static String getIBPageURL(int ib_page_id){
    return(IWMainApplication.BUILDER_SERVLET_URL+"?"+IB_PAGE_PARAMETER+"="+ib_page_id);
  }

  /**
   *
   */
  public static String getIFrameContentURL(IWContext iwc, int ICObjectInstanceId){
    String src = IWMainApplication._IFRAME_CONTENT_URL+"?"+IC_OBJECT_INSTANCE_ID_PARAMETER+"="+ICObjectInstanceId;
    String query = iwc.getQueryString();
    if(query != null && !query.equals("")){
      src += ("&"+query);
    }
    return src;
  }

  /**
   * Changes the name of the current page.
   *
   * @param name The new name for the page
   * @param iwc The IdegeWeb Context object
   */
  public void changeName(String name, IWContext iwc) {
    IBXMLPage xml = getCurrentIBXMLPage(iwc);
    if (xml != null) {
      if (!xml.getName().equals(name)) {
	xml.setName(name);
	java.util.Map tree = PageTreeNode.getTree(iwc);

	if (tree != null) {
	  String currentId = getCurrentIBPage(iwc);
	  if (currentId != null) {
	    Integer id = new Integer(currentId);
	    PageTreeNode node = (PageTreeNode)tree.get(id);
	    if (node != null) {
	      node.setNodeName(name);
	    }
	  }
	}
      }
    }
  }

  /**
   * Changes the template id for the current page.
   *
   * @param templateId The new template id for the current page.
   * @param iwc The IdegeWeb Context object
   *
   * @todo make this work for templates!
   */
  public void changeTemplateId(String templateId, IWContext iwc) {
    IBXMLPage xml = getCurrentIBXMLPage(iwc);
    if (xml != null) {
      if (xml.getType().equals(IBXMLPage.TYPE_PAGE)) {
	int newId = Integer.parseInt(templateId);
	int oldId = xml.getTemplateId();
	if (newId != oldId) {
	  xml.setTemplateId(newId);

	  String currentPageId = getCurrentIBPage(iwc);
	  setTemplateId(currentPageId,Integer.toString(newId));
	  if (newId > 0)
	    getIBXMLPage(newId).addUsingTemplate(currentPageId);
	  if (oldId > 0)
	    getIBXMLPage(oldId).removeUsingTemplate(currentPageId);
	}
      }
    }
  }

  /**
   *
   */
  public void startBuilderSession(IWContext iwc){
    iwc.setSessionAttribute(IB_APPLICATION_RUNNING_SESSION,Boolean.TRUE);
  }

  /**
   *
   */
  public void endBuilderSession(IWContext iwc){
    iwc.removeSessionAttribute(IB_APPLICATION_RUNNING_SESSION);
  }

  /**
   *
   */
  public boolean isBuilderApplicationRunning(IWContext iwc){
    return !(iwc.getSessionAttribute(IB_APPLICATION_RUNNING_SESSION)==null);
  }

  /**
   *
   */
  public PresentationObject getIFrameContent(int ibPageId, int instanceId, IWContext iwc){
    PresentationObject obj = getPopulatedObjectInstance(instanceId,iwc);
    PresentationObject iframeContent = null;

    if (obj instanceof IFrameContainer && obj != null) {
      iframeContent = ((IFrameContainer)obj).getIFrameContent();
    }
    return iframeContent;
  }

  /**
   *
   */
  public PresentationObject[] getIWPOListeners(IWContext iwc){
    String prm = iwc.getParameter(this.IB_OBJECT_INSTANCE_COORDINATE);
    String[] coordinates = null;
    if(prm != null){
      StringTokenizer tokens = new StringTokenizer(prm,",");
      coordinates = new String[tokens.countTokens()];
      int index = 0;
      while (tokens.hasMoreTokens()) {
	coordinates[index++] = tokens.nextToken();
      }
    }

    if(coordinates != null && coordinates.length > 0){
      List l = new Vector();
      for (int i = 0; i < coordinates.length; i++) {
	String crdnts = coordinates[i];
	int index = crdnts.indexOf('_');
	String page = crdnts.substring(0,index);
	String inst = crdnts.substring(index+1,crdnts.length());
	if(!"".equals(page) && !"".equals(inst)){
	  //Page parentPage = BuilderLogic.getInstance().getIBXMLPage(page).getPopulatedPage();
	  //PresentationObject obj = parentPage.getContainedICObjectInstance(Integer.parseInt(inst));
	  PresentationObject obj = getPopulatedObjectInstance(inst,iwc);
	  if(obj != null && !obj.equals(PresentationObject.NULL_CLONE_OBJECT)){
	    l.add(obj);
	  }
	}
      }
      PresentationObject[] toReturn = (PresentationObject[])l.toArray(new PresentationObject[0]);

      if(toReturn.length > 0){
	/*
	System.err.println("BuilderLogic Listeners");
	for (int i = 0; i < toReturn.length; i++) {
	  System.err.println(" - "+toReturn[i].getParentPageID()+"_"+toReturn[i].getICObjectInstanceID());
	}*/

	return toReturn;
      }else{
	//System.err.println("BuilderLogic Listeners are null");
	return null;
      }
    } else{
      //System.err.println("BuilderLogic Listeners are null");
      return null;
    }
  }

  /**
   *
   */
  public PresentationObject getIWPOEventSource(IWContext iwc){
    String coordinates = iwc.getParameter(this.IB_OBJECT_INSTANCE_EVENT_SOURCE);
    if(coordinates != null){
      String crdnts = coordinates;
      int index = crdnts.indexOf('_');
      String page = crdnts.substring(0,index);
      String inst = crdnts.substring(index+1,crdnts.length());
      if(!"".equals(page) && !"".equals(inst)){
	/*Page parentPage = BuilderLogic.getInstance().getIBXMLPage(page).getPopulatedPage();
	PresentationObject obj = parentPage.getContainedICObjectInstance(Integer.parseInt(inst));
	return obj;*/
	return getPopulatedObjectInstance(inst,iwc);
      }
    }
    return null;
  }

  /**
   *
   */
  public void setICObjectInstanceListeners(Link l, int[] ibPageId, int[] instanceId){
    String prm = "";
    for (int i = 0; i < ibPageId.length; i++) {
      if(i != 0){
	prm += ",";
      }
      prm += ibPageId[i]+"_"+instanceId[i];
    }

    l.addParameter(IB_OBJECT_INSTANCE_COORDINATE,prm);
  }

  /**
   *
   */
  public void setICObjectInstanceListener(Link l, int ibPageId, int instanceId){
    l.addParameter(IB_OBJECT_INSTANCE_COORDINATE,ibPageId+"_"+instanceId);
  }

  /**
   *
   */
  public void setICObjectInstanceEventSource(Link l, int ibPageId, int instanceId){
    l.addParameter(IB_OBJECT_INSTANCE_EVENT_SOURCE,ibPageId+"_"+instanceId);
  }

  /**
   *
   */
  public void changeLinkPageId(Link link, String linkParentPageId, String newPageId) {
    int moduleId = link.getICObjectInstanceID();
    //int pageId = link.getParentPageID();

    //IBXMLPage page = getIBXMLPage(pageId);
    IBXMLPage page = getIBXMLPage(linkParentPageId);
    XMLElement element = new XMLElement(XMLConstants.CHANGE_PAGE_LINK);
    XMLAttribute id = new XMLAttribute(XMLConstants.LINK_ID_STRING,Integer.toString(moduleId));
    XMLAttribute newPageLink = new XMLAttribute(XMLConstants.LINK_TO,newPageId);
    element.setAttribute(id);
    element.setAttribute(newPageLink);
    XMLWriter.addNewElement(page,-1,element);

    page.update();
    PageCacher.flagPageInvalid(linkParentPageId);
  }

  /**
   *
   */
  public PresentationObject getPopulatedObjectInstance(int id, IWContext iwc){
    return ObjectInstanceCacher.getObjectInstanceClone(id,iwc);
  }

  /**
   *
   */
  public PresentationObject getPopulatedObjectInstance(String key, IWContext iwc){
    return ObjectInstanceCacher.getObjectInstanceClone(key,iwc);
  }

  /**
   *
   */
  public Map getCashedObjectInstancesForPage(int pageId){
    return ObjectInstanceCacher.getObjectInstancesCachedForPage(pageId);
  }

  /**
   *
   */
  public Map getCashedObjectInstancesForPage(String pageKey){
    return ObjectInstanceCacher.getObjectInstancesCachedForPage(pageKey);
  }

  /**
   *
   */
  public Set getInstanceIdsOnPage(String pageKey) {
    Map m = ObjectInstanceCacher.getObjectInstancesCachedForPage(pageKey);
    if (m != null) {
      return m.keySet();
    }
    else {
      return null;
    }
  }

  /**
   *
   */
  public Set getInstanceIdsOnPage(int pageKey) {
    Map m = ObjectInstanceCacher.getObjectInstancesCachedForPage(pageKey);
    if(m != null){
      return m.keySet();
    }else{
      return null;
    }
  }

  /**
   *
   */
  public static int getStartPageId(IWContext iwc) {
    IBDomain domain = BuilderLogic.getInstance().getCurrentDomain(iwc);
    return domain.getStartPageID();
  }
}