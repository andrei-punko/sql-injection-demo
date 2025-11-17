package by.andd3dfx.sqlinjection.mapper;

import by.andd3dfx.sqlinjection.dto.UserDto;
import by.andd3dfx.sqlinjection.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public List<UserDto> toDto(List<User> users) {
        return users.stream().map(this::toDto)
                .toList();
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
