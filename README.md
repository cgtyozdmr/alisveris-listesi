# 🛒 Alışveriş Listesi Uygulaması

Modern ve kullanıcı dostu **Spring Boot** tabanlı ortak aile alışveriş listesi uygulaması.

## 📋 Özellikler

- ✅ **Ortak Liste** - Tüm aile üyeleri aynı listeyi görür
- ✅ **Kullanıcı Yönetimi** - Dinamik kullanıcı ekleme
- ✅ **"Kim Ekledi?" Bilgisi** - Her ürünün yanında ekleyen kişi görünür
- ✅ **Durum Takibi** - Satın alındı/alınmadı işaretleme
- ✅ **Anlık Güncelleme** - İstatistikler anında yenilenir
- ✅ **Responsive Tasarım** - Mobil uyumlu arayüz
- ✅ **MySQL Veritabanı** - Kalıcı veri saklama

## 🛠️ Teknolojiler

- **Backend:** Spring Boot 3.x, Java 17
- **Database:** MySQL 8.0
- **Frontend:** Thymeleaf, Bootstrap 5, JavaScript
- **Build Tool:** Maven

## 🚀 Kurulum

### Gereksinimler
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### Veritabanı Kurulumu
```sql
CREATE DATABASE alisveris_listesi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'alisveris_user'@'localhost' IDENTIFIED BY 'alisveris123';
GRANT ALL PRIVILEGES ON alisveris_listesi.* TO 'alisveris_user'@'localhost';
FLUSH PRIVILEGES;
```

### Projeyi Çalıştırma
```bash
# Repository'yi klonla
git clone https://github.com/USERNAME/alisveris-listesi.git

# Proje dizinine git
cd alisveris-listesi

# Uygulamayı çalıştır
mvn spring-boot:run
```

### Erişim
- **Ana Sayfa:** http://localhost:8080
- **MySQL Console:** MySQL Workbench ile localhost:3306

## 📁 Proje Yapısı

```
src/main/java/com/alisverilistesi/
├── AlisverisListesiApplication.java    # Ana uygulama
├── controller/
│   └── UrunController.java             # Web ve REST controller
├── model/
│   ├── Urun.java                       # Ürün entity
│   └── Kullanici.java                  # Kullanıcı entity
├── repository/
│   ├── UrunRepository.java             # Ürün data access
│   └── KullaniciRepository.java        # Kullanıcı data access
└── service/
    ├── UrunService.java                # Ürün business logic
    └── KullaniciService.java           # Kullanıcı business logic

src/main/resources/
├── templates/                          # Thymeleaf şablonları
├── static/                             # CSS, JS, images
└── application.properties              # Konfigürasyon
```

## 🎯 API Endpoints

### Web Sayfaları
- `GET /` - Ana sayfa (liste görünümü)
- `GET /urun/ekle` - Ürün ekleme sayfası
- `GET /kullanici/ekle` - Kullanıcı ekleme sayfası

### REST API
- `GET /api/urun` - Tüm ürünleri listele
- `POST /api/urun/{id}/durum-degistir` - Ürün durumu değiştir
- `DELETE /api/urun/{id}` - Ürün sil
- `POST /api/urun/temizle` - Tamamlananları temizle

## 👥 Kullanıcılar

Varsayılan kullanıcılar:
- **Kübra** (ID: 1)
- **Çağatay** (ID: 2)

## 🔧 Konfigürasyon

`application.properties` dosyasında veritabanı ayarlarını değiştirin:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/alisveris_listesi
spring.datasource.username=alisveris_user
spring.datasource.password=alisveris123
```

## 📱 Ekran Görüntüleri

- Modern gradient arka plan
- Card-based responsive tasarım
- Touch-friendly checkbox'lar
- Anlık bildirimler

## 🤝 Katkıda Bulunma

1. Fork edin
2. Feature branch oluşturun (`git checkout -b feature/yeni-ozellik`)
3. Değişikliklerinizi commit edin (`git commit -am 'Yeni özellik eklendi'`)
4. Branch'inizi push edin (`git push origin feature/yeni-ozellik`)
5. Pull Request oluşturun

## 📝 Lisans

Bu proje kişisel kullanım için geliştirilmiştir.

## 👨‍💻 Geliştirici

**Çağatay Özdemir**

---

⭐ Bu projeyi beğendiyseniz star vermeyi unutmayın!
