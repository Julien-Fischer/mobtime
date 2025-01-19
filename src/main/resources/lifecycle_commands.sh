#!/usr/bin/env bash

source "$(pwd)/src/main/resources/mobtime_lib.sh"

#########################################################################################
# MobTime lifecycle commands
#########################################################################################

mobstart() {
    local parameters=()
    local -A defaults

    local preference_file="${MOBTIME_CONFIG_FILE}"

    if [[ -f "${preference_file}" ]]; then
        while IFS= read -r line; do
            [[ "$line" =~ ^#.*$ || -z "$line" ]] && continue
            if [[ "$line" =~ ^--(.+) ]]; then
                key="${BASH_REMATCH[1]}"
                defaults["$key"]="${line}"
            fi
        done < "${preference_file}"
    else
        mobtime_log "W: Preference file not found at ${preference_file}"
    fi

    if [[ $1 =~ ^[0-9]+$ ]]; then
        parameters+=("--duration=$1")
        shift
    fi

    for param in "$@"; do
        if [[ "$param" =~ ^--(.+) ]]; then
            key="${BASH_REMATCH[1]}"
            unset "defaults[$key]"
        fi
        parameters+=("$param")
        mobtime_log "[override] override parameter ${defaults[$key]}"
    done

    for key in "${!defaults[@]}"; do
        parameters+=("${defaults[$key]}")
        mobtime_log "[default] adding missing default ${defaults[$key]}"
    done

    mobtime_log_lifecycle_hook "mobstart"
    mobtime_kill_instances

    if [[ ! -f "${MOBTIME_JAR_FILE}" ]]; then
        mobtime_log "E: mobtime.jar not found at ${MOBTIME_JAR_FILE}"
        return 1
    fi

    if mob start --include-uncommitted-changes; then
        java -jar "${MOBTIME_JAR_FILE}" --start "${parameters[@]}" &
        if [[ $? -eq 0 ]]; then
            local mobtime_pid=$!
            echo "${mobtime_pid}" >> "${MOBTIME_PID_FILE}"
            mobtime_log "Add instance pid ${mobtime_pid} to pid file: ${MOBTIME_PID_FILE}"
        else
            mobtime_log "E: Could not start mobtime instance"
            return 1
        fi
    else
        mobtime_log "E: mob start failed"
    fi
}

mobnext() {
    mobtime_log_lifecycle_hook "mobnext"
    mobtime_kill_instances
    mob next
}

mobdone() {
    mobtime_log_lifecycle_hook "mobdone"
    mobtime_kill_instances
    mob done
}
