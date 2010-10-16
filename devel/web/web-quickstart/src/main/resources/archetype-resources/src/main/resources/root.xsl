<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:txt="http://contextfw.net/ns/txt">

<xsl:variable name="lang"><xsl:value-of select="/WebApplication/@lang"/><xsl:value-of select="/WebApplicationUpdate/@lang"/></xsl:variable>
<xsl:variable name="servletContext"><xsl:value-of select="/WebApplication/@contextPath"/></xsl:variable>

<xsl:template match="/">
	<xsl:apply-templates select="/WebApplication.update" mode="context" />
	<xsl:apply-templates select="/WebApplication" mode="context" />
</xsl:template>

<xsl:template match="WebApplication.update" mode="context">
<updates xmlns:txt="http://contextfw.net/ns/txt"><xsl:apply-templates/>
<script>
<xsl:apply-templates select="//Script" mode="script" />
</script>
</updates>
</xsl:template>

<xsl:template match="Redirect">
<script>window.location="<xsl:value-of select="@href" />";</script>
</xsl:template>

<xsl:template match="Script"><!-- LEAVE THIS EMPTY --></xsl:template>
<xsl:template match="Script" mode="script"><xsl:apply-templates mode="script" /></xsl:template>

<xsl:template match="WebApplication" mode="context">

<html xml:lang="fi" lang="fi">
	<xsl:attribute name="xml:lang"><xsl:value-of select="$lang" /></xsl:attribute>
	<xsl:attribute name="lang"><xsl:value-of select="$lang" /></xsl:attribute>
	<head>
		<title>Context Framework Quickstart</title>
		
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="expires" content="0" />  
		<meta http-equiv="cache-control" content="no-cache" />  
		<meta http-equiv="pragma" content="no-cache, no-store" />  

		<!-- Sarissa must be loaded before jquery -->
		<script type="text/javascript" src="{$contextPath}/scripts/sarissa.js"></script>
		<script type="text/javascript" src="{$contextPath}/scripts/sarissa_ieemu_xpath.js"></script>
		<script type="text/javascript" src="{$contextPath}/scripts/jquery.js"></script>
		<script type="text/javascript" src="{$contextPath}/scripts/contextfw.js"></script>
		<script type="text/javascript" src="{$contextPath}/scripts/json.js"></script>
		<script type="text/javascript" src="{$contextPath}/scripts/jquery.textarea.js"></script>
		<script type="text/javascript" src="{$contextPath}/scripts/shortcut.js"></script>
		<script type="text/javascript" src="{$contextPath}/resources.js"></script>
		
		<link rel="stylesheet" type="text/css" href="{$contextPath}/resources.css"></link>
		<link rel="stylesheet" type="text/css" href="{$contextPath}/main.css"></link>

		<script type="text/javascript">
$(document).ready(function() {
	contextfw.init("<xsl:value-of select="$contextPath" />", "<xsl:value-of select="@handle" />");
	<xsl:apply-templates select="//Script" mode="script" />
});
$.ajaxSetup({ scriptCharset: "utf-8" ,contentType: "application/x-www-form-urlencoded; charset=UTF-8" });
$(window).unload( function () {
	contextfw.unload(); 
});
</script>
	</head>
  	<body id="body">
    	<xsl:apply-templates />
	</body>
  </html>
</xsl:template>
</xsl:stylesheet>