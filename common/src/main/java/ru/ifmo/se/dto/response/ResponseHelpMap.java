package ru.ifmo.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHelpMap extends Response {

    public ResponseHelpMap(boolean status,
                           String message,
                           Map<String, String> helpMap) {
        super(status, message);
        this.helpMap = helpMap;
    }

    private Map<String, String> helpMap;
}
