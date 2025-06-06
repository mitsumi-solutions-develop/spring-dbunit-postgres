#!/bin/bash -eu
# vi: set ft=sh :
# -*- mode: sh -*-

readonly REPLACE_SCHEMAS=(
    'demo-postgresql postgres'
)

readonly INIT_DATA_ROOT_PATH=/docker-entrypoint-initdb.d

function replace_schema() {
    echo "Replace $1 schema by $2"

    _schema=$1
    _user=$2
    _replace_schema_sql_file=$3

    psql -f ${_replace_schema_sql_file} -U ${_user} -d ${_schema}
}

function replace_schemas() {
    echo "========================================================"
    echo "Replace schemas"

    for row in "${REPLACE_SCHEMAS[@]}"; do
        _values=(${row[@]})

        _schema=${_values[0]}
        _user=${_values[1]}
        _replace_schema_sql_file=${INIT_DATA_ROOT_PATH}/sql/${_schema}/replace-schema/replace-schema.sql
        
        replace_schema ${_schema} ${_user} ${_replace_schema_sql_file}
    done

    echo "========================================================"
}

replace_schemas
