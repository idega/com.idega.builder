package com.idega.builder.dynamicpagetrigger.presentation;

import com.idega.presentation.Table;
import com.idega.presentation.Block;
import com.idega.builder.dynamicpagetrigger.business.DPTTriggerBusiness;
import com.idega.builder.dynamicpagetrigger.data.PageLink;
import com.idega.presentation.text.Link;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
//import com.idega.builder.business.BuilderLogic;

import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.util.ListIterator;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class DPTNavigation extends Block {

  Table linkTable;
  DPTTriggerBusiness business;

  public DPTNavigation() {
    linkTable = new Table();
    business = new DPTTriggerBusiness();
  }



  public void main(IWContext iwc) throws Exception {
    this.empty();
    this.add(linkTable);
    if(this.getICObjectInstanceID() > 0){

      List pLinkRecords = business.getPageLinkRecords(this.getICObjectInstance());

      if(pLinkRecords != null){
        List linkList = new Vector();
        Iterator iter = pLinkRecords.iterator();
        while (iter.hasNext()) {
          PageLink item = (PageLink)iter.next();

          Link aLink = new Link(item.getDefaultLinkText());
          aLink.setPage(item.getPageId());

          if(item.getLinkImageId() > 0){
            Image image = new Image(item.getLinkImageId());
            if(item.getOnMouseOverImageId() > 0){
              image.setOverImage(new Image(item.getOnMouseOverImageId()));
            }
            if(item.getOnClickImageId() > 0){
              image.setOnClickImage(new Image(item.getOnClickImageId()));
            }
            aLink.setObject(image);
          }

          linkList.add(aLink);
        }

        if(linkList.size() > 0){

          linkTable.resize(1,linkList.size());

          ListIterator lIter = linkList.listIterator();
          while (lIter.hasNext()) {
            int index = lIter.nextIndex();
            Link lItem = (Link)lIter.next();
            linkTable.add(lItem,1,index);
          }

        }

      }

    } else {
      throw new Exception("Block has no ICObjectInstanceId");
    }
  }
}
