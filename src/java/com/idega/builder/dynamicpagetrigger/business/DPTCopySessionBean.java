/*
 * Created on 3.5.2004
 */
package com.idega.builder.dynamicpagetrigger.business;

import com.idega.business.IBOSessionBean;


/**
 * Title: DPTCopySession
 * <p>
 * Description: This class is used to store temporarly some data such as categoryIDs, when copying a page tree, to be able to use the same id more than ones in a pagetree and not creating a new instance each time it occurs 
 * <p>
 * Copyright: Copyright (c) 2004
 * Company: idega Software
 * @author 2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version 1.0
 */
public class DPTCopySessionBean extends IBOSessionBean {

	/**
	 * 
	 */
	public DPTCopySessionBean() {
		super();
	}
	
	
	
	public void startCopySession(String sessionKey) {
		
	}
	
	public void endCopySession(String sessionKey) {
		
	}
	
	/**
	 * 
	 * @param sessionKey some key generated in DPTTriggerBusiness or passed into it.  Used to prevent mixing data when possiby generating more than one tree at a time.
	 * @param dataClass
	 * @param oldValue
	 * @return Returns a stored primarykey value if the oldvalue has been stored else it returns null
	 */
	
	public String getNewValue(String sessionKey,Class dataClass, String oldValue) {
		
		
		return null;
	}
	
	public void setNewValue(String sessionKey, Class dataclass, String oldValue, String newValue) {
		
	}
	
	

}
