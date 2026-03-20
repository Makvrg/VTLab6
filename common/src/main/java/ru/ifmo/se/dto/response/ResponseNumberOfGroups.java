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
public class ResponseNumberOfGroups extends Response {

    public ResponseNumberOfGroups(boolean status,
                                  String message,
                                  Map<Float, Integer> numberOfGroups) {
        super(status, message);
        this.numberOfGroups = numberOfGroups;
    }

    private Map<Float, Integer> numberOfGroups;
}
