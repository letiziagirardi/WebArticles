FROM ubuntu:18.04

LABEL maintainer="letizia.g00@gmail.com"

RUN apt-get update && apt-get install -y mongodb && apt-get install -y nano

# Create the MongoDB data directory
RUN mkdir -p /data/db

# Expose port 37017 from the container to the host
EXPOSE 37017

# Start MongoDB
CMD ["mongod", "-f", "/etc/mongodb.conf"]
