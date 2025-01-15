#!/usr/bin/env bash

readonly script_name=mobtime.jar

mvn clean package && chmod +x "target/${script_name}" && cp "target/${script_name}" "${HOME}"
