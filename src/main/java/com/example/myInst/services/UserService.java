package com.example.myInst.services;

import com.example.myInst.entity.User;
import com.example.myInst.entity.enums.ERole;
import com.example.myInst.exceptions.UserExistException;
import com.example.myInst.payload.request.SignupRequest;
import com.example.myInst.repository.UserRepository;
import com.example.myInst.security.JWTTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User creatUser(SignupRequest signupRequest){
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getFirstname());
        user.setLastname(signupRequest.getLastname());
        user.setUsername(signupRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(signupRequest.getPassword()));
        user.getRoles().add(ERole.ROLE_USER);

        try {
            LOG.info("Saving User");
            return userRepository.save(user);
        }catch (Exception e){
            LOG.error("Error during registration. {}" + e.getMessage());
            throw new UserExistException("The user "+ user.getUsername() + " already exist. Please check credentials");
        }
    }
}
