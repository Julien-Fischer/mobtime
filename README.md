# MobTime

An interactive timer built on top of mob.sh that automates keyboard switching during mob programming sessions.

## Getting Started

1. Clone this project

```
git clone https://github.com/Julien-Fischer/mobtime.git
```

2. Run the installation script

```
./install.sh
```

## How to use

A MobTime session typically involves two steps:

1. `mobstart` to start driving:
2. Stop driving. You can either type:
    - `mobnext` to pass the keyboard to someone else, or
    - `mobdone` to end the mob session 

## Requirements

- Bash
- Git
- mob.sh
- Java Runtime Environment (JRE) 21

This software was primarily tested on Linux, on Debian 12 and Kubuntu 24. 

## API & Settings

- `--start`      [command\] Start a new driving session
  - `--duration` [option\] Set session duration in minutes (15 by default). Accepts: any positive integer
  - `--auto`     [option\] Automatically execute mob next when time runs out
  - `--focus`    [option\] Set the focus mode (normal by default). Accepts: normal, chill, zen
- `--mini`       [global\] Minimize the GUI
- `--location`   [global\] Set GUI location (north by default). Accepts: center, north, north-east, east, south-east, etc...

### Examples

- `mobstart` Start a driving session with default duration
- `mobstart 7` Start a 7-minute driving session
- `mobstart --auto` Automatically execute `mobnext` when time runs out
- `mobstart --duration=7 --focus=zen --location=north-east --mini` Start a 7-minute driving session in Zen mode with a minimalist UI. The timer will be displayed in the top-right corner of the screen.

## Acknowledgements

Special thanks to Dr. Simmon Harrer & Josen Christ for their outstanding work on mob.sh.

Deep gratitude to my colleagues at Datanumia for our exciting mob programming sessions: 
Josian Chevalier, Thierry Lam, Elie Guedj, Amine Chaari, Rym Ben-Ali, and Sébastien Lamps.

## License

MobTime is available under the [MIT License](https://opensource.org/licenses/MIT).
