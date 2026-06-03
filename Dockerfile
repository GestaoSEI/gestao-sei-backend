# Build stage: compila a aplicação dentro da imagem
FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia os arquivos do Maven primeiro para aproveitar cache de dependências
COPY pom.xml .
COPY .mvn .mvn

RUN mvn -q -DskipTests dependency:go-offline

COPY src src

RUN mvn -q -DskipTests package

# Runtime stage: imagem menor só com o necessário para executar
FROM eclipse-temurin:21-jre-noble

RUN apt-get update && apt-get install -y --no-install-recommends fontconfig fonts-dejavu && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
