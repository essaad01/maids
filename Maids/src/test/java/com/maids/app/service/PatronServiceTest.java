package com.maids.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.maids.app.constant.ExceptionMessage;
import com.maids.app.dto.PatronDto;
import com.maids.app.entity.Patron;
import com.maids.app.exception.CustomException;
import com.maids.app.repository.PatronRepository;

import data.PatronTestData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ComponentScan(basePackages = { "com.maids.app", "data" })
@TestInstance(Lifecycle.PER_CLASS)
public class PatronServiceTest {
	@Mock
    private PatronRepository patronRepository;
    
	@Mock
	private ModelMapper modelMapper;
	
	@Autowired
	private ModelMapper realModelMapper;
	
	@InjectMocks
	private PatronService patronService;
	
	@InjectMocks
	private PatronTestData patronTestData;
	
	private Method getPatronByIdMethod;
	
	@BeforeAll
	public void setUp() throws NoSuchMethodException {
		getPatronByIdMethod = PatronService.class
				.getDeclaredMethod("getPatronById", Long.class);
		getPatronByIdMethod.setAccessible(true);
	}

	@Nested
	@DisplayName("Create Patron")
	class CreatePatron {
		
		@Test
		public void shouldSucceedAndCreatePatron() {
			PatronDto patronDto = patronTestData.patronDto();
			Patron patron = patronTestData.patron();

			when(modelMapper.map(patronDto, Patron.class)).thenReturn(patron);
			when(modelMapper.map(patron, PatronDto.class)).thenReturn(realModelMapper.map(patron, PatronDto.class));

			PatronDto result = patronService.createPatron(patronDto);

			verify(patronRepository).save(patron);
			assertNotNull(result);
			assertEquals(patron.getId(), result.getId());
			assertEquals(patron.getName(), result.getName());
			assertEquals(patron.getContactInformation(), result.getContactInformation());
		}

	}
	
    @Nested
    @DisplayName("Get All Patrons")
    class GetAllPatrons {
    	
        @Test
        public void shouldSucceedAndReturnPatrons() {
            int page = 1;
            int limit = 10;
            Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit, Sort.by(Sort.Direction.DESC, "createdTimeStamp"));

            Page<Patron> patronsWithPagination = patronTestData.PatronsWithPagination();
            List<Patron> patrons = patronsWithPagination.getContent();

            when(patronRepository.findAll(pageable)).thenReturn(patronsWithPagination);

            Page<Patron> result = patronService.getAllPatrons(page, limit);

            assertNotNull(result);
            assertEquals(patronsWithPagination, result);
            assertNotNull(result.getContent());
            assertEquals(result.getContent(), patrons);
        }
    }
    
    @Nested
    @DisplayName("Get patron by id")
    class GetPatron {
        @Test
        public void GetPatronByIdShouldSucceed() {
            Long patronId = 1L;
            Patron patron = new Patron();
            patron.setId(patronId);

            when(patronRepository.findById(anyLong())).thenReturn(Optional.of(patron));
            
            Patron result;
			try {
				result = (Patron) getPatronByIdMethod.invoke(patronService, patronId);
				assertNotNull(result);
	            assertEquals(patron, result);

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("Failed to invoke getPatronById Method");
			}
        }

        @Test
        public void GetPatronByIdShouldFailNotFound() {
            when(patronRepository.findById(anyLong())).thenReturn(Optional.empty());

			try {
				getPatronByIdMethod.invoke(patronService, 1L);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	            Throwable cause = e.getCause();
	            if (cause instanceof CustomException) {
	                CustomException customException = (CustomException) cause;
	                assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
	                assertEquals(ExceptionMessage.NotFound.PATRON_ID, customException.getMessage());
	            }
	            else log.error("Failed to invoke getPatronById Method");
			}
        }
    }

	@Nested
	@DisplayName("Edit Patron")
	class EditPatron {
		
		@Test
		public void shouldSucceedAndEditPatron() {
			Patron patron = patronTestData.patron();

			PatronDto patronDto = new PatronDto();
			patronDto.setId(1L);
	        patronDto.setName("editedName");
	        patronDto.setContactInformation("editedContactInfo");
			
			when(patronRepository.findById(anyLong())).thenReturn(Optional.of(patron));
			when(modelMapper.map(any(Patron.class), eq(PatronDto.class))).thenReturn(patronDto);

			PatronDto result = patronService.editPatron(patron.getId(), patronDto);

			verify(patronRepository).save(patron);
			assertNotNull(result);
			assertEquals(patron.getId(), result.getId());
			assertEquals("editedName", result.getName());
			assertEquals("editedContactInfo", result.getContactInformation());
		}
	}
	
    @Nested
    @DisplayName("Get Patron Details")
    class GetPatronDetails {
    	
        @Test
        public void shouldSucceedAndReturnPatronDetails() {
        	Long patronId = 1L;
        	Patron patron = patronTestData.patron();
        	
            when(patronRepository.findById(1L)).thenReturn(Optional.of(patron));
			when(modelMapper.map(patron, PatronDto.class)).thenReturn(realModelMapper.map(patron, PatronDto.class));

            PatronDto result = patronService.getPatronDetails(patronId);

            assertNotNull(result);
			assertEquals(patron.getName(), result.getName());
			assertEquals(patron.getContactInformation(), result.getContactInformation());
        }
    }

    @Nested
    @DisplayName("Delete Patron")
    class DeletePatron {
    	
        @Test
        public void shouldSucceedAndDeletePatron() {
        	Long patronId = 1L;
        	Patron patron = patronTestData.patron();
        	
            when(patronRepository.findById(1L)).thenReturn(Optional.of(patron));

            patronService.deletePatron(patronId);
            
			verify(patronRepository).delete(patron);
        }
    }

}
