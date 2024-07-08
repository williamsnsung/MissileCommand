# MissileCommand
CS4303 Video Games Practical 1 - Missile Command

## How to run
Run the following command in the submission directory:
java -jar MissileCommand.jar

## Controls
LEFT MOUSE BUTTON - launches missile
SPACEBAR          - triggers missiles
Q                 - Selects left most ballista
W                 - Selects middle ballista
E                 - Selects right most ballista

## Gameplay
- Game beings play immediately
- Ballistas are placed in the left, middle, and right of the lower screen and are seen as larger white circles
- Cities are smaller white circles between the ballista, seen in two groups of three
- Launch missiles to destroy enemies falling from the sky
    - Meteorites are red and can split after wave 2 to become 2 meteorites
    - Bombers are purple and can spawn new meteorites and begin spawning after round 2
    - Smart bombs are yellow and can avoid explosions triggered with missiles, bouncing away from the explosion
- Current wave can be seen in the top left, with more meteorites spawning each wave, with ever greater velocities
- You can increase your score by eliminating enemies
    - 25 points per meteorite/smart bomb
    - 100 for bombers
    - 5 points for each unused missile in ballistae
    - 100 points for each surviving city
- Score multiplier per round survived
    - Waves 1-2: 1x
    - Waves 3-4: 2x
    - Waves 5-6: 3x
    - Waves 7-8: 4x
    - Waves 9-10: 5x
    - Waves 11+: 6x
- Game ends when your cities (smallest white circles between ballistas) are all destroyed
    - You can regain a city every 10,000 points

GOOD LUCK HAVE FUN
