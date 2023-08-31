FROM eclipse-temurin:17-jre-jammy
RUN mkdir /opt/app 
COPY app.jar /opt/app 
CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:9071", "-jar", "/opt/app/app.jar"]
