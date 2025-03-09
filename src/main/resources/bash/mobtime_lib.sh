#!/usr/bin/env bash

MOBTIME_RUNTIME_DIR="${HOME}/mobtime"
MOBTIME_SRC_DIR="${HOME}/mobtime/src"
MOBTIME_HELP_DIR="${HOME}/mobtime/help"
MOBTIME_LOGS_DIR="${HOME}/mobtime/logs"
MOBTIME_COMMANDS_DIR="/usr/local/bin"

MOBTIME_JAR_FILE="${MOBTIME_SRC_DIR}/mobtime.jar"
MOBTIME_LIB_FILE="${MOBTIME_SRC_DIR}/mobtime_lib.sh"
MOBTIME_PID_FILE="${MOBTIME_LOGS_DIR}/pids.log"
MOBTIME_LOG_FILE="${MOBTIME_LOGS_DIR}/commands.log"
MOBTIME_INFO_FILE="${MOBTIME_HELP_DIR}/info"
MOBTIME_HELP_FILE="${MOBTIME_HELP_DIR}/help"
MOBTIME_CONFIG_FILE="${MOBTIME_RUNTIME_DIR}/preferences"

LOCAL_DIR="$(pwd)/src/main/resources"
LOCAL_BASH_DIR="${LOCAL_DIR}/bash"
LOCAL_COMMANDS_DIR="${LOCAL_BASH_DIR}/commands"

LOCAL_MOBTIME_LIB_FILE="${LOCAL_BASH_DIR}/mobtime_lib.sh"
LOCAL_INFO_FILE="${LOCAL_DIR}/info"
LOCAL_CONFIG_FILE="${LOCAL_DIR}/preferences"
LOCAL_HELP_FILE="${LOCAL_DIR}/help"

TARGET_COMPILED_JAR_FILE="$(pwd)/target/mobtime.jar"

USER_RC_FILES=("${HOME}/.bashrc" "${HOME}/.zshrc")
USER_RC_MOBTIME_LIB_SOURCE_LINE="source ${MOBTIME_LIB_FILE}"
MOBTIME_WATERMARK="# Created by \`mobtime\` on"

#########################################################################################
# Install & uninstall
#########################################################################################

mobtime_spinner_pid=

