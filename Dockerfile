FROM eclipse-temurin:21.0.5_11-jdk-alpine-3.21
# Set the working directory inside the container
#WORKDIR /app
# Do not run the application as root:
#RUN addgroup -S spring && adduser -S spring -G spring
#USER spring:spring
#Do not deploy the fat-jar into the container, but rather split dependencies and application classes and resources into separate layers:
#ARG DEPENDENCY=target/dependency
#COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY ${DEPENDENCY}/META-INF /app/META-INF
#COPY ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","pl.codeleak.samples.springboot.tc.SpringBootTestcontainersApplication"]
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
