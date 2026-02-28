package com.cesde.parkingFlow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/capacidad")
    public ResponseEntity<String> configurarCapacidad(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok("Capacidad configurada");
    }
}

