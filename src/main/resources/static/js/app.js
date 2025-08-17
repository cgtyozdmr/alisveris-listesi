// ========== GENEL FONKSİYONLAR ==========

// Sayfa yüklendiğinde çalışacak fonksiyonlar
document.addEventListener('DOMContentLoaded', function() {
    console.log('Alışveriş Listesi uygulaması yüklendi');
    
    // Form validation'ları etkinleştir
    etkinleştirFormValidation();
    
    // Auto-dismiss alert'leri ayarla
    ayarlaAutoClose();
    
    // Keyboard shortcuts ekle
    ekleKeyboardShortcuts();
});

// ========== ÜRÜN YÖNETİMİ ==========

/**
 * Ürün durumunu değiştir (satın alındı/alınmadı)
 * @param {number} urunId - Ürün ID'si
 */
async function durumDegistir(urunId) {
    try {
        // Optimistic UI update
        const checkbox = document.getElementById(`urun-${urunId}`);
        const urunContainer = checkbox.closest('.list-group-item');
        
        // Loading state göster
        checkbox.disabled = true;
        
        const response = await fetch(`/api/urun/${urunId}/durum-degistir`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        
        if (response.ok) {
            const urun = await response.json();
            
            // UI'ı güncelle
            checkbox.checked = urun.satinAlindiMi;
            
            if (urun.satinAlindiMi) {
                urunContainer.classList.add('completed-item');
                gosterBasariBildirimi('Ürün satın alındı olarak işaretlendi! ✅');
            } else {
                urunContainer.classList.remove('completed-item');
                gosterBasariBildirimi('Ürün bekliyor olarak işaretlendi! ⏳');
            }
            
            // İstatistikleri anında güncelle
            console.log('Ürün durumu değişti:', urun.satinAlindiMi);
            guncelleIstatistiklerHizli(urun.satinAlindiMi);
            
        } else {
            throw new Error('Durum değiştirilemedi');
        }
        
    } catch (error) {
        console.error('Durum değiştirme hatası:', error);
        gosterHataBildirimi('Durum değiştirilemedi. Lütfen tekrar deneyin.');
        
        // Checkbox'ı eski haline döndür
        const checkbox = document.getElementById(`urun-${urunId}`);
        checkbox.checked = !checkbox.checked;
        
    } finally {
        // Loading state'i kaldır
        const checkbox = document.getElementById(`urun-${urunId}`);
        checkbox.disabled = false;
    }
}

/**
 * Ürün sil
 * @param {number} urunId - Ürün ID'si
 */
async function urunSil(urunId) {
    // Onay al
    const onay = await gosterOnayDialogu(
        'Ürünü Sil',
        'Bu ürünü listeden silmek istediğinizden emin misiniz?',
        'Evet, Sil',
        'İptal'
    );
    
    if (!onay) return;
    
    try {
        const response = await fetch(`/api/urun/${urunId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            // Ürünü UI'dan kaldır (animasyonlu)
            const urunElement = document.getElementById(`urun-${urunId}`)?.closest('.list-group-item');
            if (urunElement) {
                urunElement.style.opacity = '0';
                urunElement.style.transform = 'translateX(-100%)';
                
                setTimeout(() => {
                    urunElement.remove();
                    kontrolEtBosListe();
                }, 300);
            }
            
            gosterBasariBildirimi('Ürün başarıyla silindi! 🗑️');
            
            // İstatistikleri güncelle
            await guncelleIstatistikler();
            
        } else {
            throw new Error('Ürün silinemedi');
        }
        
    } catch (error) {
        console.error('Ürün silme hatası:', error);
        gosterHataBildirimi('Ürün silinemedi. Lütfen tekrar deneyin.');
    }
}

/**
 * Ürün düzenle (gelecek özellik)
 * @param {number} urunId - Ürün ID'si
 */
function urunDuzenle(urunId) {
    gosterBilgiBildirimi('Düzenleme özelliği yakında eklenecek! ✏️');
}

// ========== TEMİZLEME İŞLEMLERİ ==========

/**
 * Satın alınan ürünleri temizle onayı
 */
async function temizleOnayi() {
    const onay = await gosterOnayDialogu(
        'Tamamlananları Temizle',
        'Satın alınmış tüm ürünleri listeden silmek istediğinizden emin misiniz?',
        'Evet, Temizle',
        'İptal'
    );
    
    if (onay) {
        await temizleSatinAlinanlar();
    }
}

/**
 * Satın alınan ürünleri temizle
 */
async function temizleSatinAlinanlar() {
    try {
        const response = await fetch('/api/urun/temizle', {
            method: 'POST'
        });
        
        if (response.ok) {
            // Tamamlanmış ürünleri UI'dan kaldır
            const tamamlanmisUrunler = document.querySelectorAll('.completed-item');
            
            tamamlanmisUrunler.forEach((urun, index) => {
                setTimeout(() => {
                    urun.style.opacity = '0';
                    urun.style.transform = 'translateX(-100%)';
                    
                    setTimeout(() => {
                        urun.remove();
                        kontrolEtBosListe();
                    }, 300);
                }, index * 100);
            });
            
            gosterBasariBildirimi('Tamamlanan ürünler temizlendi! 🧹');
            
            // İstatistikleri güncelle
            setTimeout(async () => {
                await guncelleIstatistikler();
            }, 1000);
            
        } else {
            throw new Error('Temizleme işlemi başarısız');
        }
        
    } catch (error) {
        console.error('Temizleme hatası:', error);
        gosterHataBildirimi('Temizleme işlemi başarısız. Lütfen tekrar deneyin.');
    }
}

// ========== İSTATİSTİK YÖNETİMİ ==========

/**
 * İstatistikleri hızlı güncelle (API çağrısı yapmadan)
 * @param {boolean} satinAlindiMi - Ürünün yeni durumu
 */
function guncelleIstatistiklerHizli(satinAlindiMi) {
    try {
        console.log('Hızlı istatistik güncelleme başlatıldı:', satinAlindiMi);
        const toplamElement = document.querySelector('.stat-item:nth-child(1) .fw-bold');
        const tamamlananElement = document.querySelector('.stat-item:nth-child(2) .fw-bold');
        const bekleyenElement = document.querySelector('.stat-item:nth-child(3) .fw-bold');
        
        console.log('Bulunan elementler:', {toplamElement, tamamlananElement, bekleyenElement});
        
        if (tamamlananElement && bekleyenElement) {
            let tamamlanan = parseInt(tamamlananElement.textContent);
            let bekleyen = parseInt(bekleyenElement.textContent);
            
            if (satinAlindiMi) {
                // Tamamlanan arttı, bekleyen azaldı
                tamamlanan++;
                bekleyen--;
            } else {
                // Tamamlanan azaldı, bekleyen arttı
                tamamlanan--;
                bekleyen++;
            }
            
            // Negatif değerleri önle
            tamamlanan = Math.max(0, tamamlanan);
            bekleyen = Math.max(0, bekleyen);
            
            // UI'ı güncelle (animasyonlu)
            tamamlananElement.style.transform = 'scale(1.2)';
            bekleyenElement.style.transform = 'scale(1.2)';
            
            tamamlananElement.textContent = tamamlanan;
            bekleyenElement.textContent = bekleyen;
            
            // Animasyonu geri döndür
            setTimeout(() => {
                tamamlananElement.style.transform = 'scale(1)';
                bekleyenElement.style.transform = 'scale(1)';
            }, 200);
            
            // Temizle butonunu göster/gizle
            const temizleButton = document.querySelector('[onclick="temizleOnayi()"]');
            if (temizleButton) {
                temizleButton.closest('.text-center').style.display = tamamlanan > 0 ? 'block' : 'none';
            }
        }
        
    } catch (error) {
        console.error('Hızlı istatistik güncelleme hatası:', error);
        // Hata durumunda normal güncellemeyi çağır
        guncelleIstatistikler();
    }
}

/**
 * İstatistikleri güncelle (API çağrısı ile)
 */
async function guncelleIstatistikler() {
    try {
        const response = await fetch('/api/urun');
        const urunler = await response.json();
        
        const toplam = urunler.length;
        const tamamlanan = urunler.filter(u => u.satinAlindiMi).length;
        const bekleyen = toplam - tamamlanan;
        
        // İstatistik elementlerini güncelle
        const toplamElement = document.querySelector('.stat-item:nth-child(1) .fw-bold');
        const tamamlananElement = document.querySelector('.stat-item:nth-child(2) .fw-bold');
        const bekleyenElement = document.querySelector('.stat-item:nth-child(3) .fw-bold');
        
        if (toplamElement) toplamElement.textContent = toplam;
        if (tamamlananElement) tamamlananElement.textContent = tamamlanan;
        if (bekleyenElement) bekleyenElement.textContent = bekleyen;
        
        // Temizle butonunu göster/gizle
        const temizleButton = document.querySelector('[onclick="temizleOnayi()"]');
        if (temizleButton) {
            temizleButton.closest('.text-center').style.display = tamamlanan > 0 ? 'block' : 'none';
        }
        
    } catch (error) {
        console.error('İstatistik güncelleme hatası:', error);
    }
}

// ========== YARDIMCI FONKSİYONLAR ==========

/**
 * Boş liste kontrolü
 */
function kontrolEtBosListe() {
    const urunListesi = document.querySelector('.list-group');
    const bosListeElement = document.querySelector('.empty-state');
    
    if (urunListesi && urunListesi.children.length === 0) {
        if (!bosListeElement) {
            const bosListe = document.createElement('div');
            bosListe.className = 'text-center py-5 empty-state';
            bosListe.innerHTML = `
                <i class="fas fa-inbox text-muted" style="font-size: 4rem;"></i>
                <h5 class="text-muted mt-3">Liste boş</h5>
                <p class="text-muted">Henüz hiç ürün eklenmemiş. Yukarıdan ilk ürününüzü ekleyin!</p>
            `;
            urunListesi.parentElement.appendChild(bosListe);
        }
    } else if (bosListeElement && urunListesi && urunListesi.children.length > 0) {
        bosListeElement.remove();
    }
}

/**
 * Form validation etkinleştir
 */
function etkinleştirFormValidation() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                e.stopPropagation();
                gosterHataBildirimi('Lütfen tüm gerekli alanları doldurun.');
            }
            form.classList.add('was-validated');
        });
    });
}

/**
 * Auto-close alert ayarla
 */
function ayarlaAutoClose() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const closeBtn = alert.querySelector('.btn-close');
            if (closeBtn) {
                closeBtn.click();
            }
        }, 5000); // 5 saniye sonra otomatik kapat
    });
}

/**
 * Keyboard shortcuts ekle
 */
function ekleKeyboardShortcuts() {
    document.addEventListener('keydown', function(e) {
        // Ctrl + Enter: Formu gönder
        if (e.ctrlKey && e.key === 'Enter') {
            const activeForm = document.querySelector('form');
            if (activeForm) {
                activeForm.requestSubmit();
            }
        }
        
        // Escape: Modal kapat (gelecekte modal eklenirse)
        if (e.key === 'Escape') {
            const modals = document.querySelectorAll('.modal.show');
            modals.forEach(modal => {
                const closeBtn = modal.querySelector('.btn-close');
                if (closeBtn) closeBtn.click();
            });
        }
    });
}

// ========== BİLDİRİM SİSTEMİ ==========

/**
 * Başarı bildirimi göster
 * @param {string} mesaj - Gösterilecek mesaj
 */
function gosterBasariBildirimi(mesaj) {
    gosterBildirim(mesaj, 'success');
}

/**
 * Hata bildirimi göster
 * @param {string} mesaj - Gösterilecek mesaj
 */
function gosterHataBildirimi(mesaj) {
    gosterBildirim(mesaj, 'danger');
}

/**
 * Bilgi bildirimi göster
 * @param {string} mesaj - Gösterilecek mesaj
 */
function gosterBilgiBildirimi(mesaj) {
    gosterBildirim(mesaj, 'info');
}

/**
 * Bildirim göster
 * @param {string} mesaj - Gösterilecek mesaj
 * @param {string} tip - Bildirim tipi (success, danger, info, warning)
 */
function gosterBildirim(mesaj, tip = 'info') {
    // Mevcut bildirimleri temizle
    const mevcutBildirimler = document.querySelectorAll('.toast-notification');
    mevcutBildirimler.forEach(b => b.remove());
    
    // Yeni bildirim oluştur
    const bildirim = document.createElement('div');
    bildirim.className = `alert alert-${tip} alert-dismissible fade show toast-notification`;
    bildirim.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    `;
    
    const iconMap = {
        success: 'fas fa-check-circle',
        danger: 'fas fa-exclamation-circle',
        info: 'fas fa-info-circle',
        warning: 'fas fa-exclamation-triangle'
    };
    
    bildirim.innerHTML = `
        <i class="${iconMap[tip]} me-2"></i>
        ${mesaj}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    document.body.appendChild(bildirim);
    
    // 4 saniye sonra otomatik kapat
    setTimeout(() => {
        if (bildirim.parentElement) {
            bildirim.remove();
        }
    }, 4000);
}

/**
 * Onay dialogu göster
 * @param {string} baslik - Dialog başlığı
 * @param {string} mesaj - Dialog mesajı
 * @param {string} evetButon - Evet buton metni
 * @param {string} iptalButon - İptal buton metni
 * @returns {Promise<boolean>} - Kullanıcı onayı
 */
function gosterOnayDialogu(baslik, mesaj, evetButon = 'Evet', iptalButon = 'İptal') {
    return new Promise((resolve) => {
        // Bootstrap modal kullanılmadığı için basit confirm kullan
        // Gelecekte custom modal eklenebilir
        const onay = confirm(`${baslik}\n\n${mesaj}`);
        resolve(onay);
    });
}

// ========== UTILITY FONKSİYONLARI ==========

/**
 * Tarihi güzel formatta göster
 * @param {string} tarih - ISO tarih string
 * @returns {string} - Formatlanmış tarih
 */
function formatTarih(tarih) {
    const date = new Date(tarih);
    const now = new Date();
    const diff = now - date;
    
    // Dakika cinsinden fark
    const dakika = Math.floor(diff / (1000 * 60));
    
    if (dakika < 1) return 'Az önce';
    if (dakika < 60) return `${dakika} dakika önce`;
    
    const saat = Math.floor(dakika / 60);
    if (saat < 24) return `${saat} saat önce`;
    
    const gun = Math.floor(saat / 24);
    if (gun < 7) return `${gun} gün önce`;
    
    return date.toLocaleDateString('tr-TR');
}

/**
 * String'i temizle (XSS koruması)
 * @param {string} str - Temizlenecek string
 * @returns {string} - Temizlenmiş string
 */
function temizleString(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

/**
 * Loading spinner göster
 * @param {HTMLElement} element - Spinner eklenecek element
 */
function gosterSpinner(element) {
    const spinner = document.createElement('div');
    spinner.className = 'spinner-border spinner-border-sm me-2';
    spinner.setAttribute('role', 'status');
    element.prepend(spinner);
}

/**
 * Loading spinner gizle
 * @param {HTMLElement} element - Spinner kaldırılacak element
 */
function gizleSpinner(element) {
    const spinner = element.querySelector('.spinner-border');
    if (spinner) {
        spinner.remove();
    }
}

// ========== DEBUG FONKSİYONLARI ==========

/**
 * Debug mode kontrolü
 */
function isDebugMode() {
    return window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
}

/**
 * Debug log
 * @param {any} message - Log mesajı
 */
function debugLog(message) {
    if (isDebugMode()) {
        console.log('[DEBUG]', message);
    }
}

// Debug mode etkinse global fonksiyonları window'a ekle
if (isDebugMode()) {
    window.debugFunctions = {
        durumDegistir,
        urunSil,
        temizleSatinAlinanlar,
        guncelleIstatistikler,
        gosterBasariBildirimi,
        gosterHataBildirimi,
        formatTarih
    };
    
    console.log('Debug mode aktif. Fonksiyonlar window.debugFunctions içinde mevcut.');
}
