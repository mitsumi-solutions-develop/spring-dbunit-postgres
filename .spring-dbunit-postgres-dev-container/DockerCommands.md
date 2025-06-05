
# docker commands

## stop all docker containers

```shell
docker stop $(docker ps -q)
```

## remove all docker containers

```shell
docker rm $(docker ps -q -a)
```

## remove all docker images

```shell
docker rmi $(docker images -q) -f
```

## docker compose up 

```shell
docker compose up -d --build
```

## docker compose down

```shell
docker compose down
```

- down with volumes

  ```shell
  docker compose down --volumes
  ```

# db

- url: localhost:15432
- username: postgres
- password: postgres


## dump local db

- db list

    - demo-postgresql

### dump

- connect mybatis-generator-demo-postgresql--container

```shell
docker exec -it mybatis-generator-demo-postgresql--container bash
```

- cd dump directory

```shell
cd /docker-entrypoint-initdb.d/dump/
ls -l
```

- set variable

  ```shell
  db=demo-postgresql
  ```

- dump
  ```shell
  pg_dump -U ${db} ${db} > ${db}/${db}.dump
  ls -l ${db}/
  ```
