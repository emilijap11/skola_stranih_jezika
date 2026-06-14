Sistem za upravljanje privatnom skolom stranih jezika
Tema projekta je privatna skola stranih jezika, a cilj aplikacije je da se na jednom mestu vode podaci o ucenicima, profesorima, kursevima, nastavnim grupama, terminima casova, uplatama ucenika i isplatama profesora.
U aplikaciji postoje tri vrste korisnika.
Administrator ima pristup svim podacima i moze da dodaje i uredjuje ucenike, profesore, kurseve, nastavne grupe i termine. On takodje evidentira uplate ucenika i isplate profesora i dobija podsetnike kada neka obaveza za tekuci mesec nije evidentirana.
Profesor ima pristup podacima koji su potrebni za njegov rad. Moze da doda ucenika, napravi i uredi svoju nastavnu grupu, doda ucenike u grupu, zakaze termin, otkaze ga ili oznaci kao odrzan. Profesor ne vidi podatke o uplatama, cenama i isplatama drugih profesora.
Ucenici koriste zajednicki nalog koji sluzi samo za pregled rasporeda. Na tom nalogu mogu da filtriraju raspored prema imenu ucenika ili nastavnoj grupi i da vide zakazane, odrzane i otkazane casove, ali ne mogu da menjaju podatke.

Prilikom rada aplikacija automatski proverava vise poslovnih pravila. Profesor moze da izabere samo kurs ciji jezik i nivo zaista predaje. Grupna nastava moze da ima najvise pet aktivnih ucenika, dok individualna nastava ima jednog ucenika. Dve nastavne grupe ne mogu imati isti naziv. Termin ne moze da se zakaze u proslosti, vreme zavrsetka mora biti posle vremena pocetka, a profesor ne moze imati dva termina koji se preklapaju. Sistem takodje pamti ko je zakazao ili otkazao termin.

Kod uplata se proverava da li je ucenik u izabranom mesecu vec bio aktivno upisan. Zbog toga nije moguce evidentirati uplatu za period pre pocetka njegovog upisa. Za istog ucenika, kurs, mesec i godinu nije moguce evidentirati istu uplatu dva puta. Slicno pravilo postoji i kod profesora, pa se isplata ne moze uneti za period pre njegovog angazovanja niti dva puta za isti mesec i godinu.

Backend aplikacije napisan je u programskom jeziku Java i koristi Spring Boot. Spring Web se koristi za REST kontrolere koji primaju HTTP zahteve sa korisnickog interfejsa. Spring Data JPA i Hibernate koriste se za povezivanje Java klasa sa tabelama baze i za cuvanje i citanje podataka. Podaci se lokalno cuvaju u H2 relacionoj bazi. Za proveru ulaznih podataka koristi se Spring Validation, dok Spring Security odredjuje kojim funkcijama moze da pristupi administrator, profesor ili ucenik.

Korisnicki interfejs napravljen je pomocu HTML-a, CSS-a i JavaScript-a, bez dodatnog frontend framework-a. Frontend prikazuje forme i tabele, salje zahteve backend-u i nakon svake uspesne promene ponovo ucitava podatke iz baze. Zbog toga se promena koju napravi administrator ili profesor vidi i na ostalim povezanim pregledima.

Kod je organizovan po slojevima.
Model paket sadrzi entitete i enumeracije koji predstavljaju podatke skole.
Repository paket sadrzi Spring Data JPA interfejse preko kojih se pristupa bazi.
Service paket definise dostupne poslovne operacije, dok se u service.impl paketu nalaze njihove implementacije i poslovne provere.
Controller paket prima zahteve sa frontenda i prosledjuje ih servisima.
DTO klase odredjuju koji podaci ulaze u aplikaciju i koji podaci se vracaju korisniku.
Config paket sadrzi bezbednosna i druga tehnicka podesavanja, a exception paket sluzi za obradu gresaka.

Kada korisnik, na primer, zakaze termin, JavaScript salje zahtev TerminCasaController klasi. Kontroler zahtev prosledjuje TerminCasaServiceImpl servisu. Servis preko repozitorijuma ucitava profesora, kurs i grupu, proverava da li su povezani i da li se termin preklapa sa drugim terminom. Tek kada su sva pravila ispunjena, TerminCasaRepository cuva termin u H2 bazi. Nakon toga se odgovor vraca frontend-u i novi termin se prikazuje u rasporedu.

