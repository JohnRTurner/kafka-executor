# Docker Compose 

## Manually Interact with Docker and Docker Compose

### docker-compose
Start by cd to the directory containing the docker-compose.yml
- `docker-compose up -d` - starts all dockers
- `docker-compose down` - removes all dockers
- `docker-compose build` - rebuild all docker images
- `docker-compose logs` - dumps logs from all docker images

### docker
- `docker ps -a` - list all dockers
- `docker stop dockerName` - stops a docker - can get name from list
- `docker start dockerName` - starts a docker - can get name from list
- `docker logs dockerName` - display logs for a docker - can get name from list
- `docker exec -it dockerName bash` - run commands inside a docker - can get name from list

## Files
### docker-compose.yml
Defines each of the dockers

### .env
- Contains all the environment variable parameters sent to docker-compose.
- Not as secure as a docker secret, but _good enough_ for this test.

