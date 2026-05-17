package pro.jiholim.awsbackend.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.jiholim.awsbackend.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
