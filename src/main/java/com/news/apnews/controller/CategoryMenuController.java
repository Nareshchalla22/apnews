package com.news.apnews.controller;

import com.news.apnews.model.*;
import com.news.apnews.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// ERROR WAS HERE: @CrossOrigin was here AND SecurityConfig also has CORS config.
// Having BOTH causes a conflict — in production the wrong one wins and
// you get "No 'Access-Control-Allow-Origin' header" errors.
// FIX: Remove @CrossOrigin entirely — SecurityConfig handles CORS globally.
@RequestMapping("/api")
public class CategoryMenuController {

    @Autowired private GlobalRepository        globalRepo;
    @Autowired private NationalRepository      nationalRepo;
    @Autowired private StateRepository         stateRepo;
    @Autowired private BusinessRepository      businessRepo;
    @Autowired private CrimeRepository         crimeRepo;
    @Autowired private EntertainmentRepository entertainmentRepo;
    @Autowired private SportsRepository        sportsRepo;
    @Autowired private HealthRepository        healthRepo;
    @Autowired private PoliticsRepository      politicsRepo;
    @Autowired private TravelRepository        travelRepo;
    @Autowired private TechnologyRepository    technologyRepo;

    // ── GLOBAL ───────────────────────────────────────────────────────
    @GetMapping("/global")
    public List<Global> getGlobal() { return globalRepo.findAll(); }

    @PostMapping("/global")
    public ResponseEntity<Global> addGlobal(@RequestBody Global news) {
        if (news.getTitle() == null || news.getTitle().isEmpty())
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(globalRepo.save(news));
    }

    @PutMapping("/global/{id}")
    public Global updateGlobal(@PathVariable Long id, @RequestBody Global d) {
        Global e = globalRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return globalRepo.save(e);
    }

