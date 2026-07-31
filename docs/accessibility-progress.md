# Přístupnostní zadání — stav rozpracování

Zdroj: *Implementační zadání · MISH APP frontend — Úpravy UI pro soulad s WCAG 2.2 AA a sjednocení
na komponentách Vaadin 25* (projekt „UI Mish design úpravy“).

Zadání se dělí na osm skupin. Doporučené pořadí commitů podle zadání je
**G → N → L + A → Q → M → V**, po každé skupině spustit `npx playwright test` a `mvn test`.

## Hotovo

| Úkol | Co bylo | Co je |
|---|---|---|
| **G-1** Jazyk dokumentu | `<html>` bez atributu | `<html lang="cs">` |
| **G-2** Barevné tokeny | Chybová červená 4,0 : 1, úspěch 3,6 : 1, primární v tmavém režimu 2,3 : 1 | `#C4141A`, `#1C6B45`, `#FF8F95` — všechny nad 4,5 : 1 |
| **G-3** Indikátor zaměření | `outline: 0` + poloprůhledný stín ≈ 2,3 : 1 | Neprůhledný rámeček 3 px v barvě textu, `outline-offset: 2px` |
| **G-4** Skip link a orientační body | Žádný skip link, chybí `<main>` | Skip link jako první prvek, obsah v `<main id="obsah">`, `<nav>` s `aria-label` |
| **G-5** Jména ikonových tlačítek | Jen `title` | `aria-label` na spouštěči mobilní navigace |
| **G-6** Oznamování stavu | Nikde `setAssertive` | Chyby a varování asertivně, úspěch a info zdvořile |
| **G-6** *(vedlejší nález)* | `new ErrorNotification(...)` místo `this(...)` | Opraveno — dosud byla **každá jednoargumentová chybová hláška neviditelná** |
| **G-7** Lišta cookies | Jediné tlačítko „Rozumím“ | Rovnocenné „Přijmout“ / „Odmítnout“, fokus se přesune do lišty, odmítnutí se respektuje |

Ověřeno v `e2e/accessibility.spec.ts` (jazyk, orientační body, skip link a volba cookies na všech
routách vyučujícího) a jednotkovými testy, které bez opravy konstruktoru padají.

## Rozhodnutí, která zadání nechávalo na zadavateli

**Způsob vykreslení seznamů (kapitola 1).** Zvolena varianta **B — řádky**, tedy výchozí volba
zadání. Netýká se zatím implementace, rozhodne se s úkolem L-2.

**G-7 — zrušit lištu, nebo doplnit odmítnutí?** Aplikace ukládá jen dvě cookies: session
s přihlášením a `themeMode`, který se zapíše až když uživatel sám přepne motiv. Obojí spadá pod
„nezbytné, resp. výslovně vyžádané uživatelem“, takže by šlo lištu zrušit úplně. Zvolil jsem menší
zásah — lištu ponechat a doplnit jí skutečnou volbu. Zrušení zůstává legitimní alternativou.

**V-3 — textová alternativa modelu.** Vyžaduje nové doménové pole. Zadání to označuje za jedinou
povolenou výjimku z mantinelu „datový model se nemění“, ale s podmínkou schválení. Zatím
neimplementováno.

## Zbývá

| Skupina | Rozsah | Poznámka |
|---|---|---|
| **N** Sdílené komponenty | `PageHeader`, `EntityRow`, `ListingToolbar`, `StatusBadge` | Stojí na nich L, A, Q, V |
| **L** Seznamy entit | L-1 … L-5 | Většina práce v `AbstractListingView` |
| **A** Administrace | A-1 … A-3 | Veze se s L |
| **Q** Kvízy | Q-1 … Q-7 | Q-5 zahrnuje časovač a upozornění minutu před vypršením; Q-7 je klávesová alternativa ke klikání do textury |
| **M** Úvodní stránka | M-1 … M-5 | M-2 (zastavitelné GIFy) je nejzávažnější nález na této stránce |
| **V** Kapitola a 3D prohlížeč | V-1 … V-4 | Nejnáročnější; klávesová obsluha scény dnes neexistuje vůbec |
| **Z** Ověření | Z-2 … Z-5 | Ruční průchod klávesnicí, odečítačem a při 400 % přiblížení |

### Poznámka k Z-1

Zadání navrhuje `@axe-core/playwright` s nulovou tolerancí pro `serious` a `critical`. Zavedl jsem
místo toho úzké strukturální kontroly, protože axe hodnotí celou stránku včetně všeho, čeho se
skupiny N–V zatím nedotkly — hlásil by tedy na každém běhu tytéž známé nálezy a přestal by se číst.
Jakmile budou skupiny hotové, je axe správný nástroj a přidání je otázka jednoho reportéru.

## Mimo repozitář (K)

Přihlašovací obrazovka patří Keycloaku; popisky, chybové hlášky a jejich kontrast se neopravují
ve Vaadin kódu, ale nasazením přístupného Keycloak tématu. Prohlášení o přístupnosti podle EAA se
vztahuje i na poskytovatele identity, takže tento bod nelze vynechat.
