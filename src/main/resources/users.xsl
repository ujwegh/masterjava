<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--<xsl:output method="xml" encoding="UTF-8" omit-xml-declaration="yes"/>-->
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/*[name()='Payload']/*[name()='Users']/*[name()='User']">
        <xsl:copy>
            <xsl:apply-templates select="@groupRef | node()"/>
        </xsl:copy>
        <!--<xsl:copy-of select="."/>-->
        <xsl:text>&#xa;</xsl:text><!-- put in the newline -->
    </xsl:template>
    <xsl:template match="text()"/>
    <!--<xsl:template match="@email | @name"/>-->

</xsl:stylesheet>