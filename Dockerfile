#Build from pom with maven
FROM maven:3-jdk-11 as build 
WORKDIR /build
ADD pom.xml /build/
ADD src /build/src/
RUN mvn package

#Create final image
FROM openjdk:11-jre-slim

#Copy files from build
WORKDIR /jar
COPY --from=build /build/jars/* ./
RUN mv *jar-with-dependencies.jar Musicbot.jar
RUN rm Teamspeak*.jar

#initiate parameter for run
WORKDIR /musicbot

#Start Bot
CMD java -jar /jar/Musicbot.jar