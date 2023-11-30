# Blitzzettel


Das Wissensmanagement soll im Rahmen des Zettelstores in einer App ermöglicht werden. Dabei soll man
jederzeit und überall seine Gedanken in Form von Zetteln festhalten können und ausgewählte 
Zettel als Gedankenstütze nutzen können.

Nicht funktionale Anforderungen:
- Stabilität: Es muss beachtet werden, dass ein User mit Anfragen nicht den Server belastet. D.h. nicht zu viele Anfragen gleichzeitig geschickt werden.
- Verfügbarkeit: Ausfallzeiten der App sollen minimiert werden
- Benutzerfreundlichkeit: UI soll intuitiv gehalten sein und einheitliches Design durchgesetzt werden
- Performanz: Ladezeit von Inhalten und Suchen max. 5s
- Client-Server-Architektur: Durch die API Kommunikation zwischen der App (Client) und dem Zettelstore, der auf einem Server läuft, herstellen
- Wartbarkeit und Erweiterbarkeit: Modularität bei der App
  
Funktionale Anforderungen
1. Als User will ich durch eine Eingabe in den Einstellungen der App die Verbindung zwischen der App und dem Server, auf dem der Zettelstore gehostet wird, herstellen und dabei muss die URL des Servers, der Benutzername und Kennwort hinterlegt werden.
2. Als User will ich Zettel anlegen und will, dass diese an meinen Zettelstore gesendet werden, der auf einem Server betrieben wird, weil ich spontan einen Gedanken dokumentieren möchte und diesen weiterbearbeiten sowie passend in den Aufbau meines Zettelstore eingliedern will. Der Zettel wird als Plaintext kodiert und enthält die Metadaten title, die Rolle Zettel und den Tag #blitz.
3. Als User will ich, dass alle Zettel mit dem Tag #blitz auf der App als Liste angezeigt werden und beim Klick auf den Titel wird durch die API der Content des Blitzzettels aufgerufen, weil diese nicht weiterverarbeiteten Zettel als Gedankenstütze, Erinnerung, To Do-Liste o.ä. fungieren sollen. Diese Zettel enthalten nur Text.
4. Als User will ich Fehlermeldungen erhalten und auf eine Fehlerseite weitergeleitet werden, weil ich wissen will warum ein Vorgang nicht funktioniert hat und entsprechende Lösungsvorschläge erhalten möchte.
   
Was gehört nicht dazu:
- Zettel bearbeiten
- Zettel löschen
- Keine Childs, Verweise oder Folge Zettel anlegen
- Keine umfassende Suchen (Inhalt
