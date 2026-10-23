FROM eclipse-temurin:17-jre-alpine-3.24@sha256:a2e1cd0a400a97cf772159f5fff4e05088b4d20097379624cab1874a381ff6f1
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --chown=app:app target/app.jar /app/app.jar
USER app
EXPOSE 9095
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
