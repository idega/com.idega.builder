//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;


/**
*@author <a href="mailto:gimmi@idega.is">Notch Johnson</a>
*@version 1.3
*/
public class IBEntity extends GenericEntity{

	public IBEntity(){
		super();
	}

	public IBEntity(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addAttribute("class_name","Nafn klasa",true,true,"java.lang.String");
		addAttribute("entity_class_name","Nafn entity",true,true, "java.lang.String");
	}

	public String getEntityName(){
		return "ib_entity";
	}

	public void setDefaultValues(){
		//setColumn("image_id",1);
	}

	public String getName(){
		return getEntityClassName();

	}

	public void setClassName(String class_name) {
		setColumn("class_name",class_name);
	}

	public String getClassName() {
		return getStringColumnValue("class_name");
	}

	public void setEntityClassName(String entity_name) {
		setColumn("entity_class_name",entity_name);
	}

	public String getEntityClassName() {
		return getStringColumnValue("entity_class_name");
	}


}
