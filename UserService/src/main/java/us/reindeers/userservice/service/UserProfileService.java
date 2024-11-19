package us.reindeers.userservice.service;

import us.reindeers.userservice.domain.dto.AvatarUrlDto;
import us.reindeers.userservice.domain.dto.PersonalInfoDto;
import us.reindeers.userservice.domain.dto.ProfileDto;

import java.util.UUID;

public interface UserProfileService {
  void checkAddressSubmitted(UUID sub, UUID jwtSub);

  void updateAvatarUrl(UUID sub, AvatarUrlDto avatarDto, UUID jwtSub);

  PersonalInfoDto showPersonalInfo(UUID sub, UUID jwtSub);

  void updateProfileInfo(UUID sub, ProfileDto profileDto, UUID jwtSub);

  PersonalInfoDto getUserInfo(UUID sub);
}
