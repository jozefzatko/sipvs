# Spracovanie informácií v podnikaní a verejnej správe
**Semestrálny projekt**

### Autori:
- Daniela Hajdu
- Erik Bujna
- Jozef Zaťko

### Použité technológie:
* Java 8 EE (32bit)
* D.Signer/XAdES .NET (verzia 3)
* Maven
* JACOB v1.8
* JAXB

### 1. časť
* aplikácia umožňujúca vyplnenie formulára (nahlásenie el. spotrebičov ubytovaného na internáte)
* vygenerovanie XML a jeho uloženie na disk
* validácia voči XSD schéme
* XSL(T) transormácia do HTML

### 2. časť
* odoslanie vygenerovaného XML do aplikácie DSigner, ktorá zabezpečí elektronický podpis
* uloženie podpísaného XML dokumentu na disk


### Import aplikácie
**pre Windows, Java 8 32bit a Eclipse IDE 32bit**

**1.Naklonovanie git repozitára**
```
git clone https://github.com/jozefzatko/sipvs.git
```

**2. Nastavenie ako maven projekt pre Eclipse**
```
cd appliances-reporter
mvn eclipse:eclipse
```

**3. Build aplikácie vrátane stiahnutia potrebných knižníc a vygenerovania tried pomocou JAXB**
```
mvn install
mvn generate-sources
```

### Inštalácia a nastavenie DSigneru
1. Inštalácia .NET aspoň verzie 3.5

2. Stiahnutie DSigner-u v3.0 [TU](https://www.slovensko.sk/_img/CMS4/DSignerV3.zip)

3. Stiahnutie certifikátu (podpisovatel.cer) a privátneho kľúča (podpisovatel.pfx) vrátane inštrukcií [TU](http://test.ditec.sk/fiit/cvicenie2.zip) (obsahuje ajdokumentáciu k DSigner-u a testovacie tlačivo na podpis)

4. Registrácia pluginov DSigner-u do .NET-u (vyžaduje administrátorské práva, treba zmeniť cestu podľa Vašej inštalácie)
```
C:\Windows\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe /verbose /nologo /codebase "C:\Program Files (x86)\Ditec\DSigXades\Ditec.Zep.DSigXades.dll"
```
```
C:\Windows\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe /verbose /nologo /codebase "C:\Program Files (x86)\Ditec\DSigXades\Ditec.Zep.DSigXades.XmlPlugin.dll"
```

### Spustenie aplikácie
```
Run as -> Java Application
```