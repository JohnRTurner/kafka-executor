FROM node:lts-alpine as build

ARG VITE_GRAFANA_URL
ARG VITE_GRAFANA_USER_PASSWORD
ARG VITE_KAFKA_ENABLE

WORKDIR /app

COPY websrc ./websrc

WORKDIR /app/websrc

RUN npm ci

RUN npm run build


# Use the official Nginx image from the Docker Hub
FROM nginx:latest

ARG WEB_USER
ARG WEB_PASSWORD
# Install the apache2-utils package to get the htpasswd command
RUN apt-get update && apt-get install -y apache2-utils  procps && rm -rf /var/lib/apt/lists/*

# Create .htpasswd file
RUN htpasswd -cb /etc/nginx/.htpasswd ${WEB_USER} ${WEB_PASSWORD}

# Copy the Nginx configuration file
COPY nginx.conf /etc/nginx/nginx.conf

# Copy the static files
#COPY ./static /usr/share/nginx/html

# Copy the built files
COPY --from=build /app/websrc/dist/ /usr/share/nginx/html/

# Expose port 80
EXPOSE 8000

# Default command to run Nginx
CMD ["nginx", "-g", "daemon off;"]