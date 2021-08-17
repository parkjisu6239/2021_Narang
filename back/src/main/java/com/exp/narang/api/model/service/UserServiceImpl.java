package com.exp.narang.api.model.service;

import com.exp.narang.api.model.network.ImgbbResponse;
import com.exp.narang.api.model.network.ImgbbResponseData;
import com.exp.narang.api.model.network.RetrofitClient;
import com.exp.narang.api.model.network.RetrofitService;
import com.exp.narang.api.model.request.UserInfoUpdateReq;
import com.exp.narang.api.model.request.UserRegisterPostReq;
import com.exp.narang.api.model.db.entity.User;
import com.exp.narang.api.model.db.repository.UserRepository;
import com.exp.narang.api.model.db.repository.UserRepositorySupport;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 *	유저 관련 비즈니스 로직 처리를 위한 서비스 구현 정의.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRepositorySupport userRepositorySupport;
	@Autowired
	private PasswordEncoder passwordEncoder;
	private static final RetrofitService retrofitService;
	private static final OkHttpClient httpClient;
	private static final String KEY = "d3e0947404ef9c6533664b5c536be532";

//	public UserServiceImpl(UserRepository userRepository, UserRepositorySupport userRepositorySupport,
//						   PasswordEncoder passwordEncoder){
//		this.userRepository = userRepository;
//		this.userRepositorySupport = userRepositorySupport;
//		this.passwordEncoder = passwordEncoder;
	static{
		retrofitService = RetrofitClient.getRetrofitInstance().create(RetrofitService.class);
		httpClient = new OkHttpClient();
	}
//	}

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
	public void deleteProfile(User user) {
		String upload_path = ":D/images/profile/";
		try {
			if (user.getThumbnailUrl() != null) {
				File file = new File(upload_path + user.getUserId() + ".jpg");
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		user.setThumbnailUrl(null);
		userRepository.save(user);
	}

	@Override
	public User updateUser(UserInfoUpdateReq updateInfo, User user) {
		String upload_path = "D:/images/profile/";
		//profileImage 설정
		if(updateInfo.getFile() != null) {
//			try {
//				if (user.getThumbnailUrl() != null) {
//					File file = new File(upload_path + user.getUserId() + ".jpg");
//					file.delete();
//				}
//				updateInfo.getFile().transferTo(new File(upload_path + user.getUserId() + ".jpg"));
//				user.setThumbnailUrl("/images/profile/" + user.getUserId() + ".jpg");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			try {
				requestUpload(updateInfo.getFile(), user);
			}catch (IOException e){
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

	private void requestUpload(MultipartFile mFile, User user) throws IOException {
		log.debug("받은 파일명 : " + mFile.getOriginalFilename());

		RequestBody body = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("key", KEY)
				.addFormDataPart("image", mFile.getOriginalFilename(), RequestBody.create(MediaType.parse(""), mFile.getBytes()))
				.build();
		MultipartBody.Part image = MultipartBody.Part.createFormData("image", mFile.getOriginalFilename(), body);

		retrofitService.postUploadImage(KEY, image)
				.enqueue(new Callback<ImgbbResponse>(){
					@Override
					public void onResponse(@NotNull Call<ImgbbResponse> call, @NotNull Response<ImgbbResponse> response) {
						log.debug("업로드 요청 성공");

						ImgbbResponse imgbbResponse = response.body();
						ImgbbResponseData data = imgbbResponse.getData();
						if(imgbbResponse.isSuccess()){
							user.setThumbnailUrl(data.getUrl());
							log.debug("업로드한 이미지 url" + data.getUrl());
							log.debug("deleteUrl" + data.getDeleteUrl());
							userRepository.save(user);
						}
					}

					@Override
					public void onFailure(Call<ImgbbResponse> call, Throwable t) {
						log.error("요청 실패");
						t.printStackTrace();
					}
				});
	}
}
