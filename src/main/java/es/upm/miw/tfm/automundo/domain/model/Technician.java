package es.upm.miw.tfm.automundo.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Technician {
    @NotBlank
    private String id;
    @NotBlank
    private String identificationId;
    private String ssNumber;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime registrationDate;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime leaveDate;
    private String mobile;
    private String name;
    private String surName;
    private String secondSurName;
    private Boolean active;

    public String getCompleteName(){
        return getName() + " " + getSurName() + " " + getSecondSurName();
    }

}
