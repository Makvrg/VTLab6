package ru.ifmo.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCount extends Response {

    public ResponseCount(boolean status,
                         String message,
                         Long count) {
        super(status, message);
        this.count = count;
    }

    private Long count;
}
