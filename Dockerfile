FROM openjdk:21-jdk

WORKDIR /app

COPY ./build/libs/rentcar-0.0.1-SNAPSHOT.jar ./rentcar.jar

CMD ["java", "-jar", "./rentcar.jar"]
