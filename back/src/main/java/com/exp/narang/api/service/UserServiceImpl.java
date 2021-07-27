package com.exp.narang.api.service;

import com.exp.narang.api.request.UserInfoUpdateReq;
import com.exp.narang.api.request.UserRegisterPostReq;
import com.exp.narang.db.entity.User;
import com.exp.narang.db.repository.UserRepository;
import com.exp.narang.db.repository.UserRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 구현 정의.
 */
@Service("userService")
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRepositorySupport userRepositorySupport;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Override
	public User createUser(UserRegisterPostReq userRegisterInfo) {
		return userRepository.save(
				User.builder().
						email(userRegisterInfo.getEmail())
						.username(userRegisterInfo.getUsername())
						.password(passwordEncoder.encode(userRegisterInfo.getPassword())) // 보안을 위해서 유저 패스워드 암호화 하여 디비에 저장.
						.build()
		);
	}

	@Override
	public User getUserByEmail(String email) {
		// 디비에 유저 정보 조회 (email 을 통한 조회).
		Optional<User> userOpt = userRepositorySupport.findUserByEmail(email);
		return userOpt.orElse(null);
	}


	@Override
	public boolean idExists(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public boolean userNameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	@Transactional
	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public User updateUser(UserInfoUpdateReq updateInfo, User user) {
		String upload_path = System.getProperty("user.dir") + "/src/main/resources/static/profileImages/";
		//profileImage 설정
		if(updateInfo.getFile() != null) {
			try {
				if (user.getThumbnailUrl() != null) {
					File file = new File(user.getThumbnailUrl());
					file.delete();
				}
				updateInfo.getFile().transferTo(new File(upload_path + user.getUserId() + ".jpg"));
				user.setThumbnailUrl("/static/profileImages/" + user.getUserId() + ".jpg");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(updateInfo.getUsername() != null) {
			user.setUsername(updateInfo.getUsername());
		}
		if(updateInfo.getCurrentPassword() != null) {
			user.setPassword(passwordEncoder.encode(updateInfo.getNewPassword()));
		}
		return userRepository.save(user);
	}
}
