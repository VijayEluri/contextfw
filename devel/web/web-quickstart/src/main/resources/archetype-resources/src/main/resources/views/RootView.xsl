<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:txt="http://contextfw.net/ns/txt">
	<xsl:template match="RootView">
	  <div class="pageHeader">
	  	<a href="/">QuickStart</a>
	  </div>
   	<div class="pageContent">
			<xsl:apply-templates select="child" />
		</div>
	</xsl:template>
</xsl:stylesheet>
