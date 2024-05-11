package com.fixadate.domain.colortype.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fixadate.domain.colortype.entity.ColorType;
import com.fixadate.domain.member.entity.Member;

public interface ColorTypeRepository extends JpaRepository<ColorType, Long> {
	List<ColorType> findColorTypesByMember(Member member);

	Optional<ColorType> findColorTypeByColor(String color);

	Optional<ColorType> findColorTypeByColorAndMember(String color, Member member);
}
