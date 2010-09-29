function CButton() {}

CButton.initialize = function(id, targetId, event, formId) {
	jQuery("#"+id+"a").mousedown(
		function() {
			if (jQuery(this).hasClass("cbutton-enabled")) {
				jQuery(this).addClass("cbutton-active");
				jQuery(this).addClass("pushed");
				jQuery(this).removeClass("cbutton-enabled");
				jQuery().bind('mouseup', {el:this, targetId:targetId,  formId:formId, event:event}, 
					function(e) {
						if (jQuery(e.data.el).hasClass("over")) {
							CButton.onClick(e.data.targetId, e.data.event, e.data.formId)
						}
						jQuery(e.data.el).addClass("cbutton-enabled");
						jQuery(e.data.el).removeClass("cbutton-active");
						jQuery(e.data.el).removeClass("pushed");
						jQuery().unbind("mouseup");
					}
				);
			}
		}
	);
	
	jQuery("#"+id+"a").mouseenter(
		function() {
			jQuery(this).addClass("over");
			if (jQuery(this).hasClass("pushed")) {
				jQuery(this).addClass("cbutton-active");
				jQuery(this).removeClass("cbutton-enabled");
			}
		}
	);		

	jQuery("#"+id+"a").mouseleave(
		function() {
			jQuery(this).removeClass("over");
			if (jQuery(this).hasClass("pushed")) {
				jQuery(this).addClass("cbutton-enabled");
				jQuery(this).removeClass("cbutton-active");
			}
		}
	);
}

CButton.onClick = function(targetId, event, formId) {
	if (formId == undefined) {
		var params = new Hash();
		HierarchyWeb.requestGetUpdate(targetId, event, params)
	}
	else {
		HierarchyWeb.requestPostUpdate2(targetId, event, CForm.fetchParams(formId));
	}
}