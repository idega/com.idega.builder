/*
 * $Id: IBPage.java,v 1.2 2001/04/30 16:40:40 palli Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import java.sql.*;
import com.idega.data.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
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
                //addAttribute("xmlvalue","XML",true,true,"com.idega.data.BlobWrapper");
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
}
