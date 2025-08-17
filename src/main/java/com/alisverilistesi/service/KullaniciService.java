package com.alisverilistesi.service;

import com.alisverilistesi.model.Kullanici;
import com.alisverilistesi.repository.KullaniciRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class KullaniciService {
    
    private final KullaniciRepository kullaniciRepository;
    
    public KullaniciService(KullaniciRepository kullaniciRepository) {
        this.kullaniciRepository = kullaniciRepository;
    }
    
    // Tüm kullanıcıları getir
    public List<Kullanici> tumKullanicilariGetir() {
        return kullaniciRepository.findAll();
    }
    
    // Aktif kullanıcıları getir
    public List<Kullanici> aktifKullanicilariGetir() {
        return kullaniciRepository.findByAktifMiTrue();
    }
    
    // ID'ye göre kullanıcı getir
    public Optional<Kullanici> kullaniciGetir(Long id) {
        return kullaniciRepository.findById(id);
    }
    
    // Kullanıcı adına göre kullanıcı getir
    public Optional<Kullanici> kullaniciGetir(String ad) {
        return kullaniciRepository.findByAdIgnoreCase(ad);
    }
    
    // Yeni kullanıcı ekle
    public Kullanici kullaniciEkle(String ad) {
        // Aynı isimde kullanıcı var mı kontrol et
        Optional<Kullanici> mevcutKullanici = kullaniciRepository.findByAdIgnoreCase(ad);
        if (mevcutKullanici.isPresent()) {
            throw new IllegalArgumentException("Bu isimde bir kullanıcı zaten mevcut: " + ad);
        }
        
        Kullanici yeniKullanici = new Kullanici(ad);
        return kullaniciRepository.save(yeniKullanici);
    }
    
    // Kullanıcı ekle (Entity ile)
    public Kullanici kullaniciEkle(Kullanici kullanici) {
        return kullaniciRepository.save(kullanici);
    }
    
    // Kullanıcı güncelle
    public Kullanici kullaniciGuncelle(Kullanici kullanici) {
        if (kullanici.getId() == null) {
            throw new IllegalArgumentException("Güncellenecek kullanıcının ID'si belirtilmeli");
        }
        return kullaniciRepository.save(kullanici);
    }
    
    // Kullanıcıyı pasif yap (silmek yerine)
    public void kullaniciPasifYap(Long id) {
        Optional<Kullanici> kullanici = kullaniciRepository.findById(id);
        if (kullanici.isPresent()) {
            kullanici.get().setAktifMi(false);
            kullaniciRepository.save(kullanici.get());
        }
    }
    
    // Kullanıcıyı aktif yap
    public void kullaniciAktifYap(Long id) {
        Optional<Kullanici> kullanici = kullaniciRepository.findById(id);
        if (kullanici.isPresent()) {
            kullanici.get().setAktifMi(true);
            kullaniciRepository.save(kullanici.get());
        }
    }
    
    // Aktif kullanıcı sayısını getir
    public Long aktifKullaniciSayisi() {
        return kullaniciRepository.aktifKullaniciSayisi();
    }
    
    // Kullanıcı adına göre arama
    public List<Kullanici> kullaniciAra(String ad) {
        return kullaniciRepository.findByAdStartingWithIgnoreCase(ad);
    }
}
