/*
 * $Id: IBPage.java,v 1.7 2001/07/16 09:51:18 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import java.sql.SQLException;
import com.idega.data.GenericEntity;
import com.idega.data.BlobWrapper;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
 */
public class IBPage extends GenericEntity {
	public IBPage() {
		super();
	}

	public IBPage(int id)throws SQLException {
		super(id);
	}

	public void initializeAttributes() {
		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...
		addAttribute(getIDColumnName());
		addAttribute("name","Nafn",true,true,"java.lang.String");
                addAttribute("page_value","Page value",true,true,"com.idega.data.BlobWrapper");
	}

        public void insertStartData()throws Exception{
          IBPage page = new IBPage();
          page.setName("Empty page");
          page.insert();
        }

	public String getEntityName() {
		return "ib_page";
	}

	public void setDefaultValues() {
		//setColumn("image_id",1);
	}

	public String getName() {
		return getStringColumnValue("name");
	}

        public void setName(String name) {
          setColumn("name",name);
        }

        public OutputStream getPageValueForWrite() {
          return getColumnOutputStream("page_value");
        }

        public void setPageValue(InputStream stream) {
          setColumn("page_value",stream);
        }

        public InputStream getPageValue() {
          try {
            return getInputStreamColumnValue("page_value");
          }
          catch(java.lang.Exception e) {
            return null;
          }
        }



}