Najvazniji entiteti u projektu su Ucenik, Profesor, Kurs, NastavnaGrupa, Upis, TerminCasa, Uplata, Isplata i KorisnickiNalog. Veza Upis povezuje ucenika sa nastavnom grupom i cuva podatak od kada je ucenik aktivan u toj grupi. NastavnaGrupa povezuje profesora sa kursom, dok TerminCasa povezuje profesora, kurs i grupu sa konkretnim vremenom odrzavanja. Enumeracije kao sto su Jezik, Nivo, TipKursa, StatusUpisa, StatusTermina, StatusUplate, StatusIsplate i UlogaNaloga ogranicavaju vrednosti koje je moguce sacuvati.

Za prijavu korisnika koristi se Spring Security. Lozinke se ne cuvaju kao obican tekst, vec kao hash dobijen pomocu PasswordEncoder komponente. Pored standardne kontrole pristupa, dodat je token vezan za pojedinacnu karticu browsera. Nakon prijave TabSessionController proverava korisnicko ime i lozinku, a TabTokenService pravi token koji se cuva u sessionStorage-u kartice. Svaki sledeci zahtev salje taj token u X-Tab-Token zaglavlju. Na ovaj nacin administrator, profesor i zajednicki ucenicki nalog mogu istovremeno biti otvoreni u razlicitim karticama istog browsera.

Kada korisnik unese neispravan podatak, servis baca BusinessException. Kada trazeni zapis ne postoji, koristi se NotFoundException. GlobalExceptionHandler ove greske pretvara u razumljiv HTTP odgovor koji frontend prikazuje korisniku. Na primer, ako korisnik pokusa da napravi grupu sa nazivom koji vec postoji, dobija poruku da nastavna grupa sa tim nazivom vec postoji.

Automatizacija razvoja i korisceni alati
Za izgradnju projekta koristi se Maven. On na osnovu pom.xml fajla preuzima potrebne biblioteke, kompajlira Java kod, pokrece testove i pravi izvrsni JAR fajl. Komanda mvn test pokrece testove, dok mvn clean verify prvo uklanja prethodne rezultate izgradnje, zatim ponovo gradi projekat, izvrsava testove i pravi JaCoCo izvestaj.

Testovi su napisani pomocu JUnit 5 okvira. Model testovi proveravaju ponasanje pojedinacnih entiteta. Servisni testovi proveravaju poslovna pravila. Mockito se u njima koristi za pripremanje zamenskih repozitorijuma sa unapred određenim odgovorima. Na taj način se ponašanje servisa može proveriti nezavisno od sadržaja lokalne H2 baze.

JaCoCo meri pokrivenost Java koda testovima. Njegov izvestaj pokazuje koje klase, metode, linije i grane uslova su izvrsene tokom testiranja. To ne znaci da je aplikacija automatski potpuno ispravna, ali pomaze da se pronadju delovi koda koji nisu provereni testovima. Izvestaj se pravi komandom mvn clean verify i nalazi se u fajlu target/site/jacoco/index.html.

SonarCloud, odnosno cloud varijanta SonarQube platforme, koristi se za analizu kvaliteta koda. On proverava moguce programske greske, bezbednosne probleme, dupliranje, slozenost i delove koda koje je tesko odrzavati. Da bi analiza radila na GitHub-u, potrebno je podesiti secret pod nazivom SONAR_TOKEN i promenljive SONAR_PROJECT_KEY i SONAR_ORGANIZATION. Secret predstavlja poverljivu vrednost koja se ne prikazuje u logovima, dok variables predstavljaju obicne konfiguracione vrednosti.

SpotBugs je dodatna staticka analiza. Za razliku od testova, on ne izvrsava poslovni scenario, vec pregleda kompajlirani Java bytecode i trazi poznate obrasce mogucih gresaka. U projektu se pokrece komandom mvn -Pquality verify.

