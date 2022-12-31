FROM maven:3-eclipse-temurin-11 as build
LABEL maintainer="Paul Schifferer <paul@schifferers.net>"

COPY . .

RUN pwd && ls -la
RUN mvn package
RUN ls -la target

FROM openjdk:11

RUN mkdir -p /app
COPY --from=build target/bot.penning.*-jar-with-dependencies.jar /app/penning.jar
RUN ls -la /app

ENV DISCORD_TOKEN="unset"

WORKDIR /app
CMD [ "java", "-jar", "/app/penning.jar" ]
