# use the latest ubuntu LTS
FROM java:openjdk-8-jre

# set the working directory to /app
WORKDIR /app

# copy the current directory contents into the container at /app
COPY target/uberjar/microblog-0.1.0-SNAPSHOT-standalone.jar /app

RUN apt-get update

CMD ["java", "-jar", "microblog-0.1.0-SNAPSHOT-standalone.jar"]
