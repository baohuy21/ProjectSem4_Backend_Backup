package com.shopee.shopeecareer.ResponseDTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationOfUserDTO {
    private int id;
    private String jobTitle;
    private Date createdAt;
    private String status;
}
