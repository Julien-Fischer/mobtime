#!/usr/bin/env bash

MOBTIME_RUNTIME_DIR="${HOME}/mobtime"
MOBTIME_SRC_DIR="${HOME}/mobtime/src"
MOBTIME_HELP_DIR="${HOME}/mobtime/help"
MOBTIME_LOGS_DIR="${HOME}/mobtime/logs"

MOBTIME_JAR_FILE="${MOBTIME_SRC_DIR}/mobtime.jar"
MOBTIME_LIB_FILE="${MOBTIME_SRC_DIR}/mobtime_lib.sh"
MOBTIME_LIFECYCLE_COMMANDS_FILE="${MOBTIME_SRC_DIR}/lifecycle_commands.sh"
MOBTIME_PID_FILE="${MOBTIME_LOGS_DIR}/pids.log"
MOBTIME_LOG_FILE="${MOBTIME_LOGS_DIR}/commands.log"
MOBTIME_INFO_FILE="${MOBTIME_HELP_DIR}/info"
MOBTIME_HELP_FILE="${MOBTIME_HELP_DIR}/help"
MOBTIME_CONFIG_FILE="${MOBTIME_RUNTIME_DIR}/preferences"

LOCAL_DIR="$(pwd)/src/main/resources"
LOCAL_ALIASES_FILE="${LOCAL_DIR}/lifecycle_commands.sh"
LOCAL_MOBTIME_LIB_FILE="${LOCAL_DIR}/mobtime_lib.sh"
LOCAL_INFO_FILE="${LOCAL_DIR}/info"
LOCAL_CONFIG_FILE="${LOCAL_DIR}/preferences"
LOCAL_HELP_FILE="${LOCAL_DIR}/help"

TARGET_COMPILED_JAR_FILE="$(pwd)/target/mobtime.jar"

USER_RC_FILES=("${HOME}/.bashrc" "${HOME}/.zshrc")
USER_RC_LIFECYCLE_SOURCE_LINE="source ${MOBTIME_LIFECYCLE_COMMANDS_FILE}"
USER_RC_MOBTIME_LIB_SOURCE_LINE="source ${MOBTIME_LIB_FILE}"
MOBTIME_WATERMARK="# Created by \`MobTime\` on"

#########################################################################################
# Install & uninstall
#########################################################################################

