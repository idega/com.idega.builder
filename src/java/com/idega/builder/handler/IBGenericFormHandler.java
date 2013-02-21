package com.idega.builder.handler;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idega.builder.form.data.Field;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;
/**
 * Title:        idegaWeb Generic Form Handler
 * Description:  A class to parse forms and get output in a structured format
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBGenericFormHandler implements java.lang.Cloneable {
	private Map parameterDescriptions;
	private Map parameterTypes;
	private List parameterNames;

	private static final String COLON = ":";
	private static final String TAB = "\t";
	private static final String NEWLINE = "\n";
	public static final String STRING_TYPE = JavaTypesHandler.STRING_TYPE;
	public static final String INTEGER_TYPE = JavaTypesHandler.INTEGER_TYPE;
	public static final String BOOLEAN_TYPE = JavaTypesHandler.BOOLEAN_TYPE;
	public static final String FLOAT_TYPE = JavaTypesHandler.FLOAT_TYPE;
	public static final String DOUBLE_TYPE = JavaTypesHandler.DOUBLE_TYPE;
	public static final String DATE_TYPE = JavaTypesHandler.DATE_TYPE;
	public static final String TIMESTAMP_TYPE = JavaTypesHandler.TIMESTAMP_TYPE;
	public static final String TIME_TYPE = JavaTypesHandler.TIME_TYPE;

	public IBGenericFormHandler() {
		this.parameterDescriptions = new HashMap();
		this.parameterTypes = new HashMap();
		this.parameterNames = new ArrayList();
	}

	public void addProcessedParameter(String parameterName, String parameterDescription, String parameterType) {
		getDescMap().put(parameterName, parameterDescription);
		getTypesMap().put(parameterName, parameterType);
		this.parameterNames.add(parameterName);
	}

	/*public void addProcessedParameter(String parameterName,String parameterDescription,String parameterType){
	  addProcessedParameter(parameterName,parameterDescription,STRING_TYPE);
	}*/
	private Map getDescMap() {
		return this.parameterDescriptions;
	}

	private Map getTypesMap() {
		return this.parameterTypes;
	}

	/**
	 * Formats the output of the form with descriptions in front of values and newlines between values
	 */
	public String processPlainTextFormatted(IWContext iwc) {
		StringBuffer buffer = new StringBuffer();
		int count = this.parameterNames.size();
		StringBuffer debugBuf = new StringBuffer();
		for (int j = 0; j < count; j++) {
			String paramName = (String) this.parameterNames.get(j);
			debugBuf.append(paramName).append(" ");
			String[] paramValues = iwc.getParameterValues(paramName);
			if (paramValues != null) {
				String desc = getDescriptionForParameter(paramName);
				buffer.append(desc);
				buffer.append(COLON);
				buffer.append(TAB);
				buffer.append(TAB);
				if (paramValues.length == 1) {
					String currentDiplayedVal = getProcessedValueForDisplay(iwc, paramName, paramValues[0]);
					buffer.append(currentDiplayedVal);
					buffer.append(NEWLINE);
				}
				else {
					for (int i = 0; i < paramValues.length; i++) {
						String currentDiplayedVal = getProcessedValueForDisplay(iwc, paramName, paramValues[i]);
						if (i != 0) {
							buffer.append(TAB);
							buffer.append(TAB);
							buffer.append(TAB);
						}
						buffer.append(currentDiplayedVal);
						buffer.append(NEWLINE);
					}
				}
			}
		}
		return buffer.toString();
	}


	/**
	 * Formats the output of the form with descriptions in front of values and newlines between values
	 */
	public List processFormToFieldList(IWContext iwc) {
		List list = new ArrayList();
		int count = this.parameterNames.size();
		for (int j = 0; j < count; j++) {
			String paramName = (String) this.parameterNames.get(j);
			String[] paramValues = iwc.getParameterValues(paramName);
			if (paramValues != null) {
				Field field = null;
				if (paramValues.length == 1) {
					String currentDiplayedVal = getProcessedValueForDisplay(iwc, paramName, paramValues[0]);
					field = new Field(paramName, currentDiplayedVal);
				}
				else {
					StringBuffer values = new StringBuffer();
					for (int i = 0; i < paramValues.length; i++) {
						String currentDiplayedVal = getProcessedValueForDisplay(iwc, paramName, paramValues[i]);
						if (i != 0) {
							values.append(",");
						}
						values.append(currentDiplayedVal);
					}
					field = new Field(paramName, values.toString());
				}

				list.add(field);
			}
		}

		return list;
	}

	public String getParameterValue(IWContext iwc, String parameterName) {
		String[] paramValues = iwc.getParameterValues(parameterName);
		if (paramValues != null && paramValues.length >= 1) {
			return getProcessedValueForDisplay(iwc, parameterName, paramValues[0]);
		}
		return null;
	}

	private String getDescriptionForParameter(String parameterName) {
		return (String) getDescMap().get(parameterName);
	}

	private String getTypeForParameter(String parameterName) {
		String type = (String) getTypesMap().get(parameterName);
		return type == null ? STRING_TYPE : type;
	}

	private String getProcessedValueForDisplay(IWContext iwc, String parameterName, String parameterValue) {
		String parameterType = getTypeForParameter(parameterName);
		if (parameterType.equals(STRING_TYPE)) {
			return parameterValue;
		}
		else if (parameterType.equals(STRING_TYPE)) {
			return parameterValue;
		}
		else if (parameterType.equals(BOOLEAN_TYPE)) {
			if (parameterValue.equalsIgnoreCase(CoreConstants.Y) || parameterValue.equalsIgnoreCase("T")) {
				/**
				 * @todo: Localize
				 */
				return "True";
			}
			else if (parameterValue.equalsIgnoreCase("N") || parameterValue.equalsIgnoreCase("F")) {
				return "False";
			}
			return parameterValue;
		}
		else if (parameterType.equals(INTEGER_TYPE)) {
			return parameterValue;
		}
		else if (parameterType.equals(DOUBLE_TYPE)) {
			return parameterValue;
		}
		else if (parameterType.equals(FLOAT_TYPE)) {
			return parameterValue;
		}
		else if (parameterType.equals(DATE_TYPE)) {
			DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, iwc.getCurrentLocale());
			com.idega.util.IWTimestamp its = new com.idega.util.IWTimestamp(parameterValue);
			java.util.Date date = its.getSQLDate();
			return format.format(date);
		}
		else if (parameterType.equals(TIMESTAMP_TYPE)) {
			DateFormat format = DateFormat.getDateInstance(DateFormat.DEFAULT, iwc.getCurrentLocale());
			com.idega.util.IWTimestamp its = new com.idega.util.IWTimestamp(parameterValue);
			java.util.Date date = its.getSQLDate();
			return format.format(date);
		}
		else if (parameterType.equals(TIME_TYPE)) {
			return parameterValue;
		}
		return parameterValue;
	}

	@Override
	public Object clone() {
		try {
			IBGenericFormHandler newHandler = (IBGenericFormHandler) super.clone();
			if (this.parameterDescriptions != null) {
				newHandler.parameterDescriptions = (Map) ((HashMap) this.parameterDescriptions).clone();
			}
			if (this.parameterTypes != null) {
				newHandler.parameterTypes = (Map) ((HashMap) this.parameterTypes).clone();
			}
			if (this.parameterNames != null) {
				newHandler.parameterNames = (List) (((ArrayList) this.parameterNames).clone());
			}
			return newHandler;
		}
		catch (java.lang.CloneNotSupportedException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
