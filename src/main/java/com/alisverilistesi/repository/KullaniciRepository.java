package com.alisverilistesi.repository;

import com.alisverilistesi.model.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {
    
    // Aktif kullanıcıları getir
    List<Kullanici> findByAktifMiTrue();
    
    // Kullanıcı adına göre arama (büyük/küçük harf duyarsız)
    Optional<Kullanici> findByAdIgnoreCase(String ad);
    
    // Kullanıcı adı ile başlayanları getir
    List<Kullanici> findByAdStartingWithIgnoreCase(String ad);
    
    // Aktif kullanıcı sayısını getir
    @Query("SELECT COUNT(k) FROM Kullanici k WHERE k.aktifMi = true")
    Long aktifKullaniciSayisi();
}
