/*
 * $Id: IBPageHandler.java,v 1.4 2002/04/06 19:07:39 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;

import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.presentation.GroupChooser;
import com.idega.user.presentation.GroupTreeNode;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class GroupHandler implements PropertyHandler {
  /**
   *
   */
  public GroupHandler() {
  }

  /**
   *
   */
  public List getDefaultHandlerTypes() {
    return(null);
  }

  /**
   *
   */
  public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
    GroupChooser chooser = new GroupChooser(name);
    try {
      if (value != null && !value.equals("")) {
      	Group group = getGroupHome().findByPrimaryKey(new Integer(value));
		 		GroupTreeNode node = new GroupTreeNode(group,iwc.getApplicationContext());
			  if (node != null)
			    chooser.setSelectedNode(node);
      }
    }
    catch(NumberFormatException e) {
    }
    catch(Exception ex){
    }
    return(chooser);
  }
  
  private GroupHome getGroupHome() throws java.rmi.RemoteException {
    return ((GroupHome)com.idega.data.IDOLookup.getHome(Group.class));
  }


  /**
   *
   */
  public void onUpdate(String values[], IWContext iwc) {
  }
}
