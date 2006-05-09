package com.idega.builder.business;
import java.sql.SQLException;
import java.util.List;
import javax.ejb.CreateException;

import com.idega.core.component.data.ICObject;
import com.idega.core.component.data.ICObjectHome;
import com.idega.core.component.data.ICObjectInstance;
import com.idega.data.EntityFinder;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
/**
 * Title:        ProjectWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author
 * @version 1.0
 */
public class IBObjectHandler
{
	private ICObject arObject;
	public IBObjectHandler() throws IDOLookupException, CreateException
	{
		this.arObject = ((com.idega.core.component.data.ICObjectHome) com.idega.data.IDOLookup.getHome(ICObject.class)).create();
	}
	public int addNewObject(String PublicName, Object obj) throws Exception
	{
		int objID = getObjectID(obj);
		if (objID == -1)
		{
			ICObjectHome icoHome = (ICObjectHome) IDOLookup.getHome(ICObject.class);
			
			ICObject newObj = icoHome.create();
			newObj.setClassName(obj.getClass().getName());
			newObj.setName(PublicName);
			newObj.store();
			return newObj.getID();
		}
		else
		{
			System.out.println(" WARNING! : This ICObject has been adden before and got the object_id = " + objID);
			return objID;
		}
	}
	public int addNewObjectInstance(Object obj) throws Exception
	{
		int instID = getObjectID(obj);
		if (instID != -1)
		{
			ICObjectInstance newInstance =
				((com.idega.core.component.data.ICObjectInstanceHome) com.idega.data.IDOLookup.getHomeLegacy(ICObjectInstance.class))
					.createLegacy();
			newInstance.setICObjectID(instID);
			newInstance.store();
			return newInstance.getID();
		}
		else
		{
			throw new Exception("ICObject is not known");
		}
	}
	public int getObjectID(Object obj) throws Exception
	{
		/*List myList =
			EntityFinder.findAllByColumn(
				this.arObject,
				com.idega.core.component.data.ICObjectBMPBean.getClassNameColumnName(),
				obj.getClass().getName());*/

		ICObjectHome home=null;
		ICObject ico =null;
		try {
			String className = obj.getClass().getName();
			home = (ICObjectHome)IDOLookup.getHome(ICObject.class);
			ico = home.findByClassName(className);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (ico != null)
		{
			return ico.getID();
		}
		else
		{
			return -1;
		}
	}
} // Class IBObjectHandler
