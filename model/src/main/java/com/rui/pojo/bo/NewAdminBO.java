package com.rui.pojo.bo;

/**
 * @Author suxiaorui
 * @Description TODO
 * @Date 2023/7/8 19:07
 * @Version 1.0
 */


import javax.validation.constraints.NotBlank;

/**
 * 添加管理人员的BO
 */
public class NewAdminBO {

    @NotBlank(message = "登录名不能为空")
    private String username;
    @NotBlank(message = "负责人不能为空")
    private String adminName;
    private String password;
    private String confirmPassword;
    private String img64;
    private String faceId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getImg64() {
        return img64;
    }

    public void setImg64(String img64) {
        this.img64 = img64;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    @Override
    public String toString() {
        return "NewAdminBO{" +
                "username='" + username + '\'' +
                ", adminName='" + adminName + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", img64='" + img64 + '\'' +
                ", faceId='" + faceId + '\'' +
                '}';
    }
}

