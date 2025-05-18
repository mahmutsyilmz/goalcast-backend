# Aşama 1: Maven ile uygulamayı build etme
# Temel imaj olarak Maven ve JDK 17 içeren bir imaj kullanıyoruz
FROM maven:3.8.5-openjdk-17 AS builder

# Çalışma dizinini /app olarak ayarla
WORKDIR /app

# Önce pom.xml dosyasını kopyala (Docker katman önbelleklemesinden faydalanmak için)
COPY pom.xml .

# Maven bağımlılıklarını indir (sadece bağımlılıklar değiştiğinde bu katman yeniden build edilir)
RUN mvn dependency:go-offline -B

# Tüm kaynak kodunu kopyala
COPY src ./src

# Uygulamayı Maven ile paketle (testleri atlayarak, CI'da testler ayrı çalışabilir veya burada çalıştırılabilir)
# Eğer testlerin burada çalışmasını istiyorsanız -DskipTests kısmını kaldırın
RUN mvn package -DskipTests -B

# Aşama 2: Çalıştırılabilir imajı oluşturma
# Temel imaj olarak sadece JRE içeren daha küçük bir imaj kullanıyoruz
FROM openjdk:17-jre-slim

# Çalışma dizinini /app olarak ayarla
WORKDIR /app

# Bir önceki aşamada (builder) oluşturulan JAR dosyasını bu imaja kopyala
# target/*.jar genellikle çalışır, ancak spesifik JAR adınızı biliyorsanız onu kullanmak daha iyidir.
# Projenizin artifactId'si "goalCast" ve versiyonu "0.0.1-SNAPSHOT" ise JAR adı "goalCast-0.0.1-SNAPSHOT.jar" olur.
COPY --from=builder /app/target/goalCast-0.0.1-SNAPSHOT.jar app.jar

# Uygulamanın çalışacağı portu belirt (Azure App Service bu portu dışarıya mapler)
EXPOSE 8080

# Uygulamayı başlatma komutu
# java -jar app.jar komutu, Spring Boot uygulamasını başlatır.
# Ortam değişkenleri (DB bağlantısı vb.) Azure App Service tarafından sağlanacaktır.
ENTRYPOINT ["java","-jar","/app/app.jar"]