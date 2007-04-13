var ADD_NEW_COMPONENT_WINDOW_LINK = "/workspase/window/";
var EDIT_COMPONENT_WINDOW_LINK = "/workspase/window/";

var ADD_NEW_COMPONENT_IMAGE = "/idegaweb/bundles/com.idega.builder.bundle/resources/add.png";
var COMPONENT_INFORMATION_IMAGE = "/idegaweb/bundles/com.idega.builder.bundle/resources/information.png";

var ADD_NEW_COMPONENT_LABEL = "Add a new Module";
var COMPONENT_INFORMATION_LABEL = "Set module properties";
var NO_IDS_ERROR_MESSAGE = "Error occurred while inserting selected module!";
var ADDING_MODULE_LABEL = "Adding...";
var REGION_LABEL = "Region";
var DELETING_LABEL = "Deleting...";
var ARE_YOU_SURE_MESSAGE = "Are You sure?";
var SAVING_LABEL = "Saving...";
var LOADING_LABEL = "Loading...";

var INSTANCE_ID = null;
var PARENT_ID = null;
var PAGE_KEY = null;

var IC_OBJECT_INSTANCE_ID_PARAMETER = "ic_object_instance_id_par";
var MODULE_NAME_PARAMETER = "moduleName";

var PROPERTIES_SHOWN = new Array();

function getBuilderInitInfo() {
	BuilderEngine.getBuilderInitInfo(getBuilderInitInfoCallback);
}

function getBuilderInitInfoCallback(list) {
	if (list == null) {
		return;
	}
	if (list.length != 16) {
		return;
	}
	
	ADD_NEW_COMPONENT_WINDOW_LINK = list[0];
	ADD_NEW_COMPONENT_LABEL = list[1];
	COMPONENT_INFORMATION_LABEL = list[2];
	ADD_NEW_COMPONENT_IMAGE = list[3];
	COMPONENT_INFORMATION_IMAGE = list[4];
	NO_IDS_ERROR_MESSAGE = list[5];
	PAGE_KEY = list[6];
	ADDING_MODULE_LABEL = list[7];
	REGION_LABEL = list[8];
	EDIT_COMPONENT_WINDOW_LINK = list[9];
	IC_OBJECT_INSTANCE_ID_PARAMETER = list[10];
	MODULE_NAME_PARAMETER = list[11];
	DELETING_LABEL = list[12];
	ARE_YOU_SURE_MESSAGE = list[13];
	SAVING_LABEL = list[14];
	LOADING_LABEL = list[15];
}

function registerBuilderActions() {
	var builderRules = {
		'div.moduleContainer' : function(element) {
			element.onmouseover = function() {
				showAllComponentsLabels(element);
				showComponentInfoImage(element);
			},
			element.onmouseout = function() {
				hideOldLabels();
				hideComponentInfoImage(element);
			}
		},
		'div.regionLabel' : function(element) {
			var parentElement = element.parentNode;
			if (parentElement == null) {
				return;
			}
			var regionLabel = "";
			var inputs = element.getElementsByTagName("input");
			if (inputs != null) {
				if (inputs.length > 0) {
					var input = inputs[0];
					if (input.type != null) {
						if (input.type == "hidden") {
							regionLabel = input.value;
						}
					}
				}
			}
			parentElement.onmouseover = function() {
				showAddComponentImage(parentElement, element, regionLabel);
			},
			parentElement.onmouseout = function() {
				closeAddComponentContainer(element.id);
			}
		}
	};
	Behaviour.register(builderRules);
	Behaviour.apply();
}

function showAllComponentsLabels(element) {
	if (element == null) {
		return;
	}
	hideOldLabels();
	
	var children = getNeededBuilderElements(element, "DnDAreaTable");
	if (children == null) {
		return;
	}
	var child = null;
	var elementsToHighlight = null;
	for (var i = 0; i < children.length; i++) {
		child = children[i];
		child.style.visibility = "visible";
		elementsToHighlight = getNeededBuilderElementsFromList(child.getElementsByTagName("div"), "moduleName");
		for (var j = 0; j < elementsToHighlight.length; j++) {
			new Effect.Highlight(elementsToHighlight[j]);
		}
	}
}

function closeAddComponentContainer(id) {
	var container = document.getElementById(id);
	if (container == null) {
		return;
	}
	container.style.visibility = "hidden";
}

