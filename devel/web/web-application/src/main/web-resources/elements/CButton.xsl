<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="CButton">
<div class="cbutton cbutton-{@state}" id="{@id}a">
<div id="{@id}">
<xsl:apply-templates select="content" />
</div>
</div>
</xsl:template>

<xsl:template match="CButtonInitScript">CButton.initialize('<xsl:value-of select="../../@id" />', '<xsl:value-of select="@targetId" />', '<xsl:value-of select="@event" />', null);
</xsl:template>

<xsl:template match="CButtonFunctionInitScript">CButton.initializeFunction('<xsl:value-of select="../../@id" />', '<xsl:apply-templates select="function" />');
</xsl:template>

<xsl:template match="CButtonFormInitScript">CButton.initialize('<xsl:value-of select="../../@id" />', '<xsl:value-of select="@targetId" />', '<xsl:value-of select="@event" />', '<xsl:value-of select="@formId" />');
</xsl:template>
</xsl:stylesheet> 