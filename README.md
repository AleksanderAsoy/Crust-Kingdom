# INF112 Project – *Crust Kingdom*

* Team: *ByteFight* (Gruppe 2): *Aleksander Æsøy, Vegard Hauge, Martin Skrolsvik, Snorre Bue*
* https://git.app.uib.no/inf112/25v/proj/bytefight

## Om spillet
Crust Kingdom er et tile-basert tycoon-spill der du bygger og driver din egen pizzarestaurant. Du starter som en enkel pizzarestaurant og jobber deg opp til å bygge et pizzaimperium. For å lykkes, må du ta imot bestillinger, lage pizza, levere dem til kundene, og bruke inntektene til å oppgradere restauranten din samtidig som du holder sjappa gående. Oppretthold en god karma underveis i spillet for å bli belønnnet! Vær på utkikk etter tyver og spesielle VIP Npcs som gir både ulemper og fordeler. 

## Hovedmål:
Bygg og utvikle din egen pizzarestaurant.
Lag og lever pizza for å tjene penger.
Bruk inntektene til å kjøpe oppgraderinger og pizzaer for å effektivisere salg.
Utvikle nye pizzatyper og eventuelt andre retter for å ekspandere menyen.
Samle prestisje og oppnå god karma for å bli belønnet.

## Tutorial

Du starter spillet som en kokk på din egen pizza-restaurant, og her er målet å tjene mest mulig på salg av pizzaer til kunder. Du starter med et inventory som består av 1 pizzatype hver, og må kontinuerlig kjøpe pizzaer i butikken for å kunne lage de. Kunder vil plassere en ordre ved disken, og du vil få opp et ark som signaliserer dette. For å godta/avslå en ordre må du stå ved innsiden av counteren. Du vil da få opp en ordre tabell med ordrer oppe til høyre. Du trykker "2" for å godta en ordre, og "3" for å avslå. Dersom du ikke godtar/avslår en pizza innen tiden løper ut, vil kunden forlate restauranten, og du vil få svekket karma. For å få god karma er det derfor viktig å godta flest mulig ordrer. Du starter med 5.0 i karma, der 0.0 er minst og 10.0 er maks. Dårlig karma vil til slutt føre til tap, og god karma vil kunne gi belønninger. For å lage pizzaen må du gå bort til ovnen, og du vil da kunne trykke "E" for å lage pizzaen du godtok. Du må da fullføre et lite minigame som avgjør om du får en boost i karma eller en senking. Pizzaen vil bli laget uansett, men det lønner seg å gjøre det skikkelig. Tyver har en 5% sjanse til å ankomme restauranten og stjele halve saldoen, men dette kan kun skje etter 1 minutt spilletid. VIP Npc´s kan oppstå når som helst med en 5% sjanse, og vil gi dobbel betaling dersom spilleren fullfører ordren. Når du har bygget opp en høyere saldo, kan du bruke penger på upgrades i butikken. Her vil du kunne kjøpe forskjellige fordeler som vil hjelpe til å effektivisere produksjonen. Hver tiende dag vil det også bli trekt rent på 10$, så det er viktig å ha penger i saldoen. 

## Kjøring
* Kompileres med `mvn package`.
* Kjøres med `java -jar target/crust-kingdom-by-bytefight-1.0-SNAPSHOT-fat.jar`
* Krever Java 17 eller senere

## Kontroller
* "WASD" for bevegelse av spiller
* "WASD" for bevegelse i butikk
* "ENTER" for kjøp av produkt i butikk
* "E" for Steking og Leveranse av pizza
* "E" for å åpne butikk
* "E" for å interagere med minigame. 
* "Tall 2, 3" for godta/avslå ordrer ved counter

## Utvikling
### SonarQube
Prosjektet bruker SonarQube for kodeanalyse. For å kjøre SonarQube lokalt:

1. Sett opp miljøvariabler:
   ```
   export SONAR_HOST_URL=<sonar-server-url>
   export SONAR_TOKEN=<sonar-token>
   export SONAR_PROJECT_KEY=no.uib.ii.inf112:gdx-template-25v
   ```

2. Kjør SonarQube-analyse:
   ```
   mvn clean verify sonar:sonar
   ```

3. Se resultater på SonarQube-dashbordet:
   ```
   https://inf112.puffling.no/sonarqube/dashboard?id=inf112_25v_proj_bytefight
   ```

## ARKITEKTUR
Spillet følger en MVC-arkitektur (Model-View-Controller):

Model (modell):
Inneholder spillets datastrukturer og logikk
Klasser som GameLogic, Player, GameObject, TileManager, OrderManager, PizzaManager, osv.
Håndterer spilltilstand, objekter, og spillregler

View (visning):
Håndterer rendering og visning av spillet
Klasser som RenderManager, MenuRenderer, HUDRenderer, WorldRenderer, GamePanel
Ansvarlig for å tegne spillobjekter, menyer og brukergrensesnitt

Controller (kontroller):
Håndterer brukerinput og kommunikasjon mellom modell og visning
Hovedsakelig representert av KeyHandler-klassen
Tolker tastaturinput og sender kommandoer til modellen

### Hvordan objekter opprettes

Initialisering i Main-klassen:
Spillet starter i Main.java som oppretter grunnleggende komponenter
Først opprettes GameConfig som inneholder konfigurasjonsverdier
Deretter opprettes modellkomponenter som Player, TileManager, OrderManager, osv.
Disse komponentene sendes til GameLogic som koordinerer dem
KeyHandler opprettes og kobles til GameLogic
Til slutt opprettes visningskomponenter som RenderManager og GamePanel

Spillobjekter (GameObject):
Representerer fysiske objekter i spillverdenen (bord, ovner, disker)
Opprettes og administreres av ObjectManager
Har egenskaper som posisjon, bilde, om de er solide (kan kollideres med) og interaktive

Manager-klasser:
Spillet bruker flere "manager"-klasser som hver håndterer en spesifikk type objekter:

ObjectManager: Håndterer statiske spillobjekter som bord og ovner

TileManager: Håndterer fliser/tiles som utgjør spillverdenen

PizzaManager: Håndterer pizzaer og matlaging

OrderManager: Håndterer bestillinger fra kunder

NPCManager: Håndterer ikke-spillbare karakterer (kunder)

GameEconomyManager: Håndterer spillets økonomi og dag-system

### Spillsløyfe:
Spillet kjører i en kontinuerlig sløyfe i GamePanel

I hver iterasjon:
Oppdateres spilltilstanden via gameLogic.update()
Renderes spillverdenen via renderManager.render()
Tastaturinput fanges opp av KeyHandler og påvirker spilltilstanden
Spesifikke detaljer om objektopprettelse
GameObject: Opprettes i ObjectManager.addObject() med posisjon, bilde, og egenskaper

Player: Opprettes i Main og initialiseres med standardverdier

NPCs: Opprettes og administreres av NPCManager

Pizzaer: Opprettes og administreres av PizzaManager

Arkitekturen følger god praksis med separasjon av ansvar, der:

Main har ansvar for å opprette og koble sammen komponentene

Modellklasser håndterer spillogikk og data

Visningsklasser håndterer rendering

Kontroller-klasser håndterer brukerinput


## Credits
