package com.alisverilistesi.repository;

import com.alisverilistesi.model.Urun;
import com.alisverilistesi.model.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrunRepository extends JpaRepository<Urun, Long> {
    
    // Tüm ürünleri eklenme tarihine göre sırala (en yeni önce)
    List<Urun> findAllByOrderByEklenmeTarihiDesc();
    
    // Satın alınmış ürünleri getir
    List<Urun> findBySatinAlindiMiTrueOrderByEklenmeTarihiDesc();
    
    // Satın alınmamış ürünleri getir
    List<Urun> findBySatinAlindiMiFalseOrderByEklenmeTarihiDesc();
    
    // Belirli kullanıcının eklediği ürünleri getir
    List<Urun> findByEkleyenKisiOrderByEklenmeTarihiDesc(Kullanici kullanici);
    
    // Ürün adına göre arama (büyük/küçük harf duyarsız)
    List<Urun> findByAdContainingIgnoreCaseOrderByEklenmeTarihiDesc(String ad);
    
    // Toplam ürün sayısını getir
    @Query("SELECT COUNT(u) FROM Urun u")
    Long toplamUrunSayisi();
    
    // Satın alınmış ürün sayısını getir
    @Query("SELECT COUNT(u) FROM Urun u WHERE u.satinAlindiMi = true")
    Long satinAlinanUrunSayisi();
    
    // Satın alınmamış ürün sayısını getir
    @Query("SELECT COUNT(u) FROM Urun u WHERE u.satinAlindiMi = false")
    Long satinAlinmamiSurunSayisi();
    
    // Belirli kullanıcının eklediği ürün sayısını getir
    @Query("SELECT COUNT(u) FROM Urun u WHERE u.ekleyenKisi = :kullanici")
    Long kullaniciUrunSayisi(@Param("kullanici") Kullanici kullanici);
    
    // Satın alınmış ürünleri sil
    void deleteBySatinAlindiMiTrue();
}
