package com.maids.app.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.maids.app.constant.ExceptionMessage;
import com.maids.app.dto.PatronDto;
import com.maids.app.entity.Patron;
import com.maids.app.exception.CustomException;
import com.maids.app.repository.PatronRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PatronService {

    @Autowired
    private PatronRepository patronRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @CacheEvict(value = "patrons", allEntries = true)
	public PatronDto createPatron(PatronDto patronDto) {
		
    	log.info("Create patron API -> create patron and save it");
    	Patron patron = modelMapper.map(patronDto, Patron.class);
		patronRepository.save(patron);
		
    	log.info("Create patron API -> convert patron to dto");
		return modelMapper.map(patron, PatronDto.class);
	}

	@Cacheable("patrons")
	public Page<Patron> getAllPatrons(Integer page, Integer limit) {
		log.info("Get all patrons API");
		Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit, Sort.by(Sort.Direction.DESC, "createdTimeStamp"));
		return patronRepository.findAll(pageable);
	}
	
	@Cacheable("patron-details")
	public PatronDto getPatronDetails(Long patronId) {
    	log.info("get patron details API -> validate patron id");
    	Patron patron = getPatronById(patronId);
    	
    	log.info("get patron details API -> convert patron to dto");
		return modelMapper.map(patron, PatronDto.class);
	}

	@Caching(evict = {
		    @CacheEvict(value = "patrons", allEntries = true),
		    @CacheEvict(value = {"patrons", "patron-details"}, key = "#patronId")
		})
	public PatronDto editPatron(Long patronId, PatronDto patronDto) {
    	log.info("Edit patron API -> validate patron id");
    	Patron patron = getPatronById(patronId);

		if(patronDto.getName() != null && !patronDto.getName().isBlank())
			patron.setName(patronDto.getName());

		if(patronDto.getContactInformation() != null && !patronDto.getContactInformation().isBlank())
			patron.setContactInformation(patronDto.getContactInformation());
		
    	log.info("Edit patron API -> save updated patron");
		patronRepository.save(patron);
		
    	log.info("Edit patron API -> convert patron to dto");
		return modelMapper.map(patron, PatronDto.class);
	}

	Patron getPatronById(Long patronId) throws CustomException {
		return patronRepository.findById(patronId)
                .orElseThrow(() -> new CustomException(ExceptionMessage.NotFound.PATRON_ID, HttpStatus.NOT_FOUND));
	}

	@Caching(evict = {
		    @CacheEvict(value = "patrons", allEntries = true),
		    @CacheEvict(value = {"patrons", "patron-details"}, key = "#patronId")
		})
	@Transactional
	public void deletePatron(Long patronId) {
    	log.info("delete patron API -> validate patron id");
    	Patron patron = getPatronById(patronId);
    	
    	log.info("delete patron API -> delete patron");
		patronRepository.delete(patron);
	}

}
