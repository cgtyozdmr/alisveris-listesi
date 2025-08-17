package com.alisverilistesi.service;

import com.alisverilistesi.model.Urun;
import com.alisverilistesi.model.Kullanici;
import com.alisverilistesi.repository.UrunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UrunService {
    
    private final UrunRepository urunRepository;
    
    public UrunService(UrunRepository urunRepository) {
        this.urunRepository = urunRepository;
    }
    
    // Tüm ürünleri getir (en yeni önce)
    public List<Urun> tumUrunleriGetir() {
        return urunRepository.findAllByOrderByEklenmeTarihiDesc();
    }
    
    // ID'ye göre ürün getir
    public Optional<Urun> urunGetir(Long id) {
        return urunRepository.findById(id);
    }
    
    // Satın alınmış ürünleri getir
    public List<Urun> satinAlinanUrunleriGetir() {
        return urunRepository.findBySatinAlindiMiTrueOrderByEklenmeTarihiDesc();
    }
    
    // Satın alınmamış ürünleri getir
    public List<Urun> satinAlinmamiSurunleriGetir() {
        return urunRepository.findBySatinAlindiMiFalseOrderByEklenmeTarihiDesc();
    }
    
    // Belirli kullanıcının eklediği ürünleri getir
    public List<Urun> kullaniciUrunleriGetir(Kullanici kullanici) {
        return urunRepository.findByEkleyenKisiOrderByEklenmeTarihiDesc(kullanici);
    }
    
    // Yeni ürün ekle
    public Urun urunEkle(String ad, Kullanici ekleyenKisi) {
        Urun yeniUrun = new Urun(ad, ekleyenKisi);
        return urunRepository.save(yeniUrun);
    }
    
    // Açıklamalı ürün ekle
    public Urun urunEkle(String ad, String aciklama, Kullanici ekleyenKisi) {
        Urun yeniUrun = new Urun(ad, aciklama, ekleyenKisi);
        return urunRepository.save(yeniUrun);
    }
    
    // Ürün ekle (Entity ile)
    public Urun urunEkle(Urun urun) {
        return urunRepository.save(urun);
    }
    
    // Ürün güncelle
    public Urun urunGuncelle(Urun urun) {
        if (urun.getId() == null) {
            throw new IllegalArgumentException("Güncellenecek ürünün ID'si belirtilmeli");
        }
        return urunRepository.save(urun);
    }
    
    // Ürün satın alındı/alınmadı durumunu değiştir
    public Urun urunDurumDegistir(Long id) {
        Optional<Urun> urunOptional = urunRepository.findById(id);
        if (urunOptional.isPresent()) {
            Urun urun = urunOptional.get();
            urun.setSatinAlindiMi(!urun.getSatinAlindiMi());
            return urunRepository.save(urun);
        }
        throw new IllegalArgumentException("Ürün bulunamadı: " + id);
    }
    
    // Ürünü satın alındı olarak işaretle
    public Urun urunSatinAlindiOlarakIsaretle(Long id) {
        Optional<Urun> urunOptional = urunRepository.findById(id);
        if (urunOptional.isPresent()) {
            Urun urun = urunOptional.get();
            urun.setSatinAlindiMi(true);
            return urunRepository.save(urun);
        }
        throw new IllegalArgumentException("Ürün bulunamadı: " + id);
    }
    
    // Ürünü satın alınmadı olarak işaretle
    public Urun urunSatinAlinmadiOlarakIsaretle(Long id) {
        Optional<Urun> urunOptional = urunRepository.findById(id);
        if (urunOptional.isPresent()) {
            Urun urun = urunOptional.get();
            urun.setSatinAlindiMi(false);
            return urunRepository.save(urun);
        }
        throw new IllegalArgumentException("Ürün bulunamadı: " + id);
    }
    
    // Ürün sil
    public void urunSil(Long id) {
        if (urunRepository.existsById(id)) {
            urunRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Silinecek ürün bulunamadı: " + id);
        }
    }
    
    // Satın alınmış ürünleri temizle
    public void satinAlinanUrunleriTemizle() {
        urunRepository.deleteBySatinAlindiMiTrue();
    }
    
    // Ürün ara
    public List<Urun> urunAra(String aranacakKelime) {
        return urunRepository.findByAdContainingIgnoreCaseOrderByEklenmeTarihiDesc(aranacakKelime);
    }
    
    // İstatistikler
    public Long toplamUrunSayisi() {
        return urunRepository.toplamUrunSayisi();
    }
    
    public Long satinAlinanUrunSayisi() {
        return urunRepository.satinAlinanUrunSayisi();
    }
    
    public Long satinAlinmamiSurunSayisi() {
        return urunRepository.satinAlinmamiSurunSayisi();
    }
    
    public Long kullaniciUrunSayisi(Kullanici kullanici) {
        return urunRepository.kullaniciUrunSayisi(kullanici);
    }
}
