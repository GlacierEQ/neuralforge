package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserMapper userMapper = new UserMapper();

    public UserResource createUser(UserResource user){
        if (userRepository.existsByEmail(user.getEmail())) throw new EntityExistsException("An user is already registrated with this email address.");

        UserRoleResource basicRole = userRoleService.getRoleByEnum(UserRoleEnum.ROLE_STUDENT);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(basicRole);

        UserEntity userEntity = userMapper.mapToEntity(user);

        return userMapper.mapToResource(userRepository.save(userEntity));
    }

    public List<UserResource> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(userMapper::mapToResource)
                .collect(Collectors.toList());
    }

}
