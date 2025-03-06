package com.shopee.shopeecareer.UserController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class jobFilterRequest {
    public String experienceType;
    public String location;
}
