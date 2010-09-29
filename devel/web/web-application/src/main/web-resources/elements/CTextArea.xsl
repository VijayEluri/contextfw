<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="CTextArea">
<xsl:choose>
<xsl:when test="@updateable='true'">
<textarea id="{@id}" class="{@sClass}" name="{@publicName}">
<xsl:if test="@disabled='true'">
<xsl:attribute name="disabled">disabled</xsl:attribute>
</xsl:if>
<xsl:choose>
        <xsl:when test="content/value = ''"><xsl:text>&#x0A;</xsl:text></xsl:when>
        <xsl:otherwise><xsl:apply-templates select="content/value" /></xsl:otherwise>
    </xsl:choose>
</textarea>
</xsl:when>
<xsl:otherwise>
<div>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:for-each select="content/value"><xsl:apply-templates/></xsl:for-each>
</div>
</xsl:otherwise>
</xsl:choose>
</xsl:template>

<xsl:template match="CTextAreaUpdate">
<replace>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:choose>
<xsl:when test="@updateable='true'">
<textarea id="{@id}" class="{@sClass}" name="{@publicName}">
<xsl:if test="@disabled='true'">
<xsl:attribute name="disabled">disabled</xsl:attribute>
</xsl:if>
<xsl:choose>
        <xsl:when test="content/value = ''"><xsl:text>&#x0A;</xsl:text></xsl:when>
        <xsl:otherwise><xsl:apply-templates select="content/value" /></xsl:otherwise>
    </xsl:choose>
</textarea>
</xsl:when>
<xsl:otherwise>
<div>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:for-each select="content/value"><xsl:apply-templates/></xsl:for-each>
</div>
</xsl:otherwise>
</xsl:choose>
</replace>
</xsl:template>
</xsl:stylesheet>
