# awt-test-siit-project-2016
Predmetni projekat iz predmeta Konstrukcija i testiranje softvera i Napredne veb tehnologije. 

# Članovi tima:
- SW3-2013  Stefan Ristanović
- SW9-2013  Bojan Blagojević
- SWF-2013  Dragutin Marjanović
- SW20-2013 Aleksa Zrnić

# Uputstvo za pokretanje

IDE projekta je **IntelliJ IDEA Ulitmate**. **NAPOMENA:** IntelliJ Community ne podržava Spring.

Potrebno je importovati Maven projekat i odabrati klonirani repozitorijum.


U fajlu **application.properties** se nalaze podešavanja vezana za aplikaciju. Trenutno, podešeno je:

* **server.port = 8091** što znači da će se server startovati na tom portu
* Hibernate
* Kredencijali za pristup bazi podataka (**username = awt**, **password = awt**)

Da bi se aplikacija pravilno pokrenula potrebno je prethodno kreirati korisnika unutar **MySQL baze podataka** sa gore navedenim kredencijalima, a zatim za datog korisnika napraviti šemu sa imenom **awt** sljedećom komandom:

```CREATE SCHEMA awt``` 


Nakon dodavanja projekta i podešavanja baze podataka, odabrati **View -> Tool Windows -> Maven Projects**. Dalje, u Maven konzoli kucati ```mvn clean install```, pa zatim ```mvn spring-boot:run```.
