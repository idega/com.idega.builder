/*
 * Created on 3.5.2004
 */
package com.idega.builder.dynamicpagetrigger.business;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.idega.builder.dynamicpagetrigger.util.DPTCrawlable;
import com.idega.builder.dynamicpagetrigger.util.DPTCrawlableContainer;
import com.idega.builder.dynamicpagetrigger.util.KeyAndValue;
import com.idega.business.IBOSessionBean;
import com.idega.core.builder.business.ICDynamicPageTriggerCopySession;
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
public class DPTCopySessionBean extends IBOSessionBean implements DPTCopySession, ICDynamicPageTriggerCopySession {
	
	private boolean runningSession = false;
	
	private HashMatrix matrix = null;
	
	private boolean copyInstancePermissions = false;
	private boolean copyPagePermissions=false;
	private LinkedList subPageQueue = null;
	private LinkedList subContainer = null;
	private Object rootPagePrimaryKey = null;
	private Map pageMap = null;
	
	/**
	 * 
	 */
	public DPTCopySessionBean() {
		super();
	}
	
	
	
	public void startCopySession() throws Exception {
		if(this.runningSession) {
			throw new Exception("Not allowed to run more than one copySession at ones.");
		} else {
			this.runningSession = true;
			this.matrix = new HashMatrix();
			this.subPageQueue = new LinkedList();
			this.subContainer = new LinkedList();
			this.copyInstancePermissions = false;
			this.copyPagePermissions=false;
			pageMap = new HashMap();
		}
	}
	
	public void endCopySession() {
		if(this.runningSession) {
			this.runningSession = false;
			this.matrix = null;
			this.subPageQueue=null;
			this.pageMap = null;
			this.subContainer=null;
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
			return this.matrix.get(dataClassKey,oldValue);
		}
		return null;
	}
	
	public void setNewValue(Class dataClassKey, Object oldValue, Object newValue) {
		if(sessionIsRunnig()) {
			this.matrix.put(dataClassKey,oldValue,newValue);
		}
	}
	
	/**
	 * 
	 * @return is copySession running
	 */
	private boolean sessionIsRunnig() {
		if(!this.runningSession) {
			System.out.println("[WARNING]: trying to use "+this.getClass().getName()+" but no copySession has started.");
		}
		
		return this.runningSession;
	}
	
	

	/**
	 * @return Returns the copyInstancePermissions.
	 */
	public boolean doCopyInstancePermissions() {
		return this.copyInstancePermissions;
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
		return this.copyPagePermissions;
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
		return this.runningSession;
	}

	public void collectDPTCrawlableContainer(Object pageID, DPTCrawlableContainer con) {
		subPageQueue.addLast(new KeyAndValue(pageID,con));
//		Collection coll = con.getDPTCrawlables();
//		if (coll != null && !coll.isEmpty()) {
//			this.subContainer.add(con);
//			this.pageMap.put(con, pageID);
//		}
	}

	public void collectDPTCrawlable(Object pageID, DPTCrawlable c) {
		this.subPageQueue.addLast(new KeyAndValue(pageID,c));
	}
	
	public KeyAndValue nextCollectedDPTCrawlable() {
		return (KeyAndValue)this.subPageQueue.removeFirst();
	}
	
	public boolean hasNextCollectedDPTCrawlable() {
		return !this.subPageQueue.isEmpty();
	}
	
	public boolean hasNextCollectedDPTCrawlableContainer() {
		return !this.subContainer.isEmpty();
	}
	
	public LinkedList nextCollectionDPTCrawlableContainerObjects() {
		DPTCrawlableContainer con = (DPTCrawlableContainer) this.subContainer.removeFirst();
		Collection coll = con.getDPTCrawlables();
		String pageID = (String) pageMap.get(con);
		Iterator iter = coll.iterator();
		LinkedList l = new LinkedList();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof DPTCrawlable && !(((DPTCrawlable)o).getLinkedDPTTemplateID() == 0)) {
				l.addLast(new KeyAndValue(pageID,(DPTCrawlable) o));
			}
		}

		return l;
	}
	
	
	/**
	 * @return Returns the rootPagePrimaryKey.
	 */
	public Object getRootPagePrimaryKey() {
		return this.rootPagePrimaryKey;
	}
	/**
	 * @param rootPagePrimaryKey The rootPagePrimaryKey to set.
	 */
	public void setRootPagePrimaryKey(Object rootPagePrimaryKey) {
		this.rootPagePrimaryKey = rootPagePrimaryKey;
	}
	
	public boolean hasRootPage() {
		return this.rootPagePrimaryKey != null;
	}
}
