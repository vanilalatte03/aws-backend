package pro.jiholim.awsbackend.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.jiholim.awsbackend.member.dto.MemberCreateRequest;
import pro.jiholim.awsbackend.member.dto.MemberResponse;
import pro.jiholim.awsbackend.member.dto.ProfileImageResponse;
import pro.jiholim.awsbackend.member.entity.Member;
import pro.jiholim.awsbackend.member.repository.MemberRepository;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;

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
        Member member = findMember(id);
        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse uploadProfileImage(Long id, MultipartFile file) {
        Member member = findMember(id);
        String key = s3Service.upload(id, file);
        member.updateProfileImageKey(key);

        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public ProfileImageResponse getProfileImage(Long id) {
        Member member = findMember(id);

        if (member.getProfileImageKey() == null) {
            throw new IllegalArgumentException("프로필 이미지가 없습니다.");
        }

        URL url = s3Service.getDownloadUrl(member.getProfileImageKey());

        Instant expiresAt = Instant.now().plus(Duration.ofDays(7));

        return new ProfileImageResponse(url.toString(), expiresAt.toString());
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}
