# Build stage: Compile the Angular application
FROM node:20-alpine AS build
WORKDIR /app

# Copy package.json and package-lock.json (if available) first for better caching
COPY package*.json ./
RUN npm ci

# Copy the rest of the application code
COPY . .

# Build the application in production mode
RUN npm run build -- --configuration production

# Runtime stage: Serve the built application using nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Remove default nginx static content
RUN rm -rf ./*

# Copy the built app from the build stage
COPY --from=build /app/dist/neuralforge_ui/browser /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Set proper permissions
RUN chown -R appuser:appgroup /usr/share/nginx/html && \
    chmod -R 755 /usr/share/nginx/html && \
    chown -R appuser:appgroup /var/cache/nginx && \
    chown -R appuser:appgroup /var/log/nginx && \
    chown -R appuser:appgroup /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R appuser:appgroup /var/run/nginx.pid

# Switch to non-root user
USER appuser

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]