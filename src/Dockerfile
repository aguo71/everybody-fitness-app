FROM maven:latest
MAINTAINER Alex Guo, David Moon, Josh Woo, Lauren Choi
WORKDIR /backend

COPY . .
RUN mvn clean install -Dmaven.test.skip=true

# IN TERMINAL: docker build -t everybody-app:latest .
