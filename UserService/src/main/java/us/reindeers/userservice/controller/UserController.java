package us.reindeers.userservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import us.reindeers.userservice.domain.dto.*;
import us.reindeers.userservice.service.UserProfileService;
import us.reindeers.userservice.service.UserRoleService;
import us.reindeers.userservice.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final UserProfileService userProfileService;

    /*
    * 接受来自cognito的用户信息 并把用户信息存到数据库中
    * */
    @PostMapping("/add")
    public void addUser(@RequestBody UserDto userDto){
        userService.addUser(userDto);
    }

    /*
    * 判断用户是否选择了自己的role 如果没有选择则强制用户跳转至选择role的页面
    * */
    @GetMapping("/check-role/{sub}")
    public String checkUserRole(@PathVariable UUID sub, @AuthenticationPrincipal Jwt jwt) {
        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        return userRoleService.checkUserRole(sub, jwtSub);
    }

    /*
    * 用户更新role
    * */
    @PostMapping("/update-role/{sub}")
    public String updateRole(@PathVariable UUID sub, @RequestBody RoleDto roleDto,
                             @AuthenticationPrincipal Jwt jwt) {
        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        return userRoleService.updateUserRole(sub, roleDto, jwtSub);
    }

    /*
    * 用户更新个人信息
    * */
    @PostMapping("/update-profile/{sub}")
    public void updateProfileInfo(@PathVariable UUID sub, @RequestBody ProfileDto profileDto,
                                  @AuthenticationPrincipal Jwt jwt){
        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        userProfileService.updateProfileInfo(sub, profileDto, jwtSub);
    }

    /*
    * 判断用户是否填写了地址
    * */
    @GetMapping("/check-address/{sub}")
    public void checkAddressSubmitted(@PathVariable UUID sub,
                                      @AuthenticationPrincipal Jwt jwt) {
        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        userProfileService.checkAddressSubmitted(sub, jwtSub);
    }

    /*
    * 用户更新头像
    * */
    @PostMapping("/update-avatar/{sub}")
    public void updateAvatarUrl(@PathVariable UUID sub, @RequestBody AvatarUrlDto avatarUrl,
                                @AuthenticationPrincipal Jwt jwt){
        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        userProfileService.updateAvatarUrl(sub, avatarUrl, jwtSub);
    }

    /*
    * 查询个人信息
    * 需要auth
    * */
    @GetMapping("/user-info/{sub}")
    public PersonalInfoDto getUserInfo(@PathVariable UUID sub, @AuthenticationPrincipal Jwt jwt) {
        UUID jwtSub = UUID.fromString(jwt.getClaimAsString("sub"));
        return userProfileService.showPersonalInfo(sub, jwtSub);
    }

    // difference compared with "/user-info/{sub}":
    // this endpoint allows any user to query detail info of another user. 不校验是否查询本人信息
    /*
    * 查询个人信息 并显示在礼物页面
    * 因为是公共page所以不需要auth
    * */
    @GetMapping("/user-public-info/{sub}")
    public PersonalInfoDto getUserPublicInfo(@PathVariable UUID sub) {
        return userProfileService.getUserInfo(sub);
    }

}
