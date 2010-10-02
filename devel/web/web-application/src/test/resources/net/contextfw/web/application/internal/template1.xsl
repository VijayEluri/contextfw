<?xml version="1.0" encoding="UTF-8"?>
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