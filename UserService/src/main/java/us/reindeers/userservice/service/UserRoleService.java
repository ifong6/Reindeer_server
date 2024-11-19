package us.reindeers.userservice.service;

import us.reindeers.userservice.domain.dto.RoleDto;

import java.util.UUID;

public interface UserRoleService {
    String checkUserRole(UUID sub, UUID jwtSub);

    String updateUserRole(UUID sub, RoleDto roleDto, UUID jwtSub);
}
