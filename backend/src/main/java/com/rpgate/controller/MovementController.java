package com.rpgate.controller;

import com.rpgate.dto.MovementDtos;
import com.rpgate.model.Category;
import com.rpgate.model.MovementStatus;
import com.rpgate.service.MovementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MovementController {
    private final MovementService service;
    public MovementController(MovementService service) { this.service = service; }
    @PostMapping("/movements") @ResponseStatus(HttpStatus.CREATED) public MovementDtos.Response create(@Valid @RequestBody MovementDtos.Request request) { return service.create(request); }
    @GetMapping("/movements") public List<MovementDtos.Response> list(@RequestParam(required=false) String search, @RequestParam(required=false) Category category, @RequestParam(required=false) String movementType, @RequestParam(required=false) String from, @RequestParam(required=false) String to, @RequestParam(required=false) MovementStatus status, @RequestParam(required=false) Long personnelId) { return service.list(new MovementDtos.Filter(search,category,movementType,from,to,status,personnelId)); }
    @GetMapping("/movements/{id}") public MovementDtos.Response get(@PathVariable long id) { return service.get(id); }
    @PutMapping("/movements/{id}") public MovementDtos.Response update(@PathVariable long id, @Valid @RequestBody MovementDtos.Request request) { return service.update(id, request); }
    @DeleteMapping("/movements/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable long id) { service.delete(id); }
    @DeleteMapping("/movements") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteAll() { service.deleteAll(); }
    @PostMapping("/{category}") @ResponseStatus(HttpStatus.CREATED) public MovementDtos.Response createCategory(@PathVariable String category, @Valid @RequestBody MovementDtos.Request request) { return service.create(new MovementDtos.Request(Category.fromValue(category),request.movementAt(),request.person(),request.movementType(),request.remarks(),request.details())); }
}
