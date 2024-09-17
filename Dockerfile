FROM openjdk:21-jdk

WORKDIR /app

COPY ./build/libs/*.jar ./rentcar.jar

CMD ["java", "-jar", "./rentcar.jar"]
