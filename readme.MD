The information is taken from: [Docker and k8s course.](https://www.youtube.com/watch?v=kTp5xUtcalw&t=1724s&ab_channel=freeCodeCamp.org)
# DOCKER 
## Basic concept 
The images run within a container. The container can be thought as a very lightweight VM. The main difference with an actual VM is that it shares HW and Kernel with the host machine rather than virtualizing it as in an actual VM. \
Within this container the images descibe what is running: os, program installed, etc.
![vmVSContainer](images/containerVSvm.png)

### Image vs container
```
To use a programming metaphor, if an image is a class, then a container is an instance of a class.
Think of a Docker container as a running image instance. You can create many containers from the same image, each with its own unique data and state.
```
<strong> Image </strong>:  
Images are read-only templates containing instructions for creating a container. A Docker image creates containers to run on the Docker platform.
Think of an image like a blueprint or snapshot of what will be in a container when it runs.
An image is composed of multiple stacked layers, like layers in a photo editor, each changing something in the environment. Images contain the code or binary, runtimes, dependencies, and other filesystem objects to run an application. The image relies on the host operating system (OS) kernel.  

<strong> Container </strong>:  
The major difference between a container and an image is the top writable layer. Containers are running instances of Docker images with top writable layer. Containers run the actual applications. A container includes an application and all of its dependencies. When the container is deleted, the writable layer is also deleted. The underlying image remains unchanged. Containers are hopefully why you're using Docker; they're lightweight and portable encapsulations of an environment in which to run applications.



## Basic commands
- `docker info` -> display system informantion \
- `docker version` -> display system version \
- `docker login` -> log in into Docker registry 

## Main commands 
- `docker pull [imageName]` -> pull an image from the registry  
- `docker run [imageName]` -> run container and enters the container terminal  
  - `... --name [desiredName]` -> run container with the specified name and enters the container terminal  
  - `... -d ` -> run container in detached mode, i.e. the container will run in the bg, not entering the container terminal  
  - `... --publish/-p [localHostPort:80]` -> map the host port to the container listening port.  
  -  `... -it -- [shellToAttach]` -> attach a shell to a starting container
```
example complete command: docker run --publish 0.0.0.0:8080:80 --name webserver nginx
```  

- `docker start [containerName]` -> start a stopped container  
- `docker ps` -> list RUNNING containers  
  - `docker ps -a` -> list ALL containers  
- `docker stop [containerName]` -> stop a running container, but the container still resides in memory  
- `docker kill [containerName]` -> kill containers  
- `docker image inspect [imageName]` -> get image info  
- `docker container exec -it [conteinerName] -- [shellToAttach]` -> attach a shell to a running container

## Clean up commands
- `docker rm [containerName]` -> remove stopped container
- `docker rm $(docker ps -a -q)` -> remove all stopped containers
- `docker images` -> list images cached locally
- `docker rmi [imageName]` -> delete an image
- `docker system prune -a` -> removes all images not used by any container

## Build containers
- `docker build -t [name:tag] .` -> build an image using a DOCKERFILE located in the SAME folder
  - `... -f [fileName]` -> build an image using a Dockerfile located in ANOTHER folder
- `docker tag [imageName] [name:tag]` -> tag an existing image (tag is usually a version number)

### DOCKERFILE
Is a text file containing instructions (steps) on how to build an image.
Example of a dockerfile (create a Node.js app):
```
FROM alpine 
RUN apk add -update nodejs nodejs.npm
COPY . /src
WORKDIR /src
RUN npm install
EXPOSE 8080
ENTRYPOINT ["node", "./app.js"]
```
- `FROM` -> defines the starting image. When creating a new image, one always starts from an existing one. 
- `RUN` -> use to run commands inside the container
- `COPY [fromFolder] [toFolder]` -> copies everythin from the specified folder (usually .) to the specified folder
- `WORKDIR` -> define the working directory
- `EXPOSE` -> metadata, tell the container to listen on this port
- `ENTRYPOINT` -> what to run when the container is started

## Volumes
Usually date are not stored in containers as they are not saved.  
Nonetheless, if we want data to persist, it is possible to do so using volumes, which is mapped to a folder in the filesystem. Basically a logical folder in the container which maps an actual folder on the host FS.

- `docker volume create [volumeName]` -> creates a new volume
- `docker volume ls` -> list the volumes
- `docker volume inspect [volumeName]` -> diplay volume info
- `docker volume rm [volumeName]` -> deletes a volume
- `docker volume prune` -> deletes all NOT MOUNTED volumes  
  
Example:
```
# create the volume
docker volume create myvol

# run a container with that volume attached
docker run -d --name devtest -v myvol:/[mappedFolderInTheContainer] nginx:latest
docker run -d --name devtest -v myvol:/app nginx:latest


# for testing purposes we can create and map temp folders in the contaier rather than volumes
docker run -d --name devtest -v d:/test:/[mappedFolderInTheContainer] nginx:latest
```

In order to remove a volume is necessary to stop and remove from memory all containers using it. 

## Docker Compose
Docker compose allows to define a single file to run multi-containers applications. It is defined using YAML files and run using docker CLI with the compose plugin.

`docker-compose ...` is compose v1 and was replaced in 2022 by v2 that now uses `docker compose ...`. All v1 commands are working as expected.

Example of a Docker compose file:
```
version: '3.9'

services: 
  webapi1:
    image: academy.azurecr.io/webapi1
    ports:
      - '8081:80'
    restart: always
  
  webapi2:
    image: academy.azurecr.io/webapi2
    ports:
      - '8082:80'
    restart: always
  
  apigateway:
    image: academy.azurecr.io/gateway
    ports:
      - '80:80'
    restart: always
```
This docker comose file defines 3 containers.  
The names are used within each container to reference other containers. For each of them there is the image location, the internal and external port. 

Docker compose is perfect for small orcherstration. When k8s would be overkill or when developing and testing.

### Docker compose commands
- `docker compose build` -> build the images
  - `... -f [fileLocation]`-> to specify the docker compose file location
- `docker compose start` -> start the containers
- `docker compose stop` -> stop the containers
- `docker compose up` -> build and start
  - `... -d` -> run in detach mode (in bg)
- `docker compose ps` -> list what's running
- `docker compose rm` -> remove from memory
- `docker compose down` -> stop and remove
- `docker compose logs` -> get logs
- `docker compose exec [container] bash` -> open a session inside a container

### new (only v2) commands
- `docker compose --project-name (or -p) [projectName] up` -> run an instance as project
- `docker compose ls` -> list running projects
- `docker compose cp [containerId]:[srcPath] [destPath]` -> copy files FROM the container
- `docker compose cp [destPath] [containerId]:[srcPath]` -> copy files TO the container
  