package ru.ifmo.se.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ifmo.se.dto.supporting.CollectionInfoDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCollectionInfo extends Response {

    public ResponseCollectionInfo(boolean status,
                                  String message,
                                  CollectionInfoDto collectionInfoDto) {
        super(status, message);
        this.collectionInfoDto = collectionInfoDto;
    }

    private CollectionInfoDto collectionInfoDto;
}
