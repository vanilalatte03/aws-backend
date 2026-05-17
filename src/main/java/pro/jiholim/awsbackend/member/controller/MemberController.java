package pro.jiholim.awsbackend.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.jiholim.awsbackend.member.dto.MemberCreateRequest;
import pro.jiholim.awsbackend.member.dto.MemberResponse;
import pro.jiholim.awsbackend.member.dto.ProfileImageResponse;
import pro.jiholim.awsbackend.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody MemberCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getMember(id));
    }

    @PostMapping("/{id}/profile-image")
    public ResponseEntity<MemberResponse> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberService.uploadProfileImage(id, file));
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<ProfileImageResponse> getProfileImage(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.getProfileImage(id));
    }
}
