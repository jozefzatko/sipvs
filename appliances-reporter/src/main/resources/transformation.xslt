<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<style>
table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
}
</style>   
<html>
  <body>

<h3>Údaje ubytovaného</h3>
<p><b>Meno: </b><xsl:value-of select="appliance-report/accommodated_info/first_name"/></p>
<p><b>Priezvisko: </b><xsl:value-of select="appliance-report/accommodated_info/family_name"/></p>
<p><b>Dátum narodenia: </b><xsl:value-of select="appliance-report/accommodated_info/birth_date"/></p>
<p><b>Fakulta: </b><xsl:value-of select="appliance-report/accommodated_info/faculty"/></p>
<p><b>Blok: </b><xsl:value-of select="appliance-report/room_info/block"/></p>
<p><b>Číslo izby: </b><xsl:value-of select="appliance-report/room_info/room_number"/></p>

<br/>
<h3>Údaje o spotrebiči</h3>
    <table border="1" width="80%" cellpadding="7">
      <tr>
        <th style="text-align:left">Typ</th>
        <th style="text-align:left">Názov</th>
        <th style="text-align:left">Sérióvé číslo</th>
        <th style="text-align:left">Rok</th>
      </tr>
      <xsl:for-each select="appliance-report/appliances/appliance">
      <tr>
        <td><xsl:value-of select="type"/></td>
        <td><xsl:value-of select="name"/></td>
        <td><xsl:value-of select="serial_number"/></td>
        <td><xsl:value-of select="year"/></td>
      </tr>
      </xsl:for-each>
    </table>

<br/>
<p>Vyplnené v <xsl:value-of select="appliance-report/place"/> dňa <xsl:value-of select="appliance-report/date"/>.</p>

  </body>
  </html>
</xsl:template>
</xsl:stylesheet>

