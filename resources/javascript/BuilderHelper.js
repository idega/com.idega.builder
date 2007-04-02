var activeAddComponentElement = null;

var COMPONENT_INFO_CONTAINER = "builderRegionInfoImageContainer";
var REGION_ADD_COMPONENT_CONTAINER = "builderRegionAddComponentContainer";
var ACTIVE_ADD_COMPONENT_ELEMENT_ID = "addComponentToActiveRegionId";
var ACTIVE_ADD_INFO_ELEMENT_ID = "addInfoPanelToActiveRegionId";

var ADD_NEW_COMPONENT_WINDOW_LINK = "/workspase/window/";
var EDIT_COMPONENT_WINDOW_LINK = "/workspase/window/";

var ADD_NEW_COMPONENT_IMAGE = "/idegaweb/bundles/com.idega.builder.bundle/resources/add.png";
var COMPONENT_INFORMATION_IMAGE = "/idegaweb/bundles/com.idega.builder.bundle/resources/information.png";

var ADD_NEW_COMPONENT_LABEL = "Add a new Module";
var COMPONENT_INFORMATION_LABEL = "Set module properties";
var NO_IDS_ERROR_MESSAGE = "Error occurred while inserting selected module!";
var ADDING_MODULE_LABEL = "Adding...";
var REGION_LABEL = "Region";

var INSTANCE_ID = null;
var PARENT_ID = null;
var PAGE_KEY = null;

var EXISTING_CONTAINERS_ID = new Array();
var INITIALIZED_ELEMENTS = new Array();

function getBuilderInitInfo() {
	BuilderEngine.getBuilderInitInfo(getBuilderInitInfoCallback);
}

