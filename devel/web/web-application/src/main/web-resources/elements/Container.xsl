<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="Container">
<div>
<xsl:attribute name="class">container-<xsl:value-of select="@class" /></xsl:attribute>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:apply-templates/>
</div>
</xsl:template>

<xsl:template match="ContainerUpdate">
<replaceInner>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:apply-templates/>
</replaceInner>
</xsl:template>
</xsl:stylesheet>