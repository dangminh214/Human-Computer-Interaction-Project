```mermaid
graph TD
    entry((fa:fa-circle))
    menu(Hauptmenü)
    settings(Einstellungen)    
    signin(Username Eingeben)
    active(Aktives Spiel)
    paused(Pausiertes Spiel)
    ended(Spiel Vorbei)
    exit((fa:fa-circle-dot))
    skinselect(Spielfigur individualisieren)

    entry-->menu
    menu-->exit

    menu-->|Zahnradsymbol|settings
    settings-.->|Username ändern|signin
    settings-->|Zurück|menu

    menu-.->|1. Appstart|signin
    signin-->|1. Appstart|menu
    signin-->|Bestätigungs Icon|settings

    menu-->|Spielfigur anpassen|skinselect
    skinselect-->|Zurück ins Hauptmenü|menu

    menu-->|tap on screen|active
    active-.->|Pausesymbol|paused
    paused-->|Weiter spielen|active
    paused-->|Hauptmenü|menu
    paused-->|Retry|active
    paused-->|Zahnradsymbol|settings
    active-.->|Spielelogik|ended
    ended-->|Retry|active
    ended-->|Home|menu


```
