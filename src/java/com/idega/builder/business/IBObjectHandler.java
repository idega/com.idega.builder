package com.idega.builder.business;



import java.sql.*;

import com.idega.core.component.data.*;
import com.idega.core.data.*;

import com.idega.data.EntityFinder;

import java.util.List;



/**

 * Title:        ProjectWeb

 * Description:

 * Copyright:    Copyright (c) 2001

 * Company:      idega

 * @author

 * @version 1.0

 */



public class IBObjectHandler {



  private ICObject arObject;

  private ICObjectInstance arObjectInstance;





  public IBObjectHandler()throws SQLException {

    arObject = ((com.idega.core.component.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();

    arObjectInstance = ((com.idega.core.component.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).createLegacy();

  }





  public int addNewObject(String PublicName, Object obj)throws Exception{

    int objID = getObjectID(obj);

    if(objID == -1){

      ICObject newObj = ((com.idega.core.component.data.ICObjectHome)com.idega.data.IDOLookup.getHomeLegacy(ICObject.class)).createLegacy();

        newObj.setClassName(obj.getClass().getName());

        newObj.setName(PublicName);

        newObj.insert();

        return newObj.getID();

    }

    else{

      System.out.println(" WARNING! : This ICObject has been adden before and got the object_id = " + objID);

      return objID;

    }



  }



  public int addNewObjectInstance(Object obj)throws Exception{

    int instID = getObjectID(obj);

    if(instID != -1){

      ICObjectInstance newInstance = ((com.idega.core.component.data.ICObjectInstanceHome)com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class)).createLegacy();

      newInstance.setICObjectID(instID);

      newInstance.insert();

      return newInstance.getID();

    }else{

      throw new Exception("ICObject is not known");

    }

  }





  public int getObjectID(Object obj)throws Exception{

    List myList = EntityFinder.findAllByColumn(arObject,com.idega.core.component.data.ICObjectBMPBean.getClassNameColumnName(),obj.getClass().getName());

    if(myList != null){

      return ((ICObject)myList.get(0)).getID();

    }else{

      return -1;

    }

  }





} // Class IBObjectHandler
