package com.idega.builder.data;

import com.idega.data.*;

import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;
import com.idega.builder.business.BuilderLogic;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public class IBDomain extends GenericEntity {

  public static final String tableName = "ib_domain";
  public static final String domain_name = "domain_name";
  public static final String domain_url = "url";
  public static final String start_page = "start_ib_page_id";

  private static Map cachedDomains;

  public IBDomain() {
    super();
  }

  private IBDomain(int id)throws java.sql.SQLException{
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(getIDColumnName());
    this.addAttribute(getColumnDomainName(),"Domain name",true,true,String.class);
    this.addAttribute(getColumnURL(),"Domain URL",true,true,String.class,1000);
    this.addAttribute(getColumnStartPage(),"Start Page",true,true,Integer.class,"many-to-one",IBPage.class);
  }

  public static IBDomain getDomain(int id)throws SQLException{
    IBDomain theReturn;
    theReturn = (IBDomain)getDomainsMap().get(new Integer(id));
    if(theReturn == null){
      theReturn = new IBDomain(id);
      if(theReturn!=null){
        getDomainsMap().put(new Integer(id),theReturn);
      }
    }
    return theReturn;
  }

  private static Map getDomainsMap(){
    if(cachedDomains==null){
      cachedDomains = new HashMap();
    }
    return cachedDomains;
  }

  public void insertStartData() throws Exception {
    BuilderLogic instance = BuilderLogic.getInstance();
    IBDomain domain = new IBDomain();
    domain.setName("Default Site");

    IBPage page = new IBPage();
    page.setName("Web root");
    page.setType(IBPage.PAGE);
    page.insert();

    instance.unlockRegion(Integer.toString(page.getID()),"-1");

    domain.setIBPage(page);
    domain.insert();

    IBPage page2 = new IBPage();
    page2.setName("Default Template");
    page2.setType(IBPage.TEMPLATE);
    page2.insert();

    page.setTemplateId(page2.getID());
    page.update();

    instance.setTemplateId(Integer.toString(page.getID()),Integer.toString(page2.getID()));
    instance.getIBXMLPage(page2.getID()).addUsingTemplate(Integer.toString(page.getID()));
  }

  public String getEntityName(){
    return tableName;
  }

  public static String getColumnDomainName(){
    return domain_name;
  }

  public static String getColumnURL(){
    return domain_url;
  }

  public static String getColumnStartPage(){
    return start_page;
  }

  public IBPage getStartPage(){
    return (IBPage)this.getColumnValue(getColumnStartPage());
  }

  public int getStartPageID(){
    return getIntColumnValue(getColumnStartPage());
  }

  public String getName(){
    return getDomainName();
  }

  public String getDomainName(){
    return getStringColumnValue(getColumnDomainName());
  }

  public String getURL(){
    return getStringColumnValue(getColumnURL());
  }


  public void setIBPage(IBPage page){
     this.setColumn(getColumnStartPage(),page);
  }

  public void setName(String name){
    this.setColumn(getColumnDomainName(),name);
  }
}