//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class IBPage extends GenericEntity{

	public IBPage(){
		super();
	}

	public IBPage(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addAttribute("name","Nafn",true,true,"java.lang.String");
                //addAttribute("xmlvalue","XML",true,true,"com.idega.data.BlobWrapper");

	}

	public String getEntityName(){
		return "ib_page";
	}

	public void setDefaultValues(){
		//setColumn("image_id",1);
	}

	public String getName(){
		return getStringColumnValue("name");
	}

}
