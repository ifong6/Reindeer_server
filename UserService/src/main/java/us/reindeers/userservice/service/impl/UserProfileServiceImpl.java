package us.reindeers.userservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;
import us.reindeers.userservice.domain.dto.AvatarUrlDto;
import us.reindeers.userservice.domain.dto.PersonalInfoDto;
import us.reindeers.userservice.domain.dto.ProfileDto;
import us.reindeers.userservice.domain.entity.*;
import us.reindeers.userservice.repository.CareAgencyRepository;
import us.reindeers.userservice.repository.DonorRepository;
import us.reindeers.userservice.repository.RecipientRepository;
import us.reindeers.userservice.repository.UserRepository;
import us.reindeers.userservice.service.UserProfileService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

  private final UserRepository userRepository;
  private final CareAgencyRepository careAgencyRepository;
  private final RecipientRepository recipientRepository;
  private final DonorRepository donorRepository;


  // update profile
  @Override
  public void updateProfileInfo(UUID sub, ProfileDto profileDto, UUID jwtSub) {
    if (!sub.equals(jwtSub)) {
      log.error(ReturnCode.INVALID_USER.getMessage());
      throw BaseException.builder()
              .returnCode(ReturnCode.INVALID_USER)
              .build();
    }
    // Retrieve the user from the repository using the provided sub
    User user = userRepository.findBySub(sub).orElseThrow(() -> {
      log.error("cannot find sub {}", sub);
      return BaseException.builder()
              .returnCode(ReturnCode.USER_NOT_EXIST)
              .build();
    });

    try {
      if (profileDto.getUsername() != null) {
        user.setUsername(profileDto.getUsername());
      }

      if (user.getRole() == RoleEnum.CARE_AGENCY) {
        CareAgency agency = careAgencyRepository.findBySub(sub);
        agency.setAddress(profileDto.getAddress());
        agency.setPhone(profileDto.getPhone());
        agency.setWebsite(profileDto.getWebsite());
        agency.setDescription(profileDto.getDescription());
        careAgencyRepository.save(agency);
      } else if (user.getRole() == RoleEnum.RECIPIENT) {
        Recipient recipient = recipientRepository.findBySub(sub);
        recipient.setAge(profileDto.getAge());
        recipient.setAddress(profileDto.getAddress());
        recipient.setStory(profileDto.getStory());
        recipient.setInterest(profileDto.getInterest());
        recipientRepository.save(recipient);
      } else if (user.getRole() == RoleEnum.DONOR) {
        Donor donor = donorRepository.findBySub(sub);
        donor.setIntroduction(profileDto.getIntroduction());
        donorRepository.save(donor);
      }
      // update time
      user.setAccountUpdated(LocalDateTime.now());
      userRepository.save(user);
    } catch (Exception e) {
      throw BaseException.builder()
              .returnCode(ReturnCode.RC500)
              .message(e.getMessage()).build();
    }
  }

  // check address
  public void checkAddressSubmitted(UUID sub, UUID jwtSub) {
    if (!sub.equals(jwtSub)) {
      log.error(ReturnCode.INVALID_USER.getMessage());
      throw BaseException.builder()
              .returnCode(ReturnCode.INVALID_USER)
              .build();
    }
    // Retrieve the user from the repository using the provided sub
    User user = userRepository.findBySub(sub).orElseThrow(() -> {
      log.error("cannot find sub {}", sub);
      return BaseException.builder()
              .returnCode(ReturnCode.USER_NOT_EXIST)
              .build();
    });

    // Get the user's role
    RoleEnum role = user.getRole();
    if (role == RoleEnum.CARE_AGENCY) {
      String address = user.getCareAgency() != null ? user.getCareAgency().getAddress() : null;
      if (address == null || address.isEmpty()) {
        throw BaseException.builder()
                .returnCode(ReturnCode.ADDRESS_NOT_SUBMITTED)
                .build();
      }
    } else if (role == RoleEnum.RECIPIENT) {
      String address = user.getRecipient() != null ? user.getRecipient().getAddress() : null;
      if (address == null || address.isEmpty()) {
        throw BaseException.builder()
                .returnCode(ReturnCode.ADDRESS_NOT_SUBMITTED)
                .build();
      }
    }

  }

  // update avatar
  @Override
  public void updateAvatarUrl(UUID sub, AvatarUrlDto avatarUrlDto, UUID jwtSub) {
    if (!sub.equals(jwtSub)) {
      log.error(ReturnCode.INVALID_USER.getMessage());
      throw BaseException.builder()
              .returnCode(ReturnCode.INVALID_USER)
              .build();
    }
    // Retrieve the user from the repository using the provided sub
    User user = userRepository.findBySub(sub).orElseThrow(() -> {
      log.error("cannot find sub {}", sub);
      return BaseException.builder()
              .returnCode(ReturnCode.USER_NOT_EXIST)
              .build();
    });

    try {
      // Update the user's avatar URL
      user.setAvatarUrl(avatarUrlDto.getAvatarUrl());
      // Update the account update timestamp
      user.setAccountUpdated(LocalDateTime.now());
      // Save the user's updated profile to the database
      userRepository.save(user);
    } catch (Exception e) {
      throw BaseException.builder()
              .returnCode(ReturnCode.ERROR_UPLOADING_AVATAR)
              .message(e.getMessage()).build();
    }
  }

  @Override
  public PersonalInfoDto showPersonalInfo(UUID sub, UUID jwtSub) {
    if (!sub.equals(jwtSub)) {
      log.error(ReturnCode.INVALID_USER.getMessage());
      throw BaseException.builder()
              .returnCode(ReturnCode.INVALID_USER)
              .build();
    }

    return getUserInfo(sub);
  }


  @Override
  public PersonalInfoDto getUserInfo(UUID sub) {
    // Retrieve the user from the repository
    User user = userRepository.findBySub(sub).orElseThrow(() -> {
      log.error("cannot find sub {}", sub);
      return BaseException.builder()
              .returnCode(ReturnCode.USER_NOT_EXIST)
              .build();
    });
    // Prepare the user's basic info
    PersonalInfoDto personalInfoDto = PersonalInfoDto.builder()
            .userId(user.getUserId())
            .sub(user.getSub())
            .username(user.getUsername())
            .email(user.getEmail())
            .avatarUrl(user.getAvatarUrl())
            .role(user.getRole().toString())
            .accountCreated(user.getAccountCreated().toString())
            .build();
    // Check and handle associated entities
    if (user.getRole() == RoleEnum.CARE_AGENCY) {
      personalInfoDto.setAddress(user.getCareAgency().getAddress());
      personalInfoDto.setPhone(user.getCareAgency().getPhone());
      personalInfoDto.setWebsite(user.getCareAgency().getWebsite());
      personalInfoDto.setDescription(user.getCareAgency().getDescription());
    } else if (user.getRole() == RoleEnum.RECIPIENT) {
      personalInfoDto.setAge(String.valueOf(user.getRecipient().getAge()));
      personalInfoDto.setAddress(user.getRecipient().getAddress());
      personalInfoDto.setInterest(user.getRecipient().getInterest());
      personalInfoDto.setStory(user.getRecipient().getStory());
    } else if (user.getRole() == RoleEnum.DONOR) {
      personalInfoDto.setIntroduction(user.getDonor().getIntroduction());
    }

    return personalInfoDto;
  }
}