function existsValueInList(list, value) {
	if (list == null || value == null) {
		return false;
	}
	for (var i = 0; i < list.length; i++) {
		if (value == list[i]) {
			return true;
		}
	}
	return false;
}

function showAddComponentImage(parentElement, element, regionLabel) {
	if (element == null) {
		return;
	}
	
	element.style.visibility = "visible";	
}

function setPropertiesForAddModule(element) {
	PARENT_ID = null;
	INSTANCE_ID = null;
	if (element == null) {	
		return;
	}
	
	var linkContainer = element.parentNode;
	if (linkContainer == null) {
		return null;
	}
	var region = linkContainer.parentNode;
	if (region == null) {
		return;
	}
	
	var children = region.childNodes;
	if (children == null) {
		return;
	}
	
	var child = null;
	var foundInstance = false;
	var foundParent = false;
	var inputs = null;
	for (var i = 0; (i < children.length && !foundInstance && !foundParent); i++) {
		child = children[i];
		if (child.className) {
			if (child.className == "moduleContainer") {
				inputs = child.getElementsByTagName("input");
				INSTANCE_ID = getInputValue(inputs, "instanceId");
				if (INSTANCE_ID != null) {
					foundInstance = true;
				}
				PARENT_ID = getInputValue(inputs, "parentId");
				if (PARENT_ID != null) {
					foundParent = true;
				}
			}
		}
	}
	
	if (!foundParent) {
		PARENT_ID = getInputValue(region.getElementsByTagName("input"), "parentKey");
	}

}

function getInputValue(inputs, inputName) {
	if (inputs == null || inputName == null) {
		return false;			
	}

	for (var i = 0; i < inputs.length; i++) {
		input = inputs[i];
		if (input.name != null) {
			if (input.name.indexOf(inputName) != -1) {
				return input.value;
			}
		}
	}
	return null;
}

function getFirstElementFromList(elements) {
	if (elements == null) {
		return null;
	}
	if (elements.length == 0) {
		return null;
	}
	return elements[0];
}

function hideComponentInfoImage(element) {
	if (element == null) {
		return;
	}
	
	var list = element.getElementsByClassName("regionInfoImageContainer");
	var container = getFirstElementFromList(list);
	if (container == null) {
		return;
	}
	
	container.style.visibility = "hidden";
}

function showComponentInfoImage(element) {
	if (element == null) {
		return;
	}
	
	var list = element.getElementsByClassName("regionInfoImageContainer");
	var container = getFirstElementFromList(list);
	if (container == null) {
		return;
	}
	
	var link = getFirstElementFromList(container.getElementsByTagName("a"));
	if (link == null) {
		return;
	}
	if (container.style.visibility == "") {	// If it is the first time
		
		var moduleName = "Undefined";
		var moduleNameContainerList = element.getElementsByClassName("moduleName");
		if (moduleNameContainerList != null) {
			if (moduleNameContainerList.length > 0) {
				if (moduleNameContainerList[0].childNodes != null) {
					if (moduleNameContainerList[0].childNodes.length > 0) {
						moduleName = moduleNameContainerList[0].childNodes[0].nodeValue;
					}
				}
			}
		}
		
		var instanceId = null;
		var inputs = element.getElementsByTagName("input");
		if (inputs != null) {
			var input = null;
			var foundInstance = false;
			for (var i = 0; (i < inputs.length && !foundInstance); i++) {
				input = inputs[i];
				if (input.id != null) {
					if (input.id.indexOf("instanceId") != -1) {
						instanceId = input.value;
						foundInstance = true;
					}
				}
			}
		}

		link.removeAttribute("href");
		link.className = "lbOn";
		var uri = EDIT_COMPONENT_WINDOW_LINK + "&" + MODULE_NAME_PARAMETER + "=" + moduleName + "&" + IC_OBJECT_INSTANCE_ID_PARAMETER +
		 "=" + instanceId;
		link.setAttribute("href", uri);
		
		addLightboxMarkup();
		editWindow = new lightbox(link);
		roundModulesListCorners();
	}
	
	container.style.visibility = "visible";
}

function getNeededBuilderElements(element, className) {
	if (element == null) {
		return null;
	}
	return getNeededBuilderElementsFromList(element.childNodes, className);
}

