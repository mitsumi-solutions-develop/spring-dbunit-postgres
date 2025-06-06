#!/bin/bash -eu
# vi: set ft=sh :
# -*- mode: sh -*-

readonly CSV_LOAD_SCHEMAS=(
    'demo-postgresql postgres'
)

readonly DUMP_LOAD_SCHEMAS=(
)

function csv_load() {
    echo "========================================================"

    _schema=$1
    _user=$2
    _csv_data_file=$3
    _sql_file_name=/docker-entrypoint-initdb.d/sql/copy.sql
    _table_name=$4
    _columns=$5

    echo "Load ${_schema} by ${_csv_data_file}, ${_sql_file_name}"

    # must be superuser to COPY to or from a file
    psql -U ${_user} -d ${_schema} -f "${_sql_file_name}" -v csv_file="'"${_csv_data_file}"'" -v table_name=${_table_name} -v columns=${_columns}
}

function csv_load_schema() {
    echo "========================================================"
    echo "Load $1 by csv data file"

    _schema=$1
    _user=$2
    _csv_data_directory=$3
    _csv_data_files=`ls ${_csv_data_directory}/*.csv`

    for _csv_data_file in ${_csv_data_files}; do
        _array=(${_csv_data_file//\// })
        _array_size=${#_array[@]}
        _csv_data_file_name=${_array[_array_size - 1]}
        _head_line=$(head -n 1 $_csv_data_file)
        _columns=${_head_line//\"/}
        _temp_table_name=${_csv_data_file_name//\.csv/}
        _table_name=`echo ${_temp_table_name} | sed 's/^\([0-9]\+\)\(-\)//g'`

        csv_load ${_schema} ${_user} ${_csv_data_file} ${_table_name} ${_columns}
    done
}

function csv_load_schemas() {
    echo "========================================================"
    echo "Load schemas by csv data file"

    for row in "${CSV_LOAD_SCHEMAS[@]}"; do
        _values=(${row[@]})

        _schema=${_values[0]}
        _user=${_values[1]}
        _csv_data_directory=/docker-entrypoint-initdb.d/csv-data/${_schema}/UTF-8
        
        csv_load_schema ${_schema} ${_user} ${_csv_data_directory}
    done

    echo "========================================================"
}

function load_dump_file() {
    echo "========================================================"

    _schema=$1
    _dump_file=/docker-entrypoint-initdb.d/dump/${_schema}/${_schema}.dump

    echo "Load $1 by ${_dump_file}"

    psql -U ${_schema} -d ${_schema} < ${_dump_file}
}

function dump_load_schemas() {
    echo "========================================================"
    echo "Load schemas by dump file"

    for row in "${DUMP_LOAD_SCHEMAS[@]}"; do
        _values=(${row[@]})
        _schema=${_values[0]}

        load_dump_file ${_schema}
    done

    echo "========================================================"
}

csv_load_schemas

dump_load_schemas
