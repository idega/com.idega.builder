package com.idega.builder.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * A bean to store state of AdminToolbar module.
 * 
 * Last modified: $Date: 2008/11/17 08:42:45 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.1 $
 */
@Scope("session")
@Service("adminToolbarSession")
public class AdminToolbarSessionBean implements AdminToolbarSession {

	private String mode;
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}