# GoalCast - Futbol Tahmin Uygulaması


GoalCast, kullanıcıların yaklaşan futbol maçları için skor tahminleri yapmalarını, bu tahminler üzerinden puan kazanmalarını ve bir rekabet ortamına dahil olmalarını sağlayan bir web uygulamasıdır.

**Canlı Demo (Frontend):** [https://thegoalcast.netlify.app](https://thegoalcast.netlify.app)
**Canlı API (Backend):** `https://goalcast-api-ethxh9f4hngugwh9.germanywestcentral-01.azurewebsites.net/api` <!-- API ana adresiniz -->

---

## 📜 Projeye Genel Bakış

Bu proje, futbolseverlere yönelik eğlenceli ve rekabetçi bir skor tahmin platformu sunmayı amaçlamaktadır. Kullanıcılar sisteme üye olup giriş yapabilir, ligleri ve maçları görüntüleyebilir, maçlar için skor tahmini yapabilir ve tahminlerinin sonuçlarına göre puan kazanabilirler. Ayrıca, admin kullanıcılar için lig ve maç yönetimi arayüzleri bulunmaktadır.

### ✨ Temel Özellikler

*   Kullanıcı Kaydı ve Girişi (JWT ile kimlik doğrulama)
*   Kullanıcı Profili Görüntüleme (Puan durumu vb.)
*   Ligleri ve Maçları Listeleme
*   Maç Skoru Tahmini Yapma
*   Kullanıcının Kendi Tahminlerini Görmesi
*   **Admin Paneli:**
    *   Lig Oluşturma, Güncelleme, Silme (CRUD)
    *   Maç Oluşturma, Güncelleme, Silme (CRUD)
    *   Maç Sonucu Girme ve Tahminlerin Otomatik Değerlendirilmesi

---

## 🚀 Kullanılan Teknolojiler

### Backend (GoalCast API - `goalcast-backend` Repository'si)

*   **Programlama Dili:** Java 17+
*   **Framework:** Spring Boot 3.x
    *   Spring Web
    *   Spring Security (JWT ile)
    *   Spring Data JPA
*   **Veritabanı:** PostgreSQL (Neon.tech üzerinde barındırılıyor)
*   **ORM:** Hibernate
*   **API Dokümantasyonu:** SpringDoc OpenAPI (Swagger UI)
*   **Build Aracı:** Maven
*   **Containerization:** Docker
*   **Hosting:** Azure App Service (Web App for Containers)
*   **Container Registry:** Azure Container Registry (ACR)

### Frontend (GoalCast UI - `goalcast-ui` Repository'si)

*   **Temel Teknolojiler:** HTML5, CSS3, JavaScript (ES6+)
*   **CSS Framework:** Bootstrap 5.x
*   **JavaScript Mimarisi:** Vanilla JS ile modüler yapı, SPA (Single Page Application) benzeri hash tabanlı client-side routing.
*   **Hosting:** Netlify

### CI/CD (Sürekli Entegrasyon / Sürekli Dağıtım)

*   **Platform:** GitHub Actions
    *   Backend için: Maven build & test -> Docker image build -> ACR'ye push -> Azure App Service'e deploy.
    *   Frontend için: Netlify'ın GitHub entegrasyonu ile otomatik deploy.

---

## 🛠️ Kurulum ve Çalıştırma (Yerel Geliştirme Ortamı)

### Gereksinimler

*   Java JDK 17 veya üzeri
*   Apache Maven 3.6 veya üzeri
*   PostgreSQL veritabanı (yerel veya uzak)
*   Node.js ve npm (Frontend için, eğer bağımlılık yönetimi veya build araçları kullanılacaksa - bu projede şu an için direkt tarayıcıda çalışıyor)
*   Modern bir web tarayıcısı (Chrome, Firefox önerilir)
*   Kod editörü (VS Code, IntelliJ IDEA vb.)

### Backend (`goalcast-backend`) Çalıştırma

1.  Projeyi klonlayın:
    ```bash
    git clone https://github.com/mahmutsyilmz/goalcast-backend.git
    cd goalcast-backend
    ```
2.  `src/main/resources/application.properties` dosyasını yerel PostgreSQL veritabanı bilgilerinize göre düzenleyin:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/sizin_db_adiniz
    spring.datasource.username=sizin_kullanici_adiniz
    spring.datasource.password=sizin_sifreniz
    # Diğer ayarlar...
    ```
3.  Uygulamayı Maven ile çalıştırın:
    ```bash
    mvn spring-boot:run
    ```
    API genellikle `http://localhost:8080/api` adresinde çalışmaya başlayacaktır.
    Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend (`goalcast-ui`) Çalıştırma

1.  Projeyi klonlayın:
    ```bash
    git clone https://github.com/mahmutsyilmz/goalcast-ui.git
    cd goalcast-ui
    ```
2.  `js/api.js` dosyasındaki `BASE_URL` sabitini, çalışan backend API adresini gösterecek şekilde güncelleyin (eğer yerelde çalıştırıyorsanız `http://localhost:8080/api`).
3.  Bir web sunucusu ile `index.html` dosyasını sunun. VS Code kullanıyorsanız "Live Server" eklentisi işinizi görecektir.
    *   VS Code'da `index.html` dosyasına sağ tıklayıp "Open with Live Server" seçeneğini seçin.
    Frontend genellikle `http://127.0.0.1:5500` (veya Live Server'ın verdiği port) adresinde açılacaktır.

---

## ☁️ Canlı Ortam Mimarisi

*   **Frontend:** `goalcast-ui` reposu Netlify'a bağlıdır. `main` branch'ine yapılan her push otomatik olarak deploy edilir.
*   **Backend:** `goalcast-backend` reposu için GitHub Actions workflow'u tanımlanmıştır. `main` branch'ine yapılan her push:
    1.  Uygulamayı Maven ile build eder ve testleri çalıştırır.
    2.  Bir Docker imajı oluşturur.
    3.  Oluşturulan imajı Azure Container Registry'ye (ACR) push'lar.
    4.  Azure App Service'i ACR'deki en son imajı kullanacak şekilde günceller.
*   **Veritabanı:** Neon.tech üzerinde barındırılan PostgreSQL veritabanı kullanılır. Bağlantı bilgileri Azure App Service ortam değişkenlerinde saklanır.

---

## 📁 Proje Yapısı

### Backend (`goalcast-backend`)

```
goalcast-backend/
  .github/
    workflows/
      backend-ci-cd.yml  # GitHub Actions workflow dosyası
  src/
    main/
      java/
        com/yilmaz/goalCast/  # Ana Java kaynak kodu
          config/
          controller/
          # ... ve diğerleri ...
      resources/
        application.properties # Uygulama yapılandırması
    test/
      java/
        com/yilmaz/goalCast/  # Test kaynak kodu
  Dockerfile                 # Backend için Docker imajı oluşturma talimatları
  .dockerignore              # Docker build'inde ignore edilecek dosyalar
  pom.xml                    # Maven proje yapılandırması
  README.md                  # Bu dosya
```

### Frontend (`goalcast-ui`)

```
goalcast-ui/
├── css/
│   └── style.css              # Özel CSS stilleri
├── js/
│   ├── api.js                 # Backend API iletişim fonksiyonları
│   ├── app.js                 # Ana uygulama mantığı, SPA router
│   ├── auth.js                # Kimlik doğrulama fonksiyonları
│   ├── ui.js                  # Genel UI yardımcı fonksiyonları
│   └── pages/                 # Sayfa-spesifik JavaScript dosyaları
│       └── *.js               # (Örn: login-page.js, dashboard-page.js vb.)
├── views/                     # HTML view parçacıkları
│   └── *.html                 # (Örn: login.html, home.html vb.)
├── .gitignore                 # Git ignore dosyası
├── index.html                 # Ana HTML şablonu
└── README.md                  # Bu dosya (veya projenin ana README'sine referans)
```

## 📝 Katkıda Bulunma

Bu proje şu anda kişisel bir projedir. Katkıda bulunma veya geri bildirim için lütfen bir "Issue" açın.

---
