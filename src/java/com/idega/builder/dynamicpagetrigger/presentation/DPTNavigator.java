package com.idega.builder.dynamicpagetrigger.presentation;

import com.idega.presentation.Block;
import com.idega.presentation.Table;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import is.idega.idegaweb.project.business.ProjectBusiness;
import is.idega.idegaweb.project.data.IPProject;
import com.idega.data.GenericEntity;
import com.idega.builder.dynamicpagetrigger.business.DPTTriggerBusiness;
import com.idega.builder.dynamicpagetrigger.data.PageLink;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Title:        IW DynamicPageTrigger
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
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
    table = new Table();
    rowTemplateTable = new Table();
    this.add(table);
  }

  public void setColumns(int cols){
    rowTemplateTable.resize(cols,rowTemplateTable.getRows());
  }

  public void add(PresentationObject prObject, int xpos, int ypos){
    table.add(prObject,xpos,ypos);
  }

//  public abstract List getEntityList(IWContext iwc) throws Exception;
//
//  public abstract void initColumns(IWContext iwc) throws SQLException ;

  public void _main(IWContext iwc) throws Exception {
    if(this.getICObjectInstanceID() > 0){
      business = DPTTriggerBusiness.getInstance();

      table.empty();
//      initColumns(iwc);

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

          table.resize(1,Math.max(linkList.size()+extraRows,minimumNumberOfRows));
          rowTemplateTable.resize(columns,rowTemplateTable.getRows());
          this.selectedElement = (linkList.size()>=selectedElement)?selectedElement:-1;
          ListIterator lIter = linkList.listIterator();
          int toAddToIndex = (extraRows<iterStartIndex)?iterStartIndex-1:extraRows;
          while (lIter.hasNext()) {
            int index = (lIter.nextIndex()+1)+toAddToIndex;
            Link lItem = (Link)lIter.next();
            Table tbl = (Table)rowTemplateTable.clone();
            rowTemplateTable.add(tbl,linkColumn,1);
            table.add(rowTemplateTable,1,index);
          }


          table.setColor(this.backgroundColor);
          table.setCellpadding(this.cellpadding);
          table.setCellspacing(this.cellspacing);
          table.setWidth(this.width);

          table.setHorizontalZebraColored(this.sebracolor1,this.sebracolor2);
          if(selectedElement > 0){
            table.setRowColor(this.selectedElement+toAddToIndex,this.selectedColor);
          }

          for (int i = 1; i <= table.getRows(); i++) {
            table.setHeight(i,rowHeight);
          }


        }

      }

    } else {
      throw new Exception("Block has no ICObjectInstanceId");
    }

    super._main(iwc);
  }




}

