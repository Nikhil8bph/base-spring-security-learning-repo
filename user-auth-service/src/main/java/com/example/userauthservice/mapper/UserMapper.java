package com.example.userauthservice.mapper;

import com.example.sharedkernel.dto.PageDTO;
import com.example.userauthservice.dto.UserDTO;
import com.example.userauthservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {
    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);

    List<UserDTO> toDTO(List<User> users);

    List<User> toEntity(List<UserDTO> userDTOS);

    default PageDTO<List<UserDTO>> toDTO(Page<User> users) {
        String sortBy = users.getSort().stream()
                .findFirst()
                .map(Sort.Order::getProperty)
                .orElse(null);
        String sortDir = users.getSort().stream()
                .findFirst()
                .map(order -> order.getDirection().name())
                .orElse(null);
        return new PageDTO<>(toDTO(users.getContent()), users.getTotalElements(),
                (long) users.getTotalPages(), users.getNumber(), users.getSize(), sortBy, sortDir);
    }
}
