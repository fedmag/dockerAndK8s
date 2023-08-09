# Basic concept 
The images run within a container. The container can be thought as a very lightweight VM. The main difference with an actual VM is that it shares HW and Kernel with the host machine rather than virtualizing it as in an actual VM. \
Within this container the images descibe what is running: os, program installed, etc.

# Commands
## Basic commands
docker info -> display system informantion \
docker version -> display system version \
docker login -> log in into Docker registry 

## Main commands 
docker pull [imageName] -> pull an image from the registry \
docker run [imageName] -> run container \
docker run -d [imageName] -> run container in detached mode \
docker start [imageName] -> start a stopped container \
docker ps -> list RUNNING containers \
docker ps -a -> list ALL containers \
docker stop [containerName] -> stop a running container \
docker kill [containerName] -> kill containers \
docker image inspect [imageName] -> get image info
