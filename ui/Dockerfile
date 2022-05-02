FROM node:17-alpine

WORKDIR /app
EXPOSE 3000
CMD ["yarn", "run", "dev"]

COPY package* ./
RUN yarn install
COPY . .
