package com.shopee.shopeecareer.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsApplicantDTO {
    private Integer jobID;
    private String message;
    private String email;

}
