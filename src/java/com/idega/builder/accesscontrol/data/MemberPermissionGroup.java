package com.idega.builder.accesscontrol.data;

import com.idega.data.genericentity.Group;
import java.sql.*;

/**
 * Title:        AccessControl
 * Description:
 * Copyright:    Copyright (c) 2001 idega.is All Rights Reserved
 * Company:      idega margmiðlun
 * @author
 * @version 1.0
 */

public class MemberPermissionGroup extends Group {


  public MemberPermissionGroup() {
    super();
  }

  public MemberPermissionGroup(int id) throws SQLException{
    super(id);
  }

  public String getGroupTypeValue(){
    return "member_permission";
  }

  public static String getClassName(){
    return "com.idega.builder.accesscontrol.data.MemberPermissionGroup";
  }

  public static MemberPermissionGroup getStaticMemberPermissionGroupInstance(){
    return (MemberPermissionGroup)getStaticInstance();
  }

} // MemberPermissionGroup