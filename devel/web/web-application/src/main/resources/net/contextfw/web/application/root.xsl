<?xml version="1.0" encoding="utf-8"?>
<!--

    Copyright 2010 Marko Lavikainen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

    See the License for the specific language governing permissions and
    limitations under the License.

-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:txt="http://contextfw.net/ns/txt">

<xsl:variable name="lang"><xsl:value-of select="/WebApplication/@lang"/><xsl:value-of select="/WebApplication.update/@lang"/></xsl:variable>
<xsl:variable name="contextPath"><xsl:value-of select="/WebApplication/@contextPath"/><xsl:value-of select="/WebApplication.update/@contextPath"/></xsl:variable>
<xsl:variable name="webApplicationHandle"><xsl:value-of select="/WebApplication/@handle"/><xsl:value-of select="/WebApplication.update/@handle"/></xsl:variable>

<xsl:template match="/">
	<xsl:apply-templates select="/WebApplication.update" mode="context" />
	<xsl:apply-templates select="/WebApplication" mode="context" />
</xsl:template>

<xsl:template match="WebApplication.update" mode="context">
<updates xmlns:txt="http://contextfw.net/ns/txt"><xsl:apply-templates/>
<script>
<xsl:apply-templates select="//Script" mode="script" />
</script>
</updates>
</xsl:template>

<xsl:template match="Reload">
<script>window.location.reload();</script>
</xsl:template>

<xsl:template match="Redirect">
<script>window.location="<xsl:value-of select="@href" />";</script>
</xsl:template>

<xsl:template match="Script"><!-- LEAVE THIS EMPTY --></xsl:template>
<xsl:template match="Script" mode="script"><xsl:apply-templates mode="script" /></xsl:template>

<xsl:template match="WebApplication" mode="context">
	<xsl:apply-templates />
</xsl:template>
</xsl:stylesheet>