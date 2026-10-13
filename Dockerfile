# syntax=docker/dockerfile:1

FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw \
    && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN mkdir -p /data \
    && groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring \
    && chown -R spring:spring /app /data

USER spring:spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

ENV APP_DATASOURCE_URL=jdbc:sqlite:/data/pastry_shop.db
EXPOSE 8080
VOLUME ["/data"]

ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "app.jar"]
