<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template name="Monday" match="Monday">
<xsl:choose>
    <xsl:when test="lang('fi')">Maanantai</xsl:when>
    <xsl:when test="lang('en')">Monday</xsl:when>
</xsl:choose>
 </xsl:template>

<xsl:template name="YouGotMail" match="YouGotMail">
    <xsl:choose>
        <xsl:when test="@count = 0">
        <xsl:choose>
            <xsl:when test="lang('fi')">Sinulla ei ole lukemattomia viestejä</xsl:when>
            <xsl:when test="lang('en')">You have no unread messages</xsl:when>
        </xsl:choose>
        </xsl:when>
        <xsl:when test="@count = 1">
        <xsl:choose>
            <xsl:when test="lang('fi')">Sinulla on 1 lukematon viesti</xsl:when>
            <xsl:when test="lang('en')">You have 1 unread message</xsl:when>
        </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
            <xsl:choose>
                <xsl:when test="lang('fi')">
                    Sinulla on <xsl:value-of select="@count" /> lukematonta viestiä.
                </xsl:when>
                <xsl:when test="lang('en')">
                    You have <xsl:value-of select="@count" /> undread messages
                </xsl:when>
            </xsl:choose>
            
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>
</xsl:stylesheet>