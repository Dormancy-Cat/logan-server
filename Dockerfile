FROM openjdk:14.0.2

COPY ./target/logan-server*.jar /logan-server.jar

WORKDIR /

CMD ["java", "-jar", "logan-server.jar"]