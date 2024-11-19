package us.reindeers.userservice.service;

import us.reindeers.userservice.domain.dto.UserDto;

public interface UserService {
    void addUser(UserDto userDto);
}
