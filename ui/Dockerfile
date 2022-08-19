# syntax=docker/dockerfile:1

# This Dockerfile performs a multi-stage build and must be built with BuildKit: `docker buildx build .`.
# By default, it will produce an optimized image with the production build, served as static content with nginx.
# Target the dev-server stage to produce a larger dev image, served over Vite dev server. Good for Skaffold/file sync.

# Copy sources and run yarn
FROM node:17-alpine as sources

WORKDIR /app

# Dependencies
COPY --link package.json ./
RUN yarn install

# Sources
COPY --link . .

# Starts Vite dev server, used by Skaffold
FROM sources as dev-server

EXPOSE 3000
ENTRYPOINT ["yarn", "run", "dev", "--host"]

# Builds the app for production
FROM sources as prod-build

ARG VITE_BASE_URL
ARG VITE_KEYCLOAK_URL
ARG VITE_KEYCLOAK_REALM
ARG VITE_KEYCLOAK_CLIENT_ID

RUN ["yarn", "run", "build"]

# Serves prod build with nginx
FROM nginx:1.23.1-alpine as prod-server

# Configure nginx
COPY --link /nginx /etc/nginx

# Copy prod files from prod-build stage
COPY --from=prod-build --link /app/dist /usr/share/nginx/html

EXPOSE 80
ENTRYPOINT ["nginx", "-g", "daemon off;"]
