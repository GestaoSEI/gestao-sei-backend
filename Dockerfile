# Imagem de execução — compile o JAR antes com: ./mvnw package -DskipTests
FROM eclipse-temurin:21-jre-noble

# Instala fontes necessárias para o JasperReports
RUN apt-get update && apt-get install -y --no-install-recommends fontconfig fonts-dejavu && rm -rf /var/lib/apt/lists/*

# Define o diretório de trabalho
WORKDIR /app

# Copia o JAR pré-compilado para a imagem
COPY target/*.jar app.jar

# Expõe a porta em que a aplicação vai rodar
EXPOSE 8081

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
