<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="CPane">
<div>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class">Pane <xsl:value-of select="@sClass" /></xsl:attribute>
<xsl:for-each select="title">
<div>
<xsl:attribute name="class">title</xsl:attribute>
<xsl:attribute name="id"><xsl:value-of select="../@id" />-title</xsl:attribute>
<!-- xsl:attribute name="onmousedown">CPane.makeDraggable('<xsl:value-of select="../@id" />');</xsl:attribute --> 
<div class="title-text">
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:apply-templates/>
</div>
<div class="title-buttons">
<img src="/hierarchy-web-static/gfx/icons/pane_close.png">
<xsl:attribute name="id"><xsl:value-of select="@id" />_img</xsl:attribute>
<xsl:attribute name="class">pane-open</xsl:attribute>
<xsl:attribute name="onclick">CPane.close('<xsl:value-of select="../@id" />');</xsl:attribute>
</img>
<img src="/hierarchy-web-static/gfx/icons/pane_open.png">
<xsl:attribute name="id"><xsl:value-of select="@id" />_img</xsl:attribute>
<xsl:attribute name="class">pane-open</xsl:attribute>
<xsl:attribute name="onclick">CPane.open('<xsl:value-of select="../@id" />');</xsl:attribute>
</img>
</div>
</div>
</xsl:for-each>
<xsl:for-each select="content">
<div class="content">
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="class">pane-content</xsl:attribute>
<xsl:apply-templates/>
</div>
</xsl:for-each>
</div>
</xsl:template>

<xsl:template match="CPaneUpdate">
<xsl:for-each select="title">
<replaceInner>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:apply-templates/>
</replaceInner>
</xsl:for-each>
<xsl:for-each select="content">
<replaceInner>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:apply-templates/>
</replaceInner>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>