/*
 * $Id: IBPage.java,v 1.37 2002/04/03 12:29:16 gummi Exp $
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
import com.idega.core.data.ICMimeType;
import com.idega.core.data.ICProtocol;
import com.idega.core.user.data.User;
import com.idega.data.TreeableEntity;
import com.idega.util.idegaTimestamp;
//import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWUserContext;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class IBPage extends TreeableEntity {
  private final static String ENTITY_NAME = "ib_page";
  private final static String FILE_COLUMN = "file_id";
  private final static String NAME_COLUMN = "name";
  private final static String TEMPLATE_ID_COLUMN = "template_id";
  private final static String TYPE_COLUMN = "page_type";
  private final static String SUBTYPE_COLUMN = "page_sub_type";
  private final static String LOCKED_COLUMN = "locked_by";
  private final static String DELETED_COLUMN = "deleted";
  private final static String DELETED_BY_COLUMN = "deleted_by";
  private final static String DELETED_WHEN_COLUMN = "deleted_when";
  private ICFile _file;
  private BlobWrapper _wrapper;

  public final static String PAGE = "P";
  public final static String TEMPLATE = "T";
  public final static String DRAFT = "D";
  public final static String FOLDER = "F";
  public final static String DPT_TEMPLATE = "A";
  public final static String DPT_PAGE = "B";

  public final static String DELETED = "Y";
  public final static String NOT_DELETED = "N";

  /**
   *
   */
	public IBPage() {
		super();
	}

  /**
   *
   */
	public IBPage(int id)throws SQLException {
		super(id);
	}

  /**
   *
   */
  public void initializeAttributes() {
    addAttribute(getIDColumnName());
    addAttribute(getColumnName(),"Nafn",true,true,String.class);
    addAttribute(getColumnFile(),"File",true,true,Integer.class,"many-to-one",ICFile.class);
    addAttribute(getColumnTemplateID(),"Template",true,true,Integer.class,"many-to-one",IBPage.class);
    addAttribute(getColumnType(),"Type",true,true,String.class,1);
    addAttribute(getColumnSubType(),"Sub type",true,true,String.class);
    addAttribute(getColumnLockedBy(),"Locked by",true,true,Integer.class,"many-to-one",User.class);
    addAttribute(getColumnDeleted(),"Deleted",true,true,String.class,1);
    addAttribute(getColumnDeletedBy(),"Deleted by",true,true,Integer.class,"many-to-one",User.class);
    addAttribute(getColumnDeletedWhen(),"Deleted when",true,true,Timestamp.class);
    addManyToManyRelationShip(ICProtocol.class,"ib_page_ic_protocol");
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
		//setColumn("image_id",1);
	}

  /**
   *
   */
	public String getName() {
		return(getStringColumnValue(getColumnName()));
	}

  /**
   *
   */
  public void setName(String name) {
    setColumn(getColumnName(),name);
  }

  /**
   *
   */
  public int getTemplateId() {
    return(getIntColumnValue(getColumnTemplateID()));
  }

  /**
   *
   */
  public void setTemplateId(int id) {
    setColumn(getColumnTemplateID(),id);
  }

  /**
   *
   */
  public String getType() {
    return(getStringColumnValue(getColumnType()));
  }

  /**
   *
   */
  public String getSubType() {
    return(getStringColumnValue(getColumnSubType()));
  }

  /**
   *
   */
  public int getLockedBy() {
    return(getIntColumnValue(getColumnLockedBy()));
  }

  /**
   *
   */
  public void setLockedBy(int id) {
    setColumn(getColumnLockedBy(),id);
  }

  /**
   *
   */
  public boolean getDeleted() {
    String deleted = getStringColumnValue(getColumnDeleted());

    if ((deleted == null) || (deleted.equals(NOT_DELETED)))
      return(false);
    else if (deleted.equals(DELETED))
      return(true);
    else
      return(false);
  }

  /**
   *
   */
  public void setDeleted(boolean deleted) {
    if (deleted) {
      setColumn(getColumnDeleted(),DELETED);
      setDeletedWhen(idegaTimestamp.getTimestampRightNow());
//      setDeletedBy(iwc.getUserId());
    }
    else {
      setColumn(getColumnDeleted(),NOT_DELETED);
//      setDeletedBy(-1);
//      setDeletedWhen(null);
    }
  }

  /**
   *
   */
  public int getDeletedBy() {
    return(getIntColumnValue(getColumnDeletedBy()));
  }

  /**
   *
   */
  private void setDeletedBy(int id) {
//    if (id == -1)
//      setColumn(getColumnDeletedBy(),(Object)null);
//    else
      setColumn(getColumnDeletedBy(),id);
  }

  /**
   *
   */
  public Timestamp getDeletedWhen() {
    return((Timestamp)getColumnValue(getColumnDeletedWhen()));
  }

  /**
   *
   */
  private void setDeletedWhen(Timestamp when) {
    setColumn(getColumnDeletedWhen(),when);
  }

  /**
   *
   */
  public void setType(String type) {
    if ((type.equals(PAGE)) || (type.equals(TEMPLATE)) || (type.equals(DRAFT)) || (type.equals(DPT_TEMPLATE)) || (type.equals(DPT_PAGE)))
      setColumn(getColumnType(),type);
  }

  /**
   *
   */
  public void setSubType(String type) {
    setColumn(getColumnSubType(),type);
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
    file.setMimeType(ICMimeType.IC_MIME_TYPE_XML);
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
  public static String getColumnName() {
    return(NAME_COLUMN);
  }

  /**
   *
   */
  public static String getColumnTemplateID() {
    return(TEMPLATE_ID_COLUMN);
  }

  /**
   *
   */
  public static String getColumnFile() {
    return(FILE_COLUMN);
  }

  /**
   *
   */
  public static String getColumnType() {
    return(TYPE_COLUMN);
  }

  /**
   *
   */
  public static String getColumnSubType() {
    return(SUBTYPE_COLUMN);
  }

  /**
   *
   */
  public static String getColumnLockedBy() {
    return(LOCKED_COLUMN);
  }

  /**
   *
   */
  public static String getColumnDeleted() {
    return(DELETED_COLUMN);
  }

  /**
   *
   */
  public static String getColumnDeletedBy() {
    return(DELETED_BY_COLUMN);
  }

  /**
   *
   */
  public static String getColumnDeletedWhen() {
    return(DELETED_WHEN_COLUMN);
  }

  /**
   *
   */
  public synchronized void update() throws SQLException {
    ICFile file = getFile();
    if (file != null) {
      try {
        if(file.getID() == -1) {
          file.insert();
          file.setName(this.getName());
          file.setMimeType("text/xml");
          setFile(file);
        }
        else {
          if (_wrapper != null) {
            file.setColumn(ICFile.getColumnFileValue(),_wrapper);
          }
          file.setName(this.getName());
          file.setMimeType("text/xml");
          file.update();
        }
      }
      catch(Exception e) {
        e.printStackTrace(System.err);
      }
    }
    else {
      System.out.println("IBPage, file == null in update");
    }
    super.update();
  }

  /**
   *
   */
  public void insert() throws SQLException {
    ICFile file = getFile();
    if(file != null) {
      //System.out.println("file != null in insert");
      try{
        file.insert();
      }
      catch(SQLException e){
        e.printStackTrace();
      }
      setFile(file);
    }
    else {
      //System.out.println("file == null in insert");
    }
    super.insert();
  }

  /**
   *
   */
  public void delete() throws SQLException {
    throw new SQLException("Use delete(int userId) instead");
  }

  /**
   *
   */
  public void delete(int userId) throws SQLException {
    setColumn(getColumnDeleted(),DELETED);
    setDeletedWhen(idegaTimestamp.getTimestampRightNow());
    setDeletedBy(userId);

    super.update();
  }

  /**
   *
   */
  public void setIsPage() {
    setType(PAGE);
  }

  /**
   *
   */
  public void setIsTemplate() {
    setType(TEMPLATE);
  }

  /**
   *
   */
  public void setIsDraft() {
    setType(DRAFT);
  }

  /**
   *
   */
  public boolean isPage() {
    String type = getType();
    if (type.equals(PAGE))
      return(true);
    else
      return(false);
  }

  /**
   *
   */
  public boolean isTemplate() {
    String type = getType();
    if (type.equals(TEMPLATE))
      return(true);
    else
      return(false);
  }

  /**
   *
   */
  public boolean isDraft() {
    String type = getType();
    if (type.equals(DRAFT))
      return(true);
    else
      return(false);
  }

  /**
   *
   */
  public boolean isDynamicTriggeredPage() {
    String type = getType();
    if (type.equals(DPT_PAGE))
      return(true);
    else
      return(false);
  }

  /**
   *
   */
  public boolean isDynamicTriggeredTemplate() {
    String type = getType();
    if (type.equals(DPT_TEMPLATE))
      return(true);
    else
      return(false);
  }


  public boolean isLeaf(){
    return true;
  }

  public void setOwner(IWUserContext iwuc){
    try{
      System.out.println("------------------  setOwner()  ----------------");
      iwuc.getAccessController().setCurrentUserAsOwner(this,iwuc);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}