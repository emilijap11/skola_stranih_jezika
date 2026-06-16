# Vodic za Git granu, Pull Request i GitHub Actions

Izmene se ne unose direktno u `main`. Za svaki zadatak pravi se posebna grana, izmene se salju na GitHub, zatim se otvara Pull Request ka `main` grani. Pull Request se spaja tek kada obavezne GitHub Actions provere uspesno zavrse.

## 1. Provera trenutnog stanja

Komande se pokrecu iz direktorijuma projekta:

```bash
cd "/Users/emilijaprvulovic/Desktop/fon master/Аутоматизација развоја софтвера/skola-stranih-jezika 2"
git status
git branch --show-current
```

Pre pravljenja grane treba proveriti da li postoje sopstvene nesacuvane izmene. Tudje ili nepoznate izmene ne treba brisati.

## 2. Pravljenje posebne grane

Naziv grane treba kratko da opisuje izmenu:

```bash
git switch -c feature/obavezni-testovi
```

Primeri naziva su `feature/obavezni-testovi`, `feature/nova-grupa` ili `fix/uplate`.

## 3. Lokalno izvrsavanje svih testova

Pre slanja koda pokrece se:

```bash
mvn clean verify
```

Komanda mora da se zavrsi porukom `BUILD SUCCESS`. U rezultatu treba da pise `Skipped: 0`, jer se testovi ne preskacu.

Detaljni rezultati nastaju u:

```text
target/surefire-reports
target/site/jacoco/index.html
```

## 4. Cuvanje izmena i slanje grane

```bash
git status
git add .github/workflows Dockerfile GITHUB_VODIC.md
git commit -m "Run tests in all build workflows"
git push -u skola_stranih_jezika feature/obavezni-testovi
```

Remote repozitorijum ovog projekta zove se `skola_stranih_jezika`. Ako se na drugom racunaru zove `origin`, poslednja komanda je:

```bash
git push -u origin feature/obavezni-testovi
```

## 5. Otvaranje Pull Request-a

Na GitHub repozitorijumu otvoriti karticu **Pull requests**, zatim izabrati **New pull request**.

- `base` grana treba da bude `main`;
- `compare` grana treba da bude `feature/obavezni-testovi`;
- opis treba kratko da navede sta je promenjeno i kako je provereno.

Predlog opisa:

```text
Uklonjeno je preskakanje testova iz k6, Lighthouse i Docker build procesa.
Svi workflow-i sada izvrsavaju Maven verify.
Glavni workflow cuva Surefire i JaCoCo izvestaje kao artifact.
Lokalno je izvrseno 37 testova: 0 failures, 0 errors, 0 skipped.
```

## 6. GitHub Actions provere na Pull Request-u

Na Pull Request-u treba sacekati sledece provere:

- **Build, test and SonarQube analysis / build-analyze** pokrece `mvn verify`, izvrsava testove i cuva testne i JaCoCo izvestaje;
- **k6 performance test / k6-test** prvo izvrsava testove, zatim pokrece aplikaciju i proverava API pod opterecenjem;
- **Lighthouse frontend quality / lighthouse** prvo izvrsava testove, zatim proverava frontend;
- Sonar analiza se izvrsava kada su podeseni `SONAR_TOKEN`, `SONAR_PROJECT_KEY` i `SONAR_ORGANIZATION`.

Test i coverage izvestaji mogu se preuzeti sa dna stranice izvrsene GitHub Actions akcije, u delu **Artifacts**, pod nazivom `test-and-coverage-reports`.

## 7. Spajanje u main

Pull Request se spaja tek kada su obavezne provere zelene. Posle spajanja pokrecu se akcije vezane za `main`, ukljucujuci Azure build i deploy.

Azure deploy korak se izvrsava kada su podeseni:

```text
AZURE_WEBAPP_NAME
AZURE_WEBAPP_PUBLISH_PROFILE
```

Ako Azure podaci nisu podeseni, aplikacija se i dalje gradi i testira, dok se samo deploy korak preskace.

## 8. Zastita main grane

Na GitHub-u otvoriti:

```text
Settings -> Rules -> Rulesets
```

Napraviti pravilo za `main` i ukljuciti:

- zahtev da se izmene unose kroz Pull Request;
- zabranu direktnog push-a na `main`;
- zahtev da status checks prodju pre spajanja;
- kao obavezne checks izabrati build/test, k6 i Lighthouse provere.

Na ovaj nacin kod ne moze biti spojen u `main` dok testovi i ostale obavezne provere ne prodju.
