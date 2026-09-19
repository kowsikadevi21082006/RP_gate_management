package com.rpgate.controller;

import com.rpgate.dto.PersonnelDtos;
import com.rpgate.service.PersonnelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {
    private final PersonnelService service;
    public PersonnelController(PersonnelService service) { this.service = service; }
    @GetMapping public List<PersonnelDtos.Response> list(@RequestParam(required=false) String search) { return service.list(search); }
    @GetMapping("/{id}") public PersonnelDtos.Response get(@PathVariable long id) { return service.getOrThrow(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public PersonnelDtos.Response create(@Valid @RequestBody PersonnelDtos.Request request) { return service.create(request); }
    @PutMapping("/{id}") public PersonnelDtos.Response update(@PathVariable long id, @Valid @RequestBody PersonnelDtos.Request request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable long id) { service.delete(id); }
    @DeleteMapping @ResponseStatus(HttpStatus.NO_CONTENT) public void clear() { service.clear(); }
}
