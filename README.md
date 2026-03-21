# Othello

## Requirements
* Java 21 
* Maven

## Install the game 
```bash
mvn clean install
```

## Run a single shown game with two agents

```
mvn exec:java -Dexec.args="<agent1> <agent2> <time1> [time2]"
```

| Argument | Values | Description |
|---|---|---|
| `agent1` | `player` / `ai` | BLACK (always goes first) |
| `agent2` | `player` / `ai` | WHITE |
| `time1` | e.g. `1000` | Thinking time in ms for agent1 (and agent2 if time2 omitted) |
| `time2` | e.g. `100` | (Optional) Thinking time in ms for agent2 |

### Examples

Player vs AI:
```bash
mvn exec:java -Dexec.args="player ai 1000"
```

AI vs AI (same time):
```bash
mvn exec:java -Dexec.args="ai ai 1000"
```

AI vs AI (different times):
```bash
mvn exec:java -Dexec.args="ai ai 1000 100"
```

Two players (local):
```bash
mvn exec:java -Dexec.args="player player 0"
```

## Run tests
```bash
mvn test
```

## Run benchmark

benchmark <number of games> <time agent black> <time agent white>

### Example
```bash
mvn exec:java -Dexec.args="benchmark 10 500 100"
```