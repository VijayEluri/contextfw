<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="TextBox">
<textarea>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<xsl:attribute name="name"><xsl:value-of select="@name" /></xsl:attribute>
<cdata><xsl:apply-templates/></cdata>
</textarea>
</xsl:template>

<xsl:template match="TextBoxUpdate">
<replaceInner>
<xsl:attribute name="id"><xsl:value-of select="@id" /></xsl:attribute>
<cdata><xsl:apply-templates/></cdata>
</replaceInner>
</xsl:template>

</xsl:stylesheet>
