/*
 * $Id: DomainTree.java,v 1.5 2007/04/27 14:59:45 eiki Exp $
 * Created on 26.5.2006 in project com.idega.builder
 *
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;

import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.builder.data.IBStartPage;
import com.idega.builder.data.IBStartPageHome;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.data.DefaultTreeNode;
import com.idega.core.data.ICTreeNode;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;


/**
 * <p>
 * Cache for each domain and its page and templates tree
 * </p>
 *  Last modified: $Date: 2007/04/27 14:59:45 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.5 $
 */
public class DomainTree extends DefaultTreeNode {
	
	private static final String PAGES_ID = "pages";
	private static final String TEMPLATES_ID = "templates";
	private ICDomain domain;
	private static String DOMAIN_TREE_KEY="ic_domain_tree_";
	
	private final int PAGEVIEWER = 0;
	private final int TEMPLATEVIEWER = 1;

	public DomainTree(ICDomain domain){
		this.domain=domain;
	}
	
	public static DomainTree getDomainTree(IWApplicationContext iwac){
		ICDomain domain = BuilderLogic.getInstance().getCurrentDomain();
		String domainKey = DOMAIN_TREE_KEY+domain.getPrimaryKey();
		DomainTree node = (DomainTree) iwac.getApplicationAttribute(domainKey);
		if(node==null){
			node = new DomainTree(domain);
			node.initialize(iwac);
			iwac.setApplicationAttribute(domainKey, node);
		}
		return node;
	}
	
	public static void clearCache(IWApplicationContext iwac){
		ICDomain domain = iwac.getDomain();
		String domainKey = DOMAIN_TREE_KEY+domain.getPrimaryKey();
		iwac.removeApplicationAttribute(domainKey);
	}
	
	/**
	 * <p>
	 * TODO tryggvil describe method initialize
	 * </p>
	 * @param iwac
	 */
	private void initialize(IWApplicationContext iwac) {
		initialize(iwac, this.PAGEVIEWER);
		initialize(iwac, this.TEMPLATEVIEWER);
	}

	/**
	 * <p>
	 * TODO tryggvil describe method initialize
	 * </p>
	 * @param iwac
	 */
	private void initialize(IWApplicationContext iwc,int type) {
			int id = -1;
			ICTreeNode parent = null;
			if (type == this.PAGEVIEWER) {
				parent = getPagesNode();
				id = this.domain.getStartPageID();
			}
			else {
				parent = getTemplatesNode();
				id = this.domain.getStartTemplateID();
			}
			PageTreeNode startNode = new PageTreeNode(id, iwc);
//
//			startNode.setOrder(order)
//
			parent.getChildren().add(startNode);
			try {
				java.util.Collection coll = null;
				if (type == this.PAGEVIEWER) {
					coll = getStartPages(this.domain);
				}
				else {
					coll = getTemplateStartPages(this.domain);
				}
				java.util.Iterator it = coll.iterator();
				//int order = 0;
				while (it.hasNext()) {
					//order++;
					com.idega.builder.data.IBStartPage startPage = (com.idega.builder.data.IBStartPage) it.next();
					if (startPage.getPageId() != id) {
						PageTreeNode node = new PageTreeNode(startPage.getPageId(), iwc);
//						node.setOrder(order);
						parent.getChildren().add(node);
//						parent.getChildren().
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				//return null;
			}

	}

	private Collection getStartPages(ICDomain domain) throws IDOLookupException, FinderException {
		return ((IBStartPageHome) IDOLookup.getHome(IBStartPage.class)).findAllPagesByDomain(((Integer) domain.getPrimaryKey()).intValue());
	}
	
	private Collection getTemplateStartPages(ICDomain domain) throws IDOLookupException, FinderException {
		return ((IBStartPageHome) IDOLookup.getHome(IBStartPage.class)).findAllTemplatesByDomain(((Integer) domain.getPrimaryKey()).intValue());
	}	
	
	public ICTreeNode getPagesNode(){
		return getSubNode("Pages",PAGES_ID);
	}
	
	public ICTreeNode getTemplatesNode(){
		return getSubNode("Templates",TEMPLATES_ID);	
	}
	
	protected ICTreeNode getSubNode(String nodeName,String id){
		DefaultTreeNode pagesNode = null;
		Collection children = getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			ICTreeNode child = (ICTreeNode) iter.next();
			if(child.getId().equals(id)){
				return child;
			}
		}
		if(pagesNode==null){
			pagesNode = new DefaultTreeNode(nodeName,id);
			addTreeNode(pagesNode);
		}
		return pagesNode;
	}
	
}