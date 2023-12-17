FROM node:lts
# Set working directory
WORKDIR /app

# install node modules
COPY ./package*.json ./
RUN npm install
RUN npm install axios
RUN npm install mysql
RUN npm install jsonwebtoken 
RUN npm install crypto
RUN npm install cookie-parser

# Copy all files from current directory to working dir in image
COPY . ./

ENV PORT 80

ENTRYPOINT [ "npm", "start"]