package com.idega.builder.business;

import java.sql.*;
import com.idega.builder.data.*;
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

  private IBObject arObject;
  private IBObjectInstance arObjectInstance;


  public IBObjectHandler()throws SQLException {
    arObject = new IBObject();
    arObjectInstance = new IBObjectInstance();
  }


  public int addNewObject(String PublicName, Object obj)throws Exception{
    int objID = getObjectID(obj);
    if(objID == -1){
      IBObject newObj = new IBObject();
        newObj.setClassName(obj.getClass().getName());
        newObj.setName(PublicName);
        newObj.insert();
        return newObj.getID();
    }else{
      System.out.println(" WARNING! : This IBObject has been adden before and got the object_id = " + objID);
      return objID;
    }

  }

  public int addNewObjectInstance(Object obj)throws Exception{
    int instID = getObjectID(obj);
    if(instID != -1){
      IBObjectInstance newInstance = new IBObjectInstance();
      newInstance.setObjectID(instID);
      newInstance.insert();
      return newInstance.getID();
    }else{
      throw new SQLException("IBObject is not known");
    }
  }


  public int getObjectID(Object obj)throws Exception{
    List myList = EntityFinder.findAllByColumn(arObject,IBObject.getClassNameColumnName(),obj.getClass().getName());
    if(myList != null){
      return ((IBObject)myList.get(0)).getID();
    }else{
      return -1;
    }
  }


} // Class IBObjectHandler