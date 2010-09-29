<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="CSelect">
<select>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@publicName" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:for-each select="items/item">
<option>
<xsl:attribute name="value"><xsl:value-of select="@index" /></xsl:attribute>
<xsl:if test="@selected='true'">
<xsl:attribute name="selected">selected</xsl:attribute>
<xsl:attribute name="style">background: #A0a0f0</xsl:attribute>
</xsl:if>
<xsl:apply-templates />
</option>
</xsl:for-each>
</select>
</xsl:template>

<xsl:template match="CSelectStatic">
<span>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:apply-templates />
</span>
</xsl:template>

<xsl:template match="CSelectStaticUpdate">
<replace>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<span>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:apply-templates />
</span>
</replace>
</xsl:template>

<xsl:template match="CSelectUpdate">
<replace>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<select>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@publicName" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:for-each select="items/item">
<option>
<xsl:attribute name="value"><xsl:value-of select="@index" /></xsl:attribute>
<xsl:if test="@selected='true'">
<xsl:attribute name="selected">selected</xsl:attribute>
<xsl:attribute name="style">background: #A0A0f0</xsl:attribute>
</xsl:if>
<xsl:apply-templates />
</option>
</xsl:for-each>
</select>
</replace>
</xsl:template>
</xsl:stylesheet>
