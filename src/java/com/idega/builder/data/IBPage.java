/*
 * $Id: IBPage.java,v 1.20 2001/09/18 17:19:45 palli Exp $
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
import java.io.InputStream;
import java.io.OutputStream;
import com.idega.core.data.ICFile;
import com.idega.data.TreeableEntity;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class IBPage extends TreeableEntity {
  private static String _templateIdColumn = "template_id";
  private static String _fileColumn = "file_id";
  private static String _nameColumn = "name";
  private static String _entityName = "ib_page";
  private static String _type = "page_type";
  private ICFile _file;
  private BlobWrapper _wrapper;

  public static String PAGE = "P";
  public static String TEMPLATE = "T";
  public static String DRAFT = "D";


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
		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addAttribute(getColumnName(),"Nafn",true,true,String.class);
    addAttribute(getColumnFile(),"File",true,true,Integer.class,"many-to-one",ICFile.class);
    addAttribute(getColumnTemplateID(),"Template",true,true,Integer.class,"many-to-one",IBPage.class);
    addAttribute(getColumnType(),"Type",true,true,String.class,1);
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
		return(_entityName);
	}

  /**
   *
   */
	public void setDefaultValues() {
		//setColumn("image_id",1);
	}

	public String getName() {
		return(getStringColumnValue(getColumnName()));
	}


  public void setName(String name) {
    setColumn(getColumnName(),name);
  }

  public int getTemplateId() {
    return(getIntColumnValue(getColumnTemplateID()));
  }

  public void setTemplateId(int id) {
    setColumn(getColumnTemplateID(),id);
  }

  public String getType() {
    return(getStringColumnValue(getColumnType()));
  }

  public void setType(String type) {
    if ((type.equals(PAGE)) || (type.equals(TEMPLATE)) || (type.equals(DRAFT)))
      setColumn(getColumnType(),type);
  }

  private int getFileID() {
    return(getIntColumnValue(getColumnFile()));
  }

  public ICFile getFile() {
    int fileID = getFileID();
    if (fileID !=- 1) {
      _file = (ICFile)getColumnValue(getColumnFile());
    }
    return(_file);
  }

  public void setFile(ICFile file) {
    //System.out.println("Calling setFile");
    setColumn(getColumnFile(),file);
    _file = file;
  }

  public void setPageValue(InputStream stream) {
    ICFile file = getFile();
    if (file == null) {
      file = new ICFile();
      setFile(file);
    }
    file.setFileValue(stream);
  }

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

  public static String getColumnName() {
    return(_nameColumn);
  }

  public static String getColumnTemplateID() {
    return(_templateIdColumn);
  }

  public static String getColumnFile() {
    return(_fileColumn);
  }

  public static String getColumnType() {
    return(_type);
  }

  public void update() throws SQLException {
    ICFile file = getFile();
    if(file != null) {
      try {
        //System.out.println("file != null in update");
        if(file.getID() == -1) {
          file.insert();
          setFile(file);
          //System.out.println("Trying insert on ICFile");
        }
        else {
          //System.out.println("Trying update on ICFile");
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
    else {
      //System.out.println("file == null in update");
    }
    super.update();
  }

  public void insert() throws SQLException {
    ICFile file = getFile();
    if(file != null) {
      //System.out.println("file != null in insert");
      file.insert();
      setFile(file);
    }
    else {
      //System.out.println("file == null in insert");
    }
    super.insert();
  }

  public void delete() throws SQLException {
    ICFile file = getFile();
    if(file != null) {
      //System.out.println("file != null in delete");
      try {
        file.delete();
      }
      catch(SQLException e) {
      }
    }
    else {
      //System.out.println("file == null in delete");
    }
    super.delete();
  }

  public void setIsPage() {
    setType(PAGE);
  }

  public void setIsTemplate() {
    setType(TEMPLATE);
  }

  public void setIsDraft() {
    setType(DRAFT);
  }

  public boolean isPage() {
    String type = getType();
    if (type.equals(PAGE))
      return(true);
    else
      return(false);
  }

  public boolean isTemplate() {
    String type = getType();
    if (type.equals(TEMPLATE))
      return(true);
    else
      return(false);
  }

  public boolean isDraft() {
    String type = getType();
    if (type.equals(DRAFT))
      return(true);
    else
      return(false);
  }
}
