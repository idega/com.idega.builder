package com.idega.builder.presentation;

import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import com.idega.util.text.StyleConstants;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class IBStyleChooser extends Block {

private HashMap _styleMap;
private String _styleString;
private String[] _styles = StyleConstants.ALL_STYLES;

  public IBStyleChooser() {
    setDefaultValues();
  }

  public void main(IWContext iwc) {
    if ( iwc.getParameter("style") != null ) {
       _styleString = iwc.getParameter("style");
       setMapStyles();
    }

    collectStyles(iwc);
    drawForm();
  }

  private void drawForm() {
    Form form = new Form();
    Table formTable = new Table();

    int row = 1;

    DropdownMenu fontFamily = new DropdownMenu(StyleConstants.ATTRIBUTE_FONT_FAMILY);
      fontFamily.addMenuElementFirst("","");
      fontFamily.addMenuElement(StyleConstants.FONT_FAMILY_ARIAL,StyleConstants.FONT_FAMILY_ARIAL);
      fontFamily.addMenuElement(StyleConstants.FONT_FAMILY_COURIER,StyleConstants.FONT_FAMILY_COURIER);
      fontFamily.addMenuElement(StyleConstants.FONT_FAMILY_GENEVA,StyleConstants.FONT_FAMILY_GENEVA);
      fontFamily.addMenuElement(StyleConstants.FONT_FAMILY_GEORGIA,StyleConstants.FONT_FAMILY_GEORGIA);
      fontFamily.addMenuElement(StyleConstants.FONT_FAMILY_TIMES,StyleConstants.FONT_FAMILY_TIMES);
      fontFamily.addMenuElement(StyleConstants.FONT_FAMILY_VERDANA,StyleConstants.FONT_FAMILY_VERDANA);
      fontFamily.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_FONT_FAMILY));
    formTable.add("Font family:",1,row);
    formTable.add(fontFamily,2,row);
    row++;

    DropdownMenu fontSize = new DropdownMenu(StyleConstants.ATTRIBUTE_FONT_SIZE);
      fontSize.addMenuElementFirst("","");
      for ( int a = 1; a <= 40; a++ ) {
        fontSize.addMenuElement(Integer.toString(a)+"pt",Integer.toString(a)+"pt");
      }
      fontSize.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_FONT_SIZE));
    formTable.add("Font size:",1,row);
    formTable.add(fontSize,2,row);
    row++;

    DropdownMenu fontStyle = new DropdownMenu(StyleConstants.ATTRIBUTE_FONT_STYLE);
      fontStyle.addMenuElementFirst("","");
      fontStyle.addMenuElement(StyleConstants.FONT_STYLE_NORMAL,StyleConstants.FONT_STYLE_NORMAL);
      fontStyle.addMenuElement(StyleConstants.FONT_STYLE_ITALIC,StyleConstants.FONT_STYLE_ITALIC);
      fontStyle.addMenuElement(StyleConstants.FONT_STYLE_OBLIQUE,StyleConstants.FONT_STYLE_OBLIQUE);
      fontStyle.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_FONT_STYLE));
    formTable.add("Font style:",1,row);
    formTable.add(fontStyle,2,row);
    row++;

    DropdownMenu fontStretch = new DropdownMenu(StyleConstants.ATTRIBUTE_FONT_STRETCH);
      fontStretch.addMenuElementFirst("","");
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_NORMAL,StyleConstants.FONT_STRETCH_NORMAL);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_WIDER,StyleConstants.FONT_STRETCH_WIDER);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_NARROWER,StyleConstants.FONT_STRETCH_NARROWER);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_ULTRA_CONDENSED,StyleConstants.FONT_STRETCH_ULTRA_CONDENSED);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_EXTRA_CONDENSED,StyleConstants.FONT_STRETCH_EXTRA_CONDENSED);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_CONDENSED,StyleConstants.FONT_STRETCH_CONDENSED);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_SEMI_DONDENSED,StyleConstants.FONT_STRETCH_SEMI_DONDENSED);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_SEMI_EXPANDED,StyleConstants.FONT_STRETCH_SEMI_EXPANDED);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_EXPANDED,StyleConstants.FONT_STRETCH_EXPANDED);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_EXTRA_EXPANDED,StyleConstants.FONT_STRETCH_EXTRA_EXPANDED);
      fontStretch.addMenuElement(StyleConstants.FONT_STRETCH_ULTRA_EXPANDED,StyleConstants.FONT_STRETCH_ULTRA_EXPANDED);
      fontStretch.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_FONT_STRETCH));
    formTable.add("Font stretch:",1,row);
    formTable.add(fontStretch,2,row);
    row++;

    DropdownMenu textDecoration = new DropdownMenu(StyleConstants.ATTRIBUTE_TEXT_DECORATION);
      textDecoration.addMenuElementFirst("","");
      textDecoration.addMenuElement(StyleConstants.TEXT_DECORATION_NONE,StyleConstants.TEXT_DECORATION_NONE);
      textDecoration.addMenuElement(StyleConstants.TEXT_DECORATION_UNDERLINE,StyleConstants.TEXT_DECORATION_UNDERLINE);
      textDecoration.addMenuElement(StyleConstants.TEXT_DECORATION_OVERLINE,StyleConstants.TEXT_DECORATION_OVERLINE);
      textDecoration.addMenuElement(StyleConstants.TEXT_DECORATION_LINETHROUGH,StyleConstants.TEXT_DECORATION_LINETHROUGH);
      textDecoration.addMenuElement(StyleConstants.TEXT_DECORATION_BLINK,StyleConstants.TEXT_DECORATION_BLINK);
      textDecoration.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_TEXT_DECORATION));
    formTable.add("Text decoration:",1,row);
    formTable.add(textDecoration,2,row);
    row++;

    DropdownMenu fontWeight = new DropdownMenu(StyleConstants.ATTRIBUTE_FONT_WEIGHT);
      fontWeight.addMenuElementFirst("","");
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_NORMAL,StyleConstants.FONT_WEIGHT_NORMAL);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_BOLD,StyleConstants.FONT_WEIGHT_BOLD);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_BOLDER,StyleConstants.FONT_WEIGHT_BOLDER);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_LIGHT,StyleConstants.FONT_WEIGHT_LIGHT);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_100,StyleConstants.FONT_WEIGHT_100);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_200,StyleConstants.FONT_WEIGHT_200);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_300,StyleConstants.FONT_WEIGHT_300);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_400,StyleConstants.FONT_WEIGHT_400);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_500,StyleConstants.FONT_WEIGHT_500);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_600,StyleConstants.FONT_WEIGHT_600);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_700,StyleConstants.FONT_WEIGHT_700);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_800,StyleConstants.FONT_WEIGHT_800);
      fontWeight.addMenuElement(StyleConstants.FONT_WEIGHT_900,StyleConstants.FONT_WEIGHT_900);
      fontWeight.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_FONT_WEIGHT));
    formTable.add("Font weight:",1,row);
    formTable.add(fontWeight,2,row);
    row++;

    TextInput color = new TextInput(StyleConstants.ATTRIBUTE_COLOR);
      color.setContent(getStyleValue(StyleConstants.ATTRIBUTE_COLOR));
    formTable.add("Font color:",1,row);
    formTable.add(color,2,row);
    row++;

    TextInput textShadow = new TextInput(StyleConstants.ATTRIBUTE_TEXT_SHADOW);
      textShadow.setContent(getStyleValue(StyleConstants.ATTRIBUTE_TEXT_SHADOW));
    formTable.add("Text shadow:",1,row);
    formTable.add(textShadow,2,row);
    row++;

    DropdownMenu fontVariant = new DropdownMenu(StyleConstants.ATTRIBUTE_FONT_VARIANT);
      fontVariant.addMenuElementFirst("","");
      fontVariant.addMenuElement(StyleConstants.FONT_VARIANT_NORMAL,StyleConstants.FONT_VARIANT_NORMAL);
      fontVariant.addMenuElement(StyleConstants.FONT_VARIANT_SMALLCAPS,StyleConstants.FONT_VARIANT_SMALLCAPS);
      fontVariant.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_FONT_VARIANT));
    formTable.add("Font variant:",1,row);
    formTable.add(fontVariant,2,row);
    row++;

    DropdownMenu textTransform = new DropdownMenu(StyleConstants.ATTRIBUTE_TEXT_TRANSFORM);
      textTransform.addMenuElementFirst("","");
      textTransform.addMenuElement(StyleConstants.TEXT_TRANSFORM_CAPITALIZE,StyleConstants.TEXT_TRANSFORM_CAPITALIZE);
      textTransform.addMenuElement(StyleConstants.TEXT_TRANSFORM_UPPERCASE,StyleConstants.TEXT_TRANSFORM_UPPERCASE);
      textTransform.addMenuElement(StyleConstants.TEXT_TRANSFORM_LOWERCASE,StyleConstants.TEXT_TRANSFORM_LOWERCASE);
      textTransform.addMenuElement(StyleConstants.TEXT_TRANSFORM_NONE,StyleConstants.TEXT_TRANSFORM_NONE);
      textTransform.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_TEXT_TRANSFORM));
    formTable.add("Text transform:",1,row);
    formTable.add(textTransform,2,row);
    row++;

    DropdownMenu textAlign = new DropdownMenu(StyleConstants.ATTRIBUTE_TEXT_ALIGN);
      textAlign.addMenuElementFirst("","");
      textAlign.addMenuElement(StyleConstants.TEXT_ALIGN_LEFT,StyleConstants.TEXT_ALIGN_LEFT);
      textAlign.addMenuElement(StyleConstants.TEXT_ALIGN_CENTER,StyleConstants.TEXT_ALIGN_CENTER);
      textAlign.addMenuElement(StyleConstants.TEXT_ALIGN_RIGHT,StyleConstants.TEXT_ALIGN_RIGHT);
      textAlign.addMenuElement(StyleConstants.TEXT_ALIGN_JUSTIFY,StyleConstants.TEXT_ALIGN_JUSTIFY);
      textAlign.setSelectedElement(getStyleValue(StyleConstants.ATTRIBUTE_TEXT_ALIGN));
    formTable.add("Text align:",1,row);
    formTable.add(textAlign,2,row);
    row++;

    TextInput letterSpacing = new TextInput(StyleConstants.ATTRIBUTE_LETTER_SPACING);
      letterSpacing.setContent(getStyleValue(StyleConstants.ATTRIBUTE_LETTER_SPACING));
    formTable.add("Letter spacing:",1,row);
    formTable.add(letterSpacing,2,row);
    row++;

    TextInput wordSpacing = new TextInput(StyleConstants.ATTRIBUTE_WORD_SPACING);
      wordSpacing.setContent(getStyleValue(StyleConstants.ATTRIBUTE_WORD_SPACING));
    formTable.add("Word spacing:",1,row);
    formTable.add(wordSpacing,2,row);
    row++;

    Text text = new Text("See preview here");
    if ( _styleString != null && _styleString.length() > 0 )
      text.setFontStyle(_styleString);
    formTable.mergeCells(1,row,2,row);
    formTable.add(text,1,row);
    row++;

    formTable.mergeCells(1,row,2,row);
    formTable.add(new SubmitButton("Preview","mode","mode"),1,row);

    form.add(formTable);
    add(form);
  }

  private void collectStyles(IWContext iwc) {
    if ( _styles != null ) {
      for ( int a = 0; a < _styles.length; a++ ) {
        getParameter(_styles[a],iwc);
      }
    }
    getMapStyleString();
  }

  private void getParameter(String attribute,IWContext iwc) {
    String value = iwc.getParameter(attribute);
    if ( value != null && value.length() > 0 ) {
      if ( attribute.equalsIgnoreCase(StyleConstants.ATTRIBUTE_LINE_HEIGHT) && value.indexOf("pt") == -1 )
        value += "pt";
      if ( attribute.equalsIgnoreCase(StyleConstants.ATTRIBUTE_LETTER_SPACING) && value.indexOf("ems") == -1 )
        value += "ems";
      if ( attribute.equalsIgnoreCase(StyleConstants.ATTRIBUTE_WORD_SPACING) && value.indexOf("ems") == -1 )
        value += "ems";
      setStyleValue(attribute,value);
    }
  }

  private void getMapStyleString() {
    Iterator iter = _styleMap.keySet().iterator();
    String attribute;
    String value;
    _styleString = "";
    while (iter.hasNext()) {
      attribute = (String) iter.next();
      value = (String) _styleMap.get(attribute);
      if ( value != null ) {
        _styleString += attribute + StyleConstants.DELIMITER_COLON + value + StyleConstants.DELIMITER_SEMICOLON;
      }
    }
  }

  private void setMapStyles() {
    if ( _styleString != null ) {
      StringTokenizer tokens = new StringTokenizer(_styleString,";");
      int a = -1;
      String attribute;
      String value;

      while (tokens.hasMoreTokens()) {
        StringTokenizer tokens2 = new StringTokenizer(tokens.nextToken(),":");

        a = 1;
        attribute = null;
        value = null;

        while (tokens2.hasMoreTokens()) {
          if ( a == 1 ) {
            attribute = tokens2.nextToken();
            a++;
          }
          else if ( a == 2 )
            value = tokens2.nextToken();
        }
        _styleMap.put(attribute,value);
      }
    }
  }

  private void setDefaultValues() {
    if ( _styleMap == null )
      _styleMap = new HashMap();

    if ( _styles != null ) {
      for ( int a = 0; a < _styles.length; a++ ) {
        _styleMap.put(_styles[a],null);
      }
    }
  }

  private void setStyleValue(String attribute,String value) {
    _styleMap.put(attribute,value);
  }

  private String getStyleValue(String attribute) {
    String value = (String) _styleMap.get(attribute);
    if ( value != null )
      return value;
    return "";
  }
}