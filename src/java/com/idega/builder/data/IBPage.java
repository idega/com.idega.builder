/*
 * $Id: IBPage.java,v 1.10 2001/08/25 12:19:42 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import java.sql.SQLException;

import java.io.InputStream;
import java.io.OutputStream;

import com.idega.data.GenericEntity;
import com.idega.data.BlobWrapper;

import com.idega.core.data.ICFile;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class IBPage extends TreeableEntity{

      private static String template_id_column = "template_id";
      private static String file_column = "file_id";
      private static String name_column = "name";
      private static String entity_name = "ib_page";

      private ICFile file;

	public IBPage() {
		super();
	}

	public IBPage(int id)throws SQLException {
		super(id);
	}

	public void initializeAttributes(){
		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addAttribute(getColumnName(),"Nafn",true,true,"java.lang.String");
                //addAttribute("page_value","Page value",true,true,"com.idega.data.BlobWrapper");
                addAttribute(getColumnFile(),"File",true,true,Integer.class,"many-to-one",ICFile.class);
                addAttribute(getColumnTemplateID(),"Template",true,true,Integer.class,"many-to-one",IBTemplatePage.class);

                //this.addTreeRelationShip();

	}

        public void insertStartData()throws Exception{
          /*IBPage page = new IBPage();
          page.setName("Empty page");
          page.insert();
          */
        }

	public String getEntityName() {
		return entity_name;
	}

	public void setDefaultValues() {
		//setColumn("image_id",1);
	}

	public String getName() {
		return getStringColumnValue(getColumnName());
	}


        public void setName(String name) {
          setColumn(getColumnName(),name);
        }

        private int getFileID(){
          return getIntColumnValue(getColumnFile());
        }

        public ICFile getFile(){
          int fileID = getFileID();
          if(fileID!=-1){
            return (ICFile)getColumnValue(getColumnFile());
          }
          else{
            return this.file;
          }
        }

        public void setFile(ICFile file){
          System.out.println("Calling setFile");
          setColumn(getColumnFile(),file);
          this.file = file;
        }

        public void setPageValue(InputStream stream) {
          //setColumn("page_value",stream);
          ICFile file = getFile();
          if(file==null){
            file = new ICFile();
            setFile(file);
          }
          file.setFileValue(stream);
        }

        public InputStream getPageValue(){
          try {
            //return getInputStreamColumnValue("page_value");
            ICFile file = getFile();
            if(file!=null){
              return file.getFileValue();
            }
            return null;

          }
          catch(Exception e) {
            return null;
          }
        }

        public OutputStream getPageValueForWrite() {
          //return getColumnOutputStream("page_value");
          ICFile file = getFile();
          if(file==null){
            file = new ICFile();
            setFile(file);
          }
          return file.getFileValueForWrite();

        }


        public static String getColumnName(){
          return name_column;
        }

        public static String getColumnTemplateID(){
          return template_id_column;
        }

        public static String getColumnFile(){
          return file_column;
        }

        public void update()throws SQLException{
          ICFile file = getFile();
          if(file!=null){
            try{
              System.out.println("file != null in update");
              if(file.getID()==-1){
                file.insert();
                setFile(file);
                System.out.println("Trying insert on ICFile");
              }
              else{
                file.update();
                System.out.println("Trying update on ICFile");
              }
            }
            catch(Exception e){
              e.printStackTrace();
            }
          }
          else{
            System.out.println("file == null in update");
          }
          super.update();
        }

        public void insert()throws SQLException{
          ICFile file = getFile();
          if(file!=null){
            System.out.println("file != null in insert");
            file.insert();
            setFile(file);
          }
          else{
            System.out.println("file == null in insert");
          }
          super.insert();
        }

        public void delete()throws SQLException{
          ICFile file = getFile();
          if(file!=null){
            System.out.println("file != null in delete");
            try{
              file.delete();
            }
            catch(SQLException e){
            }
          }
          else{
            System.out.println("file == null in delete");
          }
          super.delete();
        }


}
