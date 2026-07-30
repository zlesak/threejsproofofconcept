# Nálezy z automatizovaného průchodu

Zdroj: `e2e/usability.spec.ts` spuštěný proti čerstvě nasazenému stacku (`infra/docker-compose.yml`),
plán viz [user-testing-plan.md](user-testing-plan.md). Surová data se zapisují do
`test-results/usability-report.md`, snímky obrazovek do `test-results/usability/`.

## Naměřené hodnoty

| Úloha | Role | Cíl | Dokončeno | Čas (s) | Interakcí |
|---|---|---|---|---|---|
| T1 | vyučující | Nahrát 3D model s texturami a CSV | ano | 21,3 | 9 |
| T2 | vyučující | Vytvořit kapitolu s modelem | ano | 6,1 | 5 |
| T4 | vyučující | Najít výsledky studentů | ano | 0,8 | 0 |
| T5 | vyučující | Najít kapitolu ve výpisu | ano | 0,7 | 1 |
| S1 | student | Najít a otevřít kapitolu | ano | 1,4 | 1 |
| S2 | student | Zobrazit 3D model kapitoly | ano | 0,2 | 0 |
| S3 | student | Najít seznam kvízů | ano | 0,6 | 0 |
| S4 | student | Najít vlastní výsledky | ano | 0,7 | 0 |

Všech 8 úloh dokončeno. Studentské úlohy jsou pod 1,5 s a stojí nejvýše jedno kliknutí — ta část
aplikace je rychlá a přímočará. Veškerá zátěž je na straně vyučujícího.

## Nálezy

### 1. Nahrání modelu je nejdražší úkon aplikace — *vážné*

T1 trvá **21 s a stojí 9 interakcí**, tedy zhruba tolik jako všech sedm zbývajících úloh dohromady.
Příčina je v tom, že každý typ souboru má vlastní tlačítko a vlastní dialog: model, hlavní textura,
další textura, CSV. Vyučující musí čtyřikrát projít výběrem souboru a pokaždé počkat na náhled.

*Návrh:* jedna plocha pro přetažení všech souborů najednou s automatickým zařazením podle přípony
(`.obj`/`.glb` → model, `.jpg` → textura, `.csv` → mapa oblastí) a s možností zařazení ručně
opravit. Odhadem by to snížilo počet interakcí z devíti na dvě.

### 2. Výsledek kvízu nemá vlastní adresu — *vážné*

Po odeslání se výsledek vykreslí na místě, na adrese `/playQuiz/{id}`. Student si výsledek nemůže
uložit do záložek ani poslat odkaz, a tlačítko Zpět v prohlížeči jej vrátí do rozpracovaného kvízu,
který už nelze odeslat. Route `/quiz-result/{id}` přitom existuje a používá ji výpis pokusů.

*Návrh:* po odeslání přejít na `/quiz-result/{id}`. Chování bude konzistentní s výpisem pokusů
a Zpět povede na seznam kvízů.

Tento nález vyšel najevo při opravě testu `student-access`, který na změnu adresy čekal.

### 3. Prázdná aplikace nemá kudy začít — *vážné*

Na čerstvé instalaci nelze vytvořit kapitolu: dialog „Vybrat model" je prázdný, bez vysvětlení
a bez cesty dál. Uživatel musí sám uhodnout, že nejdřív musí do jiné sekce nahrát model.

Dokládá to i testovací sada: dokud jsem nedoplnil `e2e/fixtures.setup.ts`, který si model a kapitolu
předem vytvoří, polovina scénářů nad prázdnou databází spadla přesně na tomto místě.

*Návrh:* v prázdném dialogu zobrazit vysvětlení a tlačítko „Nahrát nový model", které povede rovnou
na formulář.

### 4. Úklid 3D scény po odebrání souboru je nespolehlivý — *vážné*

Ve formuláři pro model se po odebrání souboru `.obj` model občas nechá ve 3D scéně. E2e test
`model-assets-visibility` na tomto kroku ve třech ze čtyř běhů selhal a v jednom prošel, takže nejde
o trvalou chybu, ale o závod mezi odebráním souboru a úklidem scény (`ThreeJSScene` /
`ModelManager`). Pro uživatele to znamená, že po odebrání modelu občas zůstane vidět starý.

*Poznámka:* chování je starší než převod backendu a s daty nesouvisí.

*Návrh:* úklid scény navázat na dokončení odebrání souboru místo na samostatnou událost a doplnit
o čekání na dokončení předchozího načtení.

### 5. Hledání dříve nesahalo do obsahu — *opraveno*

Vyhledávací pole u výpisu kapitol porovnávalo pouze název, takže „najdi kapitolu o mozku" fungovalo
jen tehdy, když uživatel trefil název. Nyní se dotaz vyhodnocuje i proti fulltextovému indexu obsahu
a obojí se sjednotí.

### 6. Smazání modelu tiše rozbíjelo kapitoly — *opraveno*

Model šlo smazat i tehdy, když jej používala kapitola; ta se pak vykreslila rozbitá. Nově je mazání
odmítnuto se zprávou, která kapitoly vyjmenuje.

## Co automat neověří

Čísla výše říkají, kolik kroků úloha stojí, ale ne, jestli uživatel ví, **který** krok udělat.
Nálezy 1 a 3 jsou přesně toho druhu, kde měření jen potvrzuje podezření — skutečnou váhu jim dá až
sezení s pěti vyučujícími podle plánu v [user-testing-plan.md](user-testing-plan.md). Doporučuji je
uspořádat před obhajobou; každé sezení trvá 45 minut a odhalí i pojmy, které jsou pro uživatele
nesrozumitelné (například „Pokročilý model").
