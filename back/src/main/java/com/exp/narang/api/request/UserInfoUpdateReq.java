package com.exp.narang.api.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ApiModel("UserInformation(Profile image, Nickname) Update")
public class UserInfoUpdateReq {
    String username;
    String currentPassword;
    String newPassword;
    MultipartFile file;
}
