package com.idega.builder.dynamicpagetrigger.data;

import java.sql.SQLException;

import com.idega.core.builder.data.ICDomain;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class DPTDomainBMPBean extends com.idega.data.GenericEntity implements com.idega.builder.dynamicpagetrigger.data.DPTDomain {

  public final static String COLUMN_DPTDOMAIN_NAME = "dpt_domain_name";
  public final static String COLUMN_IBDOMAIN_ID = "ib_domain_id";
  public final static String COLUMN_IBPAGE_ID = "ib_page_id";
  public final static String COLUMN_DPT_PAGE_TYPE = "dpt_page_type";

  public DPTDomainBMPBean() {
    super();
  }

  public DPTDomainBMPBean(int id) throws SQLException {
    super(id);
  }

  public void initializeAttributes() {
    this.addAttribute(this.getIDColumnName());
    this.addAttribute(COLUMN_DPTDOMAIN_NAME,"domain name",true,true,String.class,255);
    this.addAttribute(COLUMN_IBDOMAIN_ID,"ib_domain_id",true,true,Integer.class,MANY_TO_ONE,ICDomain.class);
    this.addAttribute(COLUMN_IBPAGE_ID,"ib_page_id",true,true,Integer.class,MANY_TO_ONE,ICDomain.class);
    this.addAttribute(COLUMN_DPT_PAGE_TYPE,"dpt_page_type",true,true,String.class,100);
  }

  public String getEntityName() {
    return "dpt_domain";
  }


  public void setIBDomainID(int id){
    this.setColumn(COLUMN_IBDOMAIN_ID,id);
  }

  public void setIBPageID(int id){
    this.setColumn(COLUMN_IBPAGE_ID,id);
  }

  public void setDPTPageType(String type){
    this.setColumn(COLUMN_DPT_PAGE_TYPE,type);
  }

  public int getIBDomainID(){
    return this.getIntColumnValue(COLUMN_IBDOMAIN_ID);
  }

  public int getIBPageID(){
    return this.getIntColumnValue(COLUMN_IBPAGE_ID);
  }

  public String getDPTPageType(){
    return this.getStringColumnValue(COLUMN_DPT_PAGE_TYPE);
  }




}
