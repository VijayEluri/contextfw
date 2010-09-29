<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="CTableUpdate">
<replace id="{@id}">
<xsl:call-template name="CTable" />
</replace>
</xsl:template>

<xsl:template match="CTable" name="CTable">
<table border="0" cellpadding="0" cellspacing="0" id="{@id}" class="{@sClass}">
<tr>
	<xsl:for-each select="headerRow/header">
	<th>
	<xsl:attribute name="class"><xsl:value-of select="@sClass" /></xsl:attribute>
	<xsl:if test="@colspan &gt; 1">
	<xsl:attribute name="colspan"><xsl:value-of select="@colspan" /></xsl:attribute>
	</xsl:if>
	<xsl:apply-templates/>
	</th>
	</xsl:for-each>
</tr>
<xsl:for-each select="content/row">
<tr>
<xsl:for-each select="cell2">
<td width="15" rowspan="2" style="vertical-align: top">
<img src="/hierarchy-web-static/gfx/icons/details_closed.png">
<xsl:attribute name="onclick">jQuery("#<xsl:value-of select="@drId" />").slideToggle("fast");</xsl:attribute>
</img>
</td>
</xsl:for-each>
<xsl:for-each select="cell3">
<td>
<xsl:if test="@colspan &gt; 1">
<xsl:attribute name="colspan"><xsl:value-of select="@colspan" /></xsl:attribute>
</xsl:if>
<div style="display: none">
<xsl:attribute name="id"><xsl:value-of select="../@id" /></xsl:attribute>
<xsl:apply-templates/>
</div>
</td>
</xsl:for-each>
<xsl:for-each select="cell">
<td>
<xsl:if test="@colspan &gt; 1">
<xsl:attribute name="colspan"><xsl:value-of select="@colspan" /></xsl:attribute>
</xsl:if>
<xsl:attribute name="class"><xsl:value-of select="@class" /></xsl:attribute>
<xsl:apply-templates/>
</td>
</xsl:for-each>
</tr>
</xsl:for-each>
</table>

</xsl:template>
</xsl:stylesheet>
