package com.idega.builder.dynamicpagetrigger.data;


/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class DPTPermissionGroupBMPBean extends com.idega.core.data.GenericGroupBMPBean implements com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroup {
//public class DPTPermissionGroupBMPBean extends com.idega.user.data.GroupBMPBean implements com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroup {
	public static final String GROUP_TYPE="dpt_permission";

  /*public DPTPermissionGroupBMPBean() {
    super();
  }

  public DPTPermissionGroupBMPBean(int id) throws SQLException{
    super(id);
  }*/

  public String getGroupTypeValue(){
    return GROUP_TYPE;
  }

  public static DPTPermissionGroup getStaticGroupInstance(){
    return (DPTPermissionGroup)getStaticInstance(DPTPermissionGroup.class);
  }



}
