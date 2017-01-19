FROM fabric8/java-jboss-openjdk8-jdk:1.2.1

ENV JAVA_APP_JAR hola-swarm.jar
ENV AB_OFF true
ENV JAVA_OPTIONS -Xmx512m
ENV ZIPKIN_SERVER_URL http://zipkin-query:9411

EXPOSE 8080

ADD hola-swarm.jar /deployments/
