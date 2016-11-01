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

# API DOCS

Dokumentacija trenutno aktivnog `API` dostupna je, nakon uspešnog pokretanja, na  `/swagger-ui.html` stranici.

# Spring Security podešavanje i objašnjenje
U datoteci **application.properties** je dodata konfiguracija za *header*, *secret* i *expiration* tokena.
```
awt_test.token.header=X-Auth-Token
awt_test.token.secret=SECRET-KEY
awt_test.token.expiration=54000
```
 
U klasi `CorsFilter` koja se nalazi u paketu `configuration` najbitnija je metoda `doFilter(...)` u kojoj postavljamo dodatna zaglavlja u `response`.
 
U klasi `WebSecurityConfigurerAdapter` paketa obratiti pažnju na metodu `configure` u kojoj definišemo pristupne preduslove za metode našeg REST servisa. U samoj metodi imaju komentari pa se nadam da je to dovoljno.
 
### Paket `security`
- Klasa `AuthenticationTokenFilter` u svojoj najbitnijoj metodi `doFilter(...)` izdvaja zaglavlje tokena iz *HTTP requesta* i dobavlja *username* (u našem slučaju *email*). Ako postoji, onda dobavlja korisnika iz baze i kreira instancu `SecureUser`-a. Pokušava da validira token metodom `validateToken(...)`iz klase `TokenUtils`.
- Klasa `TokenUtils` sadrži metoda `generateToken(UserDetails userDetails)` koju koristimo prilikom generisanja tokena autentikovanom korisniku. `this.secret` je tajni ključ našeg servisa (koji samo mi znamo) i nalazi se u `aplication.properties` datoteci.
- Klasa `EntryPointUnauthorizedHandler` šalje __response__ ukoliko je korisnik pokušao da pristupi __UNAUTHORIZED__.
- Klasa `AuthenticationTokenFilter` presreće svaki zahtjev (_request_) i uzima __X-Auth-Token__ zaglavlje iz zahtjeva, dobija korisničko ime (u našem slučaju __email__) iz tokena i nakon validiranja tokena (ako je uspješan) postavlja autorizaciju u zadati kontekst.

###### NOTE: Need to do code refactor for `TokenUtils` class

### Paket `users`
- Klasa `UserFactory` dodata je samo da bi na osnovu insatance klase `User` kreirala instancu klase `SecurityUser`.
- Klasa `SecurityUser` implementira interfejs `UserDetails` i sadrži najbitnija polja vezana za klasu `User` kao i polja i metode obavezna implementacijom
- Klasa `UserController` sadrži metode koje predstavljaju API našeg servisa. U metodi `api/users/auth` vršimo **autentikaciju** korisnika tako što proslijedimo *email* i *password* putem našeg zahtjeva. Dobavimo korisnika, generišemo token i taj token vratimo korisniku.

__P.S.__ Kad budete dodavali korisnika u bazu kao ulogu mu dodajte __admin__ ili bilo šta od predviđenih tipova, a kao __šifru__ ubacite ovaj string __$2a$10$XGPks3H6/RuF1F/H1BakmuH.9y9VbsBWYNGAQd1CwbOrr1EQbo0nS__ koji je već _encode_-ovana vrijednost od __userpass__.