# shellcheck disable=SC2120
mobinstall() {
    local first_install=false
    [[ "${1}" == "--first" ]] && first_install=true

    mobtime_require_dependency git "git" "sudo apt update && sudo apt install git -y"
    mobtime_require_dependency mvn "Maven" "sudo apt update && sudo apt install maven -y"
    mobtime_require_dependency mob "mob.sh" "curl -sL install.mob.sh | sh -s - --user"
    if ! is_java_21_installed; then
        return 1
    fi

    mobtime_require_sudo "install"

    wizard_log -t "> Creating runtime directories..."
    local runtime_directories=(
        "${MOBTIME_RUNTIME_DIR}"
        "${MOBTIME_SRC_DIR}"
        "${MOBTIME_HELP_DIR}"
        "${MOBTIME_LOGS_DIR}"
    )
    for dir in "${runtime_directories[@]}"; do
        if ! mkdir -p "${dir}"; then
            wizard_log -t "E: Failed to create directory '${dir}'" >&2
            return 1
        fi
    done
    wizard_log -t "  OK - Runtime directories created"

    if $first_install; then
        mobtime_log_lifecycle_hook "install"
    else
        mobtime_log_lifecycle_hook "update"
    fi

    wizard_log -a "> Compiling mobtime..."
    wizard_log -t "  (This might take up to a few minutes depending on your setup)"

    mobtime_loader_start '[mobtime]  '

    if $first_install; then
        mvn clean package >> "${MOBTIME_LOG_FILE}"
    else
        mvn clean package | tee -a "${MOBTIME_LOG_FILE}"
    fi

    mobtime_loader_stop

    if [[ $? -eq 0 ]]; then
        wizard_log -a "  OK - mobtime compiled successfully"
    else
        wizard_log -a "  E: Could not compile mobtime"
        return 1
    fi

    mobtime_install "Java executable" "${TARGET_COMPILED_JAR_FILE}" "${MOBTIME_JAR_FILE}" -x
    mobtime_install "mobtime shared functions" "${LOCAL_MOBTIME_LIB_FILE}" "${MOBTIME_LIB_FILE}"

    wizard_log -a "> Installing mobtime commands..."
    local mobtime_commands=("${LOCAL_COMMANDS_DIR}/"*)
    local i=0
    local length=${#mobtime_commands[@]}
    for file in "${mobtime_commands[@]}"; do
        ((i++))
        wizard_log -f "  -> $(basename "${file}") (${i}/${length})"
        if chmod +x "${file}"; then
            wizard_log -f "     Granted execute permission"
        else
            wizard_log -a "  E: Failed to grant execute permission" >&2
            return 1
        fi
        if sudo cp "${file}" "${MOBTIME_COMMANDS_DIR}"; then
            wizard_log -f "     Installed"
        else
            wizard_log -a "  E: Could not install ${file} to ${dest}"
            return 1
        fi
    done
    wizard_log -a "  OK - mobtime commands installed"

    wizard_log -a "> Initializing system files..."
    touch "${MOBTIME_PID_FILE}"
    touch "${MOBTIME_LOG_FILE}"
    cp "${LOCAL_INFO_FILE}" "${MOBTIME_INFO_FILE}"
    cp "${LOCAL_HELP_FILE}" "${MOBTIME_HELP_FILE}"
    if [[ ! -f "${MOBTIME_CONFIG_FILE}" ]]; then
        cp "${LOCAL_CONFIG_FILE}" "${MOBTIME_CONFIG_FILE}"
    fi
    wizard_log -a "  OK - system files initialized"

    wizard_log -a "> Updating rc files..."
    local failed_updates=()
    for rc_file in "${USER_RC_FILES[@]}"; do
        if [[ -f "${rc_file}" ]]; then
            if ! grep -q "${MOBTIME_WATERMARK}" "${rc_file}"; then
                local current_time
                current_time="$(date "+%Y-%m-%d %H:%M:%S")"
                {
                    echo ''
                    echo "${MOBTIME_WATERMARK} ${current_time}"
                    echo "${USER_RC_MOBTIME_LIB_SOURCE_LINE}"
                } >> "${rc_file}"
                wizard_log -f "  -> Sourced ${USER_RC_MOBTIME_LIB_SOURCE_LINE} in ${rc_file}"
                wizard_log -f "  -> ${rc_file} updated"
            fi
        else
            failed_updates+=("${rc_file}")
        fi
    done
    if [[ ${#failed_updates[@]} -eq ${#USER_RC_FILES[@]} ]]; then
        wizard_log -a "  E: Could not update any rc file"
        wizard_log -a "FAILED: Could not install mobtime on your system"
        return 1
    else
        wizard_log -a "  OK - rc files updated successfully."
    fi

    wizard_log -a "DONE - mobtime installed successfully."
}

mobuninstall() {
    mobtime_require_sudo "uninstall"

    wizard_log -t "> Deleting mobtime commands..."
    local commands=("mobstart" "mobnext" "mobdone")
    local command_count=${#commands[@]}
    local deleted=0
    for file in "${commands[@]}"; do
        file_path="${MOBTIME_COMMANDS_DIR}/${file}"
        if [[ -f "${file_path}" ]]; then
            sudo rm "${file_path}"
            wizard_log -t "  -> Deleted: ${file_path}"
            ((deleted++))
        else
            wizard_log -t "  E: Could not find command ${file_path}"
        fi
    done
    if [[ $deleted -eq $command_count ]]; then
        wizard_log -t "  OK - mobtime commands deleted."
    else
        local missing=$((command_count-deleted))
        wizard_log -t "  E: Failed to delete ${missing} out of ${command_count}."
    fi

    wizard_log -t "> Deleting mobtime runtime directory..."
    if rm -rf "${MOBTIME_RUNTIME_DIR}"; then
        wizard_log -t "  OK - mobtime directory deleted."
    else
        wizard_log -t "  E: Could not delete runtime directory at: ${MOBTIME_RUNTIME_DIR}"
    fi

    wizard_log -t "> Cleaning rc files..."
    local failed_removals=0
    local missing_files=()
    for rc_file in "${USER_RC_FILES[@]}"; do
        if [[ -f "${rc_file}" ]]; then
            if sed -i "/^${MOBTIME_WATERMARK}.*$/d" "${rc_file}" && \
               sed -i "\|^${USER_RC_MOBTIME_LIB_SOURCE_LINE}|d" "${rc_file}"; then
                wizard_log -t "  -> Removed source line for ${USER_RC_MOBTIME_LIB_SOURCE_LINE} from ${rc_file}"
            else
                wizard_log -t "  E: Failed to remove source line for ${USER_RC_MOBTIME_LIB_SOURCE_LINE} from ${rc_file}"
                ((failed_removals++))
            fi
        else
            missing_files+=("${rc_file}")
        fi
    done

    if [[ ${#missing_files[@]} -eq ${#USER_RC_FILES[@]} || $failed_removals -gt 0 ]]; then
        wizard_log -t "  E: Could not update any rc file"
        wizard_log -t "FAILED: Could not completely remove mobtime from your system"
        return 1
    else
        wizard_log -t "  OK - rc files updated successfully."
    fi
    wizard_log -t "DONE - mobtime installed successfully."
}

#########################################################################################
# Misc commands
#########################################################################################

mobupdate() {
    mobinstall
}

mobinfo() {
    cat "${MOBTIME_INFO_FILE}"
}

mobstatus() {
    mob status
    echo ""
    mobps
}

mobconfig() {
    "$(get_user_preferred_editor)" "${MOBTIME_CONFIG_FILE}"
}

mobhelp() {
    cat "${MOBTIME_HELP_FILE}"
}

mobps() {
    local count
    count=$(ps aux | grep "[m]obtime" | wc -l)
    echo "mobtime instances currently running: ${count}"

    content=$(tr -d '[:space:]' < "${MOBTIME_PID_FILE}")
    if [[ -n "$content" ]]; then
        echo ""
        echo "PID file contains:"
        echo "$content"
        echo ""
        echo "Running processes:"
    fi

    if [[ $count -gt 0 ]]; then
        echo ""
        ps aux | grep "[m]obtime"
    fi
}

mobdir() {
    echo "${MOBTIME_RUNTIME_DIR}"
}

mobnav() {
    cd "$(mobdir)" || return 1
    ls --color=auto -AF --group-directories-first
}

mobkill() {
    local pid_list
    mapfile -t pid_list < <(ps aux | grep '[m]obtime' | awk '{print $2}')

    for pid in "${pid_list[@]}"; do
        if kill "${pid}" > /dev/null 2>&1; then
            mobtime_log "Killed: $pid"
            echo '' > "${MOBTIME_PID_FILE}"
            mobtime_log "Cleared pid file: ${MOBTIME_PID_FILE}"
        else
            echo "Failed to kill mobtime instance with PID: $pid"
            if kill -9 "${pid}" > /dev/null 2>&1; then
                mobtime_log "Forcefully killed: $pid"
                mobtime_log "Cleared pid file: ${MOBTIME_PID_FILE}"
            else
                mobtime_log "Failed to forcefully kill mobtime instance with PID: $pid"
            fi
        fi
    done
    echo '' > "${MOBTIME_PID_FILE}"
}

moblog() {
    cat "${MOBTIME_LOG_FILE}"
}

#########################################################################################
# Lib helpers
#########################################################################################

get_user_preferred_editor() {
    if command -v "${EDITOR}" >/dev/null 2>&1; then
        echo "${EDITOR}"
    elif command -v "${VISUAL}" >/dev/null 2>&1; then
        echo "${VISUAL}"
    elif command -v vim >/dev/null 2>&1; then
        echo "vim"
    elif command -v vi >/dev/null 2>&1; then
        echo "vi"
    elif command -v nano >/dev/null 2>&1; then
        echo "nano"
    elif command -v emacs >/dev/null 2>&1; then
        echo "emacs"
    elif command -v ed >/dev/null 2>&1; then
        echo "ed"
    else
        echo "E: No suitable editor found" >&2
        mobtime_log "E: No suitable editor found"
        return 1
    fi
}

is_java_21_installed() {
  if command -v java &> /dev/null; then
      java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
      if [ "$java_version" -lt 21 ]; then
          echo "E: Java 21+ is required. Current Java version: ${java_version}" >&2
          print_java_instructions
          return 1
      else
          return 0
      fi
  else
      echo "E: Java is not installed" >&2
      print_java_instructions
      return 1
  fi
}

print_java_instructions() {
    echo "   You can install Java 21 using:"
    echo "      wget https://download.oracle.com/java/21/latest/jdk-21_linux-x64_bin.deb"
    echo "      sudo dpkg -i jdk-21_linux-x64_bin.deb"
}

mobtime_require_sudo() {
    local action="${1}"
    echo "You are about to ${action} mobtime."
    if ! sudo -v; then
        echo "E: This script requires sudo privileges. Please provide valid credentials."
        exit 1
    fi
}

mobtime_require_dependency() {
    local command_name="${1}"
    local command_display_name="${2}"
    local installation_instructions="${3}"
    if ! command -v "${command_name}" &> /dev/null; then
        echo "E: ${command_name} is not installed" >&2
        echo "   You can install ${command_display_name} using:"
        echo "       ${installation_instructions}"
        exit 1
    fi
}

mobtime_log_lifecycle_hook() {
    local hook_name="${1}"
    local separator="------------------------------"
    mobtime_log "${separator} ${hook_name} ${separator}"
}

mobtime_log() {
    local message="${1}"
    local current_time
    current_time="$(date "+%Y-%m-%d %H:%M:%S")"
    echo "[${current_time}] ${message}" >> "${MOBTIME_LOG_FILE}"
}

wizard_log() {
    local flag="${1}"
    local message="${2}"
    if [[ "${flag}" = -f || "${flag}" = -a ]]; then
        mobtime_log "${message}"
    fi
    if [[ "${flag}" = -t || "${flag}" = -a ]]; then
        echo "[mobtime] ${message}"
    fi
}

mobtime_install() {
    local display_name="${1}"
    local src="${2}"
    local dest="${3}"
    local executable=false
    if [[ "${4}" == "-x" ]]; then
        executable=true
    fi

    wizard_log -a "> Installing ${display_name}..."
    if $executable && chmod +x "${src}"; then
        wizard_log -f "  -> Granted execute permission"
    fi
    if sudo cp "${src}" "${dest}"; then
        wizard_log -a "  OK - ${display_name} installed"
    else
        wizard_log -a "  E: Could not install ${display_name} to ${dest}"
        exit 1
    fi
}

mobtime_loader_start() {
    set +m
    echo -n "$1 "
    { while : ; do
        for i in {1..8}; do
            printf "\r%s[%${i}s•%$((9-i))s]" "$1 " "" ""
            sleep 0.1
        done
    done & } 2>/dev/null
    mobtime_spinner_pid=$!
}

mobtime_loader_stop() {
    { kill -9 $mobtime_spinner_pid && wait; } 2>/dev/null
    set -m
    echo -en "\033[2K\r"
}

mobtime_spinner_start() {
    set +m
    echo -n "$1 "
    { while : ; do for X in ' • ' ' • ' ' • ' ; do
        echo -en "\b\b\b\b\b\b\b\b$X"
        sleep 0.1
    done ; done & } 2>/dev/null
    mobtime_spinner_pid=$!
}

mobtime_spinner_stop() {
    { kill -9 $mobtime_spinner_pid && wait; } 2>/dev/null
    set -m
    echo -en "\033[2K\r"
}
