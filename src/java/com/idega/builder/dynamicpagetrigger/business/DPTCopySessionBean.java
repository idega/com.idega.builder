/*
 * Created on 3.5.2004
 */
package com.idega.builder.dynamicpagetrigger.business;

import java.util.LinkedList;

import com.idega.builder.dynamicpagetrigger.util.DPTCrawlable;
import com.idega.builder.dynamicpagetrigger.util.KeyAndValue;
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
	
	private boolean runningSession = false;
	
	private HashMatrix matrix = null;
	
	private boolean copyInstancePermissions = false;
	private boolean copyPagePermissions=false;
	private LinkedList subPageQueue = null;
	
	
	/**
	 * 
	 */
	public DPTCopySessionBean() {
		super();
	}
	
	
	
	public void startCopySession() throws Exception {
		if(runningSession) {
			throw new Exception("Not allowed to run more than one copySession at ones.");
		} else {
			runningSession = true;
			matrix = new HashMatrix();
			subPageQueue = new LinkedList();
			copyInstancePermissions = false;
			copyPagePermissions=false;
		}
	}
	
	public void endCopySession() {
		if(runningSession) {
			runningSession = false;
			matrix = null;
			subPageQueue=null;
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
		if(!runningSession) {
			System.out.println("[WARNING]: trying to use "+this.getClass().getName()+" but no copySession has started.");
		}
		
		return runningSession;
	}
	
	

	/**
	 * @return Returns the copyInstancePermissions.
	 */
	public boolean doCopyInstancePermissions() {
		return copyInstancePermissions;
	}
	/**
	 * @param copyInstancePermissions The copyInstancePermissions to set.
	 */
	public void setToCopyInstancePermissions(boolean copyInstancePermissions) {
		this.copyInstancePermissions = copyInstancePermissions;
	}
	/**
	 * @return Returns the copyPagePermissions.
	 */
	public boolean doCopyPagePermissions() {
		return copyPagePermissions;
	}
	/**
	 * @param copyPagePermissions The copyPagePermissions to set.
	 */
	public void setToCopyPagePermissions(boolean copyPagePermissions) {
		this.copyPagePermissions = copyPagePermissions;
	}
	/**
	 * @return Returns the runningSession.
	 */
	public boolean isRunningSession() {
		return runningSession;
	}
	
	public void collectDPTCrawlable(Object pageID, DPTCrawlable c) {
		subPageQueue.addLast(new KeyAndValue(pageID,c));
	}
	
	public KeyAndValue nextCollectedDPTCrawlable() {
		return (KeyAndValue)subPageQueue.removeFirst();
	}
	
	public boolean hasNextCollectedDPTCrawlable() {
		return !subPageQueue.isEmpty();
	}
	
	
}
