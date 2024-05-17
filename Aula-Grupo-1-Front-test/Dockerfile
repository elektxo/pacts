# Build stage
FROM node:22-alpine3.18 as build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

# Runtime stage
FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=build /app/dist/aula-grupo-1-front/browser /usr/share/nginx/html

EXPOSE 80 443