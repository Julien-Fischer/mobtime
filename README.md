# mobtime

An interactive timer built on top of mob.sh that automates keyboard switching during mob programming sessions.

## Getting Started

1. Clone this project

```
git clone https://github.com/Julien-Fischer/mobtime.git
```

2. Run the installation wizard

```
./install.sh
```

## How to use

A mobtime session typically involves two steps:

1. `mobstart` to start driving:
2. Stop driving. You can either type:
    - `mobnext` to pass the keyboard to someone else, or
    - `mobdone` to end the mob session 

## Requirements

- Bash
- Git
- mob.sh
- Java Runtime Environment (JRE) 21
- Maven (when installing mobtime for the first time)

This software was designed for Linux, and was primarily tested on Debian 12 and Kubuntu 24. 

## API & Settings

- `--start`      (Command) Start a new driving session
  - `--duration` (Option)  Set session duration in minutes (15 by default). Accepts: any positive integer
  - `--auto`     (Option)  Automatically execute mob next when time runs out
  - `--focus`    (Option)  Set the focus mode (normal by default). Accepts: normal, chill, zen
- `--mini`       (Option)  Minimize the GUI
- `--location`   (Option)  Set GUI location (north by default). Accepts: center, north, north-east, east, south-east, etc...
- `--user-name`  (Option)  Set a username for this session (Driver by default)

### Examples

- `mobstart` Start a driving session with default duration
- `mobstart 7` Start a 7-minute driving session
- `mobstart --auto` Automatically execute `mobnext` when time runs out
- `mobstart --duration=7 --focus=zen --location=north-east --mini` Start a 7-minute driving session in Zen mode with a minimalist UI. The timer will be displayed in the top-right corner of the screen.

## Bash commands

### High-level

| Command        | Description                                   |
|----------------|-----------------------------------------------|
| `mobstart`     | Start a new driving session                   |
| `mobnext`      | End the driving session and pass the keyboard |
| `mobdone`      | End the mob session                           |

### Low-level

| Command        | Description                                   |
|----------------|-----------------------------------------------|
| `mobhelp`      | Print an help message and exit                |
| `mobstatus`    | View the status of the current mob session    |
| `mobconfig`    | Define your personal preferences              |
| `mobinfo`      | Print mobtime version and authors             |
| `mobupdate`    | Update mobtime from the source code           |
| `mobdir`       | Navigate to mobtime runtime directory         |
| `moblog`       | View mobtime logs for debugging               |
| `mobps`        | List all mobtime running instances            |
| `mobkill`      | Kill all instances of mobtime                 |
| `mobuninstall` | Uninstall mobtime                             |

## Acknowledgements

Special thanks to Dr. Simmon Harrer & Josen Christ for their outstanding work on mob.sh.

Deep gratitude to my colleagues at Datanumia for our exciting mob programming sessions: 
Josian Chevalier, Thierry Lav, Elie Guedj, Amine Chaari, Rym Ben-Ali, and Sébastien Lamps.

## License

mobtime is available under the [MIT License](https://opensource.org/licenses/MIT).
