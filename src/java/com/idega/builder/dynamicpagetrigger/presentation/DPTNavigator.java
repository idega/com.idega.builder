package com.idega.builder.dynamicpagetrigger.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import com.idega.builder.dynamicpagetrigger.business.DPTTriggerBusiness;
import com.idega.builder.dynamicpagetrigger.data.PageLink;
import com.idega.business.IBOLookup;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;

/**
 * Title:        IW DynamicPageTrigger
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gudmundur Agust Saemundsson</a>
 * @version 1.0
 */

public abstract class DPTNavigator extends Block {

  protected Table table = null;
  protected Table rowTemplateTable = null;
  protected DPTTriggerBusiness business = null;

  protected int columns = 1;
  protected int extraRows = 0;
  protected int iterStartIndex = 1;
  protected int linkColumn = 2;
  protected int selectedElement = 3;
  protected int minimumNumberOfRows = 8;

  protected int cellspacing = 1;
  protected int cellpadding = 2;

  protected String sebracolor1 = "#FFFFFF";
  protected String sebracolor2 = "#CCCCCC";
  protected String selectedColor = "#E9E9B7";
  protected String backgroundColor = "#333333";

  protected String width = "170";
  protected String rowHeight = "20";

  public DPTNavigator() {
    this.table = new Table();
    this.rowTemplateTable = new Table();
    this.add(this.table);
  }

  public void setColumns(int cols){
    this.rowTemplateTable.resize(cols,this.rowTemplateTable.getRows());
  }

  public void add(PresentationObject prObject, int xpos, int ypos){
    this.table.add(prObject,xpos,ypos);
  }

//  public abstract List getEntityList(IWContext iwc) throws Exception;
//
//  public abstract void initColumns(IWContext iwc) throws SQLException ;

  public void _main(IWContext iwc) throws Exception {
    if(this.getICObjectInstanceID() > 0){
      this.business = (DPTTriggerBusiness)IBOLookup.getServiceInstance(iwc,DPTTriggerBusiness.class);

      this.table.empty();
//      initColumns(iwc);

      List pLinkRecords = this.business.getPageLinkRecords(this.getICObjectInstance());

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

          this.table.resize(1,Math.max(linkList.size()+this.extraRows,this.minimumNumberOfRows));
          this.rowTemplateTable.resize(this.columns,this.rowTemplateTable.getRows());
          this.selectedElement = (linkList.size()>=this.selectedElement)?this.selectedElement:-1;
          ListIterator lIter = linkList.listIterator();
          int toAddToIndex = (this.extraRows<this.iterStartIndex)?this.iterStartIndex-1:this.extraRows;
          while (lIter.hasNext()) {
            int index = (lIter.nextIndex()+1)+toAddToIndex;
            lIter.next();
            Table tbl = (Table)this.rowTemplateTable.clone();
            this.rowTemplateTable.add(tbl,this.linkColumn,1);
            this.table.add(this.rowTemplateTable,1,index);
          }


          this.table.setColor(this.backgroundColor);
          this.table.setCellpadding(this.cellpadding);
          this.table.setCellspacing(this.cellspacing);
          this.table.setWidth(this.width);

          this.table.setHorizontalZebraColored(this.sebracolor1,this.sebracolor2);
          if(this.selectedElement > 0){
            this.table.setRowColor(this.selectedElement+toAddToIndex,this.selectedColor);
          }

          for (int i = 1; i <= this.table.getRows(); i++) {
            this.table.setHeight(i,this.rowHeight);
          }


        }

      }

    } else {
      throw new Exception("Block has no ICObjectInstanceId");
    }

    super._main(iwc);
  }




}

