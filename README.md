# GardenMania 

Szeretnék egy kis segítséget adni a pontozáshoz, leírtam, hogy melyik pontot nagyjából hol valósítottam meg :)

###### Bármi probléma esetén keress fel nyugodtan (Bene Martin, DU0038, Büne#6780)

## Fordítási és futtatási hiba nincs
  * nekem nem volt

## Firebase autentikáció meg van valósítva
  * Nekem működött, a LoginActivity és a RegisterActivity kezeli

## Adatmodell definiálása
  * A User osztály az ilyen, RegisterActivity-ben a regisztráció során egy olyan objektumot töltök fel a Firesotre-ra

## Legalább 3 különböző activity használata
  * Összesen 6 activity-m van, ami el is érhető: main, login, register, search, favourite, profile

## Beviteli mezők beviteli típusa megfelelő
  * A login és a register résznél vannak beviteli mezők

## ConstraintLayout és még egy másik layout típus használata
  * Constraint sok helyen van, pl: activity_favourite.xml
  * Használok RelativeLayout-ot a list_item.xml-ben
  * FrameLayout-ot a cart_item_number.xml-ben

## Reszponzív
  * Igyekeztem mindegyikhez csinálni értelmes land-view-t is, meg amihez nagyon kellett tablet-view-t is, majd látod mennyire ítéled igényesnek

## Legalább 2 különböző animáció használata
  * A res/anim-on belül van két .xml amik animációért felelősek
  * Egyik azért felel, hogy a föoldalon beússzon a szöveg
  * A másik azért, hogy a terméklistánál előjöjjenek a card-ok

## Intentek használata
  * Mind a 6 activity elérhető, és intentekkel mászkálok mindenhol

## Legalább egy Lifecycle Hook használata
  * Több helyen is használok, pl. SearchActivity.java-ban legalul van onPause()
  * Ott mentek el pár adatot a SharedPreferences-be, hogy megmaradjanak későbbre/más activity-k is elérhessék

## Legalább egy olyan androidos erőforrás használata, amihez kell android permission
  * AndroidManifest.xml-ben kérek engedélyeket
  * Háttértár, kamera erőforrást használok, a ProfileActivity-nél lehet fényképet (profilképet készíteni)

## Legalább egy notification
  * Egy notificationom van, amikor van a kosárban termék, akkor ha rámegyünk a kosárra, akkor küld értesítést arról, hogy sikeresen leadtad a rendelést
  * Van hozzá egy NotificationHandler.java és több helyen is használom, pl. a SearchActivity onOptionsSelected() method-on belül a cart-nál

## CRUD műveletek
  * Mindegyik meg van valósítva, de AsyncTask-ot nem használok
  * Create: pl. RegisterActivity-ben sikeres regisztráció esetén lementem a user adatait
  * Read: pl. ProfileActivity-nél a queryData() method-nál lekérem a user adatait, és kiíratom a képernyőre a nevét, és megjelenítem a profilképét
  * Update: pl ProfileActivity-nél az onActivityResult()-ban ha csináltunk egy új fényképet akkor azt feltölti az adatbázisba, lecseréli az előzőt
  * Delete: pl: ProfileActivity-nél a deleteProfile()-ban tudja a felhasználó törölni a saját profilját

## Legalább 2 komplex lekérdezést
  * nem csináltam egyet se, ne keresd
