/*
 * $Id: IBPage.java,v 1.16 2001/09/13 18:49:26 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import com.idega.data.GenericEntity;
import com.idega.data.BlobWrapper;
import com.idega.core.data.ICFile;
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
  private static String templateIdColumn_ = "template_id";
  private static String fileColumn_ = "file_id";
  private static String nameColumn_ = "name";
  private static String entityName_ = "ib_page";
  private static String type_ = "page_type";

  public static String page = "P";
  public static String template = "T";
  public static String draft = "D";

  private ICFile file_;
  private BlobWrapper wrapper_;

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
		return(entityName_);
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

  public String getType() {
    return(getStringColumnValue(getColumnType()));
  }

  public void setType(String type) {
    if ((type.equalsIgnoreCase(page)) || (type.equalsIgnoreCase(template)) || (type.equalsIgnoreCase(draft)))
      setColumn(getColumnType(),type);
  }

  private int getFileID() {
    return(getIntColumnValue(getColumnFile()));
  }

  public ICFile getFile() {
    int fileID = getFileID();
    if (fileID !=- 1) {
      file_ = (ICFile)getColumnValue(getColumnFile());
    }

    return(file_);
  }

  public void setFile(ICFile file) {
    System.out.println("Calling setFile");
    setColumn(getColumnFile(),file);
    file_ = file;
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
    wrapper_ = (BlobWrapper)file.getColumnValue(ICFile.getColumnFileValue());

    return(theReturn);
  }

  public static String getColumnName() {
    return(nameColumn_);
  }

  public static String getColumnTemplateID() {
    return(templateIdColumn_);
  }

  public static String getColumnFile() {
    return(fileColumn_);
  }

  public static String getColumnType() {
    return(type_);
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
          if (wrapper_ != null) {
            file.setColumn(ICFile.getColumnFileValue(),wrapper_);
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
    setType(page);
  }

  public void setIsTemplate() {
    setType(template);
  }

  public void setIsDraft() {
    setType(draft);
  }

  public boolean isPage() {
    String type = getType();
    if (type.equalsIgnoreCase(page))
      return(true);
    else
      return(false);
  }

  public boolean isTemplate() {
    String type = getType();
    if (type.equalsIgnoreCase(template))
      return(true);
    else
      return(false);
  }

  public boolean isDraft() {
    String type = getType();
    if (type.equalsIgnoreCase(draft))
      return(true);
    else
      return(false);
  }
}
