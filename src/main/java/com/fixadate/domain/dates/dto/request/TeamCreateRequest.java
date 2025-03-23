package com.fixadate.domain.dates.dto.request;

import java.util.List;
import lombok.Data;

public record TeamCreateRequest (
    String name,
    String profile,
    List<String> inviteMemberIdList){
}
