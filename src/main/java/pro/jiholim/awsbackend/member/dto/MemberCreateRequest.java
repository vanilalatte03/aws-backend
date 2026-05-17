package pro.jiholim.awsbackend.member.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class MemberCreateRequest {

    @NotBlank
    private String name;

    @Min(1)
    private int age;

    @NotBlank
    private String mbti;
}
