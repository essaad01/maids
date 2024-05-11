package data;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.maids.app.dto.PatronDto;
import com.maids.app.entity.Patron;

public class PatronTestData {

    public PatronDto patronDto() {
        PatronDto patronDto = new PatronDto();
        patronDto.setName("name");
        patronDto.setContactInformation("contactInformation");
        patronDto.setId(1L);
        return patronDto;
    }

    public Patron patron() {
        Patron patron = new Patron();
        patron.setId(1L);
        patron.setName("name");
        patron.setContactInformation("contactInformation");
        return patron;
    }

    public Page<Patron> PatronsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        long total = 1L;
        List<Patron> patrons =Arrays.asList(patron());
        return new PageImpl<>(patrons, pageable, total);
    }
}
