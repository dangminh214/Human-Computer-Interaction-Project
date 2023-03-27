**Main Menu:**
- ZahnradIcon Button: wechselt zu Settings
- Username Anzeige ist rechts oben 
- Highscore Anzeige ist rechts oben
- Mit klicken auf den Bildschirm beginnt das Spiel (der background ist clickable also alles was kein Button oder Anzeige ist zählt dazu)

![image](Game Screens Pictures/main_menu_landscape.png)

![image](Game Screens Pictures/main_menu_portrait.png)

**Settings**
- Einstellungen sind über Schieberegler einstellbar
- Pfeil Button: navigiert zurück
- Change Username Button: wechselt zu username_entry

![image](Game Screens Pictures/settings_landscape.png)

![image](Game Screens Pictures/settings_portrait.png)

**username_entry**
- Textfeld: Username eingeben
- Pfeil Button links oben: navigiert zurück
- Pfeil Button rechts am Textfeld wird die Funktion zum wechseln des Usernames ausführen, falls nichts eingegeben wurde ändert sich der Username nicht. Es wird ein Toast ausgegeben entweder: change failed due to missing input, oder Username has been changed.

![image](Game Screens Pictures/username_entry_landscape.png)

![image](Game Screens Pictures/username_entry_portrait.png)

![image](Game Screens Pictures/username_entry_success.png)

![image](Game Screens Pictures/username_entry_failed.png)

**active_game**
- unten mittig steht der aktuelle Score
- Pause Button: wechselt zu paused_game
- Hier wird die Spielelogik ausgeführt
- Spielelogik führt durch herunterfallen zu Game Over. Dies navigiert dann zu ended_game
- Berührung des Screens (überall außer Pause Button) führt zu Gravitationswechsel der Spielfigur
- das Spiel geht bis Game Over.
- Es gibt 4 verschiedene Level Segmente die in zufälliger Reihenfolge immer wieder hintereinander kommen.

![image](Game Screens Pictures/active_game_landscape.png)

![image](Game Screens Pictures/active_game_portrait.png)

![image](Game Screens Pictures/level_ground.png)

![image](Game Screens Pictures/level_stairs.png)

![image](Game Screens Pictures/level_ramp.png)

![image](Game Screens Pictures/level_flipflop.png)

![image](Game Screens Pictures/ninja_upsidedown.png)


**paused_game**
- Es ist ein PopupDialog
- Main Menu Button: wechselt zu main_menu
- Settings Button: wechselt zu Settings
- Retry Button: wechselt zu active_game und startet neu
- Resume Button: wechselt zu active_game und fährt an der Stelle fort an der pausiert wurde

![image](Game Screens Pictures/paused_game_landscape.png)

![image](Game Screens Pictures/paused_game_portrait.png)

**ended_game**
- Anzeige des in diesem Spiel erreichten Scores
- Anzeige des Highscores des Users
- Highscore wird geupdatet falls in diesem Spiel ein höherer Score erreicht wurde
- Main Menu Button: wechselt zu main_menu
- Retry Button: wechselt zu active_game und startet neu

![image](Game Screens Pictures/ended_game_landscape.png)

![image](Game Screens Pictures/ended_game_portrait.png)