# shellcheck disable=SC2120
mobinstall() {
    local first_install=false
    [[ "${1}" == "--first" ]] && first_install=true

    wizard_log "> Compiling MobTime..."
    wizard_log "  (This might take up to a few minutes depending on your setup)"

    if $first_install; then
        mvn clean package > /dev/null
    else
        mvn clean package
    fi

    if [[ $? -eq 0 ]]; then
        wizard_log "  OK - Compilation"
    else
        wizard_log "  E: Could not compile MobTime"
        return 1
    fi

    wizard_log "> Creating runtime directories..."
    mkdir -p "${MOBTIME_RUNTIME_DIR}" || return 1
    mkdir -p "${MOBTIME_SRC_DIR}"     || return 1
    mkdir -p "${MOBTIME_HELP_DIR}"    || return 1
    mkdir -p "${MOBTIME_LOGS_DIR}"    || return 1
    wizard_log "  OK - Runtime directories created"

    wizard_log "> Installing executable..."
    if cp "${TARGET_COMPILED_JAR_FILE}" "${MOBTIME_JAR_FILE}" && chmod +x "${MOBTIME_JAR_FILE}"; then
        wizard_log "  OK - Executable installed"
    else
        wizard_log "  E: Could not install ${TARGET_COMPILED_JAR_FILE} executable to ${MOBTIME_JAR_FILE}"
        return 1
    fi

    wizard_log "> Initializing system files..."
    touch "${MOBTIME_PID_FILE}"
    touch "${MOBTIME_LOG_FILE}"
    cp "${LOCAL_INFO_FILE}" "${MOBTIME_INFO_FILE}"
    cp "${LOCAL_HELP_FILE}" "${MOBTIME_HELP_FILE}"
    if [[ ! -f "${MOBTIME_CONFIG_FILE}" ]]; then
        cp "${LOCAL_CONFIG_FILE}" "${MOBTIME_CONFIG_FILE}"
    fi
    wizard_log "  OK - system files initialized"

    wizard_log "> Installing mobtime shared functions..."
    if cp "${LOCAL_MOBTIME_LIB_FILE}" "${MOBTIME_LIB_FILE}"; then
        wizard_log "  OK - Shared functions installed"
    else
        wizard_log "  E: Could not update ${MOBTIME_LIB_FILE} with contents from ${LOCAL_MOBTIME_LIB_FILE}"
        return 1
    fi

    wizard_log "> Installing MobTime commands..."
    if cp "${LOCAL_ALIASES_FILE}" "${MOBTIME_LIFECYCLE_COMMANDS_FILE}"; then
        wizard_log "  OK - Commands installed"
    else
        wizard_log "  E: Could not update ${MOBTIME_LIFECYCLE_COMMANDS_FILE} with contents from ${LOCAL_ALIASES_FILE}"
        return 1
    fi

    wizard_log "> Updating rc files..."
    local failed_updates=()
    for rc_file in "${USER_RC_FILES[@]}"; do
        if [[ -f "${rc_file}" ]]; then
            if ! grep -q "${USER_RC_LIFECYCLE_SOURCE_LINE}" "${rc_file}"; then
                local current_time
                current_time="$(date "+%Y-%m-%d %H:%M:%S")"
                {
                    echo ''
                    echo "${MOBTIME_WATERMARK} ${current_time}"
                    echo "${USER_RC_LIFECYCLE_SOURCE_LINE}"
                    echo "${USER_RC_MOBTIME_LIB_SOURCE_LINE}"
                } >> "${rc_file}"
                wizard_log "  -> Sourced ${MOBTIME_LIFECYCLE_COMMANDS_FILE} in ${rc_file}"
                wizard_log "  -> ${rc_file} updated"
            fi
        else
            failed_updates+=("${rc_file}")
        fi
    done
    if [[ ${#failed_updates[@]} -eq ${#USER_RC_FILES[@]} ]]; then
        wizard_log "  E: Could not update any rc file"
        wizard_log "FAILED: Could not install MobTime on your system"
        return 1
    else
        wizard_log "  OK - rc files updated successfully."
    fi
    wizard_log "DONE - MobTime installed successfully."

    if $first_install; then
        mobtime_log_lifecycle_hook "installed"
    else
        mobtime_log_lifecycle_hook "updated"
    fi
}

mobuninstall() {
    wizard_log "> Deleting mobtime runtime directory..."
    if rm -rf "${MOBTIME_RUNTIME_DIR}"; then
        wizard_log "  OK - mobtime directory deleted."
    else
        wizard_log "  E: Could not delete runtime directory at: ${MOBTIME_RUNTIME_DIR}"
    fi

    wizard_log "> Cleaning rc files..."
    local failed_removals=0
    local missing_files=()
    for rc_file in "${USER_RC_FILES[@]}"; do
        if [[ -f "${rc_file}" ]]; then
            if sed -i "/^${MOBTIME_WATERMARK}.*$/d" "${rc_file}" && \
               sed -i "\|^${USER_RC_LIFECYCLE_SOURCE_LINE}|d" "${rc_file}" && \
               sed -i "\|^${USER_RC_MOBTIME_LIB_SOURCE_LINE}|d" "${rc_file}"; then
                wizard_log "  -> Removed source line for ${MOBTIME_LIFECYCLE_COMMANDS_FILE} from ${rc_file}"
            else
                wizard_log "  E: Failed to remove source line for ${MOBTIME_LIFECYCLE_COMMANDS_FILE} from ${rc_file}"
                ((failed_removals++))
            fi
        else
            missing_files+=("${rc_file}")
        fi
    done

    if [[ ${#missing_files[@]} -eq ${#USER_RC_FILES[@]} || $failed_removals -gt 0 ]]; then
        wizard_log "  E: Could not update any rc file"
        wizard_log "FAILED: Could not completely remove MobTime from your system"
        return 1
    else
        wizard_log "  OK - rc files updated successfully."
    fi
    wizard_log "DONE - MobTime installed successfully."
}

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
    vim "${MOBTIME_CONFIG_FILE}"
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
    cd "${MOBTIME_RUNTIME_DIR}" || exit 1
    ls --color=auto -AF --group-directories-first
}

#########################################################################################
# Helper
#########################################################################################

mobtime_kill_instances() {
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
                echo '' > "${MOBTIME_PID_FILE}"
                mobtime_log "Cleared pid file: ${MOBTIME_PID_FILE}"
            else
                mobtime_log "Failed to forcefully kill mobtime instance with PID: $pid"
            fi
        fi
    done
}

mobtime_log_lifecycle_hook() {
    local hook_name="${1}"
    mobtime_log "---------- ${hook_name} ----------"
}

mobtime_log() {
    local message="${1}"
    local current_time
    current_time="$(date "+%Y-%m-%d %H:%M:%S")"
    echo "[${current_time}] ${message}" >> "${MOBTIME_LOG_FILE}"
}

wizard_log() {
    local message="${1}"
    echo "[mobtime] ${message}"
}

moblog() {
    cat "${MOBTIME_LOG_FILE}"
}
