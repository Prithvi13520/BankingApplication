package com.app.banking.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = "firstName is mandatory")
    private String firstName;
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    private String otherName;
    @NotBlank(message = "gender is mandatory")
    private String gender;
    @NotBlank(message = "address is mandatory")
    private String address;
    @NotBlank(message = "stateOfOrigin is mandatory")
    private String stateOfOrigin;
    @NotBlank(message = "email is mandatory")
    private String email;
    @NotBlank(message = "phoneNumber is mandatory")
    private String phoneNumber;
    private String alternativePhoneNumber;
    @NotBlank(message = "panNumber is mandatory")
    private String panNumber;

}
