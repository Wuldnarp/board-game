# Othello

## Requirements
* Java 21 
* Maven

## Install the game 
```bash
mvn clean install
```

## Run the game
```bash
mvn exec:java
```
### Arguments for the game
The two agents in the game and the AI thinking time can be given with these arguments
```bash
-Dagent1=player -Dagent2=ai -Dtime=1000
```
Agent1 is always starting and is black

## Run tests
```bash
mvn test
```