package com.idega.builder.business;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author       <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

import com.idega.builder.data.IBPage;
import com.idega.builder.presentation.IBAdminWindow;
import com.idega.builder.presentation.IBAddModuleWindow;
import com.idega.builder.presentation.IBDeleteModuleWindow;
import com.idega.builder.presentation.IBPropertiesWindow;
import com.idega.builder.presentation.IBPermissionWindow;
import com.idega.builder.presentation.IBLockRegionWindow;

import com.idega.core.data.ICObject;
import com.idega.core.data.ICObjectInstance;

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

import java.util.ListIterator;
import java.util.List;

public class BuilderLogic{


    public static final String ic_object_id_parameter = "ic_object_id_par";
    public static final String ib_parent_parameter = "ib_parent_par";
    public static final String ib_page_parameter ="ib_page_par";

    public static final String ib_control_parameter = "ib_control_par";
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

  private static BuilderLogic instance;

  private BuilderLogic(){

  }

  public static BuilderLogic getInstance(){
    if(instance==null){
      instance = new BuilderLogic();
    }
    return instance;
  }

  public boolean updatePage(int id){
    String theID=Integer.toString(id);
    IBXMLPage xml = PageCacher.getXML(theID);
    xml.update();
    System.err.println("Invalidating page = " + theID);
    PageCacher.flagPageInvalid(theID);
    return true;
  }

  public IBXMLPage getIBXMLPage(String key){
    return PageCacher.getXML(key);
  }

  public IBXMLPage getIBXMLPage(int id){
    return PageCacher.getXML(Integer.toString(id));
  }

  public Page getPage(int id,ModuleInfo modinfo){
    try{
      boolean builderview=false;
      if(modinfo.isParameterSet("view")){
        //if(modinfo.getParameter("view").equals("builder")){
          builderview=true;
        //}
      }
      Page page = PageCacher.getPage(Integer.toString(id),modinfo);
      if(builderview){
        return BuilderLogic.getInstance().getBuilderTransformed(Integer.toString(id),page,modinfo);
      }
      else{
        return page;
      }
    }
    catch(Exception e){
      e.printStackTrace();
      Page theReturn = new Page();
      theReturn.add("Page invalid");
      return theReturn;
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
      if (!page.isLocked())
        page.add(getAddIcon(Integer.toString(-1),modinfo));
      if (page.getIsTemplate())
        page.add(getLockIcon(Integer.toString(-1),modinfo));
      return page;
  }

  private void processImageSet(String pageKey,int ICObjectInstanceID,int imageID,IWMainApplication iwma){
    setProperty(pageKey,ICObjectInstanceID,"image_id",Integer.toString(imageID),iwma);
  }

  private void transformObject(String pageKey,ModuleObject obj,int index, ModuleObjectContainer parent,String parentKey,ModuleInfo modinfo){
    boolean useBuilderObjectControl=true;

    if(obj instanceof Image){
      Image imageObj = (Image)obj;
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
      int image_id=imageObj.getImageID();
      if(image_id!=-1){
        inserter.setImageId(image_id);
      }

      inserter.setImSessionImageName(sessionID);
      inserter.setWindowClassToOpen(com.idega.jmodule.image.presentation.SimpleChooserWindow.class);
      obj = inserter;
      obj.setICObjectInstanceID(ICObjectIntanceID);
    }
    else
    if(obj instanceof JModuleObject){


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
              if (!tab.isLocked(x,y))
                tab.add(getAddIcon(newParentKey,modinfo),x,y);
              if (tab.getParentPage().getIsTemplate())
                tab.add(getLockIcon(newParentKey,modinfo),x,y);
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
          if (!((ModuleObjectContainer)obj).isLocked())
            ((ModuleObjectContainer)obj).add(getAddIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
          if (obj.getParentPage().getIsTemplate())
            ((ModuleObjectContainer)obj).add(getLockIcon(Integer.toString(obj.getICObjectInstanceID()),modinfo));
        }
      }
    }

    if (useBuilderObjectControl) {
      if(index != -1){
        //parent.remove(obj);
        //parent.add(new BuilderObjectControl(obj,parent));
        parent.set(index,new BuilderObjectControl(obj,parent,parentKey,modinfo));
      }
    }

  }

  public String getCurrentIBPage(ModuleInfo modinfo){
    String theReturn = (String)modinfo.getSessionAttribute(SESSION_PAGE_KEY);
    if(theReturn==null){
      return "1";
    }
    else
      return theReturn;
  }

  public  ModuleObject getAddIcon(String parentKey,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image addImage = bundle.getImage("add.gif","Add new component");
    Link link = new Link(addImage);
    link.setWindowToOpen(IBAddModuleWindow.class);
    link.addParameter(ib_page_parameter,getCurrentIBPage(modinfo));
    link.addParameter(ib_control_parameter,ACTION_ADD);
    link.addParameter(ib_parent_parameter,parentKey);

    return link;
  }

  public ModuleObject getLockIcon(String parentKey, ModuleInfo modinfo) {
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image lockImage = bundle.getImage("las_open.gif","Lock region");
    Link link = new Link(lockImage);
    link.setWindowToOpen(IBLockRegionWindow.class);
    link.addParameter(ib_page_parameter,"1");
    link.addParameter(ib_control_parameter,ACTION_LOCK_REGION);
    link.addParameter(ib_parent_parameter,parentKey);

    return(link);
  }

