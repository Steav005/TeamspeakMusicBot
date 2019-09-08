FROM adoptopenjdk:11-jre-openj9

WORKDIR /musicbot

#Copy Current Jar to musicbot dir
ADD jars/TeamspeakMusicBot-0.1.jar /jar/Musicbot.jar

#Start Bot
CMD java -jar /jar/Musicbot.jar