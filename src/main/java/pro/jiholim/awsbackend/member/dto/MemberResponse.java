package pro.jiholim.awsbackend.member.dto;

import lombok.Getter;
import pro.jiholim.awsbackend.member.entity.Member;

@Getter
public class MemberResponse {

    private final Long id;
    private final String name;
    private final int age;
    private final String mbti;

    public MemberResponse(
            Long id,
            String name,
            int age,
            String mbti
    ) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.mbti = mbti;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getAge(),
                member.getMbti()
        );
    }
}
