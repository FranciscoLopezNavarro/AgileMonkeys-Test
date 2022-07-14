FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine
COPY target/customer-*.jar customer-service.jar
EXPOSE 8080
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar customer-service.jar
