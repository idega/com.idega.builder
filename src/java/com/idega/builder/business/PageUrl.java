/*
 * $Id: PageUrl.java,v 1.1 2005/03/01 23:25:03 tryggvil Exp $
 * Created on 24.2.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.builder.business;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.util.StringHandler;


/**
 *  <p>
 *  Class for setting and manipulating generated URLs for builder pages
 *  <p>
 *  Last modified: $Date: 2005/03/01 23:25:03 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class PageUrl {
	
	ICPage page;
	ICPage parentPage;
	//Stack stack;
	String pageName;
	String SLASH = StringHandler.SLASH;
	int domainId=-1;
	
	public PageUrl(ICPage page,int domainId){
		this.page=page;
		this.domainId=domainId;
	}
	
	/**
	 * Create a new PageUri for a new Page whos parent is parentPage and name is pageName
	 * @param parentPage
	 * @param pageName
	 */
	public PageUrl(ICPage parentPage,String pageName,int domainId){
		this.parentPage=parentPage;
		this.pageName=pageName;
		this.domainId=domainId;
	}
	

	/**
	 * Create a new PageUri for a new Page whos name is pageName, in this case the page has no parent and is presumed to be a top level page.
	 * @param parentPage
	 * @param pageName
	 */
	public PageUrl(String pageName){
		this.pageName=pageName;
	}
	
	public String getGeneratedUrlFromName(){
		List list = getUrlPartList();
		Iterator iter = list.iterator();
		String url=SLASH;
		
		while(iter.hasNext()){
			PageNamePart part = (PageNamePart)iter.next();
			if(part.isStartPage){
				//nothing done because the starpage should have url="/";
			}
			else{
				if(part.url==null){
					url=SLASH+part.urlPartFromName+url;
				}
				else{
					url=SLASH+part.url+url;
					break;
				}
			}
		}
		String parsedUrl = StringHandler.removeMultipleSlashes(url);
		String newUrl = getStringCheckedWithExistingUrls(parsedUrl);
		return newUrl;
	}
	
	protected List getUrlPartList(){
		//if(stack!=null){
		//	stack=new Stack();
		//}
		List l = new ArrayList();
		
		ICPage ppage = null;
		if(page!=null){
			ppage=page;
		}
		else if(parentPage!=null){
			ppage=parentPage;
		}
		if(pageName!=null){
			PageNamePart part =  new PageNamePart(pageName);
			l.add(part);
		}

		while(ppage!=null){
			PageNamePart part =  getParsedName(ppage);
			l.add(part);
			ppage=(ICPage)ppage.getParentNode();
		}
		return l;
	}
	
	protected PageNamePart getParsedName(ICPage page){
		PageNamePart part = new PageNamePart(page);
		return part;
	}
	
	protected BuilderLogic getBuilderLogic(){
		return BuilderLogic.getInstance();
	}
	
	private class PageNamePart{
		boolean isStartPage=false;
		String url;
		String urlPartFromName;
		PageNamePart(ICPage page){
			String pageName = page.getName();
			if(getBuilderLogic().getCurrentDomain().getStartPage().equals(page)){
				isStartPage=true;
			}
			url = page.getDefaultPageURI();
			if(pageName!=null){
				urlPartFromName = getUrlPartFromName(pageName);
			}
		}
		PageNamePart(String pageName){
			isStartPage=false;
			urlPartFromName=getUrlPartFromName(pageName);
		}
	}
	
	
	protected String getUrlPartFromName(String name){
		return StringHandler.convertToUrlFriendly(name);
	}
	
	
	/**
	 * Checks if the url exists already for other pages and adds a suffix if it is.
	 */
	protected String getStringCheckedWithExistingUrls(String inputUrl){
		//check the url:
		int index=1;
		String url=inputUrl;
		while(doesUrlExist(url)){
			//ad a number to the end
			if(inputUrl.endsWith(SLASH)){
				url=inputUrl.substring(0,inputUrl.length()-1)+index+SLASH;
			}
			else{
				url=inputUrl+index+SLASH;
			}
			index++;
		}
		return url;
	}
	
	
	protected boolean doesUrlExist(String url){
		try {
			ICPage page = getICPageHome().findByUri(url,domainId);
			if(page!=null){
				return true;
			}
		}
		catch (FinderException e) {
			return false;
		}
		return false;
	}
	
	
	protected ICPageHome getICPageHome(){
		try {
			return (ICPageHome) IDOLookup.getHome(ICPage.class);
		}
		catch (IDOLookupException e) {
			throw new RuntimeException(e);
		}
	}
	
}
