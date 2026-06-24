package kd.mgproj.domain.user.dto.request;

import kd.mgproj.common.enums.*;
import kd.mgproj.domain.user.entity.UsersEntity;

import java.util.Date;

public record SignupRequestDto(
        String name,
        String email,
        String password
) {

}


