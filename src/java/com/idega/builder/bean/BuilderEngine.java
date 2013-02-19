package com.idega.builder.bean;


import java.rmi.RemoteException;
import java.util.List;

import org.jdom2.Document;

import com.idega.business.IBOSession;

public interface BuilderEngine extends IBOSession {
	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#getBuilderInitInfo
	 */
	public List<String> getBuilderInitInfo(String uri) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#addModule
	 */
	public String addModule(String pageKey, String containerId, String instanceId, int objectId, boolean useThread) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#addSelectedModule
	 */
	public Document addSelectedModule(String pageKey, String instanceId, int objectId, String containerId, String className, int index, boolean useThread) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#getRenderedModule
	 */
	public Document getRenderedModule(String pageKey, String uuid, int index, String parentId) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#deleteSelectedModule
	 */
	public boolean deleteSelectedModule(String pageKey, String parentId, String instanceId) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#getPropertyBox
	 */
	public Document getPropertyBox(String pageKey, String propertyName, String objectInstanceId) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#setSimpleModuleProperty
	 */
	public boolean setSimpleModuleProperty(String pageKey, String moduleId, String propertyName, String propertyValue) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#setModuleProperty
	 */
	public boolean setModuleProperty(String pageKey, String moduleId, String propertyName, String[] values) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#reRenderObject
	 */
	public Document reRenderObject(String pageKey, String instanceId) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#copyModule
	 */
	public boolean copyModule(String pageKey, String parentId, String instanceId) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#pasteModule
	 */
	public Document pasteModule(String pageKey, String parentInstanceId, int modulesCount, boolean paste) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#moveModule
	 */
	public boolean moveModule(String instanceId, String pageKey, String formerParentId, String newParentId, String neighbourInstanceId, boolean insertAbove) throws RemoteException;

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#isModuleInClipboard
	 */
	public String[] isModuleInClipboard();

	/**
	 * @see com.idega.builder.bean.BuilderEngineBean#removeProperty
	 */
	public boolean removeProperty(String pageKey, String moduleId, String propertyName);

	public boolean needReloadPropertyBox();
}