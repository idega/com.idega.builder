package com.idega.builder.dynamicpagetrigger.data;

import com.idega.core.data.GenericGroup;
import java.sql.*;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class DPTPermissionGroupBMPBean extends com.idega.user.data.GroupBMPBean implements com.idega.builder.dynamicpagetrigger.data.DPTPermissionGroup {

  public DPTPermissionGroupBMPBean() {
    super();
  }

  public DPTPermissionGroupBMPBean(int id) throws SQLException{
    super(id);
  }

  public String getGroupTypeValue(){
    return "dpt_permission";
  }

  public static DPTPermissionGroup getStaticGroupInstance(){
    return (DPTPermissionGroup)getStaticInstance(DPTPermissionGroup.class);
  }



}