  public ModuleObject getUnlockIcon(String parentKey, ModuleInfo modinfo) {
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image lockImage = bundle.getImage("las_close.gif","Unlock region");
    Link link = new Link(lockImage);
    link.setWindowToOpen(IBLockRegionWindow.class);
    link.addParameter(ib_page_parameter,"1");
    link.addParameter(ib_control_parameter,ACTION_UNLOCK_REGION);
    link.addParameter(ib_parent_parameter,parentKey);

    return(link);
  }

  public  ModuleObject getDeleteIcon(int key,String parentKey,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image deleteImage = bundle.getImage("delete.gif","Delete component");
    Link link = new Link(deleteImage);
    link.setWindowToOpen(IBDeleteModuleWindow.class);
    link.addParameter(ib_page_parameter,getCurrentIBPage(modinfo));
    link.addParameter(ib_control_parameter,ACTION_DELETE);
    link.addParameter(ib_parent_parameter,parentKey);
    link.addParameter(ic_object_id_parameter,key);
    return link;
  }


  public  ModuleObject getMoveIcon(int key,String parentKey,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image moveImage = bundle.getImage("move.gif");
    Link link = new Link(moveImage);
    link.setWindowToOpen(IBAdminWindow.class);
    link.addParameter(ib_page_parameter,"1");
    link.addParameter(ib_control_parameter,ACTION_MOVE);
    link.addParameter(ib_parent_parameter,parentKey);
    link.addParameter(ic_object_id_parameter,key);
    return link;
  }

  public  ModuleObject getPermissionIcon(int key,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image editImage = bundle.getImage("edit.gif","Set permissions");
    Link link = new Link(editImage);
    link.setWindowToOpen(IBPermissionWindow.class);
    link.addParameter(ib_page_parameter,"1");
    link.addParameter(ib_control_parameter,ACTION_PERMISSION);
    link.addParameter(ic_object_id_parameter,key);
    return link;
  }

  public  ModuleObject getEditIcon(int key,ModuleInfo modinfo){
    IWBundle bundle = modinfo.getApplication().getBundle(IW_BUNDLE_IDENTIFIER);
    Image editImage = bundle.getImage("edit.gif","Edit component");
    Link link = new Link(editImage);
    link.setWindowToOpen(IBPropertiesWindow.class);
    link.addParameter(ib_page_parameter,getCurrentIBPage(modinfo));
    link.addParameter(ib_control_parameter,ACTION_EDIT);
    link.addParameter(ic_object_id_parameter,key);
    return link;
  }

  private class BuilderObjectControl extends ModuleObjectContainer{

    private Table table;
    private ModuleObjectContainer parent;
    private String parentKey;
    private ModuleObject theObject;


    public BuilderObjectControl(ModuleObject obj,ModuleObjectContainer objectParent,String theParentKey,ModuleInfo modinfo){
      parent=objectParent;
      theObject=obj;
      parentKey=theParentKey;
      init(modinfo);
      add(obj);
    }

    private void init(ModuleInfo modinfo){
      table = new Table(1,2);
      super.add(table);
      table.setBorder(0);
      table.setCellpadding(0);
      table.setCellspacing(2);
      table.setColor("gray");
      table.setColor(1,2,"white");
      table.setHeight(1,1,"11");

      if(theObject!=null){
        //table.add(theObject.getClassName());
        table.add(getDeleteIcon(theObject.getICObjectInstanceID(),parentKey,modinfo));
        table.add(getEditIcon(theObject.getICObjectInstanceID(),modinfo));
        table.add(getPermissionIcon(theObject.getICObjectInstanceID(),modinfo));
      }
      else{
          System.out.println("theObject==null");
          table.add(getDeleteIcon(0,parentKey,modinfo));
          table.add(getEditIcon(0,modinfo));
      }
    }

    public void add(ModuleObject obj){
      if(obj instanceof Table){
        String width=((Table)obj).getWidth();
        if(width!=null){
          table.setWidth(width);
          ((Table)obj).setWidth("100%");
        }

        String height=((Table)obj).getHeight();
        if(height!=null){
          table.setHeight(height);
          ((Table)obj).setHeight("100%");

        }

      }
      table.add(obj,1,2);
      obj.setParentObject(parent);
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
          //System.out.println("propertyName="+propertyName);
          //System.out.println("propertyValue="+propertyValue);
          xml.update();
          return true;
        }
        else{
          System.out.println("SetProperty failed for ic_object_instance_id = "+ObjectInstanceId);
          return false;
        }
      }
      catch(Exception e){
        e.printStackTrace();
        return false;
      }
  }

  public boolean deleteModule(String pageKey,String parentObjectInstanceID,int ICObjectInstanceID){
    IBXMLPage xml = getIBXMLPage(pageKey);
    if(XMLWriter.deleteModule(xml,parentObjectInstanceID,ICObjectInstanceID)){
      xml.update();
      return true;
    }
    else {
      return false;
    }
  }

  public boolean lockRegion(String pageKey, String parentObjectInstanceID, int ICObjectInstanceID) {
    IBXMLPage xml = getIBXMLPage(pageKey);
    if (XMLWriter.deleteModule(xml,parentObjectInstanceID,ICObjectInstanceID)) {
      xml.update();
      return true;
    }

    return(false);
  }

  public boolean unlockRegion(String pageKey, String parentObjectInstanceID, int ICObjectInstanceID) {
/*    IBXMLPage xml = getIBXMLPage(pageKey);
    if(XMLWriter.deleteModule(xml,parentObjectInstanceID,ICObjectInstanceID)){
      xml.update();
      return true;
    }
    else {
      return false;
    }*/
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
      ICObjectInstance instance = new ICObjectInstance(icObjecctInstanceID);
      Class c = instance.getObject().getObjectClass();
      IWBundle iwb = instance.getObject().getBundle(iwma);
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

}