function getNeededBuilderElementsFromList(list, className) {
	if (list == null || className == null) {
		return new Array();
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.className != null) {
				if (childElement.className == className) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
}

function getNeededBuilderElementsFromListById(list, id) {
	if (list == null || id == null) {
		return new Array();
	}
	var childElement = null;
	var elements = new Array();
	for (var i = 0; i < list.length; i++) {
		childElement = list[i];
		if (childElement != null) {
			if (childElement.id != null) {
				if (childElement.id == id) {
					elements.push(childElement);
				}
			}
		}
	}
	return elements;
}

function removeOldContainer(element, id) {
	var oldContainer = document.getElementById(id);
	if (oldContainer != null) {
		var parentElement = oldContainer.parentNode;
		if (parentElement != null) {
			if (parentElement == element) {
				return false;
			}
			parentElement.removeChild(oldContainer);
			return true;
		}
	}
}

function hideOldLabels() {
	var children = document.getElementsByClassName("DnDAreaTable");
	if (children == null) {
		return;
	}
	var element = null;
	for (var i = 0; i < children.length; i++) {
		element = children[i];
		element.style.visibility = "hidden";
	}
}

function addSelectedModule(newObjectId, className) {
	if (PARENT_ID == null || PAGE_KEY == null) {
		alert(NO_IDS_ERROR_MESSAGE);
		return;
	}
	
	if (INSTANCE_ID == null) {
		INSTANCE_ID = PARENT_ID;
	}
	
	var index = 0;
	var container = document.getElementById(PARENT_ID);
	if (container != null) {
		var modules = getNeededBuilderElementsFromList(container.childNodes, "moduleContainer");
		if (modules != null) {
			index = modules.length;
		}
	}
	
	showLoadingMessage(ADDING_MODULE_LABEL);
	BuilderEngine.addSelectedModule(PAGE_KEY, INSTANCE_ID, newObjectId, PARENT_ID, className, index, {
		callback: function(component) {
			addSelectedModuleCallback(component, PARENT_ID);
		}
	});
}

function addSelectedModuleCallback(component, id) {
	closeLoadingMessage();
	if (component == null) {
		reloadPageAfterAddingModule();
		return;
	}
	var container = document.getElementById(id);
	if (container == null) {
		reloadPageAfterAddingModule();
		return;
	}
	var children = component.childNodes;
	if (children == null) {
		reloadPageAfterAddingModule();
		return;
	}
	if (children.length == 0) {
		reloadPageAfterAddingModule();
		return;
	}

	// Making copy
	var allNodes = getTransformedDocumentToDom(component);
	
	// Finding place where to put new module
	var elementToInsertBefore = null;
	var modules = getNeededBuilderElementsFromList(container.childNodes, "moduleContainer");
	if (modules == null) {
		reloadPageAfterAddingModule();
		return;
	}

	if (modules.length == 0) {
		elementToInsertBefore = container.firstChild;
	}
	else {
		var lastModule = modules[modules.length - 1];
		var elementAfterLastModule = lastModule.nextSibling;	// The proper place - after the last module container
		if (elementAfterLastModule == null) {
			elementToInsertBefore = container.lastChild;
		}
		else {
			elementToInsertBefore = elementAfterLastModule;
		}
	}
	
	valid.deactivate();
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	for (var i = 0; i < allNodes.length; i++) {
		activeNode = allNodes[allNodes.length - (i + 1)];
		realNode = createRealNode(activeNode);
		container.insertBefore(realNode, elementToInsertBefore);
		elementToInsertBefore = realNode;
	}
	
	registerBuilderActions();	// Need to re-register actions
}

function createRealNode(element) {
	// Text
	if(element.nodeName == '#text') {
		var textNode = document.createTextNode(element.nodeValue);
		return textNode;
	}
	// Comment
	if (element.nodeName == '#comment') {
		var commentNode = document.createComment(element.nodeValue);
		return commentNode;
	}
	// Element
	var result = document.createElement(element.nodeName);
	for (var i = 0; i < element.attributes.length; i++) {
		result.setAttribute(element.attributes[i].nodeName, element.attributes[i].nodeValue);
	}
	for(var j = 0; j < element.childNodes.length; j++) {
		result.appendChild(createRealNode(element.childNodes[j]));
	}
	return result;
}

function reloadPageAfterAddingModule() {
	valid.deactivate();
	window.location.href = window.location.href;
}

function roundModulesListCorners() {
	Nifty("ul#modules_lists h3","top");
	Nifty("ul#modules_lists div","bottom same-height");
}

function roundLightboxWindow() {
	Nifty("div#lightbox","big");
}

function initializeEditModuleWindow() {
	Nifty("div#editModuleHeader","big");
	Nifty("ul#editModuleMenuNavigation a","small transparent top");
	Nifty("div#simple_properties_box","transparent");
	Nifty("div#advanced_properties_box","transparent");
}

function manageComponentPropertiesList(id) {
	if (existsValueInList(PROPERTIES_SHOWN, id)) {
		removeElementFromArray(PROPERTIES_SHOWN, id);
		new Effect.SlideDown(id);
	}
	else {
		PROPERTIES_SHOWN.push(id);
		closeComponentPropertiesList(id);
	}
}

function addPropertyIdAndClose(id) {
	if (id == null) {
		return;
	}
	PROPERTIES_SHOWN.push(id);
	closeComponentPropertiesList(id);
}

function closeComponentPropertiesList(id) {
	if (id == null) {
		return;
	}
	new Effect.SlideUp(id);
}

function exitFromPropertiesWindow() {
	PROPERTIES_SHOWN = new Array();
	editWindow.deactivate();
}

function closeAddModuleWindow() {
	valid.deactivate();
}

function deleteModule(id, pageKey, parentId, instanceId) {
	var confirmed = confirm(ARE_YOU_SURE_MESSAGE);
	if (!confirmed) {
		return;
	}
	showLoadingMessage(DELETING_LABEL);
	BuilderEngine.deleteSelectedModule(pageKey, parentId, instanceId, {
  		callback: function(result) {
    		deleteModuleCallback(result, id);
  		}
	});
}

function deleteModuleCallback(result, id) {
	closeLoadingMessage();
	if (result) {
		var deleted = document.getElementById(id);
		if (deleted == null) {
			return;
		}
		var parentDeleted = deleted.parentNode;
		if (parentDeleted == null) {
			return;
		}
		parentDeleted.removeChild(deleted);
	}
}

function getPropertyBox(id, propertyName, objectInstanceId) {
	if (document.getElementById(id + "_property_setter_box") == null) {
		showLoadingMessage(LOADING_LABEL);
		BuilderEngine.getPropertyBox(PAGE_KEY, propertyName, objectInstanceId, {
			callback: function(box) {
				getPropertyBoxCallback(id, box);
			}
		});
	}
	else {
		//new Effect.SlideDown(id + "_property_setter_box");
	}
}

function getPropertyBoxCallback(id, box) {
	closeLoadingMessage();
	if (id == null) {
		return;
	}
	var container = document.getElementById(id);
	if (container == null) {
		return;
	}
	if (box == null) {
		// TODO: add error
		return;
	}
	
	var propertySetterBox = document.createElement("div");
	propertySetterBox.setAttribute("id", id + "_property_setter_box");
	
	// Making copy
	var nodes = getTransformedDocumentToDom(box);
	
	// Inserting nodes
	var activeNode = null;
	var realNode = null;
	for (var i = 0; i < nodes.length; i++) {
		activeNode = nodes[i];
		realNode = createRealNode(activeNode);
		propertySetterBox.appendChild(realNode);
	}
	
	container.appendChild(propertySetterBox);
	//new Effect.SlideDown(propertySetterBox);
}

function setModuleProperty(moduleId, propName, propValue) {
	showLoadingMessage(SAVING_LABEL);
	BuilderEngine.setModuleProperty(PAGE_KEY, moduleId, propName, propValue, setModulePropertyCallback);
}

function setModulePropertyCallback(result) {
	closeLoadingMessage();
}

function getTransformedDocumentToDom(component) {
	var nodes = new Array();
	if (component == null) {
		return nodes;
	}
	var children = component.childNodes;
	if (children == null) {
		return nodes;
	}
	if (children.length == 0) {
		return nodes;
	}
	
	var size = children.length;
	var node = null;
	for (var i = 0; i < size; i++) {
		node = children.item(i);
		nodes.push(node);
	}
	return nodes;
}