# GoalCast - Futbol Tahmin Uygulaması

GoalCast, kullanıcıların yaklaşan futbol maçları için skor tahminleri yapmalarını, bu tahminler üzerinden puan kazanmalarını ve bir rekabet ortamına dahil olmalarını sağlayan kapsamlı bir web uygulamasıdır. Sistem, modern teknolojilerle geliştirilmiş olup, kullanıcılara etkileşimli ve eğlenceli bir deneyim sunmayı hedefler.

**Canlı Demo (Frontend):** [https://thegoalcast.netlify.app](https://thegoalcast.netlify.app)
**Canlı API (Backend):** `https://goalcast-api-ethxh9f4hngugwh9.germanywestcentral-01.azurewebsites.net/api`
**API Dokümantasyonu (Swagger UI):** `https://goalcast-api-ethxh9f4hngugwh9.germanywestcentral-01.azurewebsites.net/swagger-ui.html`

---

## Projeye Genel Bakış

GoalCast, futbolseverlere yönelik, kullanıcı dostu arayüzü ve gelişmiş özellikleriyle dikkat çeken bir skor tahmin platformudur. Kullanıcılar sisteme üye olabilir, e-posta adreslerini doğrulayabilir, giriş yapabilir, çeşitli liglerdeki maçları görüntüleyebilir, maçlar için skor tahmini yapabilir ve tahminlerinin sonuçlarına göre (belirlenen bir puanlama algoritması ile) puan kazanabilirler. Kullanıcılar ayrıca liderlik tablosunda diğerleriyle rekabet edebilir ve önemli olaylar (yeni maç eklenmesi, tahmin sonucu vb.) hakkında site içi ve e-posta yoluyla bildirim alabilirler. Admin rolüne sahip kullanıcılar için lig, maç yönetimi ve maç sonucu girme gibi kapsamlı yönetim arayüzleri de bulunmaktadır.

### Temel Özellikler

*   **Kullanıcı Yönetimi:**
    *   Kullanıcı Kaydı ve Güvenli Giriş (JWT ile kimlik doğrulama)
    *   E-posta Doğrulama Sistemi (RabbitMQ ve E-posta Servisi ile asenkron)
    *   Kullanıcı Profili Görüntüleme (Puan durumu, e-posta doğrulama durumu vb.)
*   **Tahmin Sistemi:**
    *   Ligleri ve Yaklaşan Maçları Listeleme (Filtreleme seçenekleriyle)
    *   Maçlar İçin Skor Tahmini Yapma (Puan yatırma mekanizması ile)
    *   Kullanıcının Kendi Tahminlerini ve Sonuçlarını Görmesi
    *   Detaylı Puanlama Algoritması (Doğru skor, doğru sonuç gibi farklı senaryolara göre puan kazanma/kaybetme)
*   **Etkileşim ve Rekabet:**
    *   Liderlik Tablosu
    *   Kullanıcı Bildirim Sistemi (Site içi ve E-posta ile):
        *   Yeni maç eklendiğinde (admin seçeneğine bağlı olarak tüm kullanıcılara e-posta)
        *   Tahmin yapılan maç sonuçlandığında (kullanıcıya özel e-posta)
        *   E-posta doğrulama süreçleri
*   **Admin Paneli:**
    *   Lig Yönetimi (Oluşturma, Güncelleme, Silme - CRUD)
    *   Maç Yönetimi (Oluşturma, Güncelleme, Silme - CRUD)
    *   Maç Sonucu Girme ve Tahminlerin Otomatik Değerlendirilip Puanların Güncellenmesi
    *   Yeni maç eklerken tüm kullanıcılara e-posta ile bildirim gönderme seçeneği

---

## Kullanılan Teknolojiler

### Backend (GoalCast API - `goalcast-backend` Repository'si)

*   **Programlama Dili:** Java 17+
*   **Framework:** Spring Boot 3.x
    *   **Spring Web:** RESTful API geliştirme.
    *   **Spring Security:** JWT tabanlı kimlik doğrulama ve yetkilendirme.
    *   **Spring Data JPA:** Veritabanı etkileşimi ve ORM (Hibernate ile).
    *   **Spring AMQP:** RabbitMQ ile asenkron mesajlaşma entegrasyonu.
    *   **Spring Mail:** E-posta gönderim işlemleri.
    *   **Spring AOP:** Mesajlaşma gibi kesitleri ilgilendiren (cross-cutting concerns) konular için hata yönetimi.
*   **Veritabanı:** PostgreSQL (Canlı ortamda Neon.tech üzerinde barındırılıyor).
*   **Mesaj Kuyruğu (Message Broker):** RabbitMQ (Canlı ortamda CloudAMQP üzerinde barındırılıyor).
*   **API Dokümantasyonu:** SpringDoc OpenAPI (Swagger UI ile erişilebilir).
*   **Build Aracı:** Apache Maven.
*   **Containerization:** Docker.
*   **Hosting:** Azure App Service (Web App for Containers).
*   **Container Registry:** Azure Container Registry (ACR).

### Frontend (GoalCast UI - `goalcast-ui` Repository'si)

*   **Temel Teknolojiler:** HTML5, CSS3, JavaScript (ES6+).
*   **CSS Framework:** Bootstrap 5.x (Duyarlı ve modern arayüz bileşenleri için).
*   **JavaScript Mimarisi:** Vanilla JS ile geliştirilmiş modüler yapı. Tek Sayfa Uygulaması (SPA) benzeri bir deneyim için hash tabanlı client-side routing mekanizması kullanılmaktadır.
*   **Hosting:** Netlify.

### CI/CD (Sürekli Entegrasyon / Sürekli Dağıtım)

*   **Platform:** GitHub Actions.
    *   **Backend İçin:** `main` branch'ine yapılan her push, Maven ile build ve test süreçlerini tetikler. Başarılı olursa, bir Docker imajı oluşturulur, Azure Container Registry'ye (ACR) push'lanır ve son olarak Azure App Service'e deploy edilir.
    *   **Frontend İçin:** Netlify'ın GitHub ile entegrasyonu sayesinde, `goalcast-ui` reposunun `main` branch'ine yapılan her push otomatik olarak canlı siteyi günceller.

---

## Canlı Ortam Mimarisi ve Yapılandırma

*   **Frontend (`goalcast-ui`):** Netlify üzerinde statik olarak barındırılır ve GitHub deposuyla senkronize bir şekilde otomatik olarak güncellenir.
*   **Backend (`goalcast-backend`):** Azure App Service üzerinde bir Docker container'ı içinde çalışır. Docker imajları Azure Container Registry'de (ACR) saklanır. GitHub Actions ile CI/CD süreci yönetilir.
*   **Veritabanı (`PostgreSQL`):** Neon.tech bulut veritabanı servisi üzerinde çalışır. Bağlantı bilgileri ve diğer hassas konfigürasyonlar Azure App Service ortam değişkenlerinde (Application settings) güvenli bir şekilde saklanır.
*   **Mesaj Kuyruğu (`RabbitMQ`):** CloudAMQP bulut RabbitMQ servisi üzerinde çalışır. Bağlantı bilgileri Azure App Service ortam değişkenlerinde saklanır.
*   **Hassas Bilgiler:** Veritabanı şifreleri, JWT secret'ı, RabbitMQ ve E-posta servis şifreleri gibi tüm hassas bilgiler, `application.properties` dosyası yerine Azure App Service ortam değişkenleri aracılığıyla uygulamaya sağlanır. `application.properties` dosyasında bu değerler için placeholder'lar kullanılır.
*   **CORS:** Backend API, sadece canlı frontend (Netlify) ve yerel geliştirme ortamlarından gelen isteklere izin verecek şekilde yapılandırılmıştır. Bu ayar, Azure App Service ortam değişkeni (`ALLOWED_CORS_ORIGINS`) ile yönetilir.
*   **Veritabanı Şeması (`ddl-auto`):** Canlı ortamda `spring.jpa.hibernate.ddl-auto` ayarı `validate` olarak ayarlanmıştır. Bu, Hibernate'in mevcut veritabanı şemasını entity sınıflarıyla karşılaştırmasını ve uyumsuzluk durumunda uygulamanın başlamasını engellemesini sağlar, böylece beklenmedik veri kaybı veya şema sorunlarının önüne geçilir. Yeni tablo veya sütun eklemeleri gibi şema değişiklikleri dikkatli bir şekilde (örneğin, `update` ayarıyla tek seferlik kontrollü bir çalıştırma veya migration araçlarıyla) yönetilir.

---

## Kurulum ve Çalıştırma (Yerel Geliştirme Ortamı)

### Genel Gereksinimler

*   Java JDK 17 veya üzeri
*   Apache Maven 3.6 veya üzeri
*   PostgreSQL veritabanı (yerel kurulum veya Docker ile)
*   RabbitMQ sunucusu (yerel kurulum veya Docker ile)
*   Node.js ve npm (Frontend için herhangi bir build süreci olmasa da, genel web geliştirme araçları için faydalı olabilir)
*   Modern bir web tarayıcısı (Google Chrome, Mozilla Firefox önerilir)
*   Bir IDE veya kod editörü (IntelliJ IDEA, VS Code önerilir)

### Backend (`goalcast-backend`) Çalıştırma

1.  **Projeyi Klonlayın:**
    ```bash
    git clone https://github.com/mahmutsyilmz/goalcast-backend.git
    cd goalcast-backend
    ```
2.  **Yerel Veritabanı ve RabbitMQ Kurulumu:**
    *   Yerel makinenizde PostgreSQL ve RabbitMQ sunucularının kurulu ve çalışır durumda olduğundan emin olun.
    *   RabbitMQ için varsayılan kullanıcı (`guest`) ve şifre (`guest`) genellikle yerel kurulumlarda çalışır.
3.  **`application.properties` Dosyasını Düzenleyin:**
    *   `src/main/resources/application.properties` dosyasını açın.
    *   Yerel PostgreSQL bağlantı bilgileriniz (`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`) için placeholder'ların `: ` sonrasındaki varsayılan değerleri kendi yerel ayarlarınızla güncelleyin veya bu placeholder'lara karşılık gelen ortam değişkenlerini sisteminizde tanımlayın.
    *   Yerel RabbitMQ ayarlarınız (`spring.rabbitmq.host`, `spring.rabbitmq.port` vb.) için de benzer şekilde varsayılanları kontrol edin.
    *   Yerel geliştirme için e-posta gönderme testi yapacaksanız, `spring.mail.username` ve `spring.mail.password` için geçerli bir Gmail (uygulama şifresi ile) veya başka bir SMTP hesabı ayarlayın.
    *   `app.frontend.url` değerini yerel frontend adresinize ayarlayın (örn: `http://127.0.0.1:5500`).
4.  **Uygulamayı Çalıştırın:**
    *   IDE üzerinden Spring Boot uygulamasını çalıştırın veya terminalden:
      ```bash
      mvn spring-boot:run
      ```
    *   API genellikle `http://localhost:8080/api` adresinde çalışmaya başlayacaktır.
    *   Swagger UI (API Dokümantasyonu): `http://localhost:8080/swagger-ui.html`

### Frontend (`goalcast-ui`) Çalıştırma

1.  **Projeyi Klonlayın:**
    ```bash
    git clone https://github.com/mahmutsyilmz/goalcast-ui.git
    cd goalcast-ui
    ```
2.  **API Adresini Ayarlayın:**
    *   `js/api.js` dosyasındaki `BASE_URL` sabitini, çalışan backend API adresini gösterecek şekilde güncelleyin. Yerel backend için bu genellikle `http://localhost:8080/api` olacaktır.
3.  **Web Sunucusu ile Sunun:**
    *   `index.html` dosyasını bir web sunucusu aracılığıyla sunmanız gerekir.
    *   VS Code kullanıyorsanız, "Live Server" eklentisini kurup `index.html` dosyasına sağ tıklayıp "Open with Live Server" seçeneğini kullanabilirsiniz.
    *   Frontend genellikle `http://127.0.0.1:5500` (veya Live Server'ın atadığı port) adresinde açılacaktır.





