//idega 2001 - Tryggvi Larusson

/*

*Copyright 2001 idega.is All Rights Reserved.

*/

package com.idega.builder.data;



//import java.util.*;

import java.sql.SQLException;





/**

*@author <a href="mailto:gimmi@idega.is">Notch Johnson</a>

*@version 1.3

*/

public class IBEntityBMPBean extends com.idega.data.GenericEntity implements com.idega.builder.data.IBEntity {



	public IBEntityBMPBean(){

		super();

	}



	public IBEntityBMPBean(int id)throws SQLException{

		super(id);

	}



	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...

		addAttribute(getIDColumnName());

		addAttribute("class_name","Nafn klasa",true,true,"java.lang.String");

		addAttribute("entity_class_name","Nafn entity",true,true, "java.lang.String");

                this.addManyToManyRelationShip(IBObject.class,"ib_object_ib_entity");



	}



        public void insertStartData()throws Exception{

          IBEntity obj;

          Class textReaderClass = com.idega.block.text.presentation.TextReader.class;

          Class newsModuleClass = com.idega.block.news.presentation.NewsReader.class;



          IBObject[] newsRecords = (IBObject[]) (((com.idega.builder.data.IBEntityHome)com.idega.data.IDOLookup.getHomeLegacy(IBEntity.class)).createLegacy()).findAllByColumn("class_name",newsModuleClass.getName());

          if(newsRecords.length>0){

            IBObject newsRecord = newsRecords[0];

            IBEntity entity = ((com.idega.builder.data.IBEntityHome)com.idega.data.IDOLookup.getHomeLegacy(IBEntity.class)).createLegacy();

            entity.setClassName("Fréttir");

            entity.setEntityClassName(com.idega.block.news.data.NewsCategory.class.getName());

            entity.insert();

            entity.addTo(newsRecord);

          }



          IBObject[] textRecords = (IBObject[]) (((com.idega.builder.data.IBEntityHome)com.idega.data.IDOLookup.getHomeLegacy(IBEntity.class)).createLegacy()).findAllByColumn("class_name",textReaderClass.getName());

          if(textRecords.length>0){

            IBObject textRecord = textRecords[0];

            IBEntity entity = ((com.idega.builder.data.IBEntityHome)com.idega.data.IDOLookup.getHomeLegacy(IBEntity.class)).createLegacy();

            entity.setClassName("Texti");

            entity.setEntityClassName(com.idega.block.text.data.TxText.class.getName());

            entity.insert();

            entity.addTo(textRecord);

          }



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

