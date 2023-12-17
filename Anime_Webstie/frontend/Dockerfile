FROM node:lts
# Set working directory
WORKDIR /app

# install node modules
COPY ./package*.json .
RUN npm install
RUN npm install axios
RUN npm install multer

# Copy all files from current directory to working dir in image
COPY . .

ENV PORT 80

ENTRYPOINT [ "npm", "start"]