package pro.jiholim.awsbackend.member.dto;

import lombok.Getter;

@Getter
public class ProfileImageResponse {

    private final String url;
    private final String expiresAt;

    public ProfileImageResponse(String url, String expiresAt) {
        this.url = url;
        this.expiresAt = expiresAt;
    }
}
