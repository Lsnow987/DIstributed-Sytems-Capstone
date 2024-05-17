# Use Debian Bullseye as the base image
FROM debian:bullseye

# Update package lists and install required packages
RUN apt update --fix-missing && \
    apt install -y openjdk-17-jre ffmpeg && \
    rm -rf /var/lib/apt/lists/*

# Copy the JAR file into the container
COPY target/H264_Codec-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "app.jar"]
