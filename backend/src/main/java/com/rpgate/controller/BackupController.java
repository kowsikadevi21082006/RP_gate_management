package com.rpgate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgate.dto.BackupDtos;
import com.rpgate.service.BackupService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/backup")
public class BackupController {
    private final BackupService service; private final ObjectMapper mapper;
    public BackupController(BackupService service,ObjectMapper mapper){this.service=service;this.mapper=mapper;}
    @GetMapping(produces=MediaType.APPLICATION_JSON_VALUE) public ResponseEntity<byte[]> export() throws IOException { return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=rp-gate-backup.json").contentType(MediaType.APPLICATION_JSON).body(mapper.writeValueAsBytes(service.exportData())); }
    @GetMapping(value="/database", produces="application/vnd.sqlite3") public ResponseEntity<byte[]> exportDatabase() { return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=rp-gate.db").contentType(MediaType.parseMediaType("application/vnd.sqlite3")).body(service.exportDatabase()); }
    @PostMapping(value="/restore", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public BackupDtos.RestoreResult restore(@RequestPart("file") MultipartFile file) throws IOException { if(file.isEmpty())throw new IllegalArgumentException("Backup file is empty"); return service.restoreJson(file.getInputStream()); }
    @PostMapping(value="/validate-database", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public java.util.Map<String,Object> validateDatabase(@RequestPart("file") MultipartFile file) throws IOException { if(file.isEmpty())throw new IllegalArgumentException("Backup file is empty"); service.validateDatabaseFile(file.getInputStream()); return java.util.Map.of("valid",true,"message","SQLite backup is valid and ready to restore"); }
    @PostMapping(value="/restore-database", consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public BackupDtos.RestoreResult restoreDatabase(@RequestPart("file") MultipartFile file) throws IOException { if(file.isEmpty())throw new IllegalArgumentException("Backup file is empty"); return service.restoreDatabase(file.getInputStream()); }
}