GitHub Actions automatski pokrece definisane procese kada se kod posalje na GitHub ili kada se workflow rucno pokrene. Fajl .github/workflows/maven.yml gradi aplikaciju, pokrece testove, pravi JaCoCo izvestaj i po potrebi pokrece Sonar analizu. Fajl k6-performance.yml pokrece test opterecenja, lighthouse.yml proverava frontend, a azure-deploy.yml priprema i postavlja aplikaciju na Azure. Poseban ci.yml fajl je uklonjen jer je ponavljao Maven build i testove koji su vec postojali u maven.yml fajlu.

Azure Web App je cloud servis na kome moze da se pokrene gotova aplikacija. Azure workflow prvo preuzima kod, postavlja JDK 17, Maven komandom pravi JAR fajl, cuva ga kao artifact i zatim ga postavlja na Azure Web App. Za povezivanje GitHub-a i Azure-a potrebno je podesiti secret AZURE_WEBAPP_PUBLISH_PROFILE i promenljivu AZURE_WEBAPP_NAME. Na Azure Web App-u se takodje podesava SPRING_PROFILES_ACTIVE=azure, cime se bira konfiguracija namenjena Azure okruzenju.

Docker sluzi da se aplikacija zajedno sa potrebnim Java okruzenjem zapakuje u kontejner. Na taj nacin aplikacija moze isto da radi na macOS i Windows racunaru. Dockerfile prvo koristi Maven sliku da napravi JAR fajl, a zatim manju Java 17 sliku za njegovo pokretanje.

k6 se koristi za proveru ponasanja REST API-ja pod opterecenjem. Test postepeno povecava broj virtuelnih korisnika, salje zahteve ka glavnim endpointima i proverava vreme odgovora i broj neuspesnih zahteva. Skripta se nalazi u performance/k6/api-load-test.js. Lighthouse CI proverava korisnicki interfejs i daje rezultate za performanse, pristupacnost, dobre prakse i SEO. Njegova konfiguracija se nalazi u lighthouserc.json.

Sve ove tehnologije nisu deo poslovne logike skole, vec predstavljaju automatizaciju razvoja softvera. Njihova uloga je da se kod automatski izgradi, proveri, analizira i pripremi za postavljanje, umesto da se svaki korak obavlja rucno.

Pokretanje aplikacije
Za lokalno pokretanje potrebni su JDK 17, Maven, Git i internet browser. Projekat je konfigurisan za JDK 17 i GitHub Actions ga takodje proverava na JDK 17. Na MacBook racunaru na kome je projekat razvijan Maven trenutno koristi JDK 25, dok komanda java -version prikazuje JDK 23. Zbog toga JaCoCo 0.8.12 pri lokalnom pokretanju komande mvn verify moze da ispise upozorenje za jednu Java 25 sistemsku klasu. Izgradnja se ipak zavrsava i JaCoCo izvestaj se pravi, ali se za potpuno cistu proveru preporucuje da Maven koristi JDK 17. Java verzija koju Maven stvarno koristi proverava se komandom mvn -version.

Administrator se lokalno prijavljuje korisnickim imenom admin i lozinkom admin123. Profesorski nalozi za pocetnu proveru su profesor1 i profesor2, a oba koriste lozinku profesor123. Zajednicki ucenicki nalog koristi korisnicko ime ucenik i lozinku ucenik123. Kada se registruje novi profesor, njegov nalog se automatski kreira, a pocetna lozinka se samo jednom prikazuje osobi koja je izvrsila registraciju.

Lokalna H2 baza cuva se u fajlu skola-jezika-data.mv.db, pa podaci ostaju sacuvani i nakon ponovnog pokretanja aplikacije. H2 konzola je dostupna dok aplikacija radi .Vazno je da JDBC URL bude napisan potpuno isto kao u application.properties fajlu.

Kada se proverava da li su svi delovi povezani, administrator moze biti prijavljen u jednoj kartici, profesor u drugoj, a zajednicki ucenicki nalog u trecoj. Profesor zatim moze da napravi ili uredi svoju grupu, doda ucenika i zakaze termin. Isti termin treba da bude vidljiv administratoru, profesoru i ucenickom rasporedu. Ako profesor otkaze termin, promena se odmah cuva u bazi i prikazuje na svim povezanim pregledima.

