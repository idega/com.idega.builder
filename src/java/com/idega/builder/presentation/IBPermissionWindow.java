package com.idega.builder.presentation;

import com.idega.builder.business.BuilderLogic;
import com.idega.builder.business.IBPropertyHandler;
import com.idega.presentation.IWContext;

import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.business.ICObjectBusiness;
import com.idega.core.business.UserGroupBusiness;
import com.idega.core.data.ICObject;
import com.idega.core.data.GenericGroup;


import com.idega.idegaweb.IWProperty;
import com.idega.idegaweb.IWPropertyList;
import com.idega.idegaweb.IWPropertyListIterator;

import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;

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

  private static final String ic_object_id_parameter = BuilderLogic.IC_OBJECT_INSTANCE_ID_PARAMETER;
  private static final String ib_page_parameter = BuilderLogic.IB_PAGE_PARAMETER;

  public static final String _PARAMETERSTRING_IDENTIFIER = AccessControl._PARAMETERSTRING_IDENTIFIER;
  public static final String _PARAMETERSTRING_PERMISSION_CATEGORY = AccessControl._PARAMETERSTRING_PERMISSION_CATEGORY;

  private static final String permissionKeyParameterString = "permission_type";
  private static final String lastPermissionKeyParameterString = "last_permission_key";
  private static final String permissionGroupParameterString = "permission_groups";
  private static final String SessionAddressPermissionMap = "ib_permission_hashtable";
  private static final String SessionAddressPermissionMapOldValue = "ib_permission_hashtable_old_value";
  private boolean collectOld = false;



  private Table lineUpElements(IWContext iwc,String permissionType) throws Exception{

    String identifier = iwc.getParameter(_PARAMETERSTRING_IDENTIFIER);
    String category = iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY);
    Table frameTable = new Table(1,4);
    if(identifier != null && category != null){
      int intPermissionCategory = Integer.parseInt(category);

      frameTable.setAlignment("center");
      frameTable.setVerticalAlignment("middle");
      frameTable.setAlignment(1,1,"left");
      frameTable.setAlignment(1,2,"left");
      frameTable.setAlignment(1,3,"left");
      frameTable.setAlignment(1,4,"right");

      // PermissionString
      Text permissionKeyText = new Text("Permission Key");

      DropdownMenu permissionTypes = new DropdownMenu(permissionKeyParameterString);
      permissionTypes.keepStatusOnAction();
      permissionTypes.setToSubmit();

      String[] keys = null;

      switch (intPermissionCategory) {
        case AccessControl._CATEGORY_OBJECT_INSTANCE :
          keys = AccessControl.getICObjectPermissionKeys(ICObjectBusiness.getICObjectClassForInstance(Integer.parseInt(identifier)));
          break;
        case AccessControl._CATEGORY_OBJECT :
          keys = AccessControl.getICObjectPermissionKeys(ICObjectBusiness.getICObjectClass(Integer.parseInt(identifier)));
          break;
        case AccessControl._CATEGORY_BUNDLE :
          keys = AccessControl.getBundlePermissionKeys(Class.forName(identifier));
          break;
        case AccessControl._CATEGORY_PAGE_INSTANCE :
          keys = AccessControl.getPagePermissionKeys();
          break;
        case AccessControl._CATEGORY_PAGE :
          keys = AccessControl.getPagePermissionKeys();
          break;
        case AccessControl._CATEGORY_JSP_PAGE :
          keys = new String[0];
          break;
      }


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


      Map hash = (Map)iwc.getSessionAttribute(this.SessionAddressPermissionMap);
      List directGroups = null;
      List oldvalues = null;
      if(hash != null && hash.get(permissionType)!=null){
        directGroups = UserGroupBusiness.getGroups((String[])hash.get(permissionType));
        collectOld = false;
      } else {
        directGroups = AccessControl.getAllowedGroups(intPermissionCategory, identifier,permissionType);
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
          this.collectOldValues(iwc,oldValueIDs, permissionType);
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
      SubmitButton submit = new SubmitButton("    OK    ","subm","save");
      SubmitButton cancel = new SubmitButton("  Cancel  ","subm","cancel");

      buttonTable.add(submit,1,1);
      buttonTable.add(cancel,2,1);

      frameTable.add(permissionKeyText,1,1);
      frameTable.add(permissionTypes,1,2);
      /*frameTable.add(new SubmitButton("->"),1,2);*/
      frameTable.add(permissionBox,1,3);
      frameTable.add(buttonTable,1,4);
      frameTable.add(new HiddenInput(lastPermissionKeyParameterString, permissionType ));

    }
    return frameTable;
  }



  public void main(IWContext iwc)throws Exception{
      super.addTitle("IBPermissionWindow");
      String submit = iwc.getParameter("subm");
      Form myForm = new Form();
      myForm.maintainParameter(_PARAMETERSTRING_IDENTIFIER);
      myForm.maintainParameter(_PARAMETERSTRING_PERMISSION_CATEGORY);

      if(submit != null){
        if(submit.equals("save")){
          String permissionType = iwc.getParameter(permissionKeyParameterString);
          if(permissionType != null){
            this.collect(iwc);
            this.store(iwc);
            this.dispose(iwc);
            this.close();
          }else {
            this.add("ERROR: nothing to save");
          }
          this.setParentToReload();
        }else if(submit.equals("cancel")){
          this.dispose(iwc);
          this.close();
        } else {
          String permissionType = iwc.getParameter(permissionKeyParameterString);
          if(permissionType != null){
            collect(iwc);
          }
          myForm.add(this.lineUpElements(iwc,permissionType));
        }
      }else{
        String permissionType = iwc.getParameter(permissionKeyParameterString);
        if(permissionType != null){
          collect(iwc);
        }
        myForm.add(this.lineUpElements(iwc,permissionType));
      }
      this.add(myForm);

  }




  private void collect(IWContext iwc){
    Object obj = iwc.getSessionAttribute(SessionAddressPermissionMap);
    Map hash = null;
    if(obj != null){
      hash = (Map)obj;
      if(!hash.get(_PARAMETERSTRING_IDENTIFIER).equals(iwc.getParameter(_PARAMETERSTRING_IDENTIFIER)) && !hash.get(_PARAMETERSTRING_PERMISSION_CATEGORY).equals(iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY))){
        hash = new Hashtable();
        hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
        hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
        iwc.setSessionAttribute(SessionAddressPermissionMap,hash);
      }
    }else{
      hash = new Hashtable();
      hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
      hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
      iwc.setSessionAttribute(SessionAddressPermissionMap,hash);
    }
    String[] groups = iwc.getParameterValues(permissionGroupParameterString);
    if(groups != null){
      hash.put(iwc.getParameter(lastPermissionKeyParameterString),groups);
    } else{
      hash.put(iwc.getParameter(lastPermissionKeyParameterString),new String[0]);
    }
  }


  private void collectOldValues(IWContext iwc, List groups, String permissionKey){
    Object obj = iwc.getSessionAttribute(SessionAddressPermissionMapOldValue);
    Map hash = null;
    if(obj != null){
      hash = (Map)obj;
      if(!hash.get(_PARAMETERSTRING_IDENTIFIER).equals(iwc.getParameter(_PARAMETERSTRING_IDENTIFIER)) && !hash.get(_PARAMETERSTRING_PERMISSION_CATEGORY).equals(iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY))){
        hash = new Hashtable();
        hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
        hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
        iwc.setSessionAttribute(SessionAddressPermissionMapOldValue,hash);
      }
    }else{
      hash = new Hashtable();
      hash.put(_PARAMETERSTRING_IDENTIFIER,iwc.getParameter(_PARAMETERSTRING_IDENTIFIER));
      hash.put(_PARAMETERSTRING_PERMISSION_CATEGORY,iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY));
      iwc.setSessionAttribute(SessionAddressPermissionMapOldValue,hash);
    }

    if(hash.get(permissionKey) == null){
      if(groups != null){
        hash.put(permissionKey,groups);
      } else{
        hash.put(permissionKey,new Vector());
      }
    }
  }



  private void store(IWContext iwc) throws Exception {
    Object obj = iwc.getSessionAttribute(SessionAddressPermissionMap);
    Object oldObj= iwc.getSessionAttribute(SessionAddressPermissionMapOldValue);
    if(obj != null && oldObj != null){
      Map map = (Map)obj;
      Map oldMap = (Map)oldObj;
      String instanceId = (String)map.remove(_PARAMETERSTRING_IDENTIFIER);
      String category = (String)map.remove(_PARAMETERSTRING_PERMISSION_CATEGORY);

      if((instanceId != null && instanceId.equals(iwc.getParameter(_PARAMETERSTRING_IDENTIFIER)) && instanceId.equals(oldMap.get(_PARAMETERSTRING_IDENTIFIER)))&&(category != null && category.equals(iwc.getParameter(_PARAMETERSTRING_PERMISSION_CATEGORY)) && category.equals(oldMap.get(_PARAMETERSTRING_PERMISSION_CATEGORY)))){
        Iterator iter = map.keySet().iterator();
        while (iter.hasNext()) {
          Object item = iter.next();
          String[] groups = (String[])map.get(item);
          List oldGroups = (List)oldMap.get(item);
          if(oldGroups == null){
            oldGroups = new Vector();
          }
          int intCategory = Integer.parseInt(category);
          for (int i = 0; i < groups.length; i++) {
            oldGroups.remove(groups[i]);
            AccessControl.setPermission(intCategory, iwc, groups[i],instanceId,(String)item,Boolean.TRUE);
          }
          if(oldGroups.size()>0){
            String[] groupsToRemove = new String[oldGroups.size()];
            Iterator iter2 = oldGroups.iterator();
            int index2 = 0;
            while (iter2.hasNext()) {
              groupsToRemove[index2++] = (String)iter2.next();
            }
            AccessControl.removePermissionRecords(intCategory,iwc, instanceId,(String)item, groupsToRemove);
          }
        }
      }else{
        throw new RuntimeException("identifier or permissionCategory not set or does not match");
      }
    }
  }

  private void dispose(IWContext iwc){
    try {
      iwc.removeSessionAttribute(SessionAddressPermissionMap);
    }
    catch (Exception ex) {

    }
    try {
      iwc.removeSessionAttribute(SessionAddressPermissionMapOldValue);
    }
    catch (Exception ex) {

    }
  }


}