    @DeleteMapping("/global/{id}")
    public ResponseEntity<?> deleteGlobal(@PathVariable Long id) {
        globalRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── NATIONAL ─────────────────────────────────────────────────────
    @GetMapping("/national")
    public List<National> getNational() { return nationalRepo.findAll(); }

    @PostMapping("/national")
    public National addNational(@RequestBody National news) {
        return nationalRepo.save(news);
    }

    @PutMapping("/national/{id}")
    public National updateNational(@PathVariable Long id, @RequestBody National d) {
        National e = nationalRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return nationalRepo.save(e);
    }

    @DeleteMapping("/national/{id}")
    public ResponseEntity<?> deleteNational(@PathVariable Long id) {
        nationalRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── STATE ─────────────────────────────────────────────────────────
    @GetMapping("/state")
    public List<State> getState() { return stateRepo.findAll(); }

    @PostMapping("/state")
    public State addState(@RequestBody State news) { return stateRepo.save(news); }

    @PutMapping("/state/{id}")
    public State updateState(@PathVariable Long id, @RequestBody State d) {
        State e = stateRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return stateRepo.save(e);
    }

    @DeleteMapping("/state/{id}")
    public ResponseEntity<?> deleteState(@PathVariable Long id) {
        stateRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── BUSINESS ──────────────────────────────────────────────────────
    @GetMapping("/business")
    public List<Business> getBusiness() { return businessRepo.findAll(); }

    @PostMapping("/business")
    public Business addBusiness(@RequestBody Business news) {
        return businessRepo.save(news);
    }

    @PutMapping("/business/{id}")
    public Business updateBusiness(@PathVariable Long id, @RequestBody Business d) {
        Business e = businessRepo.findById(id).orElseThrow();
        e.setCompanyName(d.getCompanyName());
        e.setHeadline(d.getHeadline());
        e.setAnalysis(d.getAnalysis());
        e.setStockUpdate(d.getStockUpdate());
        return businessRepo.save(e);
    }

    @DeleteMapping("/business/{id}")
    public ResponseEntity<?> deleteBusiness(@PathVariable Long id) {
        businessRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── CRIME ─────────────────────────────────────────────────────────
    @GetMapping("/crime")
    public List<Crime> getCrime() { return crimeRepo.findAll(); }

    @PostMapping("/crime")
    public Crime addCrime(@RequestBody Crime news) { return crimeRepo.save(news); }

    @PutMapping("/crime/{id}")
    public Crime updateCrime(@PathVariable Long id, @RequestBody Crime d) {
        Crime e = crimeRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return crimeRepo.save(e);
    }

    @DeleteMapping("/crime/{id}")
    public ResponseEntity<?> deleteCrime(@PathVariable Long id) {
        crimeRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── ENTERTAINMENT ─────────────────────────────────────────────────
    @GetMapping("/entertainment")
    public List<Entertainment> getEntertainment() { return entertainmentRepo.findAll(); }

    @PostMapping("/entertainment")
    public Entertainment addEntertainment(@RequestBody Entertainment news) {
        return entertainmentRepo.save(news);
    }

    @PutMapping("/entertainment/{id}")
    public Entertainment updateEnt(@PathVariable Long id, @RequestBody Entertainment d) {
        Entertainment e = entertainmentRepo.findById(id).orElseThrow();
        e.setMovieTitle(d.getMovieTitle());
        e.setCelebrityName(d.getCelebrityName());
        e.setGossipContent(d.getGossipContent());
        return entertainmentRepo.save(e);
    }

    @DeleteMapping("/entertainment/{id}")
    public ResponseEntity<?> deleteEnt(@PathVariable Long id) {
        entertainmentRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── SPORTS ────────────────────────────────────────────────────────
    @GetMapping("/sports")
    public List<Sports> getSports() { return sportsRepo.findAll(); }

    @PostMapping("/sports")
    public Sports addSports(@RequestBody Sports news) { return sportsRepo.save(news); }

    @PutMapping("/sports/{id}")
    public Sports updateSports(@PathVariable Long id, @RequestBody Sports d) {
        Sports e = sportsRepo.findById(id).orElseThrow();
        e.setMatchTitle(d.getMatchTitle());
        e.setSummary(d.getSummary());
        e.setScoreUpdate(d.getScoreUpdate());
        e.setImageUrl(d.getImageUrl());
        return sportsRepo.save(e);
    }

    @DeleteMapping("/sports/{id}")
    public ResponseEntity<?> deleteSports(@PathVariable Long id) {
        sportsRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── HEALTH ────────────────────────────────────────────────────────
    @GetMapping("/health")
    public List<Health> getHealth() { return healthRepo.findAll(); }

    @PostMapping("/health")
    public Health addHealth(@RequestBody Health news) { return healthRepo.save(news); }

    @PutMapping("/health/{id}")
    public Health updateHealth(@PathVariable Long id, @RequestBody Health d) {
        Health e = healthRepo.findById(id).orElseThrow();
        e.setTopic(d.getTopic());
        e.setTitle(d.getTitle());
        e.setMedicalAdvice(d.getMedicalAdvice());
        e.setDoctorConsultant(d.getDoctorConsultant());
        e.setImageUrl(d.getImageUrl());
        return healthRepo.save(e);
    }

    @DeleteMapping("/health/{id}")
    public ResponseEntity<?> deleteHealth(@PathVariable Long id) {
        healthRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── POLITICS ──────────────────────────────────────────────────────
    @GetMapping("/politics")
    public List<Politics> getPolitics() { return politicsRepo.findAll(); }

    @PostMapping("/politics")
    public Politics addPolitics(@RequestBody Politics news) {
        return politicsRepo.save(news);
    }

    @PutMapping("/politics/{id}")
    public Politics updatePol(@PathVariable Long id, @RequestBody Politics d) {
        Politics e = politicsRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return politicsRepo.save(e);
    }

    @DeleteMapping("/politics/{id}")
    public ResponseEntity<?> deletePolitics(@PathVariable Long id) {
        politicsRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── TRAVEL ────────────────────────────────────────────────────────
    @GetMapping("/travel")
    public List<Travel> getTravel() { return travelRepo.findAll(); }

    @PostMapping("/travel")
    public Travel addTravel(@RequestBody Travel news) { return travelRepo.save(news); }

    @PutMapping("/travel/{id}")
    public Travel updateTravel(@PathVariable Long id, @RequestBody Travel d) {
        Travel e = travelRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return travelRepo.save(e);
    }

    @DeleteMapping("/travel/{id}")
    public ResponseEntity<?> deleteTravel(@PathVariable Long id) {
        travelRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ── TECHNOLOGY ────────────────────────────────────────────────────
    @GetMapping("/technology")
    public List<Technology> getTech() { return technologyRepo.findAll(); }

    @PostMapping("/technology")
    public Technology addTech(@RequestBody Technology news) {
        return technologyRepo.save(news);
    }

    @PutMapping("/technology/{id}")
    public Technology updateTech(@PathVariable Long id, @RequestBody Technology d) {
        Technology e = technologyRepo.findById(id).orElseThrow();
        e.setGadgetHead(d.getGadgetHead());
        e.setTechReview(d.getTechReview());
        e.setVersion(d.getVersion());
        e.setImageUrl(d.getImageUrl());
        return technologyRepo.save(e);
    }

    @DeleteMapping("/technology/{id}")
    public ResponseEntity<?> deleteTech(@PathVariable Long id) {
        technologyRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}