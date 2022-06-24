FROM openjdk:11

EXPOSE 80

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} /HOME/spring/app.jar

WORKDIR /HOME/spring/

ENTRYPOINT ["java", "-jar", "./app.jar"]