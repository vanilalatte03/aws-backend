package pro.jiholim.awsbackend.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.jiholim.awsbackend.member.dto.MemberCreateRequest;
import pro.jiholim.awsbackend.member.dto.MemberResponse;
import pro.jiholim.awsbackend.member.entity.Member;
import pro.jiholim.awsbackend.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse create(MemberCreateRequest request) {
        Member member = new Member(
                request.getName(),
                request.getAge(),
                request.getMbti()
        );

        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 팀원입니다.")
        );
        return MemberResponse.from(member);
    }
}
