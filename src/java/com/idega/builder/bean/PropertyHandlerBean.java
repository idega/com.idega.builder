package com.idega.builder.bean;

public class PropertyHandlerBean {
	
	private String objectInstanceId = null;
	private String propertyName = null;
	private String name = null;
	private String value = null;
	private String styleClass = null;
	private String moduleClassName;
	
	private Class<?> parameterClass = null;
	
	private int parameterIndex = -1;
	private int parametersCount = 0;
	
	private boolean needsReload = false;
	private boolean isMultivalue = false;

	public PropertyHandlerBean(String objectInstanceId, String propertyName, String name, String value, String styleClass, Class<?> parameterClass,
			int parameterIndex, boolean needsReload, boolean isMultivalue, int parametersCount, String moduleClassName) {
		
		this.objectInstanceId = objectInstanceId;
		this.propertyName = propertyName;
		this.name = name;
		this.value = value;
		this.styleClass = styleClass;
		this.parameterClass = parameterClass;
		this.parameterIndex = parameterIndex;
		this.needsReload = needsReload;
		this.isMultivalue = isMultivalue;
		this.parametersCount = parametersCount;
		this.moduleClassName = moduleClassName;
		
	}

	public boolean isMultivalue() {
		return isMultivalue;
	}

	public void setMultivalue(boolean isMultivalue) {
		this.isMultivalue = isMultivalue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNeedsReload() {
		return needsReload;
	}

	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}

	public String getObjectInstanceId() {
		return objectInstanceId;
	}

	public void setObjectInstanceId(String objectInstanceId) {
		this.objectInstanceId = objectInstanceId;
	}

	public Class<?> getParameterClass() {
		return parameterClass;
	}

	public void setParameterClass(Class<?> parameterClass) {
		this.parameterClass = parameterClass;
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

	public void setParameterIndex(int parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public int getParametersCount() {
		return parametersCount;
	}

	public void setParametersCount(int parametersCount) {
		this.parametersCount = parametersCount;
	}

	public String getModuleClassName() {
		return moduleClassName;
	}

	public void setModuleClassName(String moduleClassName) {
		this.moduleClassName = moduleClassName;
	}

}
