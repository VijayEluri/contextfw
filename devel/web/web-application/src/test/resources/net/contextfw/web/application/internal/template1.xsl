<?xml version="1.0" encoding="UTF-8"?>
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

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:txt="http://contextfw.net/ns/txt">
	<xsl:template match="TextLabel">
		The label!
		sada pokasd
		<![CDATA[abre
rerer
  kpokpok ]]>
  	<code>ser seorkpsoekr.erer...erserkpokesr..repokporke.erpokre.erer</code> seropk psoerkpok serpokr rpokp skerposekr
  	<xsl:value-of select="@a" /> <a href="{@url}"><xsl:apply-templates select="title" /></a>
	</xsl:template>
</xsl:stylesheet>