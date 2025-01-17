# MobTime

An interactive timer built on top of mob.sh that automates keyboard switching during mob programming sessions.

## Getting Started

1. Run the installation script
```
chmod +x /.install.sh && ./install.sh
```
2. Start driving
```
mobstart
```

## Requirements

- Bash
- Git
- mob.sh
- Java Runtime Environment (JRE) 21

## API & Settings

- `--start`      [command\] Start a new driving session
  - `--duration` [option\] Set session duration in minutes (15 by default)
  - `--auto`     [option\] Automatically execute mob next when time runs out
  - `--focus`    [option\] Set the focus mode (normal by default)
- `--mini`       [global\] Minimize the GUI
- `--location`   [global\] Set GUI location (north by default) 

## Acknowledgements

Special thanks to Dr. Simmon Harrer & Josen Christ for their outstanding work on mob.sh.

Deep gratitude to my colleagues at Datanumia for our exciting mob programming sessions: 
Josian Chevalier, Thierry Lam, Elie Guedj, Amine Chaari, Rym Ben-Ali, and Sébastien Lamps.

## License

MobTime is available under the [MIT License](https://opensource.org/licenses/MIT).
