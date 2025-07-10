FROM eclipse-temurin:24-jdk

WORKDIR /app

ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV JAVA_OPTS="-Duser.timezone=UTC"

ARG JAR_FILE
COPY target/${JAR_FILE} app.jar

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl --fail http://localhost:8080/api/v1/health/check || exit 1

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]