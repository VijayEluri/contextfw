function CPane() {}

CPane.isPressed = function(appId) {
	jQuery("#"+appId+"-c").slideToggle("fast");
	
	if ($(appId+"_img").src.endsWith("pane_opened.png")) {
		$(appId+"_img").src = "/hierarchy-web-static/gfx/icons/pane_closed.png";
	}
	else {
		$(appId+"_img").src = "/hierarchy-web-static/gfx/icons/pane_opened.png";
	}
}

CPane.close = function(appId) {
	jQuery("#"+appId+"-c").slideUp("fast");
}

CPane.open = function(appId) {
	jQuery("#"+appId+"-c").slideDown("fast");
}

CPane.makeDraggable = function(appId) {
	makeDraggable($(appId));
	
	$(appId+"-title").onmousedown = function(ev){
		dragObject  = $(appId);
		mouseOffset = getMouseOffset($(appId), ev);
		return false;
	}
}