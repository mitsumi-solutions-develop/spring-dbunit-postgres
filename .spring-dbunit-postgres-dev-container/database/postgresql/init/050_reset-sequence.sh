#!/bin/bash -eu
# vi: set ft=sh :
# -*- mode: sh -*-

readonly RESET_SEQUENCES=(
    'demo-postgresql postgres'
)

readonly INIT_DATA_ROOT_PATH=/docker-entrypoint-initdb.d

function reset_sequence() {
    echo "Reset $1 sequence by $2"

    _schema=$1
    _user=$2
    _reset_sequence_sql_file=$3

    psql -f ${_reset_sequence_sql_file} -U ${_user} -d ${_schema}
}

function reset_sequences() {
    echo "========================================================"
    echo "Reset sequences"

    for row in "${RESET_SEQUENCES[@]}"; do
        _values=(${row[@]})

        _schema=${_values[0]}
        _user=${_values[1]}
        _reset_sequence_sql_file=${INIT_DATA_ROOT_PATH}/sql/${_schema}/reset-sequence/reset_sequence.sql
        
        reset_sequence ${_schema} ${_user} ${_reset_sequence_sql_file}
    done

    echo "========================================================"
}

reset_sequences