function getBuilderInitInfoCallback(list) {
	if (list == null) {
		return;
	}
	if (list.length != 10) {
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
}

function registerBuilderActions() {
	var builderRules = {
		'div.moduleContainer' : function(element) {
			element.onmouseover = function() {
				showAllComponentsLabels(element);
				showComponentInfoImage(element);
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
				showAddComponentImage(parentElement, regionLabel);
			},
			parentElement.onmouseout = function() {
				removeAddComponentImageWithTimeOut(regionLabel);
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
	removeOldLabels();
	
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

function removeAddComponentImageWithTimeOut(regionLabel) {
	var id = setTimeout("closeAddComponentContainer('"+REGION_ADD_COMPONENT_CONTAINER + regionLabel+"')", 5000);
}

function closeAddComponentContainer(id) {
	var container = document.getElementById(id);
	if (container == null) {
		return;
	}
	new Effect.Fade(container);
	/*var parentCotnainer = container.parentNode;
	if (parentCotnainer == null) {
		return;
	}
	parentCotnainer.removeChild(container);*/
}

function removeAddComponentImage(element) {
	if (element == null) {
		return;
	}

	// Removing old containers
	var children = element.childNodes;
	var foundChildren = null;
	var needToResetList = false;
	for (var i = 0; i < EXISTING_CONTAINERS_ID.length; i++) {
		if (children == null) {
			if (removeOldContainer(element, EXISTING_CONTAINERS_ID[i])) {
				needToResetList = true;
			}
		}
		else {
			foundChildren = getNeededBuilderElementsFromListById(children, EXISTING_CONTAINERS_ID[i]);
			if (foundChildren.length == 0) {
				if (removeOldContainer(element, REGION_ADD_COMPONENT_CONTAINER + EXISTING_CONTAINERS_ID[i])) {
					needToResetList = true;
				}
			}
		}
	}
	if (needToResetList) {
		EXISTING_CONTAINERS_ID = new Array();
	}
}

function showAddComponentImage(element, regionLabel) {
	if (element == null) {
		return;
	}
	
	// Removing old containers
	//removeAddComponentImage(element);
	
	// Checking if container allready exists in this element
	var children = getNeededBuilderElementsFromListById(element.childNodes, REGION_ADD_COMPONENT_CONTAINER + regionLabel);
	if (children.length > 0) {
		new Effect.Appear(REGION_ADD_COMPONENT_CONTAINER + regionLabel);
		return;
	}
	
	element.className = "regionContainer";
	
	//EXISTING_CONTAINERS_ID.push(regionLabel);
	
	// Main container
	var container = document.createElement("div");
	container.setAttribute("id", REGION_ADD_COMPONENT_CONTAINER + regionLabel);
	container.className = "regionComponentImageContainer";
	
	// Region name
	var regionName = document.createTextNode(REGION_LABEL + ": " + regionLabel + " ");
	container.appendChild(regionName);
	
	// Link
	var linkToComponents = document.createElement("a");
	linkToComponents.setAttribute("title", ADD_NEW_COMPONENT_LABEL);
	linkToComponents.className = "lbOn";
	linkToComponents.setAttribute("href", ADD_NEW_COMPONENT_WINDOW_LINK);
	
	// Image
	var image = document.createElement("img");
	image.setAttribute("src", ADD_NEW_COMPONENT_IMAGE);
	image.setAttribute("title", ADD_NEW_COMPONENT_LABEL);
	linkToComponents.appendChild(image);
	if (typeof container.attachEvent == "undefined") {
		image.addEventListener("click", function(e){setPropertiesForAddModule(element);}, false);
	} else {
		image.attachEvent("onclick", function(e){setPropertiesForAddModule(element);});
	}
	
	// Appending elements to document
	container.appendChild(linkToComponents);
	element.appendChild(container);	
	
	// Initializing Lightbox
	addLightboxMarkup();
	modulesWindow = new lightbox(linkToComponents);
	roundModulesListCorners();
}

function setPropertiesForAddModule(element) {
	if (element == null) {
		PARENT_ID = null;
		INSTANCE_ID = null;
		return;
	}
	
	var children = element.childNodes;
	if (children == null) {
		PARENT_ID = null;
		INSTANCE_ID = null;
		return;
	}
	
	var child = null;
	var found = false;
	for (var i = 0; (i < children.length && !found); i++) {
		child = children[i];
		if (child.className) {
			if (child.className == "moduleContainer") {
				found = analyzeModuleContainerInputs(child.childNodes);
			}
		}
	}
	
	if (!found) {
		PARENT_ID = null;
		INSTANCE_ID = null;
		return;
	}
}

function analyzeModuleContainerInputs(inputs) {
	if (inputs == null) {
		return false;			
	}
	var setInstanceId = false;
	var setParentId = false;
	for (var i = 0; i < inputs.length; i++) {
		input = inputs[i];
		if (input.id != null) {
			if (input.id.indexOf("instanceId") != -1) {
				INSTANCE_ID = input.value;
				setInstanceId = true;
			}
			if (input.id.indexOf("parentId") != -1) {
				PARENT_ID = input.value;
				setParentId = true;
			}
		}
	}
	if (setInstanceId && setParentId) {
		return true;
	}
	return false;
}

function showComponentInfoImage(element) {
	// Removing old containers
	removeOldContainer(element, COMPONENT_INFO_CONTAINER);
	
	if (element == null) {
		return;
	}
	
	// Checking if container allready exists in this element
	var children = getNeededBuilderElementsFromListById(element.childNodes, COMPONENT_INFO_CONTAINER);
	if (children.length > 0) {
		return;
	}
	
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
	
	// Main container
	var container = document.createElement("div");
	container.setAttribute("id", COMPONENT_INFO_CONTAINER);
	container.style.display = "inline";
	container.className = "regionInfoImageContainer";
	
	// Link
	var linkToInfo = document.createElement("a");
	linkToInfo.setAttribute("title", COMPONENT_INFORMATION_LABEL);
	linkToInfo.className = "lbOn";
	linkToInfo.setAttribute("href", EDIT_COMPONENT_WINDOW_LINK + "?moduleName=" + moduleName);
	
	// Image
	var infoImage = document.createElement("img");
	infoImage.setAttribute("src", COMPONENT_INFORMATION_IMAGE);
	infoImage.setAttribute("title", COMPONENT_INFORMATION_LABEL);
	
	linkToInfo.appendChild(infoImage);
	container.appendChild(linkToInfo);
	element.appendChild(container);
	
	// Initializing Lightbox
	addLightboxMarkup();
	editWindow = new lightbox(linkToInfo);
	roundModulesListCorners();
	
	new Effect.Pulsate(container);
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

function removeOldLabels() {
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

function addSelectedModule(newObjectId) {
	if (INSTANCE_ID == null || PARENT_ID == null || PAGE_KEY == null) {
		alert(NO_IDS_ERROR_MESSAGE);
		return;
	}
	showLoadingMessage(ADDING_MODULE_LABEL);
	BuilderEngine.addSelectedModule(PAGE_KEY, INSTANCE_ID, newObjectId, PARENT_ID, addSelectedModuleCallback);
}

function addSelectedModuleCallback(result) {
	modulesWindow.deactivate();
	window.location.href = window.location.href;
	closeLoadingMessage();
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
}