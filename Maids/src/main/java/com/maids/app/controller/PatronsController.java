package com.maids.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.maids.app.dto.PatronDto;
import com.maids.app.dto.PaginationResponse;
import com.maids.app.dto.Response;
import com.maids.app.entity.Patron;
import com.maids.app.exception.CustomException;
import com.maids.app.service.PatronService;

import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/patrons")
public class PatronsController {

    @Autowired
	private ModelMapper modelMapper;

	@Autowired
    private PatronService patronService;

	@PostMapping
	public ResponseEntity<?> createPatron(@RequestBody @Validated(PatronDto.Create.class) PatronDto patronDto) {
		PatronDto createdPatronDto = patronService.createPatron(patronDto);
		return new ResponseEntity<>(
				new Response(createdPatronDto, "Patron created successfully", true, HttpStatus.CREATED.value()),
				HttpStatus.CREATED);
	}
	
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    	List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return errors;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllPatrons(@RequestParam(required = false, defaultValue = "1") Integer page,
    		@RequestParam(required = false, defaultValue = "10") Integer limit) {
    	
        Page<Patron> patrons = patronService.getAllPatrons(page, limit);
        List<PatronDto> patronDtos = patrons.getContent().stream()
        		.map(patron -> modelMapper.map(patron, PatronDto.class))
        		.collect(Collectors.toList());
        return ResponseEntity.ok(new PaginationResponse(patronDtos, "List of patrons", patrons.getTotalElements(),
                (long) patrons.getTotalPages()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatronDetails(@PathVariable("id") Long patronId) {
		try {
			PatronDto patronDto = patronService.getPatronDetails(patronId);
            return new ResponseEntity<>(new Response(patronDto, "Patron details", true, HttpStatus.OK.value()), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editPatron(@PathVariable("id") Long patronId, @RequestBody PatronDto patronDto) {
		try {
			PatronDto editedPatronDto = patronService.editPatron(patronId, patronDto);
            return new ResponseEntity<>(new Response(editedPatronDto, "Patron updated successfully", true, HttpStatus.OK.value()), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatron(@PathVariable("id") Long patronId) {
		try {
			patronService.deletePatron(patronId);
            return new ResponseEntity<>(new Response(null, "Patron deleted successfully", true, HttpStatus.OK.value()), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
    }

}
