//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import com.idega.presentation.*;
import com.idega.jmodule.news.presentation.NewsReader;
import com.idega.jmodule.text.presentation.TextReader;
import com.idega.jmodule.login.presentation.Login;
import com.idega.jmodule.text.presentation.TextReader;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@deprecated Replaced with com.idega.core.data.ICObject
*@version 1.3
*/
public class IBObject extends GenericEntity{

	public IBObject(){
		super();
	}

	public IBObject(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("object_name","Name",true,true,"java.lang.String");
		addAttribute("class_name","Class Name",true,true,"java.lang.String");
		addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer");

                addManyToManyRelationShip(IBEntity.class,"ib_object_ib_entity");
        }



        public void insertStartData()throws Exception{
          IBObject obj;

          obj = new IBObject();
          obj.setName("NewsModule");
          obj.setObjectClass(NewsReader.class);

          obj.insert();

          obj = new IBObject();
          obj.setName("TextModule");
          obj.setObjectClass(TextReader.class);
          obj.insert();

          obj = new IBObject();
          obj.setName("LoginModule");
          obj.setObjectClass(Login.class);
          obj.insert();

        }

	public String getEntityName(){
		return "ib_object";
	}

	public void setDefaultValues(){
		//setColumn("image_id",1);
//                setColumn("small_icon_image_id",1);
            //setObjectType("iw.block");
	}

	public String getName(){
		return getStringColumnValue("object_name");
	}

        public void setName(String object_name) {
                setColumn("object_name",object_name);
        }

        public static String getClassNameColumnName(){
          return "class_name";
        }

	public String getClassName(){
		return getStringColumnValue("class_name");
	}

        public void setClassName(String className){
            setColumn("class_name",className);
        }

        public Class getObjectClass()throws ClassNotFoundException{
          return Class.forName(getClassName());
        }

        public void setObjectClass(Class c){
          setClassName(c.getName());
        }

}
