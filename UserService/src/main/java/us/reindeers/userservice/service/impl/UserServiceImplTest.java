package us.reindeers.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import us.reindeers.common.dto.NotificationMessage;
import us.reindeers.common.enums.MessageType;
import us.reindeers.common.response.constant.template.ReturnCode;
import us.reindeers.common.response.exception.BaseException;
import us.reindeers.userservice.domain.dto.UserDto;
import us.reindeers.userservice.domain.entity.RoleEnum;
import us.reindeers.userservice.domain.entity.User;
import us.reindeers.userservice.repository.UserRepository;
import us.reindeers.userservice.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImplTest implements UserService {

    private final UserRepository userRepository;

    private final MessageSender messageSender;

    @Override
    public void addUser(UserDto userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw BaseException.builder()
                    .returnCode(ReturnCode.EMAIL_ALREADY_EXISTS)
                    .message("Email already exists")
                    .build();
        }

        try {
            User user = User.builder()
                    .sub(userDto.getSub())
                    .username(userDto.getUsername())
                    .email(userDto.getEmail())
                    .avatarUrl("https://reindeers-images.s3.us-west-2.amazonaws.com/avatar/default/DefaultAvatar.png")
                    .role(RoleEnum.DEFAULT)
                    .accountCreated(LocalDateTime.now())
                    .accountUpdated(LocalDateTime.now()).build();

            //save the user
            userRepository.save(user);

            // construct message
            NotificationMessage notificationMessage = NotificationMessage.builder()
                    .messageType(MessageType.WELCOME_MESSAGE)
                    .receiverSub(user.getSub())
                    .notificationCreated(LocalDateTime.now())
                    .build();

            //send welcome message
            messageSender.sendMessage("notification.user.welcome", notificationMessage);

        }catch (DataAccessException e) {
            throw BaseException.builder()
                    .returnCode(ReturnCode.DATABASE_ACCESS_ERROR)
                    .message(e.getMessage())
                    .build();
        } catch (IllegalArgumentException e) {
            throw BaseException.builder()
                    .returnCode(ReturnCode.INVALID_INPUT)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            throw BaseException.builder()
                    .returnCode(ReturnCode.RC500)
                    .message(e.getMessage())
                    .build();
        }
    }
}
