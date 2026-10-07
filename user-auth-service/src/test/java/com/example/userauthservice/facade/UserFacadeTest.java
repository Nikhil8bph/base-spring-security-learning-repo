package com.example.userauthservice.facade;

import com.example.userauthservice.mapper.UserMapper;
import com.example.userauthservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.mock;

class UserFacadeTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserFacade userFacade = new UserFacade(userRepository, userMapper, passwordEncoder);


}
