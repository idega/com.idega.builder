/*
 * Created on 3.5.2004
 */
package com.idega.builder.dynamicpagetrigger.business;

import com.idega.business.IBOSessionBean;
import com.idega.util.datastructures.HashMatrix;


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
public class DPTCopySessionBean extends IBOSessionBean implements DPTCopySession {
	
	private boolean runingSession = false;
	
	private HashMatrix matrix = null;
	
	
	/**
	 * 
	 */
	public DPTCopySessionBean() {
		super();
	}
	
	
	
	public void startCopySession() throws Exception {
		if(runingSession) {
			throw new Exception("Not allowed to run more than one copySession at ones.");
		} else {
			runingSession = true;
			matrix = new HashMatrix();
		}
	}
	
	public void endCopySession() {
		if(runingSession) {
			runingSession = false;
			matrix = null;
		} else {
			System.out.println("No copySession to end.  Either it has not started or already ended.");
		}
	}
	
	/**
	 * This method can be used inside of a Builderaware object e.g. to find out which existing category to use or if it should create a new one
	 * 
	 * @param dataClass
	 * @param oldValue
	 * @return Returns a stored primarykey value if the oldvalue has been stored else it returns null
	 */
	
	public Object getNewValue(Class dataClassKey, Object oldValue) {
		if(sessionIsRunnig()) {
			return matrix.get(dataClassKey,oldValue);
		}
		return null;
	}
	
	public void setNewValue(Class dataClassKey, Object oldValue, Object newValue) {
		if(sessionIsRunnig()) {
			matrix.put(dataClassKey,oldValue,newValue);
		}
	}
	
	/**
	 * 
	 * @return is copySession running
	 */
	private boolean sessionIsRunnig() {
		if(!runingSession) {
			System.out.println("[WARNING]: trying to use "+this.getClass().getName()+" but no copySession has started.");
		}
		
		return runingSession;
	}
	
	

}
