package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.jmodule.object.ModuleInfo;

import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.business.ICObjectBusiness;
import com.idega.core.business.UserGroupBusiness;
import com.idega.core.data.ICObject;
import com.idega.core.data.GenericGroup;


import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class IBPermissionWindow extends IBAdminWindow{

  private static final String ic_object_id_parameter = BuilderLogic.ic_object_id_parameter;
  private static final String ib_page_parameter = BuilderLogic.ib_page_parameter;
  private static final String permissionTypeParameterString = "permission_type";
  private static final String lastPermissionKeyParameterString = "last_permission_key";
  private static final String permissionGroupParameterString = "permission_groups";
  private static final String SessionAddressPermissionMap = "ib_permission_hashtable";
  private static final String SessionAddressPermissionMapOldValue = "ib_permission_hashtable_old_value";
  private boolean collectOld = false;



  private Table lineUpElements(ModuleInfo modinfo,String permissionType) throws Exception{

    String objectInstaceId = modinfo.getParameter(ic_object_id_parameter);
    Table frameTable = new Table(1,4);
    if(objectInstaceId != null){
      int intObjectInstaceId = Integer.parseInt(objectInstaceId);

      frameTable.setAlignment("center");
      frameTable.setVerticalAlignment("middle");
      frameTable.setAlignment(1,1,"left");
      frameTable.setAlignment(1,2,"left");
      frameTable.setAlignment(1,3,"left");
      frameTable.setAlignment(1,4,"right");

      // PermissionString
      Text permissionKeyText = new Text("Permission Key");

      DropdownMenu permissionTypes = new DropdownMenu(permissionTypeParameterString);
      permissionTypes.keepStatusOnAction();
      permissionTypes.setToSubmit();
      String[] keys = AccessControl.getICObjectPermissionKeys(ICObjectBusiness.getICObjectClass(intObjectInstaceId));

      for (int i = 0; i < keys.length; i++) {
        permissionTypes.addMenuElement(keys[i],keys[i]);
      }

      if(permissionType != null){
        permissionTypes.setSelectedElement(permissionType);
      } else if(keys.length > 0){
        permissionType = keys[0];
      }


      //PermissionGroups
      SelectionDoubleBox permissionBox = new SelectionDoubleBox(permissionGroupParameterString,"Detached","Allowed");

      SelectionBox left = permissionBox.getLeftBox();
        left.setHeight(8);
        left.selectAllOnSubmit();


      SelectionBox right = permissionBox.getRightBox();
        right.setHeight(8);
        right.selectAllOnSubmit();


      Map hash = (Map)modinfo.getSessionAttribute(this.SessionAddressPermissionMap);
      List directGroups = null;
      List oldvalues = null;
      if(hash != null && hash.get(permissionType)!=null){
        directGroups = UserGroupBusiness.getGroups((String[])hash.get(permissionType));
        collectOld = false;
      } else {
        directGroups = AccessControl.getAllowedGroups(intObjectInstaceId,permissionType);
        collectOld = true;

      }



      Iterator iter = null;
      if(directGroups != null){
        iter = directGroups.iterator();
        if(collectOld){
          List oldValueIDs = new Vector();
          while (iter.hasNext()) {
            Object item = iter.next();
            String groupId = Integer.toString(((GenericGroup)item).getID());
            right.addElement(groupId,((GenericGroup)item).getName());
            oldValueIDs.add(groupId);
          }
          this.collectOldValues(modinfo,oldValueIDs, permissionType);
        } else {
          while (iter.hasNext()) {
            Object item = iter.next();
            String groupId = Integer.toString(((GenericGroup)item).getID());
            right.addElement(groupId,((GenericGroup)item).getName());
          }
        }

      }

      List notDirectGroups = AccessControl.getAllPermissionGroups();
      if(notDirectGroups != null){
        if(directGroups != null){
          notDirectGroups.removeAll(directGroups);
        }
        iter = notDirectGroups.iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          left.addElement(Integer.toString(((GenericGroup)item).getID()),((GenericGroup)item).getName());
        }
      }



      // Submit
      Table buttonTable = new Table(2,1);
      SubmitButton submit = new SubmitButton("    OK    ","submit","save");
      SubmitButton cancel = new SubmitButton("  Cancel  ","submit","cancel");

      buttonTable.add(submit,1,1);
      buttonTable.add(cancel,2,1);

      frameTable.add(permissionKeyText,1,1);
      frameTable.add(permissionTypes,1,2);
      frameTable.add(new SubmitButton("->"),1,2);
      frameTable.add(permissionBox,1,3);
      frameTable.add(buttonTable,1,4);
      frameTable.add(new HiddenInput(lastPermissionKeyParameterString, permissionType ));

    }
    return frameTable;
  }



  public void main(ModuleInfo modinfo)throws Exception{
      super.addTitle("IBPermissionWindow");
      String submit = modinfo.getParameter("submit");
      Form myForm = new Form();
      myForm.maintainParameter(ic_object_id_parameter);

      if(submit != null){
        if(submit.equals("save")){
          String permissionType = modinfo.getParameter(permissionTypeParameterString);
          if(permissionType != null){
            this.collect(modinfo);
            this.store(modinfo);
            this.dispose(modinfo);
            this.close();
          }else {
            this.add("ERROR: nothing to save");
          }
          this.setParentToReload();
        }else if(submit.equals("cancel")){
          this.dispose(modinfo);
          this.close();
        } else {
          String permissionType = modinfo.getParameter(permissionTypeParameterString);
          if(permissionType != null){
            collect(modinfo);
          }
          myForm.add(this.lineUpElements(modinfo,permissionType));
        }
      }else{
        String permissionType = modinfo.getParameter(permissionTypeParameterString);
        if(permissionType != null){
          collect(modinfo);
        }
        myForm.add(this.lineUpElements(modinfo,permissionType));
      }
      this.add(myForm);

  }




  private void collect(ModuleInfo modinfo){
    Object obj = modinfo.getSessionAttribute(SessionAddressPermissionMap);
    Map hash = null;
    if(obj != null){
      hash = (Map)obj;
      if(!hash.get(ic_object_id_parameter).equals(modinfo.getParameter(ic_object_id_parameter))){
        hash = new Hashtable();
        hash.put(ic_object_id_parameter,modinfo.getParameter(ic_object_id_parameter));
        modinfo.setSessionAttribute(SessionAddressPermissionMap,hash);
      }
    }else{
      hash = new Hashtable();
      hash.put(ic_object_id_parameter,modinfo.getParameter(ic_object_id_parameter));
      modinfo.setSessionAttribute(SessionAddressPermissionMap,hash);
    }
    String[] groups = modinfo.getParameterValues(permissionGroupParameterString);
    if(groups != null){
      hash.put(modinfo.getParameter(lastPermissionKeyParameterString),groups);
    } else{
      hash.put(modinfo.getParameter(lastPermissionKeyParameterString),new String[0]);
    }
  }


  private void collectOldValues(ModuleInfo modinfo, List groups, String permissionKey){
    Object obj = modinfo.getSessionAttribute(SessionAddressPermissionMapOldValue);
    Map hash = null;
    if(obj != null){
      hash = (Map)obj;
      if(!hash.get(ic_object_id_parameter).equals(modinfo.getParameter(ic_object_id_parameter))){
        hash = new Hashtable();
        hash.put(ic_object_id_parameter,modinfo.getParameter(ic_object_id_parameter));
        modinfo.setSessionAttribute(SessionAddressPermissionMapOldValue,hash);
      }
    }else{
      hash = new Hashtable();
      hash.put(ic_object_id_parameter,modinfo.getParameter(ic_object_id_parameter));
      modinfo.setSessionAttribute(SessionAddressPermissionMapOldValue,hash);
    }
    if(hash.get(permissionKey) == null){
      if(groups != null){
        hash.put(permissionKey,groups);
      } else{
        hash.put(permissionKey,new Vector());
      }
    }
  }



  private void store(ModuleInfo modinfo) throws Exception {
    Object obj = modinfo.getSessionAttribute(SessionAddressPermissionMap);
    Object oldObj= modinfo.getSessionAttribute(SessionAddressPermissionMapOldValue);
    if(obj != null && oldObj != null){
      Map map = (Map)obj;
      Map oldMap = (Map)oldObj;
      String instanceId = (String)map.remove(ic_object_id_parameter);

      if(instanceId != null && instanceId.equals(modinfo.getParameter(ic_object_id_parameter)) && instanceId.equals(oldMap.get(ic_object_id_parameter))){
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          String[] groups = (String[])map.get(item);
          List oldGroups = (List)oldMap.get(item);
          if(oldGroups == null){
            oldGroups = new Vector();
          }
          for (int i = 0; i < groups.length; i++) {
            oldGroups.remove(groups[i]);
            AccessControl.setObjectInstacePermission(groups[i],instanceId,(String)item,Boolean.TRUE);
          }
          if(oldGroups.size()>0){
            String[] groupsToRemove = new String[oldGroups.size()];
            Iterator iter2 = oldGroups.iterator();
            int index2 = 0;
            while (iter2.hasNext()) {
              groupsToRemove[index2++] = (String)iter2.next();
            }
            AccessControl.removeICObjectInstancePermissionRecords(instanceId,(String)item, groupsToRemove);
          }
        }
      }else{
        throw new RuntimeException("ICObjectInstanceId not set or does not match");
      }
    }
  }

  private void dispose(ModuleInfo modinfo){
    try {
      modinfo.removeSessionAttribute(SessionAddressPermissionMap);
    }
    catch (Exception ex) {

    }
    try {
      modinfo.removeSessionAttribute(SessionAddressPermissionMapOldValue);
    }
    catch (Exception ex) {

    }
  }


}