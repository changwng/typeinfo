<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="tableHeader">^ № ^ Property ^ DataType ^ Single or\\ Repeating ^ Distinct\\ count ^ Example ^ Label ^ Description ^</xsl:param>


    <xsl:output omit-xml-declaration="yes"/>


<xsl:template match="/">
======<xsl:value-of select="DmType/name"/> ======

STUB

|  svn                        |    [[objrefsvn<xsl:text disable-output-escaping="yes">></xsl:text><xsl:value-of select="DmType/name"/>]]   |
|  super_name                 |    <xsl:choose>
    <xsl:when test="DmType/superName=''">none</xsl:when>
    <xsl:otherwise>[[<xsl:value-of select="DmType/superName"/>]]</xsl:otherwise>
</xsl:choose>   |
|  attr_count                 |    <xsl:value-of select="DmType/attrCount"/>   |
|  start_pos                  |    <xsl:value-of select="DmType/startPos"/>   |
|  direct subtypes            |    <xsl:for-each select="DmType/subTypeList/subType">[[<xsl:value-of select="node()"/>]] </xsl:for-each>   |
|  object count               |    <xsl:value-of select="DmType/count"/> (на  <xsl:value-of select="DmType/dateOfCollectInfo"/>)   |
\\

===== Таблица атрибутов =====
<xsl:call-template name="attributeTableByDomain">
    <xsl:with-param name="domain" select="'Custom'"/>
</xsl:call-template>
<xsl:call-template name="attributeTableByDomain">
    <xsl:with-param name="domain" select="'General'"/>
</xsl:call-template>
<xsl:call-template name="attributeTableByDomain">
    <xsl:with-param name="domain" select="'System'"/>
</xsl:call-template>
<xsl:call-template name="attributeTableByDomain">
    <xsl:with-param name="domain" select="'Application'"/>
</xsl:call-template>
    <xsl:call-template name="attributeTableByDomain">
    <xsl:with-param name="domain" select="'Internal'"/>
</xsl:call-template>
    </xsl:template>


    <xsl:template match="DmType/attributeList/attribute"  name="attributeTableByDomain" >
        <xsl:param name="domain"/>

==== <xsl:value-of select="$domain"/> ====

<xsl:value-of select="$tableHeader"/>
<xsl:for-each select="DmType/attributeList/attribute[@domain=$domain]">
| <xsl:value-of
        select="pos"/> | <xsl:value-of
        select="name"/> | <xsl:value-of
        select="type"/>  | <xsl:choose>
  <xsl:when test="repeating='true'">R</xsl:when>
  <xsl:otherwise>S</xsl:otherwise>
</xsl:choose> | <xsl:value-of
        select="distinctCount"/> |   <xsl:for-each
        select="possValuesList/possValue">''<xsl:value-of
            select="normalize-space(node())"/>''  \\  </xsl:for-each>   |   ''<xsl:value-of
        select="label"/>''   |   <xsl:for-each
        select="fkReferenceList/fkReference">[[<xsl:value-of
            select="node()"/>]]  \\  </xsl:for-each> <xsl:value-of select="note"/>   |</xsl:for-each>
    </xsl:template>


</xsl:stylesheet>
