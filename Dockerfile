FROM eclipse-temurin:22-jdk

WORKDIR /app

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV JAVA_OPTS="-Duser.timezone=UTC"

ARG JAR_FILE
COPY target/${JAR_FILE} app.jar

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]