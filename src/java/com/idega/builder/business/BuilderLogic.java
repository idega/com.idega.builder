/*
 * $Id: BuilderLogic.java,v 1.33 2001/10/02 15:40:09 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.business;

import com.idega.builder.data.IBPage;
import com.idega.builder.presentation.IBAdminWindow;
import com.idega.builder.presentation.IBAddModuleWindow;
import com.idega.builder.presentation.IBDeleteModuleWindow;
import com.idega.builder.presentation.IBPropertiesWindow;
import com.idega.builder.presentation.IBPermissionWindow;
import com.idega.builder.presentation.IBLockRegionWindow;
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
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.ModuleObjectContainer;
import com.idega.jmodule.object.Page;
import com.idega.jmodule.object.Image;
import com.idega.jmodule.object.textObject.Link;
import com.idega.jmodule.object.JModuleObject;
import com.idega.jmodule.object.textObject.Link;
import com.idega.jmodule.object.textObject.Text;
import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.jmodule.image.presentation.ImageInserter;
import com.idega.jmodule.image.presentation.ImageEditorWindow;
import java.util.ListIterator;
import java.util.List;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class BuilderLogic {
  public static final String IC_OBJECT_ID_PARAMETER = "ic_object_id_par";
  public static final String IB_PARENT_PARAMETER = "ib_parent_par";
  public static final String IB_PAGE_PARAMETER ="ib_page_par";

  public static final String IB_CONTROL_PARAMETER = "ib_control_par";
  public static final String ACTION_DELETE ="ACTION_DELETE";
  public static final String ACTION_EDIT ="ACTION_EDIT";
  public static final String ACTION_ADD ="ACTION_ADD";
  public static final String ACTION_MOVE ="ACTION_MOVE";
  public static final String ACTION_LOCK_REGION ="ACTION_LOCK";
  public static final String ACTION_UNLOCK_REGION ="ACTION_UNLOCK";
  public static final String ACTION_PERMISSION ="ACTION_PERMISSION";

  public static final String IW_BUNDLE_IDENTIFIER="com.idega.builder";

  public static final String SESSION_PAGE_KEY = "ib_page_id";

  public static final String IMAGE_ID_SESSION_ADDRESS = "ib_image_id";
  public static final String IMAGE_IC_OBJECT_INSTANCE_SESSION_ADDRESS = "ic_object_id_image";

  private static final String DEFAULT_PAGE = "1";

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

  public Page getPage(int id,ModuleInfo modinfo) {
    try {
      boolean builderview = false;
      if (modinfo.isParameterSet("view")) {
        builderview = true;
      }

      Page page = PageCacher.getPage(Integer.toString(id),modinfo);
      if (builderview) {
        return(BuilderLogic.getInstance().getBuilderTransformed(Integer.toString(id),page,modinfo));
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

  public Page getBuilderTransformed(String pageKey,Page page,ModuleInfo modinfo){
      List list = page.getAllContainingObjects();
      if(list!=null){
        ListIterator iter = list.listIterator();
        ModuleObjectContainer parent = page;
        while (iter.hasNext()) {
          int index = iter.nextIndex();
          ModuleObject item = (ModuleObject)iter.next();
          transformObject(pageKey,item,index,parent,"-1",modinfo);
        }
      }
      //"-1" is identified as the top page object (parent)
      if (page.getIsExtendingTemplate()) {
        if (!page.isLocked()) {
          page.add(getAddIcon(Integer.toString(-1),modinfo));
          if (page.getIsTemplate())
            page.add(getUnlockedIcon(Integer.toString(-1),modinfo));
        }
      }
      else {
        page.add(getAddIcon(Integer.toString(-1),modinfo));
        if (page.getIsTemplate())
          if (page.isLocked())
            page.add(getLockedIcon(Integer.toString(-1),modinfo));
          else
            page.add(getUnlockedIcon(Integer.toString(-1),modinfo));
      }

      return page;
  }

  private void processImageSet(String pageKey,int ICObjectInstanceID,int imageID,IWMainApplication iwma){
    setProperty(pageKey,ICObjectInstanceID,"image_id",Integer.toString(imageID),iwma);
  }

  private void transformObject(String pageKey,ModuleObject obj,int index, ModuleObjectContainer parent,String parentKey,ModuleInfo modinfo){
    if(obj instanceof Image){
      Image imageObj = (Image)obj;
      boolean useBuilderObjectControl = obj.getUseBuilderObjectControl();
      ImageInserter inserter = null;
      int ICObjectIntanceID = imageObj.getICObjectInstanceID();
      String sessionID="ic_"+ICObjectIntanceID;
      String session_image_id = (String)modinfo.getSessionAttribute(sessionID);
      if(session_image_id!=null){
        int image_id = Integer.parseInt(session_image_id);
        /**
         * @todo
         * Change this so that id is done in a more appropriate place, i.e. set the image_id permanently on the image
         */
        processImageSet(pageKey,ICObjectIntanceID,image_id,modinfo.getApplication());
        modinfo.removeSessionAttribute(sessionID);
        imageObj.setImageID(image_id);
      }
      inserter = new ImageInserter();
      inserter.setHasUseBox(false);
      inserter.limitImageWidth(false);
      int image_id=imageObj.getImageID();
      if(image_id!=-1){
        inserter.setImageId(image_id);
      }

      inserter.setImSessionImageName(sessionID);
      inserter.setWindowClassToOpen(com.idega.jmodule.image.presentation.SimpleChooserWindow.class);
      //inserter.setWindowClassToOpen(ImageEditorWindow.class);

      obj = inserter;
      obj.setICObjectInstanceID(ICObjectIntanceID);
      obj.setUseBuilderObjectControl(useBuilderObjectControl);
    }
    else if(obj instanceof JModuleObject) {

    }
    else if(obj instanceof ModuleObjectContainer){
      if(obj instanceof Table){
        Table tab = (Table)obj;
        int cols = tab.getColumns();
        int rows = tab.getRows();
        for (int x=1;x<=cols ;x++ ) {
          for (int y=1;y<=rows ;y++ ) {
            ModuleObjectContainer moc = tab.containerAt(x,y);
            String newParentKey = obj.getICObjectInstanceID()+"."+x+"."+y;
            if(moc!=null){
              transformObject(pageKey,moc,-1,tab,newParentKey,modinfo);
            }

            Page curr = PageCacher.getPage(this.getCurrentIBPage(modinfo),modinfo);
            if (curr.getIsExtendingTemplate()) {
              if (tab.getBelongsToParent()) {
                if (!tab.isLocked(x,y))
                  tab.add(getAddIcon(newParentKey,modinfo),x,y);
              }
              else {
                tab.add(getAddIcon(newParentKey,modinfo),x,y);
                if (curr.getIsTemplate()) {
                  if (tab.isLocked(x,y))
                    tab.add(getLockedIcon(newParentKey,modinfo),x,y);
                  else
                    tab.add(getUnlockedIcon(newParentKey,modinfo),x,y);
                }
              }
            }
            else {
              tab.add(getAddIcon(newParentKey,modinfo),x,y);
              if (curr.getIsTemplate()) {
                if (tab.isLocked(x,y))
                  tab.add(getLockedIcon(newParentKey,modinfo),x,y);
                else
                  tab.add(getUnlockedIcon(newParentKey,modinfo),x,y);
              }
            }
          }
        }
      }
      else{
        List list = ((ModuleObjectContainer)obj).getAllContainingObjects();
        if(list!=null){
          ListIterator iter = list.listIterator();
          while (iter.hasNext()) {
            int index2 = iter.nextIndex();
            ModuleObject item = (ModuleObject)iter.next();
            /**
             * If parent is Table
             */
            if(index==-1){
                transformObject(pageKey,item,index2,(ModuleObjectContainer)obj,parentKey,modinfo);
            }
            else{
              String newParentKey = Integer.toString(obj.getICObjectInstanceID());
              transformObject(pageKey,item,index2,(ModuleObjectContainer)obj,newParentKey,modinfo);
            }
          }
        }

        if (index != -1) {
          Page curr = PageCacher.getPage(this.getCurrentIBPage(modinfo),modinfo);
          if (curr.getIsExtendingTemplate()) {
            if (obj.getBelongsToParent()) {
              if (!((ModuleObjectContainer)obj).isLocked())
                ((ModuleObjectContainer)obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
            }
            else {
              ((ModuleObjectContainer)obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
              if (curr.getIsTemplate()) {
                if (!((ModuleObjectContainer)obj).isLocked())
                  ((ModuleObjectContainer)obj).add(getLockedIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
                else
                  ((ModuleObjectContainer)obj).add(getUnlockedIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
              }
            }
          }
          else {
            ((ModuleObjectContainer)obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
            if (curr.getIsTemplate()) {
              if (!((ModuleObjectContainer)obj).isLocked())
                ((ModuleObjectContainer)obj).add(getLockedIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
              else
                ((ModuleObjectContainer)obj).add(getUnlockedIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
            }
          }
        }
      }
    }

    if (obj.getUseBuilderObjectControl()) {
      if(index != -1){
        //parent.remove(obj);
        //parent.add(new BuilderObjectControl(obj,parent));
        parent.set(index,new BuilderObjectControl(obj,parent,parentKey,modinfo));
      }
    }

  }

  public String getCurrentIBPage(ModuleInfo modinfo) {
    String theReturn = (String)modinfo.getSessionAttribute(SESSION_PAGE_KEY);
    if (theReturn == null) {
      return(DEFAULT_PAGE);
    }
    else
      return theReturn;
  }

  public  ModuleObject getAddIcon(String parentKey,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image addImage = bundle.getImage("add.gif","Add new component");
    Link link = new Link(addImage);
    link.setWindowToOpen(IBAddModuleWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(modinfo));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_ADD);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);

    return link;
  }

  public ModuleObject getLockedIcon(String parentKey, ModuleInfo modinfo) {
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image lockImage = bundle.getImage("las_close.gif","Unlock region");
    Link link = new Link(lockImage);
    link.setWindowToOpen(IBLockRegionWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(modinfo));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_UNLOCK_REGION);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);

    return(link);
  }

  public ModuleObject getUnlockedIcon(String parentKey, ModuleInfo modinfo) {
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image lockImage = bundle.getImage("las_open.gif","Lock region");
    Link link = new Link(lockImage);
    link.setWindowToOpen(IBLockRegionWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(modinfo));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_LOCK_REGION);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);

    return(link);
  }

  public  ModuleObject getDeleteIcon(int key,String parentKey,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image deleteImage = bundle.getImage("delete.gif","Delete component");
    Link link = new Link(deleteImage);
    link.setWindowToOpen(IBDeleteModuleWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(modinfo));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_DELETE);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IC_OBJECT_ID_PARAMETER,key);
    return link;
  }


  public  ModuleObject getMoveIcon(int key,String parentKey,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image moveImage = bundle.getImage("move.gif");
    Link link = new Link(moveImage);
    link.setWindowToOpen(IBAdminWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(modinfo));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_MOVE);
    link.addParameter(IB_PARENT_PARAMETER,parentKey);
    link.addParameter(IC_OBJECT_ID_PARAMETER,key);
    return link;
  }

  public  ModuleObject getPermissionIcon(int key,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image editImage = bundle.getImage("key_small.gif","Set permissions");
    Link link = new Link(editImage);
    link.setWindowToOpen(IBPermissionWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(modinfo));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_PERMISSION);
    link.addParameter(IBPermissionWindow._PARAMETERSTRING_IDENTIFIER,key);
    link.addParameter(IBPermissionWindow._PARAMETERSTRING_PERMISSION_CATEGORY,AccessControl._CATEGORY_OBJECT_INSTANCE);

    return link;
  }

  public  ModuleObject getEditIcon(int key,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image editImage = bundle.getImage("edit.gif","Edit component");
    Link link = new Link(editImage);
    link.setWindowToOpen(IBPropertiesWindow.class);
    link.addParameter(IB_PAGE_PARAMETER,getCurrentIBPage(modinfo));
    link.addParameter(IB_CONTROL_PARAMETER,ACTION_EDIT);
    link.addParameter(IC_OBJECT_ID_PARAMETER,key);
    return link;
  }

  private class BuilderObjectControl extends ModuleObjectContainer {
    private com.idega.jmodule.object.Layer _layer;
    private Table _table;
    private ModuleObjectContainer _parent;
    private String _parentKey;
    private ModuleObject _theObject;

    public BuilderObjectControl(ModuleObject obj, ModuleObjectContainer objectParent, String theParentKey, ModuleInfo modinfo) {
      _parent = objectParent;
      _theObject = obj;
      _parentKey = theParentKey;
      init(modinfo);
      add(obj);
    }

    private void init(ModuleInfo modinfo){
      _layer = new com.idega.jmodule.object.Layer();
      _table = new Table(1,2);
      _layer.add(_table);
      super.add(_layer);
      _table.setBorder(0);
      _table.setCellpadding(0);
      _table.setCellspacing(2);
      _table.setColor("gray");
      _table.setColor(1,2,"white");
      _table.setHeight(1,1,"11");

      if(_theObject!=null){
        //table.add(theObject.getClassName());
        _table.add(getDeleteIcon(_theObject.getICObjectInstanceID(),_parentKey,modinfo));
        _table.add(getEditIcon(_theObject.getICObjectInstanceID(),modinfo));
        _table.add(getPermissionIcon(_theObject.getICObjectInstanceID(),modinfo));
      }
      else{
          _table.add(getDeleteIcon(0,_parentKey,modinfo));
          _table.add(getEditIcon(0,modinfo));
      }
    }

    public void add(ModuleObject obj){
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
      _table.add(obj,1,2);

      obj.setParentObject(_parent);
    }
  }

  /**
   * Returns a List of Strings
   */
  public List getPropertyValues(String pageKey,int ObjectInstanceId,String propertyName){
      IBXMLPage xml = getIBXMLPage(pageKey);
      return XMLWriter.getPropertyValues(xml,ObjectInstanceId,propertyName);
  }

  public boolean removeProperty(String pageKey,int ObjectInstanceId,String propertyName,String value){
      IBXMLPage xml = getIBXMLPage(pageKey);
      return XMLWriter.removeProperty(xml,ObjectInstanceId,propertyName,value);
  }

  /**
   * Returns the first property if there is an array of properties set
   */
  public String getProperty(String pageKey,int ObjectInstanceId,String propertyName){
    IBXMLPage xml = getIBXMLPage(pageKey);
    return XMLWriter.getProperty(xml,ObjectInstanceId,propertyName);
  }

  public boolean setProperty(String pageKey,int ObjectInstanceId,String propertyName,String propertyValue,IWMainApplication iwma){
      String[] values = {propertyValue};
      return setProperty(pageKey,ObjectInstanceId,propertyName,values,iwma);
  }

  public boolean setProperty(String pageKey,int ObjectInstanceId,String propertyName,String[] propertyValues,IWMainApplication iwma){
      try{
        IBXMLPage xml = getIBXMLPage(pageKey);
        boolean allowMultivalued=isPropertyMultivalued(propertyName,ObjectInstanceId,iwma);
        if(XMLWriter.setProperty(xml,ObjectInstanceId,propertyName,propertyValues,allowMultivalued)){
          xml.update();
          return true;
        }
        else{
          return false;
        }
      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }
  }

   // add by Aron 20.sept 2001 01:49
   public boolean deleteModule(String pageKey,String parentObjectInstanceID,int ICObjectInstanceID){
    IBXMLPage xml = getIBXMLPage(pageKey);
    boolean blockDeleted = false;
    /** @todo  */
      ////////
      try {
        ModuleObject Block = ICObjectBusiness.getNewObjectInstance(ICObjectInstanceID);
        if(Block != null){
          if(Block instanceof IWBlock){
            blockDeleted = ((IWBlock) Block).deleteBlock(ICObjectInstanceID);
          }
        }
        else
          blockDeleted = true;
      }
      catch (Exception ex) {
        blockDeleted = false;
        ex.printStackTrace();
      }

    if(XMLWriter.deleteModule(xml,parentObjectInstanceID,ICObjectInstanceID) ){
      xml.update();
      return true;
    }
    else {
      return false;
    }
  }

  public boolean lockRegion(String pageKey, String parentObjectInstanceID) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.lockRegion(xml,parentObjectInstanceID)) {
      xml.update();
      return true;
    }

    return(false);
  }

  public boolean unlockRegion(String pageKey, String parentObjectInstanceID) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.unlockRegion(xml,parentObjectInstanceID)) {
      xml.update();
      return true;
    }

    return(false);
  }

  public boolean addNewModule(String pageKey,String parentObjectInstanceID,int newICObjectID){
    IBXMLPage xml = getIBXMLPage(pageKey);
    if(XMLWriter.addNewModule(xml,parentObjectInstanceID,newICObjectID)){
      xml.update();
      return true;
    }
    else{
      return false;
    }
  }

  public boolean addNewModule(String pageKey,String parentObjectInstanceID,ICObject newObjectType){
    IBXMLPage xml = getIBXMLPage(pageKey);
    if(XMLWriter.addNewModule(xml,parentObjectInstanceID,newObjectType)){
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
      Class c = null;
      IWBundle iwb = null;
      if(icObjecctInstanceID==-1){
        c = com.idega.jmodule.object.Page.class;
        iwb = iwma.getBundle(ModuleObject.IW_BUNDLE_IDENTIFIER);
      }
      else{
        ICObjectInstance instance = new ICObjectInstance(icObjecctInstanceID);
        c = instance.getObject().getObjectClass();
        iwb = instance.getObject().getBundle(iwma);
      }

      IWPropertyList complist = iwb.getComponentList();
      IWPropertyList component = complist.getPropertyList(c.getName());
      IWPropertyList methodlist = component.getPropertyList(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED);
      if (methodlist == null)
        return(false);
      IWPropertyList method = methodlist.getPropertyList(propertyName);
      if (method == null)
        return(false);
      IWProperty prop = method.getIWProperty(IBPropertyHandler.METHOD_PROPERTY_ALLOW_MULTIVALUED);
      if(prop!=null){
        String value = prop.getValue();
        try{
          return Boolean.getBoolean(value);
        }
        catch(Exception e){
          return false;
        }
      }
      else return false;
    }

  public boolean setTemplateId(String pageKey, String id) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.setAttribute(xml,"-1",XMLConstants.TEMPLATE_STRING,id)) {
      xml.update();
      return true;
    }

    return(false);
  }
}