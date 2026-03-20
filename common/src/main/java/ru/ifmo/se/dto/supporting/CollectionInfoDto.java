package ru.ifmo.se.dto.supporting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionInfoDto implements Serializable {

    private String collectionType;
    private Date initializationDate;
    private String elementsType;
    private int countOfElements;
}
