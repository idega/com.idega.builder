package com.idega.builder.presentation;

import com.idega.idegaweb.*;
import com.idega.builder.business.BuilderLogic;
import com.idega.util.text.TextSoap;
import com.idega.presentation.text.Link;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.util.IWColor;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import com.idega.util.text.StyleConstants;
import com.idega.presentation.text.Text;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class IBColorChooserWindow extends AbstractChooserWindow {

private String _colorString;

  public IBColorChooserWindow() {
    this.setWidth(340);
    this.setHeight(260);
    this.setScrollbar(false);
  }

  public void displaySelection(IWContext iwc) {
    addTitle("Color chooser",IWConstants.BUILDER_FONT_STYLE_TITLE);
    getParameters(iwc);

    if ( iwc.isParameterSet("add_remove") ) {
      if ( iwc.isParameterSet("addToPalette") )
	doBusiness(iwc,false);
      if ( iwc.isParameterSet("removeFromPalette") )
	doBusiness(iwc,true);
    }

    if ( iwc.isParameterSet("submit") ) {
      getParentPage().setOnLoad(SELECT_FUNCTION_NAME+"('"+_colorString+"','"+_colorString+"')");
    }
    else {
      add(drawForm(iwc));
    }
  }

  private Form drawForm(IWContext iwc) {
    IWResourceBundle iwrb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER).getResourceBundle(iwc);

    IWColor color = new IWColor(0,0,0);
    IWColor iwColor = null;
    try {
      iwColor = new IWColor(IWColor.getIntFromHex(TextSoap.findAndCut(_colorString,"#")));
    }
    catch (Exception e) {
      iwColor = null;
    }

    Form form = new Form();
      form.setMethod("get");
    Table formTable = new Table();
      formTable.setCellpadding(5);
      formTable.setCellspacing(0);
      formTable.setHeight("100%");
    int row = 1;
    int column = 1;

    if ( iwc.isParameterSet("from_editor") ) {
      form.maintainParameter("from_editor");
    }

    Image image = formTable.getTransparentCell(iwc);
      image.setWidth(10);
      image.setHeight(10);

    Table paletteTable = getColorPalette(iwc);
    if ( paletteTable != null ) {
      Text custom = new Text("Custom colors:");
      custom.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
      formTable.add(custom,column,row);
      formTable.add(Text.getBreak(),column,row);
      formTable.add(paletteTable,column,row);
      formTable.add(Text.getBreak(),column,row);
    }

    Text webPalette = new Text("Web safe colors:");
      webPalette.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);

    Table table = new Table();
    if ( iwc.getApplicationAttribute("color_palette_table") == null ) {
      table.setRows(12);
      table.setColumns(18);
      table.setCellpadding(0);
      table.setCellspacing(1);
      table.setColor(color.getHexColorString());

      Link link = null;

      int R = 0;
      int G = 0;
      int B = 0;

      for ( int a = 1; a <= 12; a++ ) {
	for ( int b = 1; b <= 18; b++ ) {
	  if ( a <= 6 && b <= 6 )
	    R = 0;
	  else if ( a <= 6 && b > 6 && b <= 12 )
	    R = 51;
	  else if ( a <= 6 && b > 12 )
	    R = 102;
	  else if ( a > 6 && b <= 6 )
	    R = 153;
	  else if ( a > 6 && b > 6 && b <= 12 )
	    R = 204;
	  else if ( a > 6 && b > 12 )
	    R = 255;

	  color = new IWColor(R,G,B);

	  link = new Link(image);
	  table.add(link,b,a);
	    link.addParameter("color",color.getHexColorString());
	  table.setColor(b,a,color.getHexColorString());

	  G += 51;
	  if ( G > 255 ) G = 0;
	}
	B += 51;
	if ( B > 255 ) B = 0;
      }
      iwc.setApplicationAttribute("color_palette_table",table);
    }
    else
      table = (Table) iwc.getApplicationAttribute("color_palette_table");

    Block block = new Block();
      block.add(table);
      block.setCacheable("web_color_palette",0);

    formTable.add(webPalette,column,row);
    formTable.add(Text.getBreak(),column,row);
    formTable.add(block,column,row);

    row = 1;
    column = 2;

    Text hexText = new Text("Hex color:");
      hexText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    TextInput hexInput = new TextInput("color");
      hexInput.setLength(7);
      hexInput.setMaxlength(7);
      hexInput.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
      if ( _colorString != null )
	hexInput.setContent(_colorString);

    formTable.add(hexText,column,row);
    formTable.add(Text.getNonBrakingSpace(2),column,row);
    formTable.add(hexInput,column,row++);

    Text rgbText = new Text("RGB color:");
      rgbText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);
    TextInput redInput = new TextInput("red");
      redInput.setLength(3);
      redInput.setMaxlength(3);
      redInput.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
      if ( iwColor != null )
	redInput.setContent(Integer.toString(iwColor.getRed()));
    TextInput greenInput = new TextInput("green");
      greenInput.setLength(3);
      greenInput.setMaxlength(3);
      greenInput.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
      if ( iwColor != null )
	greenInput.setContent(Integer.toString(iwColor.getGreen()));
    TextInput blueInput = new TextInput("blue");
      blueInput.setLength(3);
      blueInput.setMaxlength(3);
      blueInput.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_INTERFACE);
      if ( iwColor != null )
	blueInput.setContent(Integer.toString(iwColor.getBlue()));

    formTable.add(rgbText,column,row);
    formTable.add(Text.getBreak(),column,row);
    formTable.add(redInput,column,row);
    formTable.add(greenInput,column,row);
    formTable.add(blueInput,column,row++);

    Table inputTable = new Table(3,1);
      inputTable.setCellpadding(0);
      inputTable.setCellspacing(0);
      inputTable.setWidth(2,"3");
      inputTable.setColumnVerticalAlignment(1,Table.VERTICAL_ALIGN_TOP);
      inputTable.setColumnAlignment(1,Table.HORIZONTAL_ALIGN_RIGHT);
      if ( _colorString != null )
	inputTable.setColor(3,1,_colorString);

    Text previewText = new Text("Preview:");
      previewText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_LARGE);

    Image previewImage = (Image) image.clone();
      previewImage.setWidth(50);
      previewImage.setHeight(50);
      previewImage.setBorder(1);

    inputTable.add(previewText,1,1);
    inputTable.add(previewImage,3,1);

    formTable.add(inputTable,column,row++);

    Text removeText = new Text("Remove custom color");
      removeText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
    CheckBox removeFromPalette = new CheckBox("removeFromPalette");

    Text paletteText = new Text("Add to custom colors");
      paletteText.setFontStyle(IWConstants.BUILDER_FONT_STYLE_SMALL);
    CheckBox addToPalette = new CheckBox("addToPalette");

    boolean isInCustom = isInCustomColors(iwc,_colorString);
    if ( _colorString != null && isInCustom ) {
      formTable.add(removeFromPalette,column,row);
      formTable.add(removeText,column,row);
      formTable.add(Text.getBreak(),column,row);
      formTable.add(new SubmitButton(iwrb.getLocalizedImageButton("remove","Remove"),"add_remove"),column,row++);
    }
    else {
      formTable.add(addToPalette,column,row);
      formTable.add(paletteText,column,row);
      formTable.add(Text.getBreak(),column,row);
      formTable.add(new SubmitButton(iwrb.getLocalizedImageButton("add","Add"),"add_remove"),column,row++);
    }

    if ( iwc.getSessionAttribute(SCRIPT_PREFIX_PARAMETER) != null ) {
      form.add(new HiddenInput(SCRIPT_PREFIX_PARAMETER,(String)iwc.getSessionAttribute(SCRIPT_PREFIX_PARAMETER)));
      form.add(new HiddenInput(SCRIPT_SUFFIX_PARAMETER,(String)iwc.getSessionAttribute(SCRIPT_SUFFIX_PARAMETER)));
      form.add(new HiddenInput(DISPLAYSTRING_PARAMETER_NAME,(String)iwc.getSessionAttribute(DISPLAYSTRING_PARAMETER_NAME)));
      form.add(new HiddenInput(VALUE_PARAMETER_NAME,(String)iwc.getSessionAttribute(VALUE_PARAMETER_NAME)));
    }

    if ( _colorString != null )
      form.add(new HiddenInput("old_color",_colorString));

    formTable.setColumnVerticalAlignment(1,Table.VERTICAL_ALIGN_TOP);
    formTable.setColumnVerticalAlignment(2,Table.VERTICAL_ALIGN_TOP);
    formTable.setHeight(column,row,"100%");
    formTable.setVerticalAlignment(column,row,Table.VERTICAL_ALIGN_BOTTOM);
    formTable.mergeCells(1,1,1,row);
    formTable.setAlignment(column,row,Table.HORIZONTAL_ALIGN_RIGHT);
    formTable.add(new SubmitButton(iwrb.getLocalizedImageButton("preview","Preview"),"preview"),column,row);
    formTable.add(Text.getNonBrakingSpace(),column,row);
    SubmitButton submit = new SubmitButton(iwrb.getLocalizedImageButton("submit","Submit"),"submit");
    if ( iwc.isParameterSet("from_editor") )
      submit.setOnClick("javascript:save()");
    formTable.add(submit,column,row);

    form.add(formTable);
    return(form);
  }

  private Table getColorPalette(IWContext iwc) {
    IWPropertyList list = iwc.getApplication().getSettings().getIWPropertyList("color_palette");
    if ( list != null ) {
      Table table = new Table();
	table.setCellpadding(0);
	table.setCellspacing(1);
	table.setColor("#000000");
      Image image = table.getTransparentCell(iwc);
	image.setWidth(10);
	image.setHeight(10);
      Link link = null;
      int column = 1;
      int row = 1;

      Iterator iter = list.iterator();
      if ( iter != null ) {
	boolean hasValues = false;
	while (iter.hasNext()) {
	  hasValues = true;
	  String color = ((IWProperty) iter.next()).getValue();
	  table.setColor(column,row,color);
	  link = new Link(image);
	  link.addParameter("color",color);
	  link.maintainParameter(SCRIPT_PREFIX_PARAMETER,iwc);
	  link.maintainParameter(SCRIPT_SUFFIX_PARAMETER,iwc);
	  link.maintainParameter(DISPLAYSTRING_PARAMETER_NAME,iwc);
	  link.maintainParameter(VALUE_PARAMETER_NAME,iwc);
	  table.add(link,column,row);

	  column++;
	  if ( column > 18 ) {
	    column = 1;
	    row++;
	  }
	}
	if ( column != 1 && row > 1 ) {
	  table.mergeCells(column,row,table.getColumns(),row);
	  table.setColor(column,row,"#FFFFFF");
	}
	if ( hasValues )
	  return table;
	return null;
      }
      return null;
    }
    return null;
  }

  private void getParameters(IWContext iwc) {
    IWColor color = null;
    String oldColor = null;
    if ( iwc.isParameterSet("old_color") && iwc.getParameter("old_color").length() > 0 ) {
      oldColor = iwc.getParameter("old_color");
    }

    if ( iwc.isParameterSet("color") && iwc.getParameter("color").length() > 0 ) {
      _colorString = iwc.getParameter("color");
    }
    if ( _colorString != null && _colorString.indexOf("#") == -1 )
      _colorString = "#" + _colorString;

    if ( iwc.isParameterSet("red") && iwc.isParameterSet("green") && iwc.isParameterSet("blue") ) {
      try {
	color = new IWColor(Integer.parseInt(iwc.getParameter("red")),Integer.parseInt(iwc.getParameter("green")),Integer.parseInt(iwc.getParameter("blue")));
	if ( _colorString == null )
	  _colorString = color.getHexColorString();
      }
      catch (Exception e) {
	color = null;
      }
    }

    if ( oldColor != null && color != null ) {
      if ( oldColor.equalsIgnoreCase(_colorString) && color.getHexColorString() != oldColor )
	_colorString = color.getHexColorString();
    }

    if ( iwc.isParameterSet("bgcolor") ) {
      _colorString = "bgcolor";
    }

    if ( iwc.isParameterSet(SCRIPT_PREFIX_PARAMETER) ) {
      iwc.setSessionAttribute(SCRIPT_PREFIX_PARAMETER,iwc.getParameter(SCRIPT_PREFIX_PARAMETER));
      iwc.setSessionAttribute(SCRIPT_SUFFIX_PARAMETER,iwc.getParameter(SCRIPT_SUFFIX_PARAMETER));
      iwc.setSessionAttribute(DISPLAYSTRING_PARAMETER_NAME,iwc.getParameter(DISPLAYSTRING_PARAMETER_NAME));
      iwc.setSessionAttribute(VALUE_PARAMETER_NAME,iwc.getParameter(VALUE_PARAMETER_NAME));
    }

    if ( iwc.isParameterSet("from_editor") )
      addScript();
  }

  private void addScript() {
    Script script = getParentPage().getAssociatedScript();
    script.addFunction("save","function save() { window.returnValue = "+this._colorString+"; window.close; }");
    getParentPage().setAssociatedScript(script);
  }

  private void doBusiness(IWContext iwc,boolean remove) {
    IWPropertyList list = iwc.getApplication().getSettings().getIWPropertyList("color_palette");
    if ( list != null )
      if ( remove )
	list.removeProperty(_colorString);
      else
	list.setProperty(_colorString,_colorString);
    else
      iwc.getApplication().getSettings().getNewPropertyList("color_palette").setProperty(_colorString,_colorString);
  }

  private boolean isInCustomColors(IWContext iwc,String color) {
    IWPropertyList list = iwc.getApplication().getSettings().getIWPropertyList("color_palette");
    if ( list != null ) {
      String returnString = list.getProperty(color);
      if ( returnString != null )
	return true;
    }
    return false;
  }
}
