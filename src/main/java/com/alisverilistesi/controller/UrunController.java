package com.alisverilistesi.controller;

import com.alisverilistesi.model.Urun;
import com.alisverilistesi.model.Kullanici;
import com.alisverilistesi.service.UrunService;
import com.alisverilistesi.service.KullaniciService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class UrunController {
    
    private final UrunService urunService;
    private final KullaniciService kullaniciService;
    
    public UrunController(UrunService urunService, KullaniciService kullaniciService) {
        this.urunService = urunService;
        this.kullaniciService = kullaniciService;
    }
    
    // Ana sayfa - ortak liste görünümü
    @GetMapping("/")
    public String anaSayfa(Model model) {
        List<Urun> tumUrunler = urunService.tumUrunleriGetir();
        List<Kullanici> aktifKullanicilar = kullaniciService.aktifKullanicilariGetir();
        
        // İstatistikler
        Long toplamUrunSayisi = urunService.toplamUrunSayisi();
        Long satinAlinanSayisi = urunService.satinAlinanUrunSayisi();
        Long satinAlinmamiSayisi = urunService.satinAlinmamiSurunSayisi();
        
        model.addAttribute("urunler", tumUrunler);
        model.addAttribute("kullanicilar", aktifKullanicilar);
        model.addAttribute("yeniUrun", new Urun());
        model.addAttribute("toplamUrunSayisi", toplamUrunSayisi);
        model.addAttribute("satinAlinanSayisi", satinAlinanSayisi);
        model.addAttribute("satinAlinmamiSayisi", satinAlinmamiSayisi);
        
        return "index";
    }
    
    // Ürün ekleme (POST)
    @PostMapping("/urun/ekle")
    public String urunEkle(@Valid @ModelAttribute("yeniUrun") Urun urun,
                           BindingResult result,
                           @RequestParam("ekleyenKisiId") Long ekleyenKisiId,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("hata", "Ürün bilgilerinde hata var!");
            return "redirect:/";
        }
        
        try {
            Optional<Kullanici> kullanici = kullaniciService.kullaniciGetir(ekleyenKisiId);
            if (kullanici.isEmpty()) {
                redirectAttributes.addFlashAttribute("hata", "Kullanıcı bulunamadı!");
                return "redirect:/";
            }
            
            urun.setEkleyenKisi(kullanici.get());
            urunService.urunEkle(urun);
            redirectAttributes.addFlashAttribute("basari", "Ürün başarıyla eklendi!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("hata", "Ürün eklenirken hata: " + e.getMessage());
        }
        
        return "redirect:/";
    }
    
    // Ürün ekleme sayfası
    @GetMapping("/urun/ekle")
    public String urunEkleSayfasi(Model model) {
        List<Kullanici> aktifKullanicilar = kullaniciService.aktifKullanicilariGetir();
        model.addAttribute("kullanicilar", aktifKullanicilar);
        model.addAttribute("yeniUrun", new Urun());
        return "urun-ekle";
    }
    
    // Kullanıcı ekleme sayfası
    @GetMapping("/kullanici/ekle")
    public String kullaniciEkleSayfasi(Model model) {
        model.addAttribute("yeniKullanici", new Kullanici());
        return "kullanici-ekle";
    }
    
    // Kullanıcı ekleme (POST)
    @PostMapping("/kullanici/ekle")
    public String kullaniciEkle(@Valid @ModelAttribute("yeniKullanici") Kullanici kullanici,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("hata", "Kullanıcı bilgilerinde hata var!");
            return "redirect:/kullanici/ekle";
        }
        
        try {
            kullaniciService.kullaniciEkle(kullanici);
            redirectAttributes.addFlashAttribute("basari", "Kullanıcı başarıyla eklendi!");
            return "redirect:/";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("hata", e.getMessage());
            return "redirect:/kullanici/ekle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("hata", "Kullanıcı eklenirken hata: " + e.getMessage());
            return "redirect:/kullanici/ekle";
        }
    }
    
    // ========== REST API ENDPOINTS ==========
    
    // Tüm ürünleri getir (JSON)
    @GetMapping("/api/urun")
    @ResponseBody
    public ResponseEntity<List<Urun>> apiTumUrunleriGetir() {
        List<Urun> urunler = urunService.tumUrunleriGetir();
        return ResponseEntity.ok(urunler);
    }
    
    // Ürün durumunu değiştir (AJAX)
    @PostMapping("/api/urun/{id}/durum-degistir")
    @ResponseBody
    public ResponseEntity<?> urunDurumDegistir(@PathVariable Long id) {
        try {
            Urun urun = urunService.urunDurumDegistir(id);
            return ResponseEntity.ok(urun);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
    
    // Ürün sil (AJAX)
    @DeleteMapping("/api/urun/{id}")
    @ResponseBody
    public ResponseEntity<?> urunSil(@PathVariable Long id) {
        try {
            urunService.urunSil(id);
            return ResponseEntity.ok("Ürün silindi");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
    
    // Satın alınmış ürünleri temizle
    @PostMapping("/api/urun/temizle")
    @ResponseBody
    public ResponseEntity<?> satinAlinanUrunleriTemizle() {
        try {
            urunService.satinAlinanUrunleriTemizle();
            return ResponseEntity.ok("Satın alınmış ürünler temizlendi");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
    
    // Ürün ara
    @GetMapping("/api/urun/ara")
    @ResponseBody
    public ResponseEntity<List<Urun>> urunAra(@RequestParam String q) {
        List<Urun> bulunanUrunler = urunService.urunAra(q);
        return ResponseEntity.ok(bulunanUrunler);
    }
    
    // Tüm kullanıcıları getir (JSON)
    @GetMapping("/api/kullanici")
    @ResponseBody
    public ResponseEntity<List<Kullanici>> apiTumKullanicilariGetir() {
        List<Kullanici> kullanicilar = kullaniciService.aktifKullanicilariGetir();
        return ResponseEntity.ok(kullanicilar);
    }
    
    // Yeni kullanıcı ekle (JSON)
    @PostMapping("/api/kullanici")
    @ResponseBody
    public ResponseEntity<?> apiKullaniciEkle(@RequestBody Kullanici kullanici) {
        try {
            Kullanici yeniKullanici = kullaniciService.kullaniciEkle(kullanici);
            return ResponseEntity.ok(yeniKullanici);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Hata: " + e.getMessage());
        }
    }
}
