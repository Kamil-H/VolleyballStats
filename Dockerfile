FROM gradle:8-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM openjdk:17
RUN mkdir /app
COPY --from=build /home/gradle/src/server/build/libs/server-all.jar /app/server-application.jar
COPY --from=build /home/gradle/src/application.conf /app/application.conf
ENTRYPOINT ["java","-jar","/app/server-application.jar", "-config=/app/application.conf"]