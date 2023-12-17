# FROM: tells Docker what base image to use as a starting point.
FROM node:latest
# Set working directory
# WORKDIR: changes the active directory.
WORKDIR /app

# install node modules
COPY ./package*.json ./

# RUN: executes commands inside the container.
RUN npm install 
RUN npm install --save-dev nodemon
COPY . ./

ENV PORT 80

ENTRYPOINT [ "npm", "start"]