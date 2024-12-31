package com.shopee.shopeecareer.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsDTO {
    private String message;
    private Integer employerID;
    private Integer applicationID;
}
