# Aşama 1: Maven ile uygulamayı build etme
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Aşama 2: Çalıştırılabilir imajı oluşturma
# TEMEL İMAJI BURADA DEĞİŞTİRİN
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=builder /app/target/goalCast-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]