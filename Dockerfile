FROM eclipse-temurin:24-jre-alpine-3.22@sha256:4044b6c87cb088885bcd0220f7dc7a8a4aab76577605fa471945d2e98270741f
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --chown=app:app target/app.jar /app/app.jar
USER app
EXPOSE 9095
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
