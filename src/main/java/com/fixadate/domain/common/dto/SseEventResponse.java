package com.fixadate.domain.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

// swagger 기재용
@Schema(description = "SSE 이벤트 응답 데이터 모델")
public class SseEventResponse {

    @Schema(description = "이벤트 ID", example = "")
    private String id;

    @Schema(description = "이벤트 이름", example = "alive_notification, ...")
    private String name;

    @Schema(description = "이벤트 데이터", example = "Connection completed(연결 성공), New notification(새로운 알림), ...")
    private String data;

    public SseEventResponse(String id, String name, String data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
