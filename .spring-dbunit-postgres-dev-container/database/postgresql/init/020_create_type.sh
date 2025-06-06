#!/bin/bash -eu
# vi: set ft=sh :
# -*- mode: sh -*-

readonly CREATE_TYPES=(
    'demo-postgresql postgres'
)

readonly INIT_DATA_ROOT_PATH=/docker-entrypoint-initdb.d

function create_type() {
    echo "$1 create type by $2"

    _schema=$1
    _user=$2
    _create_type_sql_file=$3

    psql -f ${_create_type_sql_file} -U ${_user} -d ${_schema}
}

function create_types() {
    echo "========================================================"
    echo "Create Types"

    for row in "${CREATE_TYPES[@]}"; do
        _values=(${row[@]})

        _schema=${_values[0]}
        _user=${_values[1]}
        _create_type_sql_file=${INIT_DATA_ROOT_PATH}/sql/${_schema}/create-type/create-type.sql
        
        create_type ${_schema} ${_user} ${_create_type_sql_file}
    done

    echo "========================================================"
}

create_types
