FROM openjdk:17-jdk-slim

# Install prerequisites for Docker CLI
RUN apt-get update \
    && apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release

# Add Docker GPG key
RUN curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker repository
RUN echo "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker CLI
RUN apt-get update \
    && apt-get install -y docker-ce-cli

# Copy the jar file
COPY target/H264_Codec-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Set Docker as the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]
