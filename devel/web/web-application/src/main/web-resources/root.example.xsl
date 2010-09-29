<!--
This is a stylesheet example that can be used as the root of your applications. 
-->
<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<xsl:apply-templates select="/WebApplicationUpdate" mode="context" />
<xsl:apply-templates select="/WebApplication" mode="context" />
</xsl:template>

<xsl:template match="WebApplicationUpdate" mode="context">
<updates xmlns:txt="http://example.com/2006/some-data" 
         xmlns:data="http://example.com/2006/some-data"><xsl:apply-templates/>
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
  <html>
  	<head>
  		<title>Title</title>
  		<script type="text/javascript" src="/hierarchy-context-static/jquery.js"></script>
<script type="text/javascript" src="/hierarchy-context-static/prototype.js"></script>
<script type="text/javascript" src="/hierarchy-context-static/sarissa.js"></script>
<script type="text/javascript" src="/hierarchy-context-static/sarissa_ieemu_xpath.js"></script>
<script type="text/javascript" src="/hierarchy-context-static/hierarchy-web.js"></script>
<script type="text/javascript">
	<xsl:attribute name="src"><xsl:value-of select="$jsURL" /></xsl:attribute>
</script>
<link rel="stylesheet" type="text/css">
	<xsl:attribute name="href"><xsl:value-of select="$cssURL" /></xsl:attribute>
</link>
<script type="text/javascript">

$(document).ready(function() {
contextfw.init("<xsl:value-of select="@handle" />");
<xsl:apply-templates select="//Script" mode="script" />
});

$(window).unload( function () {
    contextfw.unload(); 
});
</script>
    </head>
  <body>
    <xsl:apply-templates /> 
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>