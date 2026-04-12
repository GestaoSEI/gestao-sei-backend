# Estágio 1: Build da aplicação com Maven
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo pom.xml e baixa as dependências (cache de deps entre builds)
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

# Copia o código-fonte e compila a aplicação
COPY src ./src
# Executa os testes e gera o pacote JAR (reutiliza cache Maven)
RUN --mount=type=cache,target=/root/.m2 mvn package

# Estágio 2: Criação da imagem final de execução
FROM eclipse-temurin:21-jdk-alpine

# Instala fontes necessárias para o JasperReports
RUN apk add --no-cache fontconfig ttf-dejavu

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR gerado no estágio de build para a imagem final
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta em que a aplicação vai rodar
EXPOSE 8081

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
