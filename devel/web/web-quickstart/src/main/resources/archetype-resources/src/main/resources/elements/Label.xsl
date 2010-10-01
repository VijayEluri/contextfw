<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:data="http://example.com/2006/some-data"
xmlns:txt="http://contextfw.net/ns/txt">

<xsl:template match="Label">
	<span>
		<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
		<xsl:apply-templates/>
	</span>
</xsl:template>

<xsl:template match="Label.update">
	<replaceInner>
		<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
		<xsl:apply-templates/>
	</replaceInner>
</xsl:template>

</xsl:stylesheet>