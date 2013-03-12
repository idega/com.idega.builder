package com.idega.builder.business;
import java.sql.SQLException;
import com.idega.builder.data.IBJspPage;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
/**
 * Title:        IC
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author idega 2001 - <a href="mailto:idega@idega.is">idega team</a>
 * @version 1.0
 */
public class IBJspHandler
{
	public IBJspHandler()
	{
	}
	public static int setICJspPage(String Url, String AttributeName, String AttributeValue) throws SQLException
	{
		IBJspPage page = ((com.idega.builder.data.IBJspPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBJspPage.class)).createLegacy();
		page.setUrl(Url);
		page.setAttributeName(AttributeName);
		page.setAttributeValue(AttributeValue);
		page.insert();
		return page.getID();
	}
	public static void setJspPageInstanceID(IWContext iwc, String AttributeName, String AttributeValue) throws SQLException
	{
		IBJspHandler.ICJspHandlerVariables variables;
		Object SessionObject = iwc.getSession().getAttribute("ICJspHandlerVariables");
		String Url = iwc.getRequest().getRequestURI();
		if (SessionObject == null)
		{
			variables = (new IBJspHandler()).new ICJspHandlerVariables();
		}
		else
		{
			variables = (ICJspHandlerVariables) SessionObject;
		}
		if (variables.getUrl() != Url || variables.getAttributeName() != AttributeName || variables.getAttributeValue() != AttributeValue)
		{
			IBJspPage page =
				((com.idega.builder.data.IBJspPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBJspPage.class)).createLegacy();
			IBJspPage Pages[] =
				(IBJspPage[]) page.findAll(
					"SELECT * FROM "
						+ page.getEntityName()
						+ " WHERE "
						+ page.getUrlColumnName()
						+ " = "
						+ Url
						+ " AND "
						+ page.getAttributeNameColumnName()
						+ " = "
						+ AttributeName
						+ " AND "
						+ page.getAttributeValueColumnName()
						+ " = "
						+ AttributeValue);
			if (Pages == null)
			{
				iwc.setSessionAttribute("JspPageInstanceID", new Integer(setICJspPage(Url, AttributeName, AttributeValue)));
			}
			else
			{
				iwc.setSessionAttribute("JspPageInstanceID", new Integer(Pages[0].getID()));
			}
			variables.setUrl(Url);
			variables.setAttributeName(AttributeName);
			variables.setAttributeValue(AttributeValue);
			iwc.getSession().setAttribute("ICJspHandlerVariables", variables);
		}
	}
	public static int getJspPageInstanceID(IWUserContext iwc)
	{
		return ((Integer) iwc.getSessionAttribute("JspPageInstanceID")).intValue();
	}
	public static IBJspPage getIBJspPage(IWUserContext iwc) throws SQLException
	{
		return ((com.idega.builder.data.IBJspPageHome) com.idega.data.IDOLookup.getHomeLegacy(IBJspPage.class)).findByPrimaryKeyLegacy(
			getJspPageInstanceID(iwc));
	}
	public class ICJspHandlerVariables
	{
		String URL;
		String Attribute_name;
		String Attribute_value;
		public ICJspHandlerVariables()
		{
		}
		public String getUrl()
		{
			return this.URL;
		}
		public String getAttributeName()
		{
			return this.Attribute_name;
		}
		public String getAttributeValue()
		{
			return this.Attribute_value;
		}
		public void setUrl(String url)
		{
			this.URL = url;
		}
		public void setAttributeName(String AttributeName)
		{
			this.Attribute_name = AttributeName;
		}
		public void setAttributeValue(String AttributeValue)
		{
			this.Attribute_value = AttributeValue;
		}
	} // inner Class ICJspHandlerVariables
} // class ICJspHandler
