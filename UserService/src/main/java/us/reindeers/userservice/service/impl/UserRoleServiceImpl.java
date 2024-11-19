package us.reindeers.userservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;
import us.reindeers.userservice.domain.dto.RoleDto;
import us.reindeers.userservice.domain.entity.*;
import us.reindeers.userservice.repository.CareAgencyRepository;
import us.reindeers.userservice.repository.DonorRepository;
import us.reindeers.userservice.repository.RecipientRepository;
import us.reindeers.userservice.repository.UserRepository;
import us.reindeers.userservice.service.UserRoleService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final CareAgencyRepository careAgencyRepository;
    private final RecipientRepository recipientRepository;
    private final DonorRepository donorRepository;

    @Override
    public String checkUserRole(UUID sub, UUID jwtSub) {
        if (!sub.equals(jwtSub)) {
            System.out.println(sub);
            System.out.println(jwtSub);
            log.error(ReturnCode.INVALID_USER.getMessage());
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .build();
        }
        User user = userRepository.findBySub(sub).orElseThrow(() -> {
            log.error("cannot find sub {}", sub);
            return BaseException.builder()
                    .returnCode(ReturnCode.USER_NOT_EXIST)
                    .build();
        });
            return user.getRole().toString();
    }

    @Override
    public String updateUserRole(UUID sub, RoleDto roleDto, UUID jwtSub) {
        if (!sub.equals(jwtSub)) {
            log.error(ReturnCode.INVALID_USER.getMessage());
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_USER)
                    .build();
        }
        User user = userRepository.findBySub(sub).orElseThrow(() -> {
            log.error("cannot find sub {}", sub);
            return BaseException.builder()
                    .returnCode(ReturnCode.USER_NOT_EXIST)
                    .build();
        });

        try {
            RoleEnum roleEnum = RoleEnum.valueOf(roleDto.getRole().toUpperCase());
            user.setRole(roleEnum);

            CognitoService cognitoService = new CognitoService("us-west-2_gx1ErGyAQ");
          
            if(roleEnum==RoleEnum.DONOR){
                Donor donor = new Donor();
                donor.setUser(user);
                cognitoService.setCognitoGroup(sub,"DONOR");
                donorRepository.save(donor);
            }else if(roleEnum==RoleEnum.CARE_AGENCY){
                CareAgency careAgency = new CareAgency();
                careAgency.setUser(user);
                cognitoService.setCognitoGroup(sub,"CARE_AGENCY");
                careAgencyRepository.save(careAgency);
            }else if(roleEnum==RoleEnum.RECIPIENT){
                Recipient recipient = new Recipient();
                recipient.setUser(user);
                cognitoService.setCognitoGroup(sub,"RECIPIENT");
                recipientRepository.save(recipient);
            }

            // update time
            user.setAccountUpdated(LocalDateTime.now());
            userRepository.save(user);
            return roleEnum.toString();
        } catch (IllegalArgumentException e) {
            throw BaseException.builder()
                    .returnCode(ReturnCode.ROLE_NOT_EXIST)
                    .message(e.getMessage())
                    .build();
        }

    }

}