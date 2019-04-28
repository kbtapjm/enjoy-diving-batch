FROM java:8
ENV SPRING_ACTIVE_PROFILE stg
ENV JAVA_OPTS=""
VOLUME /tmp
ADD enjoy-diving-batch-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=${SPRING_ACTIVE_PROFILE}","-jar","/app.jar"]
