package com.alisverilistesi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "urunler")
public class Urun {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Ürün adı boş olamaz")
    @Size(min = 1, max = 100, message = "Ürün adı 1-100 karakter arası olmalı")
    @Column(name = "ad", nullable = false, length = 100)
    private String ad;
    
    @Column(name = "satin_alindi_mi", nullable = false)
    private Boolean satinAlindiMi = false;
    
    @Column(name = "eklenme_tarihi", nullable = false)
    private LocalDateTime eklenmeTarihi;
    
    @Size(max = 255, message = "Açıklama 255 karakterden uzun olamaz")
    @Column(name = "aciklama", length = 255)
    private String aciklama;
    
    @ManyToOne(fetch = FetchType.EAGER)//Ürün çekildiğinde kullanıcı bilgisi de hemen yüklenirDatabase'den tek query ile her ikisi gelir
    @JoinColumn(name = "ekleyen_kisi_id", nullable = false)
    private Kullanici ekleyenKisi;
    
    // Constructor'lar
    public Urun() {
        this.eklenmeTarihi = LocalDateTime.now();
        this.satinAlindiMi = false;
    }
    
    public Urun(String ad, Kullanici ekleyenKisi) {
        this();
        this.ad = ad;
        this.ekleyenKisi = ekleyenKisi;
    }
    
    public Urun(String ad, String aciklama, Kullanici ekleyenKisi) {
        this(ad, ekleyenKisi);
        this.aciklama = aciklama;
    }
    
    // Getter ve Setter metodları
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAd() {
        return ad;
    }
    
    public void setAd(String ad) {
        this.ad = ad;
    }
    
    public Boolean getSatinAlindiMi() {
        return satinAlindiMi;
    }
    
    public void setSatinAlindiMi(Boolean satinAlindiMi) {
        this.satinAlindiMi = satinAlindiMi;
    }
    
    public LocalDateTime getEklenmeTarihi() {
        return eklenmeTarihi;
    }
    
    public void setEklenmeTarihi(LocalDateTime eklenmeTarihi) {
        this.eklenmeTarihi = eklenmeTarihi;
    }
    
    public String getAciklama() {
        return aciklama;
    }
    
    public void setAciklama(String aciklama) {
        this.aciklama = aciklama;
    }
    
    public Kullanici getEkleyenKisi() {
        return ekleyenKisi;
    }
    
    public void setEkleyenKisi(Kullanici ekleyenKisi) {
        this.ekleyenKisi = ekleyenKisi;
    }
    
    @Override
    public String toString() {
        return "Urun{" +
                "id=" + id +
                ", ad='" + ad + '\'' +
                ", satinAlindiMi=" + satinAlindiMi +
                ", eklenmeTarihi=" + eklenmeTarihi +
                ", aciklama='" + aciklama + '\'' +
                ", ekleyenKisi=" + (ekleyenKisi != null ? ekleyenKisi.getAd() : "null") +
                '}';
    }
}
