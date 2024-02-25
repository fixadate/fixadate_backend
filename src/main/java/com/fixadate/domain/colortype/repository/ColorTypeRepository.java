package com.fixadate.domain.colortype.repository;

import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColorTypeRepository extends JpaRepository<ColorType, Long> {
    List<ColorType> findColorTypesByMember(Member member);

    Optional<ColorType> findColorTypeByColor(String color);
}
