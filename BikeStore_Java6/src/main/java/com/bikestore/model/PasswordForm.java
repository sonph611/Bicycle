package com.bikestore.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter@Setter
public class PasswordForm {
	@NotEmpty(message = "Vui lòng nhập tài khoản")
	String username;

	@NotEmpty(message = "Vui lòng nhập mật khẩu hiện tại")
	String password;

	@NotEmpty(message = "Vui lòng nhập mật khẩu mới")
	String newPassword;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	

}
