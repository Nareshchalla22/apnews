package com.news.apnews.controller;

import com.news.apnews.model.*;
import com.news.apnews.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// @CrossOrigin(origins = "http://localhost:5173")
@CrossOrigin(origins = {
    "https://flash-news-ui.vercel.app",
    "http://localhost:5173"
})
@RequestMapping("/api")
public class CategoryMenuController {

    @Autowired
    private GlobalRepository globalRepo;
    @Autowired
    private NationalRepository nationalRepo;
    @Autowired
    private StateRepository stateRepo;
    @Autowired
    private BusinessRepository businessRepo;
    @Autowired
    private CrimeRepository crimeRepo;
    @Autowired
    private EntertainmentRepository entertainmentRepo;
    @Autowired
    private SportsRepository sportsRepo;
    @Autowired
    private HealthRepository healthRepo;
    @Autowired
    private PoliticsRepository politicsRepo;
    @Autowired
    private TravelRepository travelRepo;
    @Autowired
    private TechnologyRepository technologyRepo;

    // --- GLOBAL ---
    @GetMapping("/global")
    public List<Global> getGlobal() {
        return globalRepo.findAll();
    }

    @PostMapping("/global")
    public Global addGlobal(@RequestBody Global news) {
        return globalRepo.save(news);
    }

    @PutMapping("/global/{id}")
    public Global updateGlobal(@PathVariable Long id, @RequestBody Global d) {
        Global e = globalRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return globalRepo.save(e);
    }

    // --- NATIONAL ---
    @GetMapping("/national")
    public List<National> getNational() {
        return nationalRepo.findAll();
    }

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

    // --- STATE ---
    @GetMapping("/state")
    public List<State> getState() {
        return stateRepo.findAll();
    }

    @PostMapping("/state")
    public State addState(@RequestBody State news) {
        return stateRepo.save(news);
    }

    @PutMapping("/state/{id}")
    public State updateState(@PathVariable Long id, @RequestBody State d) {
        State e = stateRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return stateRepo.save(e);
    }

    // --- BUSINESS ---
    @GetMapping("/business")
    public List<Business> getBusiness() {
        return businessRepo.findAll();
    }

    @PostMapping("/business")
    public Business addBusiness(@RequestBody Business news) {
        return businessRepo.save(news);
    }

    @PutMapping("/business/{id}")
    public Business updateBusiness(@PathVariable Long id, @RequestBody Business d) {
        Business e = businessRepo.findById(id).orElseThrow();

        // Using your specific Business fields
        e.setCompanyName(d.getCompanyName());
        e.setHeadline(d.getHeadline());
        e.setAnalysis(d.getAnalysis());
        e.setStockUpdate(d.getStockUpdate());

        return businessRepo.save(e);
    }

    // --- CRIME ---
    @GetMapping("/crime")
    public List<Crime> getCrime() {
        return crimeRepo.findAll();
    }

    @PostMapping("/crime")
    public Crime addCrime(@RequestBody Crime news) {
        return crimeRepo.save(news);
    }

    @PutMapping("/crime/{id}")
    public Crime updateCrime(@PathVariable Long id, @RequestBody Crime d) {
        Crime e = crimeRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return crimeRepo.save(e);
    }

    // --- ENTERTAINMENT ---
    @GetMapping("/entertainment")
    public List<Entertainment> getEntertainment() {
        return entertainmentRepo.findAll();
    }

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

    // --- SPORTS ---
    @GetMapping("/sports")
    public List<Sports> getSports() {
        return sportsRepo.findAll();
    }

    @PostMapping("/sports")
    public Sports addSports(@RequestBody Sports news) {
        return sportsRepo.save(news);
    }

    @PutMapping("/sports/{id}")
    public Sports updateSports(@PathVariable Long id, @RequestBody Sports d) {
        Sports e = sportsRepo.findById(id).orElseThrow();

        e.setMatchTitle(d.getMatchTitle());
        e.setSummary(d.getSummary());
        e.setScoreUpdate(d.getScoreUpdate());
        e.setImageUrl(d.getImageUrl());

        return sportsRepo.save(e);
    }

    // --- HEALTH ---
    @GetMapping("/health")
    public List<Health> getHealth() {
        return healthRepo.findAll();
    }

    @PostMapping("/health")
    public Health addHealth(@RequestBody Health news) {
        return healthRepo.save(news);
    }

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

    // --- POLITICS ---
    @GetMapping("/politics")
    public List<Politics> getPolitics() {
        return politicsRepo.findAll();
    }

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

    // --- TRAVEL ---
    @GetMapping("/travel")
    public List<Travel> getTravel() {
        return travelRepo.findAll();
    }

    @PostMapping("/travel")
    public Travel addTravel(@RequestBody Travel news) {
        return travelRepo.save(news);
    }

    @PutMapping("/travel/{id}")
    public Travel updateTravel(@PathVariable Long id, @RequestBody Travel d) {
        Travel e = travelRepo.findById(id).orElseThrow();
        e.setTitle(d.getTitle());
        e.setDescription(d.getDescription());
        e.setImageUrl(d.getImageUrl());
        return travelRepo.save(e);
    }

    // --- TECHNOLOGY ---
    @GetMapping("/technology")
    public List<Technology> getTech() {
        return technologyRepo.findAll();
    }

    @PostMapping("/technology")
    public Technology addTech(@RequestBody Technology news) {
        return technologyRepo.save(news);
    }
    
}