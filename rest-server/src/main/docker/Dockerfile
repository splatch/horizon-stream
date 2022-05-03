FROM eclipse-temurin:11
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN apt-get update && apt-get install -y iputils-ping
CMD bash
ENTRYPOINT ["java","-jar","/app.jar"]
