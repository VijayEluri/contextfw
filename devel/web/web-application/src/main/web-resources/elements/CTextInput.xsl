<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="CTextInput">
<xsl:choose>
<xsl:when test="@updateable='true'">
<input type="text">
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@publicName" /></xsl:attribute>
<xsl:attribute name="value"><xsl:for-each select="content/value"><xsl:apply-templates/></xsl:for-each></xsl:attribute>
<xsl:if test="@disabled='true'">
<xsl:attribute name="disabled">disabled</xsl:attribute>
</xsl:if>
</input>
</xsl:when>
<xsl:otherwise>
<span>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:for-each select="content/value"><xsl:apply-templates/></xsl:for-each>
</span>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="CTextInputUpdate">
<replace>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:choose>
<xsl:when test="@updateable='true'">
<input type="text">
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@publicName" /></xsl:attribute>
<xsl:attribute name="value"><xsl:for-each select="content/value"><xsl:apply-templates/></xsl:for-each></xsl:attribute>
<xsl:if test="@disabled='true'">
<xsl:attribute name="disabled">disabled</xsl:attribute>
</xsl:if>
<xsl:for-each select="onFocus">
<xsl:attribute name="onfocus"><xsl:apply-templates/></xsl:attribute>
</xsl:for-each>
<xsl:for-each select="onClick">
<xsl:attribute name="onclick"><xsl:apply-templates/></xsl:attribute>
</xsl:for-each>
</input>
</xsl:when>
<xsl:otherwise>
<span>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:for-each select="content/value"><xsl:apply-templates/></xsl:for-each>
</span>
</xsl:otherwise>
</xsl:choose>
</replace>
</xsl:template>
</xsl:stylesheet>
