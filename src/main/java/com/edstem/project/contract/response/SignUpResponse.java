package com.edstem.project.contract.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SignUpResponse {
    private Long id;
    private String name;
    private String email;
    private String password;
}
