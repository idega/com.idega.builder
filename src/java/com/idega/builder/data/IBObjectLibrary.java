/*
 * $Id: IBObjectLibrary.java,v 1.3 2001/12/17 15:23:24 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import com.idega.data.BlobWrapper;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.io.InputStream;
import java.io.OutputStream;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICObject;
import com.idega.core.user.data.User;
import com.idega.data.TreeableEntity;
import com.idega.data.GenericEntity;
import com.idega.util.idegaTimestamp;
import com.idega.presentation.IWContext;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IBObjectLibrary extends TreeableEntity {
  private final static String ENTITY_NAME = "ib_library";
  private final static String FILE_COLUMN = "file_id";
  private final static String OWNER_COLUMN = "user_id";

  private ICFile _file;
  private BlobWrapper _wrapper;

  /**
   *
   */
  public IBObjectLibrary() {
    super();
  }

  /**
   *
   */
  public IBObjectLibrary(int id) throws SQLException {
    super(id);
  }

  /**
   *
   */
  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnFile(),"File",true,true,Integer.class,GenericEntity.MANY_TO_ONE,ICFile.class);
    addAttribute(getColumnOwner(),"Owner",true,true,Integer.class,GenericEntity.MANY_TO_ONE,User.class);
  }

  /**
   *
   */
  public void insertStartData() throws Exception {
  }

  /**
   *
   */
  public String getEntityName() {
    return(ENTITY_NAME);
  }

  /**
   *
   */
  public void setDefaultValues() {
  }

  /*
   *
   */
  private int getFileID() {
    return(getIntColumnValue(getColumnFile()));
  }

  /**
   *
   */
  public ICFile getFile() {
    int fileID = getFileID();
    if (fileID !=- 1) {
      _file = (ICFile)getColumnValue(getColumnFile());
    }
    return(_file);
  }

  /**
   *
   */
  public void setFile(ICFile file) {
    setColumn(getColumnFile(),file);
    _file = file;
  }

  /**
   *
   */
  public void setPageValue(InputStream stream) {
    ICFile file = getFile();
    if (file == null) {
      file = new ICFile();
      setFile(file);
    }
    file.setFileValue(stream);
  }

  /**
   *
   */
  public InputStream getPageValue() {
    try {
      ICFile file = getFile();
      if (file != null) {
        return(file.getFileValue());
      }
    }
    catch(Exception e) {
    }

    return(null);
  }

  /**
   *
   */
  public OutputStream getPageValueForWrite() {
    ICFile file = getFile();
    if (file == null) {
      file = new ICFile();
      setFile(file);
    }
    OutputStream theReturn = file.getFileValueForWrite();
    _wrapper = (BlobWrapper)file.getColumnValue(ICFile.getColumnFileValue());

    return(theReturn);
  }

  /**
   *
   */
  public void update() throws SQLException {
    ICFile file = getFile();
    if (file != null) {
      try {
        if(file.getID() == -1) {
          file.insert();
          setFile(file);
        }
        else {
          if (_wrapper != null) {
            file.setColumn(ICFile.getColumnFileValue(),_wrapper);
          }
          file.update();
        }
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
    super.update();
  }

  /**
   *
   */
  public void insert() throws SQLException {
    ICFile file = getFile();
    if(file != null) {
      file.insert();
      setFile(file);
    }
    super.insert();
  }

  /**
   *
   */
  public void delete() throws SQLException {
    ICFile file = getFile();
    if(file != null) {
      try {
        file.delete();
      }
      catch(SQLException e) {
      }
    }
    super.delete();
  }

  /**
   *
   */
  public String getColumnFile() {
    return(FILE_COLUMN);
  }

  /**
   *
   */
  public String getColumnOwner() {
    return(OWNER_COLUMN);
  }

  /**
   *
   */
  public void setOwnerId(int id) {
    setColumn(getColumnOwner(),id);
  }

  /**
   *
   */
  public int getOwnerId() {
    return(getIntColumnValue(getColumnOwner()));
  }
}