package com.maids.app.dto;
import com.maids.app.constant.ExceptionMessage.Validation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatronDto {

    private Long id;

    @NotBlank(message = Validation.REQUIRED_NAME, groups = { Create.class })
    private String name;

    @NotBlank(message = Validation.REQUIRED_CONTACT_INFORMATION, groups = { Create.class })
    private String contactInformation;
    
    public interface Create{}
}
