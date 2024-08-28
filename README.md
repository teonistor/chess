# ** Archived **

because I'm combining all the games I made as programming exercies here:
https://github.com/teonistor/draughts/pull/4  
(That repo will proably be renamed _katagames_ soon enough)

# Chess
An unnecessary programming exercise to make a chess game, complete with two other people setting off for the same challenge so that we might see whose result is the greatest:
 * [Dev](https://github.com/dixit81/Chess)
 * [Rhys](https://github.com/rhys-saldanha/chess)

## Bonus features
 * A game mode where parallel moves are possible
 * Ability to save and load a game

## How to run it
Currently there are two runners to choose from depending on what interface you want:
1. Run the terminal interface (will stop after one game)  
  a. From an IDE: run class `io.github.teonistor.chess.term.TerminalLauncher` OR  
  b. With Maven: run `mvn verify` to build a JAR file then run it
2. Run the web interface standalone  
   From an IDE: run class `io.github.teonistor.chess.spring.WebLauncher`
3. Integrate the web interface into an existing Spring application  
   Run `mvn install` in this project, add it as a dependency to the other, then annotate any configuration class in your other project with `@io.github.teonistor.chess.spring.EnableChess`. There are caveats around this to do with setting up web sockets and the object mapper though.

To begin once the web interface is running, point your browser to `/chess/front.html`
