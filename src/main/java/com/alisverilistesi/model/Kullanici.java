package com.alisverilistesi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "kullanicilar")
public class Kullanici {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Kullanıcı adı boş olamaz")
    @Size(min = 2, max = 50, message = "Kullanıcı adı 2-50 karakter arası olmalı")
    @Column(name = "ad", nullable = false, length = 50)
    private String ad;
    
    @Column(name = "aktif_mi", nullable = false)
    private Boolean aktifMi = true;
    
    // Constructor'lar
    public Kullanici() {}
    
    public Kullanici(String ad) {
        this.ad = ad;
        this.aktifMi = true;
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
    
    public Boolean getAktifMi() {
        return aktifMi;
    }
    
    public void setAktifMi(Boolean aktifMi) {
        this.aktifMi = aktifMi;
    }
    
    @Override
    public String toString() {
        return "Kullanici{" +
                "id=" + id +
                ", ad='" + ad + '\'' +
                ", aktifMi=" + aktifMi +
                '}';
    }
}
